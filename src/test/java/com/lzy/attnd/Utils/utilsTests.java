package com.lzy.attnd.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lzy.attnd.model.Location;
import com.lzy.attnd.utils.Utils;
import junit.runner.BaseTestRunner;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)

public class utilsTests {

    @Test
    public void test0(){
        String cipherA = Utils.CalCipher('A',62,121);

        System.out.println(cipherA);
        System.out.println(Utils.Base62LastKToLong(cipherA, cipherA.length()-3-1-Utils.ChkIDBase62Length(105,10)));

        System.out.println(Utils.LongToBase62LastK(105,10));
        System.out.println(Utils.Base62LastKToLong("1H", 2));

        System.out.println(Utils.ChkIDBase62Length(1024,10));
    }

}
