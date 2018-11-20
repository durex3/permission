package com.durex.controller;

import com.durex.beans.PageQuery;
import com.durex.beans.PageResult;
import com.durex.common.JsonData;
import com.durex.model.SysUser;
import com.durex.param.UserParam;
import com.durex.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/sys/user")
@Controller
public class SysUserController {


    @Autowired
    private SysUserService sysUserService;


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
}
