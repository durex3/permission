package com.durex.controller;

import com.durex.common.JsonData;
import com.durex.dto.DeptLevelDto;
import com.durex.param.DeptParam;
import com.durex.service.SysDpetService;
import com.durex.service.SysTreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/sys/dept")
public class SysDeptController {

    @Autowired
    private SysDpetService sysDpetService;
    @Autowired
    private SysTreeService sysTreeService;

    @RequestMapping("/dept.page")
    public ModelAndView page() {
        return new ModelAndView("dept");
    }

    @RequestMapping(value = "/save.json", method = RequestMethod.POST)
    @ResponseBody
    public JsonData saveDept(DeptParam deptParam) {
        sysDpetService.save(deptParam);
        return JsonData.success();
    }

    @RequestMapping("/tree.json")
    @ResponseBody
    public JsonData tree() {
        List<DeptLevelDto> deptLevelDtoList = sysTreeService.deptTree();
        return JsonData.success(deptLevelDtoList);
    }

    @RequestMapping(value = "/update.json", method = RequestMethod.POST)
    @ResponseBody
    public JsonData updateDept(DeptParam deptParam) {
        sysDpetService.update(deptParam);
        return JsonData.success();
    }
}

