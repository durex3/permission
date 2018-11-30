package com.durex.service;

import com.durex.model.SysAcl;

import java.util.List;

public interface SysCoreService {

    public List<SysAcl> getCurrentUserAclList();

    public List<SysAcl> getRoleAclList(int roleId);

    public List<SysAcl> getUserAclList(int userId);
}
