package com.durex.service.impl;

import com.durex.common.RequestHolder;
import com.durex.dao.SysDeptMapper;
import com.durex.dao.SysRoleAclMapper;
import com.durex.model.SysDept;
import com.durex.model.SysRoleAcl;
import com.durex.service.TransactionalService;
import com.durex.util.IpUtil;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


/**
 * 专门用于做事务
 */
@Service
public class TransactionalServiceImpl implements TransactionalService {

    @Autowired
    private SysRoleAclMapper sysRoleAclMapper;
    @Autowired
    private SysDeptMapper sysDeptMapper;


    /**
     * 更新角色的权限点
     * @param roleId
     * @param aclIdList
     */
    @Transactional
    @Override
    public void updateRoleAcl(Integer roleId, List<Integer> aclIdList) {
        sysRoleAclMapper.deleteByRoleId(roleId);
        if (CollectionUtils.isEmpty(aclIdList)) {
            return;
        }
        List<SysRoleAcl> roleAclList = Lists.newArrayList();
        for (Integer i : aclIdList) {
            SysRoleAcl sysRoleAcl = SysRoleAcl.builder()
                    .roleId(roleId)
                    .aclId(i)
                    .operator(RequestHolder.getCurrentUser().getUsername())
                    .operateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()))
                    .operateTime(new Date())
                    .build();
            roleAclList.add(sysRoleAcl);
        }
        sysRoleAclMapper.batchInset(roleAclList);
    }

    @Transactional
    public void updateWithChild(SysDept before, SysDept after) {
        // 如果自己是自己的上级部门是不合理的
        if(before.getId() == after.getParentId()) {
            return;
        }
        String newLevelPrefix = after.getLevel();
        String oldLevelPrefix = before.getLevel();
        if (!after.getLevel().equals(before.getLevel())) {
            String curLevel = before.getLevel() + "." + before.getId();
            List<SysDept> deptList = sysDeptMapper.getChildDeptListByLevel(curLevel + "%");
            if (CollectionUtils.isNotEmpty(deptList)) {
                for (SysDept dept : deptList) {
                    // getChildDeptListByLevel可能会取出多余的内容，因此需要加个判断
                    // 比如0.1* 可能取出0.1、0.1.3、0.11、0.11.3，而期望取出  0.1、0.1.3， 因此呢需要判断等于0.1或者以0.1.为前缀才满足条件
                    String level = dept.getLevel();
                    if (level.equals(curLevel) || level.indexOf(curLevel + ".") == 0) {
                        level = newLevelPrefix + level.substring(oldLevelPrefix.length());
                        dept.setLevel(level);
                    }
                }
                // 批量更新
                sysDeptMapper.batchUpdateLevel(deptList);
            }
        }
        sysDeptMapper.updateByPrimaryKey(after);
    }
}
