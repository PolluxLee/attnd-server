package com.lzy.attnd.controller;


import com.lzy.attnd.configure.ConfigBean;
import com.lzy.attnd.constant.Code;
import com.lzy.attnd.service.SignInService;
import com.lzy.attnd.service.WechatService;
import com.lzy.attnd.utils.Session;
import org.hamcrest.Matchers;
import org.junit.After;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static com.lzy.attnd.controller.AttndController.testTimestamp;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@WebAppConfiguration
@TestPropertySource(locations="classpath:application-test.properties")
public class AttndControllerTests {

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mvc;
    private MockHttpSession session;
    private Session sessionMy;

    @Autowired
    private ConfigBean configBean;

    @Before
    public void setupMockMvc(){
        mvc = MockMvcBuilders.webAppContextSetup(wac).build(); //初始化MockMvc对象
        session = new MockHttpSession();
        sessionMy = new Session(1,"lzy",0,"toid123","wxsessionkey","23");
        session.setAttribute(configBean.getSession_key(),sessionMy);
    }

    @After
    public void clean(){
        testTimestamp = 0;
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
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_NOAUTH)));
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
                .andExpect(status().is(400));
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
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_PARAM_INVALID)));
    }

    /**
     * param invalid location
     * @throws Exception
     */
    @Test
    public void attnd_Location_Invalid()throws Exception{
        mvc.perform(MockMvcRequestBuilders.post("/attnd")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"attnd_name\":\"操作系统\",\"start_time\":15577418,\"last\":20,\"location\":{\"latitude\":-100000,\"longitude\":174.4,\"accuracy\":30.0},\"addr_name\":\"外环西路\",\"teacher_name\":\"wjx\"}")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_PARAM_INVALID)));
    }


    /**
     * 发布考勤 ， 第一次 用户需要新建
     * @throws Exception
     */
    @Test
    @Transactional
    public void attnd_User_NotExist_Group_NotExist()throws Exception{
        session.setAttribute(configBean.getSession_key(),new Session(0,"",0,"oid22","wxsessionkey","23"));
        mvc.perform(MockMvcRequestBuilders.post("/attnd")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"attnd_name\":\"操作系统\",\"start_time\":15577418,\"last\":20,\"location\":{\"latitude\":23.4,\"longitude\":174.4,\"accuracy\":30.0},\"addr_name\":\"外环西路\",\"teacher_name\":\"wjx\"}")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(jsonPath("$.data.cipher",startsWith(String.valueOf((Code.CIPHER_ATTND)))))
                .andExpect(jsonPath("$.data.attnd_id", Matchers.isA(Integer.TYPE)))
                .andExpect(jsonPath("$.data.userinfo.name", is("wjx")))
                .andExpect(jsonPath("$.data.userinfo.openid", is("oid22")));
    }

    /**
     * 发布考勤 ， 用户存在
     * @throws Exception
     */
    @Test
    @Transactional
    public void attnd_User_Exist_Group_NotExist()throws Exception{
        session.setAttribute(configBean.getSession_key(),new Session(1,"lzy",0,"toid123","wxsessionkey","23"));
        mvc.perform(MockMvcRequestBuilders.post("/attnd")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"attnd_name\":\"操作系统\",\"start_time\":15577418,\"last\":20,\"location\":{\"latitude\":23.4,\"longitude\":174.4,\"accuracy\":30.0},\"addr_name\":\"外环西路\",\"teacher_name\":\"wjx\"}")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(jsonPath("$.data.cipher",startsWith(String.valueOf(Code.CIPHER_ATTND))))
                .andExpect(jsonPath("$.data.attnd_id", Matchers.isA(Integer.TYPE)))
                .andExpect(jsonPath("$.data.userinfo.name", is("lzy")))
                .andExpect(jsonPath("$.data.userinfo.openid", is("toid123")));
    }



    /*chk attnd------------------------------------------------------**/
    @Test
    public void chk_attnd_param_invalid()throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/attnd")
                .param("ciphefefsr","test")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    public void chk_attnd_group_existA()throws Exception{
        testTimestamp = 1522512000+10*60;
        mvc.perform(MockMvcRequestBuilders.get("/attnd")
                .param("cipher","AwXk2")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(jsonPath("$.data.status",is(1)))
                .andExpect(jsonPath("$.data.attnd_id",is(2)))
                .andExpect(jsonPath("$.data.attnd_name",is("计算机网络")))
                .andExpect(jsonPath("$.data.start_time",is(1522512000)))
                .andExpect(jsonPath("$.data.last",is(20)))
                .andExpect(jsonPath("$.data.addr_name",is("理南315")))
                .andExpect(jsonPath("$.data.teacher_name",is("lzy")))
                .andExpect(jsonPath("$.data.teacher_id",is(1)))
                .andExpect(jsonPath("$.data.cipher",is("AwXk2")))
                .andExpect(jsonPath("$.data.location.longitude",is(174.4)));

    }


    @Test
    public void chk_attnd_expired()throws Exception{
        testTimestamp = 1522512000+30*60*1000;
        mvc.perform(MockMvcRequestBuilders.get("/attnd")
                .param("cipher","AwXk2")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(jsonPath("$.data.status",is(-1)))
                .andExpect(jsonPath("$.data.attnd_id",is(2)))
                .andExpect(jsonPath("$.data.attnd_name",is("计算机网络")))
                .andExpect(jsonPath("$.data.start_time",is(1522512000)))
                .andExpect(jsonPath("$.data.last",is(20)))
                .andExpect(jsonPath("$.data.addr_name",is("理南315")))
                .andExpect(jsonPath("$.data.teacher_name",is("lzy")))
                .andExpect(jsonPath("$.data.teacher_id",is(1)))
                .andExpect(jsonPath("$.data.cipher",is("AwXk2")))
                .andExpect(jsonPath("$.data.location.longitude",is(174.4)));
    }

    @Test
    public void chk_attnd_bedel()throws Exception{
        testTimestamp = 1522512000+30*60*1000;
        mvc.perform(MockMvcRequestBuilders.get("/attnd")
                .param("cipher","AQSA4")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.ATTND_NOT_EXIST)));
    }


    @Test
    public void chk_attnd_not_exist()throws Exception{
        testTimestamp = 1522512000+10*60;
        mvc.perform(MockMvcRequestBuilders.get("/attnd")
                .param("cipher","123fe")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.ATTND_NOT_EXIST)));

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
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_PARAM_INVALID)));
    }

    @Test
    @Transactional
    public void SignIn_location_invalid()throws Exception{
        mvc.perform(MockMvcRequestBuilders.post("/attnd/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"cipher\":\"X574AQ\",\"location\":{\"latitude\":-10000,\"longitude\":174.4,\"accuracy\":30.0}}")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_PARAM_INVALID)));
    }

    @Test
    @Transactional
    public void SignIn_cipher_empty()throws Exception{
        mvc.perform(MockMvcRequestBuilders.post("/attnd/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"cipher\":\"\",\"location\":{\"latitude\":-10,\"longitude\":174.4,\"accuracy\":30.0}}")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_PARAM_INVALID)));
    }

    /**
     * 用户不存在
     * @throws Exception
     */
    @Test
    @Transactional
    public void SignIn_normal_user_not_exist()throws Exception{
        session.setAttribute(configBean.getSession_key(),new Session(3,"db",0,"dawczx","wxsessionkey","23"));

        mvc.perform(MockMvcRequestBuilders.post("/attnd/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"cipher\":\"Awvk34534\",\"location\":{\"latitude\":23.4,\"longitude\":174.4005,\"accuracy\":30.0}}")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SYS_ERROR)));
    }

    /**
     * 口令不存在
     * @throws Exception
     */
    @Test
    @Transactional
    public void SignIn_normal_attnd_not_exist()throws Exception{
        mvc.perform(MockMvcRequestBuilders.post("/attnd/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"cipher\":\"Awvk34534\",\"location\":{\"latitude\":23.4,\"longitude\":174.4005,\"accuracy\":30.0}}")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.ATTND_NOT_EXIST)));
    }

    /**
     * 已经签过到
     * @throws Exception
     */
    @Test
    @Transactional
    public void SignIn_attnd_has_signin()throws Exception{
        session.setAttribute(configBean.getSession_key(),new Session(3,"lz",0,"toid789","wxsessionkey","23"));
        mvc.perform(MockMvcRequestBuilders.post("/attnd/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"cipher\":\"Awvk1\",\"location\":{\"latitude\":23.4,\"longitude\":174.4005,\"accuracy\":30.0}}")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.ATTND_HAS_SIGNIN)));
    }


    /**
     * 创建者去签到
     * @throws Exception
     */
    @Test
    @Transactional
    public void SignIn_creator()throws Exception{
        //sign in at 10 minutes later after add attnd
        session.setAttribute(configBean.getSession_key(),new Session(1,"lzy",0,"toid123","wxsessionkey","23"));
        testTimestamp = 1522512000+10*60;
        mvc.perform(MockMvcRequestBuilders.post("/attnd/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"cipher\":\"AwvF5\",\"location\":{\"latitude\":23.4,\"longitude\":174.4005,\"accuracy\":30.0},\"attnd_id\":1}")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.SIGNIN_CREATOR)));
    }


    /**
     * 超时
     * @throws Exception
     */
    @Test
    @Transactional
    public void SignIn_normal_attnd_expired()throws Exception{
        session.setAttribute(configBean.getSession_key(),new Session(3,"lz",0,"toid789","wxsessionkey","23"));
        testTimestamp = System.currentTimeMillis();
        mvc.perform(MockMvcRequestBuilders.post("/attnd/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"cipher\":\"AwvF5\",\"location\":{\"latitude\":23.4,\"longitude\":174.4005,\"accuracy\":30.0}}")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(jsonPath("$.data",is(Code.SIGNIN_EXPIRED)));
    }

    /**
     * 位置超出
     * @throws Exception
     */
    @Test
    @Transactional
    public void SignIn_normal_location_beyond()throws Exception{
        //sign in at 10 minutes later after add attnd
        session.setAttribute(configBean.getSession_key(),new Session(3,"lz",0,"toid789","wxsessionkey","23"));
        testTimestamp = 1522512000+10*60;
        mvc.perform(MockMvcRequestBuilders.post("/attnd/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"cipher\":\"AwvF5\",\"location\":{\"latitude\":23.4,\"longitude\":150.4005,\"accuracy\":30.0}}")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(jsonPath("$.data",is(Code.SIGNIN_LOCATION_BEYOND)));
    }

    @Test
    @Transactional
    public void SignIn_normal_ok()throws Exception{
        //sign in at 10 minutes later after add attnd
        session.setAttribute(configBean.getSession_key(),new Session(3,"lz",0,"toid789","wxsessionkey","25"));
        testTimestamp = 1522512000+10*60;
        mvc.perform(MockMvcRequestBuilders.post("/attnd/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"cipher\":\"AwvF5\",\"location\":{\"latitude\":23.4,\"longitude\":174.4005,\"accuracy\":30.0}}")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(jsonPath("$.data",is(Code.SIGNIN_OK)));
    }

    //get his attnd name ---------------------------------------------
    @Test
    public void HisName_user_not_exist()throws Exception{
        session.setAttribute(configBean.getSession_key(),new Session(0,"",0,"toid456456","wxsessionkey","23"));
        mvc.perform(MockMvcRequestBuilders.get("/attnd/hisname")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_USER_NOT_EXIST)));
    }

    @Test
    public void HisName_user_exist()throws Exception{
        //["计算机网络","操作系统1","数据结构"]
        session.setAttribute(configBean.getSession_key(),new Session(1,"lzy",0,"toid123","wxsessionkey",""));
        mvc.perform(MockMvcRequestBuilders.get("/attnd/hisname")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(jsonPath("$.data[0]",is("计算机网络")))
                .andExpect(jsonPath("$.data[1]",is("操作系统1")))
                .andExpect(jsonPath("$.data[2]",is("数据结构")));
    }


    //get his addr name ---------------------------------------------

    @Test
    public void HisAddr_user_exist()throws Exception{
        //{"高级网站开发","计算机网络","操作系统1","编译原理"}
        session.setAttribute(configBean.getSession_key(),new Session(1,"lzy",0,"toid123","wxsessionkey",""));
        mvc.perform(MockMvcRequestBuilders.get("/attnd/hisaddr")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(jsonPath("$.data[0]",is("理南315")))
                .andExpect(jsonPath("$.data[1]",is("文新512")))
                .andExpect(jsonPath("$.data[2]",is("电子417")));
    }


    /*------------------chk signin list-------------------*/

    @Test
    public void signinList_page_invalid()throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/attnd/situation")
                .param("cipher","ACcq3")
                .param("pafege","1")
                .param("page_size","10")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_PARAM_INVALID)));

    }

    @Test
    public void signinList_ALL()throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/attnd/situation")
                .param("cipher","ACcq3")
                .param("page","1")
                .param("page_size","10")
                .param("signin_status","0")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(jsonPath("$.data.count",is(2)))

                .andExpect(jsonPath("$.data.attnds[0].openid",is("toid123")))
                .andExpect(jsonPath("$.data.attnds[0].name",is("lzy")))
                .andExpect(jsonPath("$.data.attnds[0].stu_id",is("23")))
                .andExpect(jsonPath("$.data.attnds[0].attnd_status",is(Code.SIGNIN_LOCATION_BEYOND)))
                .andExpect(jsonPath("$.data.attnds[0].distance",is(64.4)))

                .andExpect(jsonPath("$.data.attnds[1].openid",is("toid789")))
                .andExpect(jsonPath("$.data.attnds[1].name",is("lz")))
                .andExpect(jsonPath("$.data.attnds[1].attnd_status",is(Code.SIGNIN_OK)))

                .andExpect(jsonPath("$.data.my_signin.openid",is("toid123")))
                .andExpect(jsonPath("$.data.my_signin.name",is("lzy")))
                .andExpect(jsonPath("$.data.my_signin.stu_id",is("23")))
                .andExpect(jsonPath("$.data.my_signin.attnd_status",is(Code.SIGNIN_LOCATION_BEYOND)))
                .andExpect(jsonPath("$.data.my_signin.distance",is(64.4)));
    }


    @Test
    public void signinList_A_param_invalid()throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/attnd/situation")
                .param("cipher","Awcq31")
                .param("page","1")
                .param("page_size","10")
                .param("signin_status",Integer.toString(Code.SIGNIN_NOT_EXIST))
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_PARAM_INVALID)));
    }

    @Test
    public void signinList_status_loc()throws Exception{
        session.setAttribute(configBean.getSession_key(),new Session(4,"wj",0,"toid222","wxsessionkey",""));

        mvc.perform(MockMvcRequestBuilders.get("/attnd/situation")
                .param("cipher","ACcq3")
                .param("page","1")
                .param("page_size","5")
                .param("signin_status",Integer.toString(Code.SIGNIN_LOCATION_BEYOND))
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(jsonPath("$.data.count",is(1)))

                .andExpect(jsonPath("$.data.attnds[0].openid",is("toid123")))
                .andExpect(jsonPath("$.data.attnds[0].name",is("lzy")))
                .andExpect(jsonPath("$.data.attnds[0].stu_id",is("23")))
                .andExpect(jsonPath("$.data.attnds[0].attnd_status",is(Code.SIGNIN_LOCATION_BEYOND)))
                .andExpect(jsonPath("$.data.attnds[0].distance",is(64.4)))

                .andExpect(jsonPath("$.data.my_signin.size()",is(0)));
    }

    @Test
    public void signinList_page2()throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/attnd/situation")
                .param("cipher","ACcq3")
                .param("page","2")
                .param("page_size","1")
                .param("signin_status",Integer.toString(Code.SIGNIN_ALL))
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(jsonPath("$.data.count",is(2)))

                .andExpect(jsonPath("$.data.attnds[0].openid",is("toid789")))
                .andExpect(jsonPath("$.data.attnds[0].name",is("lz")))
                .andExpect(jsonPath("$.data.attnds[0].attnd_status",is(Code.SIGNIN_OK)));
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
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_PARAM_INVALID)));
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
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(jsonPath("$.data.count",is(3)))

                .andExpect(jsonPath("$.data.attnds[0].attnd_id",is(2)))
                .andExpect(jsonPath("$.data.attnds[0].attnd_name",is("计算机网络")))
                .andExpect(jsonPath("$.data.attnds[0].start_time",is(1522512000)))
                .andExpect(jsonPath("$.data.attnds[0].addr_name",is("理南315")))
                .andExpect(jsonPath("$.data.attnds[0].teacher_name",is("lzy")))
                .andExpect(jsonPath("$.data.attnds[0].cipher",is("AwXk2")))
                .andExpect(jsonPath("$.data.attnds[0].location.longitude",is(174.4)))

                .andExpect(jsonPath("$.data.attnds[1].attnd_id",is(1)))
                .andExpect(jsonPath("$.data.attnds[1].attnd_name",is("操作系统1")))
                .andExpect(jsonPath("$.data.attnds[1].start_time",is(1522512000)))
                .andExpect(jsonPath("$.data.attnds[1].addr_name",is("文新512")))
                .andExpect(jsonPath("$.data.attnds[1].teacher_name",is("lzy")))
                .andExpect(jsonPath("$.data.attnds[1].cipher",is("Awvk1")))

                .andExpect(jsonPath("$.data.attnds[2].attnd_id",is(5)))
                .andExpect(jsonPath("$.data.attnds[2].attnd_name",is("数据结构")));
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
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))

                .andExpect(jsonPath("$.data.count",is(1)))

                .andExpect(jsonPath("$.data.attnds[0].attnd_id",is(1)))
                .andExpect(jsonPath("$.data.attnds[0].attnd_name",is("操作系统1")))
                .andExpect(jsonPath("$.data.attnds[0].start_time",is(1522512000)))
                .andExpect(jsonPath("$.data.attnds[0].addr_name",is("文新512")))
                .andExpect(jsonPath("$.data.attnds[0].teacher_name",is("lzy")))
                .andExpect(jsonPath("$.data.attnds[0].cipher",is("Awvk1")));
    }

    @Test
    public void attndlist_chkAttnd_page1()throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/attndlist")
                .param("page","1")
                .param("page_size","1")
                .param("list_type","1")
                .param("query","")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(jsonPath("$.data.count",is(3)))

                .andExpect(jsonPath("$.data.attnds[0].attnd_id",is(2)))
                .andExpect(jsonPath("$.data.attnds[0].attnd_name",is("计算机网络")))
                .andExpect(jsonPath("$.data.attnds[0].start_time",is(1522512000)))
                .andExpect(jsonPath("$.data.attnds[0].addr_name",is("理南315")))
                .andExpect(jsonPath("$.data.attnds[0].teacher_name",is("lzy")))
                .andExpect(jsonPath("$.data.attnds[0].cipher",is("AwXk2")))
                .andExpect(jsonPath("$.data.attnds[0].location.longitude",is(174.4)));
    }

    @Test
    public void attndlist_chkAttnd_page2()throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/attndlist")
                .param("page","2")
                .param("page_size","2")
                .param("list_type","1")
                .param("query","")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(jsonPath("$.data.count",is(3)))

                .andExpect(jsonPath("$.data.attnds[0].attnd_id",is(5)))
                .andExpect(jsonPath("$.data.attnds[0].attnd_name",is("数据结构")));
    }


    @Test
    public void attndlist_chksignin_query()throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/attndlist")
                .param("page","1")
                .param("page_size","10")
                .param("list_type","2")
                .param("query","编译原理")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))

                .andExpect(jsonPath("$.data.count",is(1)))
                .andExpect(jsonPath("$.data.attnds[0].attnd_id",is(3)))
                .andExpect(jsonPath("$.data.attnds[0].attnd_name",is("编译原理")));
    }

    @Test
    public void attndlist_chkAttnd_signin_page()throws Exception{
        session.setAttribute(configBean.getSession_key(),new Session(3,"lz",0,"toid789","wxsessionkey","25"));
        mvc.perform(MockMvcRequestBuilders.get("/attndlist")
                .param("page","1")
                .param("page_size","10")
                .param("list_type","2")
                .param("query","")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(jsonPath("$.data.count",is(2)))

                .andExpect(jsonPath("$.data.attnds[0].attnd_id",is(1)))
                .andExpect(jsonPath("$.data.attnds[0].attnd_name",is("操作系统1")))
                .andExpect(jsonPath("$.data.attnds[0].start_time",is(1522512000)))
                .andExpect(jsonPath("$.data.attnds[0].addr_name",is("文新512")))
                .andExpect(jsonPath("$.data.attnds[0].teacher_name",is("lzy")))
                .andExpect(jsonPath("$.data.attnds[0].cipher",is("Awvk1")))

                .andExpect(jsonPath("$.data.attnds[1].attnd_id",is(3)))
                .andExpect(jsonPath("$.data.attnds[1].attnd_name",is("编译原理")));
    }

    /**-----------------del ATTND --------------------------*/
    @Test
    @Transactional
    public void delAttnd_ongoing()throws Exception{
        testTimestamp=1522512000+10*60*1000;
        mvc.perform(MockMvcRequestBuilders.post("/attnd/del")
                .content("cipher=AwXk2")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.ATTND_ONGOING)));
    }

    @Test
    @Transactional
    public void delAttnd_hasdeled()throws Exception{
        testTimestamp=1522512000+30*60*1000;
        mvc.perform(MockMvcRequestBuilders.post("/attnd/del")
                .content("cipher=AQSA4")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.ATTND_HAS_DEL)));
    }

    @Test
    @Transactional
    public void delAttnd_notexist()throws Exception{
        testTimestamp=1522512000+30*60*1000;
        mvc.perform(MockMvcRequestBuilders.post("/attnd/del")
                .content("cipher=Awcq64531")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.ATTND_NOT_EXIST)));
    }

    @Test
    @Transactional
    public void delAttnd_notbelongme()throws Exception{
        session.setAttribute(configBean.getSession_key(),new Session(2,"lzp",0,"toid456","wxsessionkey","25"));
        testTimestamp=1522512000+30*60*1000;
        mvc.perform(MockMvcRequestBuilders.post("/attnd/del")
                .content("cipher=Awvk1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.ATTND_NOT_CREATOR)));
    }

    @Test
    @Transactional
    public void delAttnd_success()throws Exception{
        testTimestamp=1522512000+30*60*1000;
        mvc.perform(MockMvcRequestBuilders.post("/attnd/del")
                .content("cipher=AwXk2")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)));
    }

    //upd status -----------------------------------------

    @Test
    @Transactional
    public void updStatus_param_invalid()throws Exception{
        testTimestamp=1522512000+30*60*1000;
        mvc.perform(MockMvcRequestBuilders.post("/signin/status/upd")
                .content("cipher=Awcq31&openid=toid7354389&attnd_us=4")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_PARAM_INVALID)));
    }

    /**
     * 考勤进行中
     * @throws Exception
     */
    @Test
    @Transactional
    public void updStatus_ongoing()throws Exception{
        testTimestamp=1522512000+5*60*1000;
        mvc.perform(MockMvcRequestBuilders.post("/signin/status/upd")
                .content("cipher=AwXk2&openid=toid789&attnd_status=4")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.ATTND_ONGOING)));
    }

    /**
     * 考勤已经被删除
     * @throws Exception
     */
    @Test
    @Transactional
    public void updStatus_hasdeled()throws Exception{
        testTimestamp=1522512000+30*60*1000;
        mvc.perform(MockMvcRequestBuilders.post("/signin/status/upd")
                .content("cipher=AQSA4&openid=toid789&attnd_status=4")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.ATTND_HAS_DEL)));
    }

    /**
     * 考勤不存在
     * @throws Exception
     */
    @Test
    @Transactional
    public void updStatus_notexist()throws Exception{
        testTimestamp=1522512000+30*60*1000;
        mvc.perform(MockMvcRequestBuilders.post("/signin/status/upd")
                .content("cipher=Awcq64531&openid=toid789&attnd_status=4")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.ATTND_NOT_EXIST)));
    }

    /**
     * 非本考勤的创建者
     * @throws Exception
     */
    @Test
    @Transactional
    public void updStatus_notbelongme()throws Exception{
        session.setAttribute(configBean.getSession_key(),new Session(2,"lzp",0,"toid456","wxsessionkey","25"));
        testTimestamp=1522512000+30*60*1000;
        mvc.perform(MockMvcRequestBuilders.post("/signin/status/upd")
                .content("cipher=Awvk1&openid=toid789&attnd_status=4")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.ATTND_NOT_CREATOR)));
    }

    /**
     * 用户不存在
     * @throws Exception
     */
    @Test
    @Transactional
    public void updStatus_user_notexist()throws Exception{
        testTimestamp=1522512000+30*60*1000;
        mvc.perform(MockMvcRequestBuilders.post("/signin/status/upd")
                .content("cipher=Awvk1&openid=toid7354389&attnd_status=4")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.ATTND_HASNOT_SIGNIN)));
    }

    @Test
    @Transactional
    public void updStatus_success()throws Exception{
        testTimestamp=1522512000+30*60*1000;
        mvc.perform(MockMvcRequestBuilders.post("/signin/status/upd")
                .content("cipher=Awvk1&openid=toid789&attnd_status=4")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)));
    }
}
