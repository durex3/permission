package com.durex.service;

import com.durex.model.SysRole;

import java.util.List;

public interface SysRoleAclService {
    void changeRoleAcl(Integer roleId, List<Integer> aclIdList);

    List<SysRole> getRoleListByAclId(int aclId);
}
