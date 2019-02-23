package com.durex.service;

import com.durex.beans.PageQuery;
import com.durex.beans.PageResult;
import com.durex.model.*;
import com.durex.param.SearchLogParam;

import java.util.List;

public interface SysLogService {

    void saveDeptLog(SysDept before, SysDept after);

    void saveUserLog(SysUser before, SysUser after);

    void saveAclModuleLog(SysAclModule before, SysAclModule after);

    void saveAclLog(SysAcl before, SysAcl after);

    void saveRoleLog(SysRole before, SysRole after);

    PageResult<SysLogWithBLOBs> searchPageList(SearchLogParam param, PageQuery page);

    void recover(int id);
}
