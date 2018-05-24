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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@WebAppConfiguration
@TestPropertySource(locations="classpath:application-test.properties")
public class UserGroupControllerTests {

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

    @Test
    public void groupName()throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/group/name")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_SUCCESS)));
    }

    @Test
    public void groupName_nodata()throws Exception{
        session.setAttribute(configBean.getSession_key(),new Session(2,"lzp",0,"toid456","wxsessionkey","24"));
        mvc.perform(MockMvcRequestBuilders.get("/group/name")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_SUCCESS)));
    }

    /**-------------------group list---------------*/
    @Test
    public void groupList()throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/group/list")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_SUCCESS)));
    }

    /**-------------------group info ----------------*/
    @Test
    public void group_info()throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/group")
                .param("group_id","1")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_SUCCESS)));
    }

    @Test
    public void group_info_not_exist()throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/group")
                .param("group_id","3543")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GROUP_NOTEXIST)));
    }

    /**---------------------group user list-----------*/
    @Test
    public void group_UserList()throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/group/userlist")
                .param("group_id","1")
                .param("page","1")
                .param("page_size","1")
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_SUCCESS)));
    }

    /**---------------------/group/del--------------------------*/

    @Test
    @Transactional
    public void delGroup()throws Exception{
        mvc.perform(MockMvcRequestBuilders.post("/group/del")
                .content("group_id=1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_SUCCESS)));

    }

    /**---------------------/group/user/add */
    @Test
    @Transactional
    public void addUserGroup()throws Exception{
        session.setAttribute(configBean.getSession_key(),new Session(1,"lzy",0,"toid123","wxsessionkey","23"));
        mvc.perform(MockMvcRequestBuilders.post("/group/user/add")
                .content("group_id=1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code",is(Code.GLOBAL_SUCCESS)))
                .andExpect(jsonPath("$.data",startsWith(String.valueOf((Code.CIPHER_SINGLE)))));

    }
}
