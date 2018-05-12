package com.lzy.attnd.controller;


import com.lzy.attnd.AttndApplication;
import com.lzy.attnd.constant.Code;
import com.lzy.attnd.service.WechatService;
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
import org.springframework.web.client.RestTemplate;
import static org.hamcrest.Matchers.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {
    @Autowired
    private MockMvc mvc;
    private MockHttpSession session;

    @Autowired
    private WechatService wechatService;

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
                 .andExpect(MockMvcResultMatchers.jsonPath("$.data.name",is("")))
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
}
