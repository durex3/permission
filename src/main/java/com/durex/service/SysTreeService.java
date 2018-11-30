package com.durex.service;

import com.durex.dto.AclModuleLevelDto;
import com.durex.dto.DeptLevelDto;

import java.util.List;

public interface SysTreeService {
    public List<DeptLevelDto> deptTree();

    public List<AclModuleLevelDto> aclModuleTree();

    public List<AclModuleLevelDto> roleAclTree(int roleId);
}
