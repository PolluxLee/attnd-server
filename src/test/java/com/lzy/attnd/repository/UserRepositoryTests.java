package com.lzy.attnd.repository;


import com.lzy.attnd.model.AttndState;
import com.lzy.attnd.model.User;
import com.lzy.attnd.service.SignInService;
import com.lzy.attnd.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;

import static org.hamcrest.Matchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
public class UserRepositoryTests {

    @Autowired
    UserService userService;

    @Autowired
    SignInService signInService;

    //adduser

    @Test
    public void openidnull(){
        boolean success = false;
        try {
            User user = new User(1,"testlzy",null,null,0,"23",new int[0]);
            success = userService.InsOrUpdUserInfo(user);
        } catch (ConstraintViolationException e) {
            return;
        }catch (Exception e){
            throw e;
        }
    }

    public class testc {
        public String a;
    }


    @Test
    @Transactional
    public void normal(){
        boolean success = userService.InsOrUpdUserInfo(new User(1,"testlzy","testoid",new testc(),0,"23",new int[0]));
        Assert.assertTrue(success);
    }

    //UpdUserNameByOpenid

    @Test
    public void updopenidnull()throws Exception{
        try {
            userService.UpdUserInfoByOpenid(null);
        } catch (ConstraintViolationException e) {
            return;
        }
        throw new Exception();

    }

    @Test
    public void updEmpty()throws Exception{
        try {
            User user = new User();
            userService.UpdUserInfoByOpenid(user);
        } catch (ConstraintViolationException e) {
            return;
        }
        throw new Exception();
    }

    @Test
    @Transactional
    public void normalupd(){
        User u = new User(1,"testlzy","testoid",new testc(),0,"23",new int[0]);
        boolean success = userService.InsOrUpdUserInfo(u);
        Assert.assertTrue(success);

        u.setName("JAA");
        u.setStu_id("111");
        success = userService.UpdUserInfoByOpenid(u);
        Assert.assertTrue(success);
    }


    @Test
    @Transactional
    public void normalfind(){
        User u = new User(1,"testlzy","ac",new testc(),0,"23",new int[0]);
        boolean success = userService.InsOrUpdUserInfo(u);
        Assert.assertTrue(success);

        User newUser = userService.FindUserByOpenid(u.getOpenid());
        Assert.assertThat(newUser.getName(),is("testlzy"));
        Assert.assertThat(newUser.getOpenid(),is("ac"));
    }


    @Test
    @Transactional
    public void normalfindnothing(){
        User newUser = userService.FindUserByOpenid("abcest");
        Assert.assertThat(newUser,nullValue());
    }


    @Test
    public void normalfindempty() throws Exception{
        try {
            User newUser = userService.FindUserByOpenid("");
        } catch (ConstraintViolationException e) {
            return;
        }
        throw new Exception();
    }


    /* groups adding ........................................*/
/*    @Test
    @Transactional
    public void groupsAdd_group_exist() throws Exception{
        boolean flag = userService.AddUserToGroup("toid123","计科151",1);
        Assert.assertTrue(flag);
    }*/


    @Test
    @Transactional
    public void groupsAdd_group_notexist() throws Exception{
        boolean flag = false;
        try {
            flag = userService.AddUserToGroup("toid123","151",1);
        } catch (DataAccessException e) {
            return;
        }
        throw new Exception();
    }


    @Test
    public void getSigninList() throws Exception{
        AttndState[] attndStateList = signInService.ChkSignInList("Awvq3",0,10,1,1);
        for (AttndState state : attndStateList) {
            System.out.println(state);
        }
        System.out.println(signInService.CountSignInList("Awvq3",1));
    }
}
