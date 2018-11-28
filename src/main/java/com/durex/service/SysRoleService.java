package com.durex.service;

import com.durex.model.SysRole;
import com.durex.param.RoleParam;

import java.util.List;

public interface SysRoleService {

    void save(RoleParam roleParam);

    void update(RoleParam roleParam);

    List<SysRole> getAll();
}
