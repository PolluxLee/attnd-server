package com.lzy.attnd.repository;


import com.lzy.attnd.model.User;
import com.lzy.attnd.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static org.hamcrest.Matchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRepositoryTests {

    @Autowired
    UserService userService;

    //adduser

    @Test
    public void openidnull(){
        boolean success = false;
        try {
            User user = new User(1,"testlzy",null,null,0,"23");
            success = userService.InsOrUpdUserInfo(user);
        } catch (ConstraintViolationException e) {
            e.printStackTrace();
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
        boolean success = userService.InsOrUpdUserInfo(new User(1,"testlzy","testoid",new testc(),0,"23"));
        Assert.assertTrue(success);
    }

    //UpdUserNameByOpenid

    @Test
    public void updopenidnull()throws Exception{
        try {
            userService.UpdUserInfoByOpenid(null);
        } catch (ConstraintViolationException e) {
            e.printStackTrace();
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
            e.printStackTrace();
            return;
        }
        throw new Exception();
    }

    @Test
    @Transactional
    public void normalupd(){
        User u = new User(1,"testlzy","testoid",new testc(),0,"23");
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
        User u = new User(1,"testlzy","ac",new testc(),0,"23");
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
            e.printStackTrace();
            return;
        }
        throw new Exception();
    }

}
