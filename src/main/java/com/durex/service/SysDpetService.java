package com.durex.service;

import com.durex.exception.ParamException;
import com.durex.param.DeptParam;

public interface SysDpetService {

    void save(DeptParam deptParam);

    void update(DeptParam deptParam);

    void delete(Integer deptId);
}
