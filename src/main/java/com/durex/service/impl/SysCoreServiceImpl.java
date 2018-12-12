package com.durex.service.impl;

import com.durex.common.RequestHolder;
import com.durex.dao.SysAclMapper;
import com.durex.dao.SysRoleAclMapper;
import com.durex.dao.SysRoleMapper;
import com.durex.dao.SysRoleUserMapper;
import com.durex.model.SysAcl;
import com.durex.model.SysUser;
import com.durex.service.SysCoreService;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        // 自定义的管理员规则
        SysUser sysUser = RequestHolder.getCurrentUser();
        if (sysUser.getMail().contains("admin")) {
            return true;
        }
        return false;
    }

    @Override
    public boolean hasAcl(String url) {
        if (isSuperAdmin()) {
            return true;
        }
        List<SysAcl> aclList = sysAclMapper.getByUrl(url);
        // 如果访问的不是系统的权限点，直接给通过
        if (CollectionUtils.isEmpty(aclList)) {
            return true;
        }
        List<SysAcl> userAclList = getCurrentUserAclList();
        Set<Integer> userAclIdList = userAclList.stream().map(sysAcl -> sysAcl.getId()).collect(Collectors.toSet());
        boolean hasValidAcl = false;
        // 规则：只要有一个权限点的权限，那么我们就认为有访问权限
        for (SysAcl acl : aclList) {
            if (acl == null || acl.getStatus() != 1) {
                continue;
            }
            hasValidAcl = true;
            if (userAclIdList.contains(acl.getId())) {
                return true;
            }
        }
        // 如果权限点都是无效的
        if (!hasValidAcl) {
            return true;
        }
        return false;
    }
}

