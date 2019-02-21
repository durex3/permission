package com.durex.service.impl;

import com.durex.beans.PageQuery;
import com.durex.beans.PageResult;
import com.durex.common.RequestHolder;
import com.durex.dao.SysAclMapper;
import com.durex.exception.ParamException;
import com.durex.model.SysAcl;
import com.durex.param.AclParam;
import com.durex.service.SysAclService;
import com.durex.service.SysLogService;
import com.durex.util.BeanValidator;
import com.durex.util.IpUtil;
import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class SysAclServiceImpl implements SysAclService {

    @Autowired
    private SysAclMapper sysAclMapper;
    @Autowired
    private SysLogService sysLogService;

    @Override
    public void save(AclParam aclParam) {
        BeanValidator.check(aclParam);
        if (checkExist(aclParam.getAclModuleId(), aclParam.getName(), aclParam.getId())) {
            throw new ParamException("当前权限模块下存在相同名称的权限点");
        }
        SysAcl sysAcl = SysAcl.builder()
                .name(aclParam.getName())
                .aclModuleId(aclParam.getAclModuleId())
                .status(aclParam.getStatus())
                .type(aclParam.getType())
                .remark(aclParam.getRemark())
                .url(aclParam.getUrl())
                .seq(aclParam.getSeq())
                .build();
        sysAcl.setCode(generateCode());
        sysAcl.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysAcl.setOperateTime(new Date());
        sysAcl.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysAclMapper.insertSelective(sysAcl);
        sysLogService.saveAclLog(null, sysAcl);
    }

    @Override
    public void update(AclParam aclParam) {
        BeanValidator.check(aclParam);
        if (checkExist(aclParam.getAclModuleId(), aclParam.getName(), aclParam.getId())) {
            throw new ParamException("当前权限模块下存在相同名称的权限点");
        }
        SysAcl before = sysAclMapper.selectByPrimaryKey(aclParam.getId());
        Preconditions.checkNotNull(before, "待更新的权限点不存在");
        SysAcl after = SysAcl.builder()
                .id(aclParam.getId())
                .name(aclParam.getName())
                .aclModuleId(aclParam.getAclModuleId())
                .status(aclParam.getStatus())
                .type(aclParam.getType())
                .remark(aclParam.getRemark())
                .url(aclParam.getUrl())
                .seq(aclParam.getSeq())
                .build();
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateTime(new Date());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysAclMapper.updateByPrimaryKeySelective(after);
        sysLogService.saveAclLog(before, after);

    }

    @Override
    public PageResult<SysAcl> getPageByAclModuleId(int aclModuleId, PageQuery pageQuery) {
        BeanValidator.check(pageQuery);
        int count = sysAclMapper.countByAclModuleId(aclModuleId);
        if (count > 0) {
            List<SysAcl> aclList = sysAclMapper.getPagebByAclModuleId(aclModuleId, pageQuery);
            return PageResult.<SysAcl>builder().data(aclList).total(count).build();
        }
        return PageResult.<SysAcl>builder().build();
    }

    private boolean checkExist(int aclModuleId, String name, Integer aclId) {
        return sysAclMapper.countByNameAndAclModuleId(aclModuleId, name, aclId) > 0;
    }

    private String generateCode() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddmmss");
        return simpleDateFormat.format(new Date()) + "_" + (int)(Math.random() * 100);
    }
}
