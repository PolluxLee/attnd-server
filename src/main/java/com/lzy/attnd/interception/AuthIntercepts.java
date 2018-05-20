package com.lzy.attnd.interception;

import com.lzy.attnd.configure.ConfigBean;
import com.lzy.attnd.exception.NoAuthException;
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
    private final static Logger logger = LoggerFactory.getLogger(AuthIntercepts.class);

    private final ConfigBean configBean;

    public AuthIntercepts(ConfigBean configBean) {
        this.configBean = configBean;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HttpSession httpSession = request.getSession();
        if (httpSession==null){
            String msg = "AuthIntercepts getSession null";
            logger.error(msg);
            throw new NoAuthException(msg);
        }
        Session session = null;
        try {
            session = ((Session) httpSession.getAttribute(configBean.getSession_key()));
        } catch (ClassCastException cce) {
            String msg = "AuthIntercepts session cast failed ";
            logger.error(msg);
            throw new NoAuthException(msg);
        }

        if (session==null||session.getOpenid()==null||session.getOpenid().equals("")){
            throw new NoAuthException("session openid invalid");
        }
        request.setAttribute("attnd",session);

        return true;
    }
}
