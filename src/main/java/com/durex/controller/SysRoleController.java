package com.durex.controller;

import com.durex.common.JsonData;
import com.durex.param.RoleParam;
import com.durex.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@RequestMapping("/sys/role")
@Controller
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;


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

}
