package com.durex.service;

import com.durex.beans.PageQuery;
import com.durex.beans.PageResult;
import com.durex.model.SysUser;
import com.durex.param.UserParam;

public interface SysUserService {

    void save(UserParam userParam);

    void update(UserParam userParam);

    SysUser findByKeyword(String keyword);

    PageResult<SysUser> getPageByDeptId(int deptId, PageQuery pageQuery);
}
