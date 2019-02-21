package com.durex.service;

import com.durex.model.SysAclModule;
import com.durex.model.SysDept;

import java.util.List;

public interface TransactionalService {

    public void updateRoleAcl(Integer roleId, List<Integer> aclIdList);
    public void updateRoleUser(Integer roleId, List<Integer> userIdList);
    public void updateWithChild(SysDept before, SysDept after);
    public void updateWithChild(SysAclModule before, SysAclModule after);
}
