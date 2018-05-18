package com.lzy.attnd.controller;


import com.lzy.attnd.configure.ConfigBean;
import com.lzy.attnd.constant.Code;
import com.lzy.attnd.service.WechatService;
import com.lzy.attnd.utils.Session;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@WebAppConfiguration
public class AttndControllerTests {

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mvc;
    private MockHttpSession session;

    @Autowired
    private ConfigBean configBean;

    @Before
    public void setupMockMvc(){
        mvc = MockMvcBuilders.webAppContextSetup(wac).build(); //初始化MockMvc对象
        session = new MockHttpSession();
        session.setAttribute(configBean.getSession_key(),new Session(1,"lzy",0,"oid","wxsessionkey"));
    }

    /**
     * NO SESSION
     * @throws Exception
     */
    @Test
    public void attnd_NOSESSION()throws Exception{
        mvc.perform(MockMvcRequestBuilders.post("/attnd")
                .contentType(MediaType.APPLICATION_JSON)
                .content("")
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(401));
    }

    /**
     * param not json
     * @throws Exception
     */
    @Test
    public void attnd_ParamNotJson()throws Exception{
        mvc.perform(MockMvcRequestBuilders.post("/attnd")
                .contentType(MediaType.APPLICATION_JSON)
                .content("")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(400));
    }

    /**
     * param invalid
     * @throws Exception
     */
    @Test
    public void attnd_ParamInvalid()throws Exception{
        mvc.perform(MockMvcRequestBuilders.post("/attnd")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_PARAM_INVALID)));
    }

    /**
     * param invalid location
     * @throws Exception
     */
    @Test
    public void attnd_Location_Invalid()throws Exception{
        mvc.perform(MockMvcRequestBuilders.post("/attnd")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"attnd_name\":\"操作系统\",\"start_time\":15577418,\"last\":20,\"location\":{\"latitude\":-100000,\"longitude\":174.4,\"accuracy\":30.0},\"addr_name\":\"外环西路\",\"teacher_name\":\"wjx\",\"group_name\":\"计科151\"}")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_PARAM_INVALID)));
    }


    /**
     * 发布考勤 ， 第一次 用户需要新建，用户组需要新建+录入
     * @throws Exception
     */
    @Test
    @Transactional
    public void attnd_User_NotExist_Group_NotExist()throws Exception{
        session.setAttribute(configBean.getSession_key(),new Session(0,"",0,"oid","wxsessionkey"));
        mvc.perform(MockMvcRequestBuilders.post("/attnd")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"attnd_name\":\"操作系统\",\"start_time\":15577418,\"last\":20,\"location\":{\"latitude\":23.4,\"longitude\":174.4,\"accuracy\":30.0},\"addr_name\":\"外环西路\",\"teacher_name\":\"wjx\",\"group_name\":\"计科151\"}")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.cipher",startsWith(String.valueOf((Code.CIPHER_ENTRY)))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.attnd_id", Matchers.isA(Integer.TYPE)));
    }

    /**
     * 发布考勤 ， 第一次 用户需要新建，不填用户组名
     * @throws Exception
     */
    @Test
    @Transactional
    public void attnd_User_NotExist_Group_NotFill()throws Exception{
        session.setAttribute(configBean.getSession_key(),new Session(0,"",0,"oid","wxsessionkey"));
        mvc.perform(MockMvcRequestBuilders.post("/attnd")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"attnd_name\":\"操作系统\",\"start_time\":15577418,\"last\":20,\"location\":{\"latitude\":23.4,\"longitude\":174.4,\"accuracy\":30.0},\"addr_name\":\"外环西路\",\"teacher_name\":\"wjx\"}")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.cipher",startsWith(String.valueOf(Code.CIPHER_NOGROUP))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.attnd_id", Matchers.isA(Integer.TYPE)));
    }


    /**
     * 发布考勤 ， 用户存在，对应名称用户组不存在
     * @throws Exception
     */
    @Test
    @Transactional
    public void attnd_User_Exist_Group_NotExist()throws Exception{
        session.setAttribute(configBean.getSession_key(),new Session(1,"lzy",0,"toid123","wxsessionkey"));
        mvc.perform(MockMvcRequestBuilders.post("/attnd")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"attnd_name\":\"操作系统\",\"start_time\":15577418,\"last\":20,\"location\":{\"latitude\":23.4,\"longitude\":174.4,\"accuracy\":30.0},\"addr_name\":\"外环西路\",\"teacher_name\":\"wjx\",\"group_name\":\"计科151\"}")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.cipher",startsWith(String.valueOf(Code.CIPHER_ENTRY))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.attnd_id", Matchers.isA(Integer.TYPE)));
    }

    /**
     * 发布考勤 ， 用户存在，对应名称用户组存在
     * @throws Exception
     */
    @Test
    @Transactional
    public void attnd_User_Exist_Group_Exist_BELONG_ME()throws Exception{
        session.setAttribute(configBean.getSession_key(),new Session(2,"lzy",0,"toid456","wxsessionkey2"));
        mvc.perform(MockMvcRequestBuilders.post("/attnd")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"attnd_name\":\"操作系统\",\"start_time\":15577418,\"last\":20,\"location\":{\"latitude\":23.4,\"longitude\":174.4,\"accuracy\":30.0},\"addr_name\":\"外环西路\",\"teacher_name\":\"wjx\",\"group_name\":\"网工151\"}")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.cipher",startsWith(String.valueOf(Code.CIPHER_ATTND))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.attnd_id", Matchers.isA(Integer.TYPE)));
    }

    /**
     * 发布考勤 ， 用户存在，对应名称用户组存在，但是不是自己建的用户组
     * @throws Exception
     */
    @Test
    @Transactional
    public void attnd_User_Exist_Group_Exist_BELONG_NOTME()throws Exception{
        session.setAttribute(configBean.getSession_key(),new Session(1,"lzp",0,"toid123","wxsessionkey"));
        mvc.perform(MockMvcRequestBuilders.post("/attnd")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"attnd_name\":\"操作系统\",\"start_time\":15577418,\"last\":20,\"location\":{\"latitude\":23.4,\"longitude\":174.4,\"accuracy\":30.0},\"addr_name\":\"外环西路\",\"teacher_name\":\"wjx\",\"group_name\":\"网工151\"}")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.cipher",startsWith(String.valueOf(Code.CIPHER_ENTRY))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.attnd_id", Matchers.isA(Integer.TYPE)));
    }


    /*chk attnd------------------------------------------------------**/
    @Test
    public void chk_attnd_param_invalid()throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/attnd")
                .param("ciphefefsr","test")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(400));
    }

    @Test
    public void chk_attnd_group_exist()throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/attnd")
                .param("cipher","Gwvk1")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.status",is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.attnd_id",is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.attnd_name",is("操作系统1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.start_time",is(15577418)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.last",is(20)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.addr_name",is("外环西路")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.group_name",is("网工151")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.teacher_name",is("lzy")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.teacher_id",is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.cipher",is("Gwvk1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.location.longitude",is(174.4)));

    }


    @Test
    public void chk_attnd_group_not_exist()throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/attnd")
                .param("cipher","Awvk2")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.status",is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.attnd_id",is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.attnd_name",is("计算机网络")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.start_time",is(15577418)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.last",is(20)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.addr_name",is("外环西路")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.group_name",is("")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.teacher_name",is("lzp")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.teacher_id",is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.cipher",is("Awvk2")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.location.longitude",is(174.4)));
    }


    @Test
    public void chk_attnd_not_exist()throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/attnd")
                .param("cipher","123fe")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.ATTND_NOT_EXIST)));

    }

}
