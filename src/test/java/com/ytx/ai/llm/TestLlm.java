package com.ytx.ai.llm;


import com.ytx.ai.Application;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@Slf4j
public class TestLlm {


    @Autowired
    private ChatClient.Builder builder;

    @Test
    public void test_llm_chat(){
        OpenAiChatOptions options= OpenAiChatOptions.builder().model("gpt-5").temperature(1D).build();

        ChatClient client = builder.defaultOptions(options).build();
        client.prompt().messages();

        String content = client.prompt().user("你好").call().content();
        log.info("llm response: {}", content);
    }


}
