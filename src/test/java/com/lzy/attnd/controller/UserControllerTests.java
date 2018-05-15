package com.lzy.attnd.controller;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.lzy.attnd.AttndApplication;
import com.lzy.attnd.configure.ConfigBean;
import com.lzy.attnd.constant.Code;
import com.lzy.attnd.service.WechatService;
import com.lzy.attnd.utils.Session;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.hamcrest.Matchers.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mvc;
    private MockHttpSession session;

    @Autowired
    private ConfigBean configBean;

    @Autowired
    private WechatService wechatService;

    @Before
    public void setupMockMvc(){
        mvc = MockMvcBuilders.webAppContextSetup(wac).build(); //初始化MockMvc对象
        session = new MockHttpSession();
    }

    /**
     * normal
     * @throws Exception
     */
    @Test
    public void login()throws Exception{
        String openid = "openid-test";
        String session_key = "session_key-test";
        Mockito.when(wechatService.Wx_Login("123")).thenReturn(new WechatService.WxLoginFb(openid,session_key));

        mvc.perform(MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .content("code=123")
        )
                 .andExpect(MockMvcResultMatchers.status().is(200))
                 .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.USER_NOT_EXIST)))
                 .andExpect(MockMvcResultMatchers.jsonPath("$.data.openid",is(openid)))
                 .andExpect(MockMvcResultMatchers.jsonPath("$.data",not(containsString("name"))))
                 .andDo(MockMvcResultHandlers.print());
    }


    /**
     * exist
     * @throws Exception
     */
    @Test
    public void loginexist()throws Exception{
        String openid = "toid123";
        String session_key = "session_key-test";
        Mockito.when(wechatService.Wx_Login("123")).thenReturn(new WechatService.WxLoginFb(openid,session_key));

        mvc.perform(MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .content("code=123")
        )
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.USER_EXIST)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.openid",is(openid)))
                .andDo(MockMvcResultHandlers.print());
    }
    /**
     * param invalid
     * @throws Exception
     */
    @Test
    public void loginParamInvalid()throws Exception{
        mvc.perform(MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .content("A=B")
        )
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_PARAM_INVALID)))
                .andDo(MockMvcResultHandlers.print());
    }


    //chk_user_info-----------------------------------------------------------------

    /**
     * find not exist
     */
    @Test
    public void chkUserNotExist() throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/user/info")
            .param("openid","test")
        )
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.USER_NOT_EXIST)))
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * no param
     */
    @Test
    public void chkUserNOPARAM() throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/user/info")
        )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * openid blank
     */
    @Test
    public void chkUserOIDemply() throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/user/info")
                .param("openid","")
        )
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_PARAM_INVALID)))
                .andDo(MockMvcResultHandlers.print());
    }


    /**
     * find exist
     */
    @Test
    public void chkUserExist() throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/user/info")
                .param("openid","toid123")
        )
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andDo(MockMvcResultHandlers.print());
    }


    //addOrUpdUser-----------------------------------------------------------------

    @Test
    public void addOrUpdUser_PARAM_INVALID() throws Exception{
        mvc.perform(MockMvcRequestBuilders.post("/user/info")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"na564me\":\"ok\"}")
        )
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_PARAM_INVALID)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void addOrUpdUser_NOSESSION() throws Exception{
        mvc.perform(MockMvcRequestBuilders.post("/user/info")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"stu_id\":\"123\",\"name\":\"ok\"}")
        )
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_SYS_ERROR)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void addOrUpdUser_NOSESSION2() throws Exception{

        mvc.perform(MockMvcRequestBuilders.post("/user/info")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"ok\"}")
        )
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_SYS_ERROR)))
                .andDo(MockMvcResultHandlers.print());
    }


    @Test
    public void addOrUpdUser_INS() throws Exception{
        session.setAttribute(configBean.getSession_key(),new Session("lzy",0,"oid","wxsessionkey"));
        mvc.perform(MockMvcRequestBuilders.post("/user/info")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"ok4\"}")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_SUCCESS)));

    }

}
