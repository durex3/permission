package com.durex.service.impl;

import com.durex.common.RequestHolder;
import com.durex.dao.SysAclModuleMapper;
import com.durex.exception.ParamException;
import com.durex.model.SysAclModule;
import com.durex.param.AclModuleParam;
import com.durex.service.SysAclModuleService;
import com.durex.util.BeanValidator;
import com.durex.util.IpUtil;
import com.durex.util.LevelUtil;
import com.google.common.base.Preconditions;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;

@Service
public class SysAclModuleServiceImpl implements SysAclModuleService {

    @Autowired
    private SysAclModuleMapper sysAclModuleMapper;

    @Override
    public void save(AclModuleParam aclModuleParam) {
        BeanValidator.check(aclModuleParam);
        if (checkExist(aclModuleParam.getParentId(), aclModuleParam.getName(), aclModuleParam.getId())) {
            throw new ParamException("同一层级下存在相同的权限模块");
        }
        SysAclModule sysAclModule = SysAclModule.builder()
                .name(aclModuleParam.getName())
                .level(LevelUtil.calculateLevel(getLevel(aclModuleParam.getParentId()), aclModuleParam.getParentId()))
                .seq(aclModuleParam.getSeq())
                .status(aclModuleParam.getStatus())
                .remark(aclModuleParam.getRemark())
                .parentId(aclModuleParam.getParentId())
                .build();
        sysAclModule.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysAclModule.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysAclModule.setOperateTime(new Date());
        sysAclModuleMapper.insertSelective(sysAclModule);
    }

    @Override
    public void update(AclModuleParam aclModuleParam) {
        BeanValidator.check(aclModuleParam);
        if (checkExist(aclModuleParam.getParentId(), aclModuleParam.getName(), aclModuleParam.getId())) {
            throw new ParamException("同一层级下存在相同的权限模块");
        }
        SysAclModule before = sysAclModuleMapper.selectByPrimaryKey(aclModuleParam.getId());
        Preconditions.checkNotNull(before, "待更新的权限模块不存在");
        SysAclModule after = SysAclModule.builder()
                .id(aclModuleParam.getId())
                .name(aclModuleParam.getName())
                .level(LevelUtil.calculateLevel(getLevel(aclModuleParam.getParentId()), aclModuleParam.getParentId()))
                .seq(aclModuleParam.getSeq())
                .status(aclModuleParam.getStatus())
                .remark(aclModuleParam.getRemark())
                .build();
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperateTime(new Date());
        updateWithChild(before, after);
    }

    @Transactional
    protected void updateWithChild(SysAclModule before, SysAclModule after) {
        // 如果自己是自己的上级是不合理的
        if(before.getId() == after.getParentId()) {
            return;
        }
        String newLevelPrefix = after.getLevel();
        String oldLevelPrefix = before.getLevel();
        if (!after.getLevel().equals(before.getLevel())) {
            String curLevel = before.getLevel() + "." + before.getId();
            List<SysAclModule> aclModuleList = sysAclModuleMapper.getChildAclModuleListByLevel(curLevel + "%");
            if (CollectionUtils.isNotEmpty(aclModuleList)) {
                for(SysAclModule aclModule : aclModuleList) {
                    String level = aclModule.getLevel();
                    if (level.equals(curLevel) || level.indexOf(curLevel + ".") == 0) {
                        level = newLevelPrefix + level.substring(oldLevelPrefix.length());
                        aclModule.setLevel(level);
                    }
                }
                // 批量更新
                sysAclModuleMapper.batchUpdateLevel(aclModuleList);
            }
            sysAclModuleMapper.updateByPrimaryKey(after);
        }
    }

    private boolean checkExist(Integer parentId, String deptName, Integer aclModuleId) {
        return sysAclModuleMapper.countByNameAndParentId(parentId, deptName, aclModuleId) > 0;
    }

    private String getLevel(Integer aclModuleId) {
        SysAclModule sysAclModule = sysAclModuleMapper.selectByPrimaryKey(aclModuleId);
        if (sysAclModule == null) {
            return null;
        }
        return sysAclModule.getLevel();
    }
}
