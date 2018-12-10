package com.durex.service;

import com.durex.dto.AclModuleLevelDto;
import com.durex.dto.DeptLevelDto;

import java.util.List;

public interface SysTreeService {
     List<DeptLevelDto> deptTree();

     List<AclModuleLevelDto> aclModuleTree();

     List<AclModuleLevelDto> roleAclTree(int roleId);

     List<AclModuleLevelDto> userAclTree(int userId);
}
