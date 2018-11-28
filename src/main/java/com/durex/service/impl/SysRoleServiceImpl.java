package com.durex.service.impl;

import com.durex.common.RequestHolder;
import com.durex.dao.SysRoleMapper;
import com.durex.exception.ParamException;
import com.durex.model.SysRole;
import com.durex.param.RoleParam;
import com.durex.service.SysRoleService;
import com.durex.util.BeanValidator;
import com.durex.util.IpUtil;
import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
public class SysRoleServiceImpl implements SysRoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Override
    public void save(RoleParam roleParam) {
        BeanValidator.check(roleParam);
        if (checkExist(roleParam.getName(), roleParam.getId())) {
            throw new ParamException("角色名称已经存在");
        }
        SysRole sysRole = SysRole.builder()
                .name(roleParam.getName())
                .status(roleParam.getStatus())
                .type(roleParam.getType())
                .remark(roleParam.getRemark())
                .build();
        sysRole.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysRole.setOperateTime(new Date());
        sysRole.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysRoleMapper.insertSelective(sysRole);
    }

    @Override
    public void update(RoleParam roleParam) {
        BeanValidator.check(roleParam);
        if (checkExist(roleParam.getName(), roleParam.getId())) {
            throw new ParamException("角色名称已经存在");
        }
        SysRole before = sysRoleMapper.selectByPrimaryKey(roleParam.getId());
        Preconditions.checkNotNull(before, "待更新的角色不存在");
        SysRole after = SysRole.builder()
                .id(roleParam.getId())
                .name(roleParam.getName())
                .status(roleParam.getStatus())
                .type(roleParam.getType())
                .remark(roleParam.getRemark())
                .build();
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateTime(new Date());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysRoleMapper.updateByPrimaryKeySelective(after);
    }

    @Override
    public List<SysRole> getAll() {
        return sysRoleMapper.getAll();
    }

    private boolean checkExist(String name, Integer id) {
        return sysRoleMapper.countByName(name, id) > 0;
    }
}
