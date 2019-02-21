package com.durex.service.impl;

import com.durex.common.RequestHolder;
import com.durex.dao.SysAclMapper;
import com.durex.dao.SysAclModuleMapper;
import com.durex.exception.ParamException;
import com.durex.model.SysAclModule;
import com.durex.param.AclModuleParam;
import com.durex.service.SysAclModuleService;
import com.durex.service.SysLogService;
import com.durex.service.TransactionalService;
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
    @Autowired
    private SysAclMapper sysAclMapper;
    @Autowired
    private TransactionalService transactionalService;
    @Autowired
    private SysLogService sysLogService;

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
        sysLogService.saveAclModuleLog(null, sysAclModule);
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
                .parentId(aclModuleParam.getParentId())
                .level(LevelUtil.calculateLevel(getLevel(aclModuleParam.getParentId()), aclModuleParam.getParentId()))
                .seq(aclModuleParam.getSeq())
                .status(aclModuleParam.getStatus())
                .remark(aclModuleParam.getRemark())
                .build();
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperateTime(new Date());
        transactionalService.updateWithChild(before, after);
        sysLogService.saveAclModuleLog(before, after);
    }



    @Override
    public void delete(Integer aclModuleId) {
        SysAclModule sysAclModule = sysAclModuleMapper.selectByPrimaryKey(aclModuleId);
        Preconditions.checkNotNull(sysAclModule, "待更新的权限模块不存在");
        if (sysAclModuleMapper.countByParentId(sysAclModule.getId()) > 0) {
            throw new ParamException("当前权限模块下面有子模块，无法删除");
        }
        if (sysAclMapper.countByAclModuleId(sysAclModule.getId()) > 0) {
            throw new ParamException("当前权限模块下面有权限点，无法删除");
        }
        sysAclModuleMapper.deleteByPrimaryKey(aclModuleId);
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
