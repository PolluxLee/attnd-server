package com.lzy.attnd.controller;


import com.lzy.attnd.configure.ConfigBean;
import com.lzy.attnd.constant.Code;
import com.lzy.attnd.service.SignInService;
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
        session.setAttribute(configBean.getSession_key(),new Session(1,"lzy",0,"toid123","wxsessionkey","23"));
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
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_NOAUTH)));
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
        session.setAttribute(configBean.getSession_key(),new Session(0,"",0,"oid","wxsessionkey","23"));
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
        session.setAttribute(configBean.getSession_key(),new Session(0,"",0,"oid","wxsessionkey","23"));
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
        session.setAttribute(configBean.getSession_key(),new Session(1,"lzy",0,"toid123","wxsessionkey","23"));
        mvc.perform(MockMvcRequestBuilders.post("/attnd")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"attnd_name\":\"操作系统\",\"start_time\":15577418,\"last\":20,\"location\":{\"latitude\":23.4,\"longitude\":174.4,\"accuracy\":30.0},\"addr_name\":\"外环西路\",\"teacher_name\":\"wjx\",\"group_name\":\"计科141\"}")
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
        session.setAttribute(configBean.getSession_key(),new Session(1,"lzy",0,"toid123","wxsessionkey2","23"));
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
        session.setAttribute(configBean.getSession_key(),new Session(2,"lzp",0,"toid456","wxsessionkey22","23"));
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
    public void chk_attnd_group_existG()throws Exception{
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.start_time",is(1522512000)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.last",is(20)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.addr_name",is("外环西路1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.group_name",is("网工151")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.teacher_name",is("lzy")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.teacher_id",is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.cipher",is("Gwvk1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.location.longitude",is(174.4)));

    }


    @Test
    public void chk_attnd_group_existA()throws Exception{
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.start_time",is(1522512000)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.last",is(20)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.addr_name",is("外环西路2")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.group_name",is("计科151")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.teacher_name",is("lzy")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.teacher_id",is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.cipher",is("Awvk2")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.location.longitude",is(174.4)));
    }

    @Test
    public void chk_attnd_group_not_existN()throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/attnd")
                .param("cipher","NQSA4")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.status",is(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.attnd_id",is(4)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.attnd_name",is("高级网站开发")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.start_time",is(1522512000)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.last",is(20)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.addr_name",is("外环西路4")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.group_name",is("")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.teacher_name",is("lzy")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.teacher_id",is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.cipher",is("NQSA4")))
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


    /*------------------------------sign in----------------------------------------------*/
    @Test
    public void SignIn_cipher_invalid()throws Exception{
        mvc.perform(MockMvcRequestBuilders.post("/attnd/signin")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"ciphfefeer\":\"X574AQ\",\"location\":{\"latitude\":35.4,\"longitude\":174.4,\"accuracy\":30.0}}")
            .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_PARAM_INVALID)));
    }

    @Test
    public void SignIn_location_invalid()throws Exception{
        mvc.perform(MockMvcRequestBuilders.post("/attnd/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"cipher\":\"X574AQ\",\"location\":{\"latitude\":-10000,\"longitude\":174.4,\"accuracy\":30.0}}")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_PARAM_INVALID)));
    }

    @Test
    @Transactional
    public void SignIn_normal_attnd_not_exist()throws Exception{
        mvc.perform(MockMvcRequestBuilders.post("/attnd/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"cipher\":\"Awvk34534\",\"location\":{\"latitude\":23.4,\"longitude\":174.4005,\"accuracy\":30.0}}")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_SYS_ERROR)));
    }

    @Test
    @Transactional
    public void SignIn_attnd_has_signin()throws Exception{
        session.setAttribute(configBean.getSession_key(),new Session(3,"lz",0,"toid789","wxsessionkey","23"));
        mvc.perform(MockMvcRequestBuilders.post("/attnd/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"cipher\":\"Gwvk1\",\"location\":{\"latitude\":23.4,\"longitude\":174.4005,\"accuracy\":30.0}}")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.ATTND_HAS_SIGNIN)));
    }


    @Test
    @Transactional
    public void SignIn_normal_attnd_expired()throws Exception{
        AttndController.testTimestamp = System.currentTimeMillis();
        mvc.perform(MockMvcRequestBuilders.post("/attnd/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"cipher\":\"Awvq3\",\"location\":{\"latitude\":23.4,\"longitude\":174.4005,\"accuracy\":30.0}}")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.ATTND_EXPIRED)));
    }

    @Test
    @Transactional
    public void SignIn_normal_location_beyond()throws Exception{
        //sign in at 10 minutes later after add attnd
        AttndController.testTimestamp = 1522512000+10*60;
        mvc.perform(MockMvcRequestBuilders.post("/attnd/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"cipher\":\"Awvq3\",\"location\":{\"latitude\":23.4,\"longitude\":150.4005,\"accuracy\":30.0}}")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data",is(Code.SIGNIN_LOCATION_BEYOND)));
    }

    @Test
    @Transactional
    public void SignIn_normal_ok()throws Exception{
        //sign in at 10 minutes later after add attnd
        AttndController.testTimestamp = 1522512000+10*60;
        mvc.perform(MockMvcRequestBuilders.post("/attnd/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"cipher\":\"Awvq3\",\"location\":{\"latitude\":23.4,\"longitude\":174.4005,\"accuracy\":30.0}}")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data",is(Code.SIGNIN_OK)));
    }


    @Test
    @Transactional
    public void SignIn_NOGROUP_ok()throws Exception{
        //sign in at 10 minutes later after add attnd
        AttndController.testTimestamp = 1522512000+10*60;
        mvc.perform(MockMvcRequestBuilders.post("/attnd/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"cipher\":\"NQSA4\",\"location\":{\"latitude\":23.4,\"longitude\":174.4005,\"accuracy\":30.0}}")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data",is(Code.SIGNIN_OK)));
    }

    @Test
    @Transactional
    public void SignIn_GROUP_ok()throws Exception{
        session.setAttribute(configBean.getSession_key(),new Session(2,"lzp",0,"toid456","wxsessionkey","25"));
        //sign in at 10 minutes later after add attnd
        AttndController.testTimestamp = 1522512000+10*60;
        mvc.perform(MockMvcRequestBuilders.post("/attnd/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"cipher\":\"Gwvk1\",\"location\":{\"latitude\":23.4,\"longitude\":174.4005,\"accuracy\":30.0}}")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data",is(Code.SIGNIN_OK)));
    }

    @Test
    @Transactional
    public void SignIn_Entry_TOGROUP_1()throws Exception{
        //sign in at 10 minutes later after add attnd
        AttndController.testTimestamp = 1522512000+10*60;
        mvc.perform(MockMvcRequestBuilders.post("/attnd/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"cipher\":\"Swvk1\",\"location\":{\"latitude\":23.4,\"longitude\":174.4005,\"accuracy\":30.0}}")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").doesNotExist());
    }

    //get his attnd name ---------------------------------------------
    @Test
    public void HisName_user_not_exist()throws Exception{
        session.setAttribute(configBean.getSession_key(),new Session(0,"",0,"toid456456","wxsessionkey","23"));
        mvc.perform(MockMvcRequestBuilders.get("/attnd/hisname")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_USER_NOT_EXIST)));
    }

    @Test
    public void HisName_user_exist()throws Exception{
        //{"高级网站开发","计算机网络","操作系统1","编译原理"}
        session.setAttribute(configBean.getSession_key(),new Session(1,"lzy",0,"toid123","wxsessionkey","23"));
        mvc.perform(MockMvcRequestBuilders.get("/attnd/hisname")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0]",is("高级网站开发")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1]",is("计算机网络")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[2]",is("操作系统1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[3]",is("编译原理")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[4]",is("数据结构")));
    }


    //get his addr name ---------------------------------------------

    @Test
    public void HisAddr_user_exist()throws Exception{
        //{"高级网站开发","计算机网络","操作系统1","编译原理"}
        session.setAttribute(configBean.getSession_key(),new Session(1,"lzy",0,"toid123","wxsessionkey","23"));
        mvc.perform(MockMvcRequestBuilders.get("/attnd/hisaddr")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0]",is("外环西路4")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1]",is("外环西路2")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[2]",is("外环西路1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[3]",is("外环西路3")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[4]",is("外环西路5")));
    }


    /*------------------chk signin list-------------------*/

    @Test
    public void signinList_page_invalid()throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/attnd/situation")
                .param("cipher","Awvq1")
                .param("pafege","1")
                .param("page_size","10")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_PARAM_INVALID)));

    }

    @Test
    public void signinList_A()throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/attnd/situation")
                .param("cipher","Awvq1")
                .param("page","1")
                .param("page_size","10")
                .param("fail_only","false")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.count",is(3)));
    }

    @Test
    public void signinList_A_fail_only()throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/attnd/situation")
                .param("cipher","Awvq1")
                .param("page","1")
                .param("page_size","10")
                .param("fail_only","true")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.count",is(3)));
    }

    @Test
    public void signinList_page_2()throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/attnd/situation")
                .param("cipher","Awvq1")
                .param("page","1")
                .param("page_size","2")
                .param("fail_only","false")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.count",is(3)));
    }

    @Test
    public void signinList_G()throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/attnd/situation")
                .param("cipher","Gwvk1")
                .param("page","1")
                .param("page_size","10")
                .param("fail_only","false")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.count",is(2)));
    }

    /*---------------------------------------chkAttndlist------------------*/

    @Test
    public void attndlist_type_invalid()throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/attndlist")
                .param("page","1")
                .param("page_size","10")
                .param("list_type","0")
                .param("query","fa")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_PARAM_INVALID)));
    }

    @Test
    public void attndlist_chkAttnd()throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/attndlist")
                .param("page","1")
                .param("page_size","10")
                .param("list_type","1")
                .param("query","")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_SUCCESS)));
    }

    @Test
    public void attndlist_chkAttnd_query()throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/attndlist")
                .param("page","1")
                .param("page_size","10")
                .param("list_type","1")
                .param("query","操作")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_SUCCESS)));
    }


    @Test
    public void attndlist_chkAttnd_signin_query()throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/attndlist")
                .param("page","1")
                .param("page_size","10")
                .param("list_type","2")
                .param("query","数据")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_SUCCESS)));
    }

    @Test
    public void attndlist_chkAttnd_signin_page()throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/attndlist")
                .param("page","1")
                .param("page_size","1")
                .param("list_type","2")
                .param("query","")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_SUCCESS)));
    }

}
