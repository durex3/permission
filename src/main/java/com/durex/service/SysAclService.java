package com.durex.service;

import com.durex.beans.PageQuery;
import com.durex.beans.PageResult;
import com.durex.model.SysAcl;
import com.durex.param.AclParam;

public interface SysAclService {

    void save(AclParam aclParam);

    void update(AclParam aclParam);

    PageResult<SysAcl> getPageByAclModuleId(int aclModuleId, PageQuery pageQuery);
}
