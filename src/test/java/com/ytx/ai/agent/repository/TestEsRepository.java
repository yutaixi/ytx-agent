package com.ytx.ai.agent.repository;

import com.ytx.ai.Application;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@Slf4j
public class TestEsRepository {
    @Autowired
    private Repository repository;


    @Test
    public void create_index(){
        repository.createIndex("knowledge",Knowledge.class);
    }
}
