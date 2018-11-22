package com.durex.controller;

import com.durex.common.JsonData;
import com.durex.param.AclModuleParam;
import com.durex.param.DeptParam;
import com.durex.service.SysAclModuleService;
import com.durex.service.SysTreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/sys/aclModule")
public class SysAclModuleController {

    @Autowired
    private SysAclModuleService sysAclModuleService;
    @Autowired
    private SysTreeService sysTreeService;

    @RequestMapping("/acl.page")
    public ModelAndView page() {
        return new ModelAndView("acl");
    }

    @RequestMapping(value = "/save.json")
    @ResponseBody
    public JsonData saveAclModule(AclModuleParam aclModuleParam) {
        sysAclModuleService.save(aclModuleParam);
        return JsonData.success();
    }

    @RequestMapping(value = "/update.json")
    @ResponseBody
    public JsonData updateAclModule(AclModuleParam aclModuleParam) {
        sysAclModuleService.update(aclModuleParam);
        return JsonData.success();
    }


    @RequestMapping(value = "/tree.json")
    @ResponseBody
    public JsonData tree() {
        return JsonData.success(sysTreeService.aclModuleTree());
    }

}
