package com.durex.service.impl;

import com.durex.common.RequestHolder;
import com.durex.dao.SysAclMapper;
import com.durex.dao.SysRoleAclMapper;
import com.durex.dao.SysRoleMapper;
import com.durex.dao.SysRoleUserMapper;
import com.durex.model.SysAcl;
import com.durex.service.SysCoreService;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SysCoreServiceImpl implements SysCoreService {

    @Autowired
    private SysAclMapper sysAclMapper;
    @Autowired
    private SysRoleUserMapper sysRoleUserMapper;
    @Autowired
    private SysRoleAclMapper sysRoleAclMapper;

    @Override
    public List<SysAcl> getCurrentUserAclList() {
        int userId = RequestHolder.getCurrentUser().getId();
        return getUserAclList(userId);
    }

    @Override
    public List<SysAcl> getRoleAclList(int roleId) {
        // 根据角色id取出权限点id
        List<Integer> aclIdList = sysRoleAclMapper.getAclIdListByRoleIdList(Lists.newArrayList(roleId));
        if (CollectionUtils.isEmpty(aclIdList)) {
            return Lists.newArrayList();
        }
        return sysAclMapper.getByIdList(aclIdList);
    }

    @Override
    public List<SysAcl> getUserAclList(int userId) {
        if (isSuperAdmin()) {
            return sysAclMapper.getAll();
        }
        // 根据用户ID取出角色ID列表
        List<Integer> userRoleIdList = sysRoleUserMapper.getRoleIdListByUserId(userId);
        if (CollectionUtils.isEmpty(userRoleIdList)) {
            return Lists.newArrayList();
        }
        // 根据用户角色ID列表取出权限ID列表
        List<Integer> userAclIdList = sysRoleAclMapper.getAclIdListByRoleIdList(userRoleIdList);
        if (CollectionUtils.isEmpty(userRoleIdList)) {
            return Lists.newArrayList();
        }
        return sysAclMapper.getByIdList(userAclIdList);
    }

    public boolean isSuperAdmin() {
        return true;
    }
}

