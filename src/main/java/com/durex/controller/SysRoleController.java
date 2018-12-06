package com.durex.controller;

import com.durex.common.JsonData;
import com.durex.model.SysRoleAcl;
import com.durex.model.SysRoleUser;
import com.durex.model.SysUser;
import com.durex.param.RoleParam;
import com.durex.service.*;
import com.durex.util.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequestMapping("/sys/role")
@Controller
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysTreeService sysTreeService;
    @Autowired
    private SysRoleAclService sysRoleAclService;
    @Autowired
    private SysRoleUserService sysRoleUserService;
    @Autowired
    private SysUserService sysUserService;


    @RequestMapping("/role.page")
    public ModelAndView page() {
        return new ModelAndView("role");
    }

    @RequestMapping(value = "/save.json", method = RequestMethod.POST)
    @ResponseBody
    public JsonData saveRole(RoleParam roleParam) {
        sysRoleService.save(roleParam);
        return JsonData.success();
    }

    @RequestMapping(value = "/update.json", method = RequestMethod.POST)
    @ResponseBody
    public JsonData updateRole(RoleParam roleParam) {
        sysRoleService.update(roleParam);
        return JsonData.success();
    }


    @RequestMapping(value = "/list.json", method = RequestMethod.GET)
    @ResponseBody
    public JsonData list() {
        return JsonData.success(sysRoleService.getAll());
    }

    @RequestMapping(value = "/roleAclTree.json", method = RequestMethod.GET)
    @ResponseBody
    public JsonData aclTree(@RequestParam int roleId) {
        return JsonData.success(sysTreeService.roleAclTree(roleId));
    }

    @RequestMapping(value = "/changeAcl.json", method = RequestMethod.POST)
    @ResponseBody
    public JsonData changeAcl(@RequestParam("roleId") int roleId, @RequestParam(value = "aclIds", required = false, defaultValue = "") String aclIds) {
        sysRoleAclService.changeRoleAcl(roleId, StringUtil.splitToListInt(aclIds));
        return JsonData.success();
    }

    @RequestMapping(value = "/users.json", method = RequestMethod.GET)
    @ResponseBody
    public JsonData users(@RequestParam("roleId") int roleId) {
        List<SysUser> selectedUserList = sysRoleUserService.getListByRoleId(roleId);
        List<SysUser> allUserList = sysUserService.getAll();
        List<SysUser> unselectedUserList = Lists.newArrayList();
        Set<Integer> selectedUserIdSet = selectedUserList.stream().map(sysUser -> sysUser.getId()).collect(Collectors.toSet());
        for (SysUser sysUser : allUserList) {
            if (sysUser.getStatus() == 1 && !selectedUserIdSet.contains(sysUser.getId())) {
                unselectedUserList.add(sysUser);
            }
        }
        Map<String, List<SysUser>> map = Maps.newHashMap();
        map.put("selected", selectedUserList);
        map.put("unSelected", unselectedUserList);
        return JsonData.success(map);
    }

    @RequestMapping(value = "/changeUser.json", method = RequestMethod.POST)
    @ResponseBody
    public JsonData changeUser(@RequestParam("roleId") int roleId, @RequestParam(value = "userIds", required = false, defaultValue = "") String userIds) {
        sysRoleUserService.changeRoleUser(roleId, StringUtil.splitToListInt(userIds));
        return JsonData.success();
    }

}
