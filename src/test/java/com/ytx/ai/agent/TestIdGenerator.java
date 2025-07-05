package com.ytx.ai.agent;

import com.ytx.ai.Application;
import com.ytx.ai.base.util.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@Slf4j
public class TestIdGenerator {


    @Test
    public void test_id_generator(){
        System.out.printf(IdGenerator.next());
    }
}
