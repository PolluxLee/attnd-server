package com.lzy.attnd.controller;

import com.lzy.attnd.configure.ConfigBean;
import com.lzy.attnd.constant.Code;
import com.lzy.attnd.utils.Session;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@WebAppConfiguration
@TestPropertySource(locations="classpath:application-test.properties")
public class loggerControllerTests {
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

    @Test
    @Transactional
    public void loggerNormal()throws Exception{
        mvc.perform(MockMvcRequestBuilders.post("/fe/log")
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
                .content("[\n" +
                        "    {\n" +
                        "        \"level\": \"info\",\n" +
                        "        \"msg\": \"1000\",\n" +
                        "        \"loc\": \"pagination/userinfo\",\n" +
                        "        \"oper\": \"fill in userinfo\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"level\": \"error\",\n" +
                        "        \"msg\": \"1003\",\n" +
                        "        \"loc\": \"attnd/signin\",\n" +
                        "        \"oper\": \"student_signin\"\n" +
                        "    }\n" +
                        "]")
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)));
    }

    @Test
    //@Transactional
    public void loggerNormal3()throws Exception{
        mvc.perform(MockMvcRequestBuilders.post("/fe/log")
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
                .content("[\n" +
                        "    {\n" +
                        "        \"level\": \"info\",\n" +
                        "        \"msg\": \"1000\",\n" +
                        "        \"loc\": \"pagination/userinfo\",\n" +
                        "        \"oper\": \"fill in userinfo\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"level\": \"error\",\n" +
                        "        \"msg\": \"1003\",\n" +
                        "        \"loc\": \"attnd/signin\",\n" +
                        "        \"oper\": \"student_signin\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"level\": \"error\",\n" +
                        "        \"msg\": \"1003\",\n" +
                        "        \"loc\": \"attnd/signin\",\n" +
                        "        \"oper\": \"student_signin\"\n" +
                        "    }\n" +
                        "]")
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)));
    }

    @Test
    //@Transactional
    public void loggerNormalT()throws Exception{
        mvc.perform(MockMvcRequestBuilders.post("/fe/log")
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
                .content("[{\n" +
                        "\t\"level\": \"info\",\n" +
                        "\t\"msg\": \"1000\",\n" +
                        "\t\"loc\": \"utils/zmid.js function zmid/getFreshSelfinfo()\",\n" +
                        "\t\"time\": 1528182823676\n" +
                        "}, {\n" +
                        "\t\"level\": \"info\",\n" +
                        "\t\"msg\": \"1000\",\n" +
                        "\t\"loc\": \"pages/mine function getAttndList()\",\n" +
                        "\t\"oper\": \"获取考勤列表\",\n" +
                        "\t\"time\": 1528182823909\n" +
                        "}, {\n" +
                        "\t\"level\": \"info\",\n" +
                        "\t\"msg\": \"{\\\"res\\\":{\\\"latitude\\\":39.92,\\\"longitude\\\":116.46,\\\"speed\\\":-1,\\\"accuracy\\\":30,\\\"altitude\\\":0,\\\"verticalAccuracy\\\":65,\\\"horizontalAccuracy\\\":65,\\\"errMsg\\\":\\\"getLocation:ok\\\"}}\",\n" +
                        "\t\"loc\": \"pages/mine function getAddrName()\",\n" +
                        "\t\"oper\": \"获取经纬度\",\n" +
                        "\t\"time\": 1528182824219\n" +
                        "}, {\n" +
                        "\t\"level\": \"info\",\n" +
                        "\t\"msg\": \"1000\",\n" +
                        "\t\"loc\": \"pages/mine function getAttndList()\",\n" +
                        "\t\"oper\": \"获取考勤列表\",\n" +
                        "\t\"time\": 1528182825292\n" +
                        "}, {\n" +
                        "\t\"level\": \"info\",\n" +
                        "\t\"msg\": \"1000\",\n" +
                        "\t\"loc\": \"pages/mine function getAttndList()\",\n" +
                        "\t\"oper\": \"获取考勤列表\",\n" +
                        "\t\"time\": 1528182826300\n" +
                        "}, {\n" +
                        "\t\"level\": \"info\",\n" +
                        "\t\"msg\": \"1000\",\n" +
                        "\t\"loc\": \"utils/zmid.js function zmid/getFreshSelfinfo()\",\n" +
                        "\t\"time\": 1528182828410\n" +
                        "}, {\n" +
                        "\t\"level\": \"info\",\n" +
                        "\t\"msg\": \"1000\",\n" +
                        "\t\"loc\": \"utils/zmid.js function zmid/getFreshSelfinfo()\",\n" +
                        "\t\"time\": 1528182831208\n" +
                        "}]")
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)));
    }

    @Test
    @Transactional
    public void loggerNormal1()throws Exception{
        mvc.perform(MockMvcRequestBuilders.post("/fe/log")
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
                .content("[\n" +
                        "    {\n" +
                        "        \"level\": \"info\",\n" +
                        "        \"msg\": \"1000\",\n" +
                        "        \"loc\": \"pagination/userinfo\",\n" +
                        "        \"oper\": \"fill in userinfo\"\n" +
                        "    }\n" +
                        "]")
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)));
    }

    @Test
    @Transactional
    public void loggerOnlyInneed()throws Exception{
        mvc.perform(MockMvcRequestBuilders.post("/fe/log")
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
                .content("[\n" +
                        "    {\n" +
                        "        \"level\": \"info\",\n" +
                        "        \"msg\": \"1000\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"level\": \"error\",\n" +
                        "        \"msg\": \"1003\"\n" +
                        "    }\n" +
                        "]")
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_SUCCESS)));
    }

    @Test
    @Transactional
    public void loggerLEVEL_EMPTY()throws Exception{
        mvc.perform(MockMvcRequestBuilders.post("/fe/log")
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
                .content("[\n" +
                        "    {\n" +
                        "        \"level\": \"\",\n" +
                        "        \"msg\": \"\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"level\": \"error\",\n" +
                        "        \"msg\": \"1003\"\n" +
                        "    }\n" +
                        "]")
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_PARAM_INVALID)));
    }

    @Test
    @Transactional
    public void loggerLEVEL_TOOLONG()throws Exception{
        mvc.perform(MockMvcRequestBuilders.post("/fe/log")
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
                .content("[\n" +
                        "    {\n" +
                        "        \"level\": \"1111111110111111111011111111101111111110111111111011111111101111111110\",\n" +
                        "        \"msg\": \"OK\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"level\": \"error\",\n" +
                        "        \"msg\": \"1003\"\n" +
                        "    }\n" +
                        "]")
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code",is(Code.GLOBAL_PARAM_INVALID)));
    }
}
