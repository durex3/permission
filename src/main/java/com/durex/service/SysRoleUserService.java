package com.durex.service;

import com.durex.model.SysRole;
import com.durex.model.SysUser;

import java.util.List;

public interface SysRoleUserService {

    List<SysUser> getListByRoleId(int roleId);

    void changeRoleUser(int roleId, List<Integer> userIdList);

    List<SysRole> getRoleListByUserId(int userId);

    List<SysUser> getUserListByRoleList(List<SysRole> roleList);
}
