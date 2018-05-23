package com.lzy.attnd.pit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.jayway.jsonpath.JsonPath;
import com.lzy.attnd.configure.ConfigBean;
import com.lzy.attnd.constant.Code;
import com.lzy.attnd.model.Attnd;
import com.lzy.attnd.model.AttndState;
import com.lzy.attnd.model.Location;
import com.lzy.attnd.service.WechatService;
import com.lzy.attnd.utils.Session;
import org.hamcrest.Matchers;
import org.json.JSONArray;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
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

@RunWith(SpringRunner.class)
@TestPropertySource(locations="classpath:application-pit.properties")
@SpringBootTest
public class IntegrationTests {
    /*


     //get his group name
        mvc.perform(MockMvcRequestBuilders.get("/group/name")
                .session(s_tea1)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(jsonPath("$.data",Matchers.arrayWithSize(0)));
*/
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mvc;
    private MockHttpSession s_stu1 = new MockHttpSession();
    private MockHttpSession s_stu2 = new MockHttpSession();
    private MockHttpSession s_tea1 = new MockHttpSession();

    @Autowired
    private ConfigBean configBean;

    @Autowired
    private WechatService wechatService;

    @Before
    public void setupMockMvc(){
        //default session
        mvc = MockMvcBuilders.webAppContextSetup(wac).build(); //初始化MockMvc对象
        s_stu1.setAttribute(configBean.getSession_key(),new Session(0,"",0,"","",""));
        s_stu2.setAttribute(configBean.getSession_key(),new Session(0,"",0,"","",""));
        s_tea1.setAttribute(configBean.getSession_key(),new Session(0,"",0,"","",""));
    }

    @After
    public void clean(){
        testTimestamp = 0;
    }

    //warning no check here
    private Object readRespWithKey(MvcResult result,String path)throws Exception{
        return JsonPath.read(result.getResponse().getContentAsString(),path);
    }


    private void studentSignin(MockHttpSession s_stu,String cipher,String stu_Oid,String code,String stuName,long attndTime)throws Exception{

        //student login
        mvc.perform(MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .content("code="+code)
                .session(s_stu)
        )
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.USER_NOT_EXIST)))
                .andExpect(jsonPath("$.data.openid",is(stu_Oid)));

        //student fillinfo
        //no stuid (optional)
        //String stu1Name= "stu1";
        mvc.perform(MockMvcRequestBuilders.post("/user/info")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"name\":\"%s\"}",stuName))
                .session(s_stu)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)));



        //student signin
        //sign in at 10 minutes later after add attnd
        testTimestamp = attndTime+10*60*1000;
        mvc.perform(MockMvcRequestBuilders.post("/attnd/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"cipher\":\"%s\",\"location\":{\"latitude\":23.4,\"longitude\":174.4005,\"accuracy\":30.0}}",cipher))
                .session(s_stu)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(jsonPath("$.data",is(Code.SIGNIN_OK)));
    }


    /**
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void TestPIT()throws Exception{
        MvcResult r;

        String teac_Oid = "openid_tec";
        String teac_session_key = "session_key-test1";

        String stu1_Oid = "openid_stu1";
        String stu1Name= "stu1";
        String stu1_session_key = "session_key-test2";

        String stu2_Oid = "openid_stu2";
        String stu2Name= "stu2";
        String stu2_fillid="23";
        String stu2_session_key = "session_key-test3";

        //teacher
        Mockito.when(wechatService.Wx_Login("123")).thenReturn(new WechatService.WxLoginFb(teac_Oid,teac_session_key));
        //stu1
        Mockito.when(wechatService.Wx_Login("456")).thenReturn(new WechatService.WxLoginFb(stu1_Oid,stu1_session_key));
        //stu2
        Mockito.when(wechatService.Wx_Login("789")).thenReturn(new WechatService.WxLoginFb(stu2_Oid,stu2_session_key));

        //teacher login
        mvc.perform(MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .content("code=123")
                .session(s_tea1)
        )
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.USER_NOT_EXIST)))
                .andExpect(jsonPath("$.data.openid",is(teac_Oid)));

        //get his attnd name
        mvc.perform(MockMvcRequestBuilders.get("/attnd/hisname")
                .session(s_tea1)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_USER_NOT_EXIST)));

        //get his addr name
        mvc.perform(MockMvcRequestBuilders.get("/attnd/hisaddr")
                .session(s_tea1)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_USER_NOT_EXIST)));

        //get his group name
        mvc.perform(MockMvcRequestBuilders.get("/group/name")
                .session(s_tea1)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_USER_NOT_EXIST)));

        //add attnd
        //add user 郭思文
        //add groupname 计科151
        long attndTime = Long.valueOf("1526996690000");
        Attnd attndG = new Attnd(0,"高水平大学诗词鉴赏课",attndTime,20,new Location(23.4,174.4,30.0),"文新510","计科151","郭思文",1,"x");
        ObjectMapper mapper = new ObjectMapper();
        String attndJson = mapper.writeValueAsString(attndG);
        r = mvc.perform(MockMvcRequestBuilders.post("/attnd")
                .contentType(MediaType.APPLICATION_JSON)
                .content(attndJson)
                .session(s_tea1)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(jsonPath("$.data.cipher",startsWith(String.valueOf((Code.CIPHER_ENTRY)))))
                .andExpect(jsonPath("$.data.attnd_id", Matchers.isA(Integer.TYPE)))
                .andReturn();

        //type G
        String cipher_G = ((String) readRespWithKey(r, "$.data.cipher"));
        attndG.setCipher(cipher_G);
        //----------------------stu1

        //student login
        mvc.perform(MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .content("code=456")
                .session(s_stu1)
        )
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.USER_NOT_EXIST)))
                .andExpect(jsonPath("$.data.openid",is(stu1_Oid)));

        //student fillinfo
        //no stuid (optional)
        mvc.perform(MockMvcRequestBuilders.post("/user/info")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"name\":\"%s\"}",stu1Name))
                .session(s_stu1)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)));



        //student signin
        //sign in at 10 minutes later after add attnd
        testTimestamp = attndTime+10*60*1000;
        mvc.perform(MockMvcRequestBuilders.post("/attnd/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"cipher\":\"%s\",\"location\":{\"latitude\":23.4,\"longitude\":174.4005,\"accuracy\":30.0}}",cipher_G))
                .session(s_stu1)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(jsonPath("$.data",is(Code.SIGNIN_OK)));

        //stu2----------------------------------------------
        //student login
        mvc.perform(MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .content("code=789")
                .session(s_stu2)
        )
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.USER_NOT_EXIST)))
                .andExpect(jsonPath("$.data.openid",is(stu2_Oid)));

        //student fillinfo
        //WITH stuid (optional)
        mvc.perform(MockMvcRequestBuilders.post("/user/info")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"name\":\"%s\",\"stu_id\":\"%s\"}",stu2Name,stu2_fillid))
                .session(s_stu2)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)));



        //student signin
        //sign in at 12 minutes later after add attnd
        testTimestamp = attndTime+12*60*1000;
        mvc.perform(MockMvcRequestBuilders.post("/attnd/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"cipher\":\"%s\",\"location\":{\"latitude\":23.4,\"longitude\":174.4005,\"accuracy\":30.0}}",cipher_G))
                .session(s_stu2)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(jsonPath("$.data",is(Code.SIGNIN_OK)));

        //stu2 chk attnd info + situation
        r = mvc.perform(MockMvcRequestBuilders.get("/attnd")
                .param("cipher",cipher_G)
                .session(s_stu2)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(jsonPath("$.data.status",is(2)))
                .andExpect(jsonPath("$.data.attnd_name",is(attndG.getAttnd_name())))
                .andExpect(jsonPath("$.data.start_time",is(attndG.getStart_time())))
                .andExpect(jsonPath("$.data.last",is(attndG.getLast())))
                .andExpect(jsonPath("$.data.addr_name",is(attndG.getAddr_name())))
                .andExpect(jsonPath("$.data.group_name",is(attndG.getGroup_name())))
                .andExpect(jsonPath("$.data.teacher_name",is(attndG.getTeacher_name())))
                .andExpect(jsonPath("$.data.cipher",is(cipher_G)))
                .andExpect(jsonPath("$.data.location.longitude",is(attndG.getLocation().getLongitude())))
                .andReturn();

        attndG.setTeacher_id(((int) readRespWithKey(r, "$.data.teacher_id")));
        attndG.setAttnd_id(((int) readRespWithKey(r, "$.data.attnd_id")));

        AttndState attndStateStu1 = new AttndState(stu1_Oid,stu1Name,"",1,51.022419667381016);
        AttndState attndStateStu2 = new AttndState(stu2_Oid,stu2Name,stu2_fillid,1,51.022419667381016);

        //stu2 chk situation
        mvc.perform(MockMvcRequestBuilders.get("/attnd/situation")
                .param("cipher",cipher_G)
                .param("page","1")
                .param("page_size","10")
                .param("fail_only","false")
                .session(s_stu2)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(jsonPath("$.data.count",is(2)))
                .andExpect(jsonPath("$.data.attnds[0].openid",is(attndStateStu1.getOpenid())))
                .andExpect(jsonPath("$.data.attnds[0].name",is(attndStateStu1.getName())))
                .andExpect(jsonPath("$.data.attnds[0].stu_id",is(attndStateStu1.getStu_id())))
                .andExpect(jsonPath("$.data.attnds[0].attnd_status",is(attndStateStu1.getAttnd_status())))
                .andExpect(jsonPath("$.data.attnds[0].distance",is(attndStateStu1.getDistance())))

                .andExpect(jsonPath("$.data.attnds[1].openid",is(attndStateStu2.getOpenid())))
                .andExpect(jsonPath("$.data.attnds[1].name",is(attndStateStu2.getName())))
                .andExpect(jsonPath("$.data.attnds[1].stu_id",is(attndStateStu2.getStu_id())))
                .andExpect(jsonPath("$.data.attnds[1].attnd_status",is(attndStateStu2.getAttnd_status())))
                .andExpect(jsonPath("$.data.attnds[1].distance",is(attndStateStu2.getDistance())))
                .andReturn();


        //teacher chk attnd info + situation
        mvc.perform(MockMvcRequestBuilders.get("/attnd")
                .param("cipher",cipher_G)
                .session(s_tea1)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(jsonPath("$.data.status",is(2)))
                .andExpect(jsonPath("$.data.attnd_name",is(attndG.getAttnd_name())))
                .andExpect(jsonPath("$.data.start_time",is(attndG.getStart_time())))
                .andExpect(jsonPath("$.data.last",is(attndG.getLast())))
                .andExpect(jsonPath("$.data.addr_name",is(attndG.getAddr_name())))
                .andExpect(jsonPath("$.data.group_name",is(attndG.getGroup_name())))
                .andExpect(jsonPath("$.data.teacher_name",is(attndG.getTeacher_name())))
                .andExpect(jsonPath("$.data.cipher",is(cipher_G)))
                .andExpect(jsonPath("$.data.location.longitude",is(attndG.getLocation().getLongitude())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.attnd_id",is(attndG.getAttnd_id())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.teacher_id",is(attndG.getTeacher_id())))
                .andReturn();

        //teacher chk situation
        mvc.perform(MockMvcRequestBuilders.get("/attnd/situation")
                .param("cipher",cipher_G)
                .param("page","1")
                .param("page_size","10")
                .param("fail_only","false")
                .session(s_tea1)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(jsonPath("$.data.count",is(2)))
                .andExpect(jsonPath("$.data.attnds[0].openid",is(attndStateStu1.getOpenid())))
                .andExpect(jsonPath("$.data.attnds[0].name",is(attndStateStu1.getName())))
                .andExpect(jsonPath("$.data.attnds[0].stu_id",is(attndStateStu1.getStu_id())))
                .andExpect(jsonPath("$.data.attnds[0].attnd_status",is(attndStateStu1.getAttnd_status())))
                .andExpect(jsonPath("$.data.attnds[0].distance",is(attndStateStu1.getDistance())))

                .andExpect(jsonPath("$.data.attnds[1].openid",is(attndStateStu2.getOpenid())))
                .andExpect(jsonPath("$.data.attnds[1].name",is(attndStateStu2.getName())))
                .andExpect(jsonPath("$.data.attnds[1].stu_id",is(attndStateStu2.getStu_id())))
                .andExpect(jsonPath("$.data.attnds[1].attnd_status",is(attndStateStu2.getAttnd_status())))
                .andExpect(jsonPath("$.data.attnds[1].distance",is(attndStateStu2.getDistance())))
                .andReturn();


        //teacher add attnd A type
        //with user 郭思文
        //with groupname 计科151
        //2018/5/23 21:45:15
        long attndTimeA1 = Long.valueOf("1527083115000");
        Attnd attndA1 = new Attnd(0,"大学英语3",attndTimeA1,10,new Location(23.405,174.402,30.0),"理南315","计科151","郭思文",1,"x");
        String attndAJson1 = mapper.writeValueAsString(attndA1);
        r = mvc.perform(MockMvcRequestBuilders.post("/attnd")
                .contentType(MediaType.APPLICATION_JSON)
                .content(attndAJson1)
                .session(s_tea1)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(jsonPath("$.data.cipher",startsWith(String.valueOf((Code.CIPHER_ATTND)))))
                .andExpect(jsonPath("$.data.attnd_id", Matchers.isA(Integer.TYPE)))
                .andReturn();

        //type A
        String cipher_A1 = ((String) readRespWithKey(r, "$.data.cipher"));
        attndA1.setCipher(cipher_A1);

        //student signin
        //sign in at 10 minutes later after add attnd
        //beyond the addr >556M
        testTimestamp = attndTimeA1+5*60*1000;
        mvc.perform(MockMvcRequestBuilders.post("/attnd/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"cipher\":\"%s\",\"location\":{\"latitude\":23.41,\"longitude\":174.402,\"accuracy\":30.0}}",cipher_A1))
                .session(s_stu1)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(jsonPath("$.data",is(Code.SIGNIN_LOCATION_BEYOND)));

        attndStateStu1 = new AttndState(stu1_Oid,stu1Name,"",Code.SIGNIN_LOCATION_BEYOND,555.9478812801552);
        attndStateStu2 = new AttndState(stu2_Oid,stu2Name,stu2_fillid,Code.SIGNIN_NOT_EXIST,-1);

        //teacher chk situation attnd A
        mvc.perform(MockMvcRequestBuilders.get("/attnd/situation")
                .param("cipher",cipher_A1)
                .param("page","1")
                .param("page_size","10")
                .param("fail_only","false")
                .session(s_tea1)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                //应到两人
                .andExpect(jsonPath("$.data.count",is(2)))
                .andExpect(jsonPath("$.data.attnds[0].openid",is(attndStateStu1.getOpenid())))
                .andExpect(jsonPath("$.data.attnds[0].name",is(attndStateStu1.getName())))
                .andExpect(jsonPath("$.data.attnds[0].stu_id",is(attndStateStu1.getStu_id())))
                .andExpect(jsonPath("$.data.attnds[0].attnd_status",is(attndStateStu1.getAttnd_status())))
                .andExpect(jsonPath("$.data.attnds[0].distance",is(attndStateStu1.getDistance())))

                .andExpect(jsonPath("$.data.attnds[1].openid",is(attndStateStu2.getOpenid())))
                .andExpect(jsonPath("$.data.attnds[1].name",is(attndStateStu2.getName())))
                .andExpect(jsonPath("$.data.attnds[1].stu_id",is(attndStateStu2.getStu_id())))
                .andExpect(jsonPath("$.data.attnds[1].attnd_status",is(attndStateStu2.getAttnd_status())))
                .andExpect(jsonPath("$.data.attnds[1].distance",is(attndStateStu2.getDistance())))
                .andReturn();

        //student2 signin
        //sign in at 3 minutes later after add attnd
        testTimestamp = attndTimeA1+3*60*1000;
        mvc.perform(MockMvcRequestBuilders.post("/attnd/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"cipher\":\"%s\",\"location\":{\"latitude\":23.405,\"longitude\":174.4025,\"accuracy\":30.0}}",cipher_A1))
                .session(s_stu2)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(jsonPath("$.data",is(Code.SIGNIN_OK)));


        attndStateStu2 = new AttndState(stu2_Oid,stu2Name,stu2_fillid,Code.SIGNIN_OK,51.02047675350656);
        //teacher chk situation attnd A
        mvc.perform(MockMvcRequestBuilders.get("/attnd/situation")
                .param("cipher",cipher_A1)
                .param("page","1")
                .param("page_size","10")
                .param("fail_only","false")
                .session(s_tea1)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(jsonPath("$.data.count",is(2)))
                .andExpect(jsonPath("$.data.attnds[0].openid",is(attndStateStu1.getOpenid())))
                .andExpect(jsonPath("$.data.attnds[0].name",is(attndStateStu1.getName())))
                .andExpect(jsonPath("$.data.attnds[0].stu_id",is(attndStateStu1.getStu_id())))
                .andExpect(jsonPath("$.data.attnds[0].attnd_status",is(attndStateStu1.getAttnd_status())))
                .andExpect(jsonPath("$.data.attnds[0].distance",is(attndStateStu1.getDistance())))

                .andExpect(jsonPath("$.data.attnds[1].openid",is(attndStateStu2.getOpenid())))
                .andExpect(jsonPath("$.data.attnds[1].name",is(attndStateStu2.getName())))
                .andExpect(jsonPath("$.data.attnds[1].stu_id",is(attndStateStu2.getStu_id())))
                .andExpect(jsonPath("$.data.attnds[1].attnd_status",is(attndStateStu2.getAttnd_status())))
                .andExpect(jsonPath("$.data.attnds[1].distance",is(attndStateStu2.getDistance())))
                .andReturn();

        //teacher chk situation attnd A
        //fail only
        //page_size 1
        mvc.perform(MockMvcRequestBuilders.get("/attnd/situation")
                .param("cipher",cipher_A1)
                .param("page","1")
                .param("page_size","1")
                .param("fail_only","true")
                .session(s_tea1)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(jsonPath("$.data.count",is(1)))
                .andExpect(jsonPath("$.data.attnds[0].openid",is(attndStateStu1.getOpenid())))
                .andExpect(jsonPath("$.data.attnds[0].name",is(attndStateStu1.getName())))
                .andExpect(jsonPath("$.data.attnds[0].stu_id",is(attndStateStu1.getStu_id())))
                .andExpect(jsonPath("$.data.attnds[0].attnd_status",is(attndStateStu1.getAttnd_status())))
                .andExpect(jsonPath("$.data.attnds[0].distance",is(attndStateStu1.getDistance())))
                .andReturn();


        //teacher add attnd N type
        //with user 郭思文
        //with no groupname
        //2018/5/23 21:45:15
        long attndTimeN1 = Long.valueOf("1527083115000");
        Attnd attndN1 = new Attnd(0,"高等数学1",attndTimeN1,10,new Location(23.405,174.402,30.0),"文清504","","郭思文",1,"x");
        String attndNJson1 = mapper.writeValueAsString(attndN1);
        r = mvc.perform(MockMvcRequestBuilders.post("/attnd")
                .contentType(MediaType.APPLICATION_JSON)
                .content(attndNJson1)
                .session(s_tea1)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(jsonPath("$.data.cipher",startsWith(String.valueOf((Code.CIPHER_NOGROUP)))))
                .andExpect(jsonPath("$.data.attnd_id", Matchers.isA(Integer.TYPE)))
                .andReturn();

        //type N
        String cipher_N1 = ((String) readRespWithKey(r, "$.data.cipher"));
        attndN1.setCipher(cipher_N1);


        //student signin
        //sign in at 5 minutes later after add attnd
        attndStateStu1 = new AttndState(stu1_Oid,stu1Name,"",Code.SIGNIN_OK,66.71453);
        testTimestamp = attndTimeN1+5*60*1000;
        mvc.perform(MockMvcRequestBuilders.post("/attnd/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"cipher\":\"%s\",\"location\":{\"latitude\":23.4056,\"longitude\":174.402,\"accuracy\":30.0}}",cipher_N1))
                .session(s_stu1)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(jsonPath("$.data",is(Code.SIGNIN_OK)));

        //student signin
        //sign in at 12 minutes later after add attnd
        attndStateStu2 = new AttndState(stu2_Oid,stu2Name,stu2_fillid,Code.SIGNIN_EXPIRED,77.8315);
        testTimestamp = attndTimeN1+12*60*1000;
        mvc.perform(MockMvcRequestBuilders.post("/attnd/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"cipher\":\"%s\",\"location\":{\"latitude\":23.4057,\"longitude\":174.402,\"accuracy\":30.0}}",cipher_N1))
                .session(s_stu2)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(jsonPath("$.data",is(Code.SIGNIN_EXPIRED)));


        //teacher chk situation attnd N
        mvc.perform(MockMvcRequestBuilders.get("/attnd/situation")
                .param("cipher",cipher_N1)
                .param("page","1")
                .param("page_size","10")
                .param("fail_only","false")
                .session(s_tea1)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(jsonPath("$.data.count",is(2)))
                .andExpect(jsonPath("$.data.attnds[0].openid",is(attndStateStu1.getOpenid())))
                .andExpect(jsonPath("$.data.attnds[0].name",is(attndStateStu1.getName())))
                .andExpect(jsonPath("$.data.attnds[0].stu_id",is(attndStateStu1.getStu_id())))
                .andExpect(jsonPath("$.data.attnds[0].attnd_status",is(attndStateStu1.getAttnd_status())))
                .andExpect(jsonPath("$.data.attnds[0].distance",is(attndStateStu1.getDistance())))

                .andExpect(jsonPath("$.data.attnds[1].openid",is(attndStateStu2.getOpenid())))
                .andExpect(jsonPath("$.data.attnds[1].name",is(attndStateStu2.getName())))
                .andExpect(jsonPath("$.data.attnds[1].stu_id",is(attndStateStu2.getStu_id())))
                .andExpect(jsonPath("$.data.attnds[1].attnd_status",is(attndStateStu2.getAttnd_status())))
                .andExpect(jsonPath("$.data.attnds[1].distance",is(attndStateStu2.getDistance())))
                .andReturn();
    }

}
