package com.durex.service;

import com.durex.model.SysDept;

import java.util.List;

public interface TransactionalService {

    public void updateRoleAcl(Integer roleId, List<Integer> aclIdList);
    public void updateWithChild(SysDept before, SysDept after);
}
