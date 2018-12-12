package com.durex.service;

import com.durex.model.SysAcl;

import java.util.List;

public interface SysCoreService {

    List<SysAcl> getCurrentUserAclList();

    List<SysAcl> getRoleAclList(int roleId);

    List<SysAcl> getUserAclList(int userId);

    boolean hasAcl(String url);

}
