package com.ytx.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Arrays;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);
        // 添加配置源调试
        application.addListeners(new ApplicationListener<ApplicationEnvironmentPreparedEvent>() {
            @Override
            public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
                ConfigurableEnvironment environment = event.getEnvironment();
                System.out.println("Active profiles: " + Arrays.toString(environment.getActiveProfiles()));
                System.out.println("Redis host: " + environment.getProperty("spring.redis.host"));
                System.out.println("spring.elasticsearch.rest.uris: " + environment.getProperty("spring.elasticsearch.rest.uris"));
            }
        });
        application.run(args);
    }
}
