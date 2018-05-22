package com.lzy.attnd.interception;

import com.lzy.attnd.configure.ConfigBean;
import com.lzy.attnd.constant.Code;
import com.lzy.attnd.exception.NoAuthException;
import com.lzy.attnd.exception.VisitorNoAuthException;
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
import java.util.Map;


public class AuthIntercepts implements HandlerInterceptor {
    private final static Logger logger = LoggerFactory.getLogger(AuthIntercepts.class);

    private final ConfigBean configBean;

    private final Map<String,Integer> rightMap;

    public AuthIntercepts(ConfigBean configBean,Map<String,Integer> rightMap) {
        this.configBean = configBean;
        this.rightMap = rightMap;
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

        String key = request.getMethod().toUpperCase() + request.getRequestURI();
        if (rightMap.containsKey(key)){
            int rightCode = rightMap.get(key);
            if (rightCode == Code.RIGHT_USER &&
                (session.getUserID()<=0 || session.getName()==null ||session.getName().equals("")
                        ||session.getStuid()==null || session.getStuid().equals(""))){
                throw new VisitorNoAuthException("");
            }
        }

        request.setAttribute("attnd",session);

        return true;
    }
}
