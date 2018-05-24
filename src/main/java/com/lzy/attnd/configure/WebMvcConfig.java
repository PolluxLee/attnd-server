package com.lzy.attnd.configure;

import com.lzy.attnd.constant.Code;
import com.lzy.attnd.interception.AuthIntercepts;
import com.lzy.attnd.interception.PageIngercepts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.HashMap;
import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final HashMap<String,Integer> rightMap;

    private final ConfigBean configBean;

    @Autowired
    public WebMvcConfig(ConfigBean configBean) {
        this.configBean = configBean;
        this.rightMap = new HashMap<String,Integer>(){
            {
                put("GET/attnd/hisname", Code.RIGHT_USER);
                put("GET/attnd/hisaddr", Code.RIGHT_USER);
                put("GET/attndlist", Code.RIGHT_USER);
                put("POST/attnd/signin", Code.RIGHT_USER);
                put("GET/attnd/situation", Code.RIGHT_USER);
                put("POST/attnd/del", Code.RIGHT_USER);
                put("POST/signin/status/upd", Code.RIGHT_USER);

                put("GET/group/name", Code.RIGHT_USER);
                put("GET/group/list", Code.RIGHT_USER);
                put("GET/group", Code.RIGHT_USER);
                put("GET/group/userlist", Code.RIGHT_USER);
                put("POST/group/del", Code.RIGHT_USER);
                put("POST/group/user/add", Code.RIGHT_USER);
            }
        };
    }



    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthIntercepts(configBean,rightMap)).addPathPatterns("/**").excludePathPatterns("/login","/mocklogin**","/chk/session");
        registry.addInterceptor(new PageIngercepts()).addPathPatterns("/attnd/situation","/attndlist","/group/userlist");
    }
}
