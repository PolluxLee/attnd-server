package com.lzy.attnd.configure;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.lzy.attnd.service.WechatService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.TestPropertySource;

@Configuration
@TestPropertySource(locations="classpath:application-test.properties")
public class WechatMockConfigure {
    @Bean
    @Primary
    public WechatService wechatService() {
        return Mockito.mock(WechatService.class);
    }

}
