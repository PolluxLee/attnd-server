package com.lzy.attnd.interception;

import com.lzy.attnd.configure.ConfigBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

public class PageIngercepts implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String rawPage = request.getParameter("page");
        String rawPageSize = request.getParameter("page_size");
        if (rawPageSize==null||rawPage==null){
            throw new ConstraintViolationException("no param page or page_size",null);
        }

        int page;
        try {
            page = Integer.parseInt(rawPage);
        } catch (NumberFormatException e) {
            throw new ConstraintViolationException("page raw to int failed",null);
        }
        int pageSize;
        try {
            pageSize = Integer.parseInt(rawPageSize);
        } catch (NumberFormatException e) {
            throw new ConstraintViolationException("pageSize raw to int failed",null);
        }

        if (page<0||pageSize<=0){
            throw new ConstraintViolationException("param page or pagesize invalid",null);
        }

        request.setAttribute("start",(page-1)*pageSize);
        request.setAttribute("rows",pageSize);
        return true;
    }
}
