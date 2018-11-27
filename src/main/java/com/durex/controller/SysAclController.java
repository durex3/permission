package com.durex.controller;

import com.durex.beans.PageQuery;
import com.durex.beans.PageResult;
import com.durex.common.JsonData;
import com.durex.model.SysAcl;
import com.durex.model.SysUser;
import com.durex.param.AclParam;
import com.durex.service.SysAclService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/sys/acl")
@Slf4j
public class SysAclController {

    @Autowired
    private SysAclService sysAclService;

    @RequestMapping(value = "/save.json")
    @ResponseBody
    public JsonData saveAcl(AclParam aclParam) {
        sysAclService.save(aclParam);
        return JsonData.success();
    }

    @RequestMapping(value = "/update.json")
    @ResponseBody
    public JsonData updateAcl(AclParam aclParam) {
        sysAclService.update(aclParam);
        return JsonData.success();
    }

    @RequestMapping(value = "/page.json")
    @ResponseBody
    public JsonData page(@RequestParam Integer aclModuleId, PageQuery pageQuery) {
        PageResult<SysAcl> pageResult = sysAclService.getPageByAclModuleId(aclModuleId, pageQuery);
        return JsonData.success(pageResult);
    }

}
