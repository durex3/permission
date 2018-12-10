package com.durex.service;

import com.durex.param.AclModuleParam;
import com.durex.param.DeptParam;

public interface SysAclModuleService {

    void save(AclModuleParam aclModuleParam);

    void update(AclModuleParam aclModuleParam);

    void delete(Integer aclModuleId);
}
