package com.durex.controller;

import com.durex.beans.PageQuery;
import com.durex.common.JsonData;
import com.durex.param.AclParam;
import com.durex.service.SysAclService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/sys/acl")
@Slf4j
public class SysAclController {

    @Autowired
    private SysAclService sysAclService;

    @RequestMapping(value = "/save.json")
    @ResponseBody
    public JsonData saveAcl(AclParam aclParam) {
        return JsonData.success();
    }

    @RequestMapping(value = "/update.json")
    @ResponseBody
    public JsonData updateAcl(AclParam aclParam) {
        return JsonData.success();
    }

    @RequestMapping(value = "/page.json")
    @ResponseBody
    public JsonData page(@RequestParam Integer deptId, PageQuery pageQuery) {
        return JsonData.success();
    }

}
