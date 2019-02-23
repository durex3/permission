package com.durex.service.impl;

import com.durex.beans.LogType;
import com.durex.common.RequestHolder;
import com.durex.dao.SysLogMapper;
import com.durex.dao.SysRoleAclMapper;
import com.durex.dao.SysRoleMapper;
import com.durex.model.SysLogWithBLOBs;
import com.durex.model.SysRole;
import com.durex.model.SysRoleAcl;
import com.durex.model.SysRoleUser;
import com.durex.service.SysLogService;
import com.durex.service.SysRoleAclService;
import com.durex.service.TransactionalService;
import com.durex.util.IpUtil;
import com.durex.util.JsonMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class SysRoleAclServiceImpl implements SysRoleAclService {

    @Autowired
    private SysRoleAclMapper sysRoleAclMapper;
    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private TransactionalService transactionalService;
    @Autowired
    private SysLogMapper sysLogMapper;

    @Override
    public void changeRoleAcl(Integer roleId, List<Integer> aclIdList) {
        List<Integer> originAclIdList = sysRoleAclMapper.getAclIdListByRoleIdList(Lists.newArrayList(roleId));
        if (originAclIdList.size() == aclIdList.size()) {
            Set<Integer> originAclIdSet = Sets.newHashSet(originAclIdList);
            Set<Integer> aclIdSet = Sets.newHashSet(aclIdList);
            originAclIdSet.removeAll(aclIdSet);
            if (CollectionUtils.isEmpty(originAclIdSet)) {
                return;
            }
        }
        transactionalService.updateRoleAcl(roleId, aclIdList);
        saveRoleAclLog(roleId, originAclIdList, aclIdList);
    }

    @Override
    public List<SysRole> getRoleListByAclId(int aclId) {
        List<Integer> roleIdList = sysRoleAclMapper.getRoleIdListByAclId(aclId);
        if (CollectionUtils.isEmpty(roleIdList)) {
            return Lists.newArrayList();
        }
        return sysRoleMapper.getByIdList(roleIdList);
    }


    private void saveRoleAclLog(int roleId, List<Integer> before, List<Integer> after) {
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setType(LogType.TYPE_ROLE_ACL);
        sysLog.setTargetId(roleId);
        sysLog.setOldValue(before == null ? "" : JsonMapper.object2String(before));
        sysLog.setNewValue(after == null ? "" : JsonMapper.object2String(after));
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysLog.setOperateTime(new Date());
        sysLog.setStatus(1);
        sysLogMapper.insertSelective(sysLog);
    }
}
