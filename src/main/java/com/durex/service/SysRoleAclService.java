package com.durex.service;

import java.util.List;

public interface SysRoleAclService {

    public void changeRoleAcl(Integer roleId, List<Integer> aclIdList);
}
