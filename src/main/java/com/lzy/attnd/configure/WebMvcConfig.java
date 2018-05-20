package com.lzy.attnd.configure;

import com.lzy.attnd.interception.AuthIntercepts;
import com.lzy.attnd.interception.PageIngercepts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final ConfigBean configBean;

    @Autowired
    public WebMvcConfig(ConfigBean configBean) {
        this.configBean = configBean;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthIntercepts(configBean)).addPathPatterns("/**").excludePathPatterns("/login","/mocklogin**","/chk/session");
        registry.addInterceptor(new PageIngercepts()).addPathPatterns("/attnd/situation");
    }
}
