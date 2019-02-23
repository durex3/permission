package com.durex.controller;

import com.durex.beans.PageQuery;
import com.durex.common.JsonData;
import com.durex.param.SearchLogParam;
import com.durex.service.SysLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/sys/log")
public class SysLogController {

    @Autowired
    private SysLogService sysLogService;

    @RequestMapping("/log.page")
    public ModelAndView page() {
        return new ModelAndView("log");
    }

    @RequestMapping("/recover.json")
    @ResponseBody
    public JsonData recover(@RequestParam("id") int id) {
        sysLogService.recover(id);
        return JsonData.success();
    }

    @RequestMapping("/page.json")
    @ResponseBody
    public JsonData searchPage(SearchLogParam param, PageQuery page) {
        return JsonData.success(sysLogService.searchPageList(param, page));
    }

}
