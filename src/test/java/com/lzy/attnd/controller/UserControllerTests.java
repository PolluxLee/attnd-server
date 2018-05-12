package com.lzy.attnd.controller;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {
/*    @Autowired
    private WebApplicationContext wac;*/

    @Autowired
    private MockMvc mvc;
    private MockHttpSession session;

    /*    @Before
        public void setupMockMvc(){
            mvc = MockMvcBuilders.webAppContextSetup(wac).build(); //初始化MockMvc对象
            session = new MockHttpSession();
        }*/
    @Test
    public void addUser()throws Exception{
        mvc.perform(MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .content("code=123")
        )
                /* .andExpect(MockMvcResultMatchers.jsonPath("$.code",new Object()).value(1000056))
                 .andExpect(MockMvcResultMatchers.status().is(200))*/
                .andDo(MockMvcResultHandlers.print());
    }


    @Test
    public void addUserNULL()throws Exception{
        mvc.perform(MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .content("A=B")
        )
                /* .andExpect(MockMvcResultMatchers.jsonPath("$.code",new Object()).value(1000056))
                 .andExpect(MockMvcResultMatchers.status().is(200))*/
                .andDo(MockMvcResultHandlers.print());
    }


    //get client test
/*    @Test
    public void gettest(){
        RestTemplate rest = new RestTemplate();
        rest.getFor
    }*/

}
