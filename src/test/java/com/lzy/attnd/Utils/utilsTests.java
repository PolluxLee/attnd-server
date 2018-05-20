package com.lzy.attnd.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lzy.attnd.model.Location;
import com.lzy.attnd.utils.Utils;
import junit.runner.BaseTestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
public class utilsTests {


    @Test
    public void testA(){
        ObjectMapper mapper = new ObjectMapper();
        try {
            int[] a = mapper.readValue("[1,2,3,4,5]",int[].class);
            for (int i : a) {
                System.out.println(i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testB(){
        int a[] = new int[0];
        System.out.println(a.length);
        System.out.println(a==null);

    }

    @Test
    public void testC(){
        Location loc1 = new Location(100, 23.4,30.0);
        Location loc2 = new Location(101, 23.5,30.0);
        System.out.println(Location.calDistanceBetweenLocation(loc1, loc2));

    }

    @Test
    public void testD(){

        Assert.assertEquals(Utils.Base62LastKToLong("456=kq",3),170402);

    }

}
