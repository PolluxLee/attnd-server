package com.lzy.attnd;

import com.lzy.attnd.configure.ConfigBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableConfigurationProperties({ConfigBean.class})
public class AttndApplication {

    public static void main(String[] args) {
        SpringApplication.run(AttndApplication.class, args);
    }
}
