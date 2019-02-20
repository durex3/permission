package com.durex.controller;

import com.durex.beans.PageQuery;
import com.durex.beans.PageResult;
import com.durex.common.JsonData;
import com.durex.model.SysUser;
import com.durex.param.UserParam;
import com.durex.service.SysRoleUserService;
import com.durex.service.SysTreeService;
import com.durex.service.SysUserService;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@RequestMapping("/sys/user")
@Controller
public class SysUserController {


    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysTreeService sysTreeService;
    @Autowired
    private SysRoleUserService sysRoleUserService;


    @RequestMapping(value = "/save.json")
    @ResponseBody
    public JsonData saveUser(UserParam userParam) {
        System.out.println(userParam);
        sysUserService.save(userParam);
        return JsonData.success();
    }

    @RequestMapping(value = "/update.json")
    @ResponseBody
    public JsonData updateUser(UserParam userParam) {
        sysUserService.update(userParam);
        return JsonData.success();
    }

    @RequestMapping("/page.json")
    @ResponseBody
    public JsonData page(@RequestParam Integer deptId, PageQuery pageQuery) {
        PageResult<SysUser> pageResult = sysUserService.getPageByDeptId(deptId, pageQuery);
        return JsonData.success(pageResult);
    }

    @RequestMapping(value = "/acl.json")
    @ResponseBody
    public JsonData acl(@RequestParam Integer userId) {
        return JsonData.success(sysTreeService.userAclTree(userId));
    }

    @RequestMapping(value = "/role.json")
    @ResponseBody
    public JsonData role(@RequestParam Integer userId) {
        return JsonData.success(sysRoleUserService.getRoleListByUserId(userId));
    }

    @RequestMapping(value = "/noAuth.page")
    public ModelAndView noAuth() {
        return new ModelAndView("noAuth");
    }

}
