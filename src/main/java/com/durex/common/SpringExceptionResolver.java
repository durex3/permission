package com.durex.common;

import com.durex.exception.ParamException;
import com.durex.exception.PermissionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class SpringExceptionResolver implements HandlerExceptionResolver {


    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object o, Exception e) {
        String url = request.getRequestURI().toString();
        ModelAndView modelAndView;
        String defaultMsg = "System error";

        //.json, .page
        if (url.endsWith(".json")) {
            if (e instanceof PermissionException || e instanceof ParamException) {
                JsonData result = JsonData.fail(e.getMessage());
                modelAndView = new ModelAndView("jsonView", result.toMap());
            } else {
                log.error("unknow json exception, url:" + url, e);
                JsonData result = JsonData.fail(defaultMsg);
                modelAndView = new ModelAndView("jsonView", result.toMap());
            }
        } else if (url.endsWith(".page")) {
            log.error("unknow page exception, url:" + url, e);
            JsonData result = JsonData.fail(defaultMsg);
            modelAndView = new ModelAndView("exception", result.toMap());
        } else {
            log.error("unknow exception, url:" + url, e);
            JsonData result = JsonData.fail(defaultMsg);
            modelAndView = new ModelAndView("jsonView", result.toMap());
        }
        return modelAndView;
    }
}
