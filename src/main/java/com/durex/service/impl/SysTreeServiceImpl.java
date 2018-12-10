package com.durex.service.impl;

import com.durex.dao.SysAclMapper;
import com.durex.dao.SysAclModuleMapper;
import com.durex.dao.SysDeptMapper;
import com.durex.dto.AclDto;
import com.durex.dto.AclModuleLevelDto;
import com.durex.dto.DeptLevelDto;
import com.durex.model.SysAcl;
import com.durex.model.SysAclModule;
import com.durex.model.SysDept;
import com.durex.service.SysAclModuleService;
import com.durex.service.SysCoreService;
import com.durex.service.SysTreeService;
import com.durex.util.LevelUtil;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SysTreeServiceImpl implements SysTreeService {

    @Autowired
    private SysDeptMapper sysDeptMapper;
    @Autowired
    private SysAclModuleMapper sysAclModuleMapper;
    @Autowired
    private SysCoreService sysCoreService;
    @Autowired
    private SysAclMapper sysAclMapper;

    @Override
    public List<AclModuleLevelDto> userAclTree(int userId) {
        // 用户已分配的权限点
        List<SysAcl> userAclList = sysCoreService.getUserAclList(userId);
        List<AclDto> aclList = Lists.newArrayList();
        for (SysAcl sysAcl : userAclList) {
            AclDto aclDto = AclDto.adapt(sysAcl);
            aclDto.setHasAcl(true);
            aclDto.setChecked(true);
            aclList.add(aclDto);
        }
        return aclListToTree(aclList);
    }

    //============================ 角色权限树-start =================================//
    @Override
    public List<AclModuleLevelDto> roleAclTree(int roleId) {
        // 1.当前用户已分配的权限点
        List<SysAcl> userAclList = sysCoreService.getCurrentUserAclList();
        // 2.当前角色分配的权限点
        List<SysAcl> roleAclList = sysCoreService.getRoleAclList(roleId);
        Set<Integer> userAclIdList = userAclList.stream().map(sysAcl -> sysAcl.getId()).collect(Collectors.toSet());
        Set<Integer> roleAclIdList = roleAclList.stream().map(sysAcl -> sysAcl.getId()).collect(Collectors.toSet());
       // 3. 取出所有的权限点
        List<SysAcl> allAclList = sysAclMapper.getAll();
        List<AclDto> aclList = Lists.newArrayList();
        for (SysAcl sysAcl : allAclList) {
            AclDto aclDto = AclDto.adapt(sysAcl);
            // 当前用户是否有权限
            if (userAclIdList.contains(sysAcl.getId())){
                aclDto.setHasAcl(true);
            }
            // 当前用户所操作的角色有此权限则选中
            if (roleAclIdList.contains(sysAcl.getId())) {
                aclDto.setChecked(true);
            }
            aclList.add(aclDto);
        }
        return aclListToTree(aclList);
    }

    public List<AclModuleLevelDto> aclListToTree(List<AclDto> aclDtoList) {
        if (CollectionUtils.isEmpty(aclDtoList)) {
            return Lists.newArrayList();
        }
        List<AclModuleLevelDto> aclModuleList = aclModuleTree();
        Multimap<Integer, AclDto> aclModuleIdMap = ArrayListMultimap.create();
        for(AclDto aclDto : aclDtoList) {
            if (aclDto.getStatus() == 1) {
                aclModuleIdMap.put(aclDto.getAclModuleId(), aclDto);
            }
        }
        bindAclWithOrder(aclModuleList, aclModuleIdMap);
        return aclModuleList;
    }

    public void bindAclWithOrder(List<AclModuleLevelDto> aclModuleList, Multimap<Integer, AclDto> aclModuleIdMap) {
        if (CollectionUtils.isEmpty(aclModuleList)) {
            return;
        }
        for (AclModuleLevelDto dto : aclModuleList) {
            List<AclDto> aclList = (List<AclDto>)aclModuleIdMap.get(dto.getId());
            if (CollectionUtils.isNotEmpty(aclList)) {
                Collections.sort(aclList, aclComparator);
                dto.setAclList(aclList);
            }
            bindAclWithOrder(dto.getAclModuleList(), aclModuleIdMap);
        }
    }
    private Comparator<AclDto> aclComparator = new Comparator<AclDto>() {
        @Override
        public int compare(AclDto o1, AclDto o2) {
            return o1.getSeq() - o2.getSeq();
        }
    };

    //============================ 角色权限树-end =================================//

    //============================ 权限模块树-start =================================//
    @Override
    public List<AclModuleLevelDto> aclModuleTree() {
        List<SysAclModule> aclModuleList = sysAclModuleMapper.getAllAclModule();
        List<AclModuleLevelDto> dtoList = Lists.newArrayList();
        for (SysAclModule sysAclModule : aclModuleList) {
            AclModuleLevelDto dto = AclModuleLevelDto.adapt(sysAclModule);
            dtoList.add(dto);
        }
        return aclModuleListToTree(dtoList);
    }

    private List<AclModuleLevelDto> aclModuleListToTree(List<AclModuleLevelDto> aclModuleLevelDtoList) {
        if (CollectionUtils.isEmpty(aclModuleLevelDtoList)) {
            return Lists.newArrayList();
        }
        Multimap<String, AclModuleLevelDto> levelAclModuleMap = ArrayListMultimap.create();
        List<AclModuleLevelDto> rootList = Lists.newArrayList();
        for (AclModuleLevelDto dto : aclModuleLevelDtoList) {
            levelAclModuleMap.put(dto.getLevel(), dto);
            if (LevelUtil.ROOT.equals(dto.getLevel())) {
                rootList.add(dto);
            }
        }
        // 按照seq从小到大排序
        Collections.sort(rootList, aclModuleLevelDtoComparator);
        transformAclModuleTree(rootList, LevelUtil.ROOT, levelAclModuleMap);
        return rootList;
    }

    private void transformAclModuleTree(List<AclModuleLevelDto> aclModuleList, String level,
                                                           Multimap<String, AclModuleLevelDto> levelAclModuleMap) {
        for (int i = 0; i < aclModuleList.size(); i++) {
            AclModuleLevelDto dto = aclModuleList.get(i);
            String nextLevel = LevelUtil.calculateLevel(level, dto.getId());
            List<AclModuleLevelDto> tempList = (List<AclModuleLevelDto>)levelAclModuleMap.get(nextLevel);
            if (CollectionUtils.isNotEmpty(tempList)) {
                Collections.sort(tempList, aclModuleLevelDtoComparator);
                dto.setAclModuleList(tempList);
                transformAclModuleTree(tempList, nextLevel, levelAclModuleMap);
            }
        }
    }

    private Comparator<AclModuleLevelDto> aclModuleLevelDtoComparator = new Comparator<AclModuleLevelDto>() {
        @Override
        public int compare(AclModuleLevelDto o1, AclModuleLevelDto o2) {
            return o1.getSeq() - o2.getSeq();
        }
    };
    //============================ 权限模块树-end =================================//

    //============================ 部门树-start =================================//
    @Override
    public List<DeptLevelDto> deptTree() {
        List<SysDept> deptList = sysDeptMapper.getAllDept();
        List<DeptLevelDto> dtoList = Lists.newArrayList();
        for (SysDept dept : deptList) {
            DeptLevelDto dto = DeptLevelDto.adapt(dept);
            dtoList.add(dto);
        }
        return deptListToTree(dtoList);
    }

    private List<DeptLevelDto> deptListToTree(List<DeptLevelDto> deptLevelDtoList) {
        if (CollectionUtils.isEmpty(deptLevelDtoList)) {
            return Lists.newArrayList();
        }
        // level -> [dept1, dept2]
        Multimap<String, DeptLevelDto> levelDeptMap = ArrayListMultimap.create();
        List<DeptLevelDto> rootList = Lists.newArrayList();
        for (DeptLevelDto dto : deptLevelDtoList) {
            levelDeptMap.put(dto.getLevel(), dto);
            if (LevelUtil.ROOT.equals(dto.getLevel())) {
                rootList.add(dto);
            }
        }
        // 按照seq从小到大排序
        Collections.sort(rootList, deptLevelDtoComparator);
        transformDeptTree(rootList, LevelUtil.ROOT, levelDeptMap);
        return rootList;
    }

    private void transformDeptTree(List<DeptLevelDto> deptList, String level,  Multimap<String, DeptLevelDto> levelDeptMap) {
        for (int i = 0; i < deptList.size(); i++) {
            // 遍历该层的每个元素
            DeptLevelDto deptLevelDto = deptList.get(i);
            // 处理当下层的数据
            String nextLevel = LevelUtil.calculateLevel(level, deptLevelDto.getId());
            // 处理下一层
            List<DeptLevelDto> tempDeptList = (List<DeptLevelDto>)levelDeptMap.get(nextLevel);
            if (CollectionUtils.isNotEmpty(tempDeptList)) {
                // 按照seq从小到大排序
                Collections.sort(tempDeptList, deptLevelDtoComparator);
                //设置下一层部门
                deptLevelDto.setDeptList(tempDeptList);
                //进入下一层处理
                transformDeptTree(tempDeptList, nextLevel, levelDeptMap);
            }
        }
    }

    private Comparator<DeptLevelDto> deptLevelDtoComparator = new Comparator<DeptLevelDto>() {
        @Override
        public int compare(DeptLevelDto o1, DeptLevelDto o2) {
            return o1.getSeq() - o2.getSeq();
        }
    };
    //============================ 部门树-end =================================//
}


