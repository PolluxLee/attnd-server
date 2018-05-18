package com.lzy.attnd.interception;

import com.lzy.attnd.configure.ConfigBean;
import com.lzy.attnd.repository.UserRepository;
import com.lzy.attnd.utils.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class AuthIntercepts implements HandlerInterceptor {
    private final static Logger logger = LoggerFactory.getLogger(UserRepository.class);

    private final ConfigBean configBean;

    public AuthIntercepts(ConfigBean configBean) {
        this.configBean = configBean;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HttpSession httpSession = request.getSession();
        if (httpSession==null){
            logger.error("AuthIntercepts getSession null");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        Session session = null;
        try {
            session = ((Session) httpSession.getAttribute(configBean.getSession_key()));
        } catch (ClassCastException cce) {
            logger.error("AuthIntercepts session cast failed ");
            cce.printStackTrace();
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        if (session==null||session.getOpenid()==null||session.getOpenid().equals("")){
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        request.setAttribute("attnd",session);

        return true;
    }
}
