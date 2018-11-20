package com.durex.controller;

import com.durex.common.ApplicationContextHelper;
import com.durex.common.JsonData;
import com.durex.dao.SysAclModuleMapper;
import com.durex.exception.ParamException;
import com.durex.exception.PermissionException;
import com.durex.model.SysAclModule;
import com.durex.param.TestVo;
import com.durex.util.BeanValidator;
import com.durex.util.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;


@Slf4j
@RequestMapping("/test")
@Controller
public class TestController {

    @ResponseBody
    @RequestMapping("/hello.json")
    public JsonData hello() {
        log.info("hello");
        throw new PermissionException("test exception");
    }

    @ResponseBody
    @RequestMapping("/validate.json")
    public JsonData validate(TestVo testVo) throws ParamException {

        BeanValidator.check(testVo);
        SysAclModuleMapper  moduleMapper = ApplicationContextHelper.popBean(SysAclModuleMapper.class);
        SysAclModule module = moduleMapper.selectByPrimaryKey(1);
        log.info(JsonMapper.object2String(module));
        return JsonData.success("test validate");
    }
}
