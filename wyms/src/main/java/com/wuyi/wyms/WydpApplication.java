package com.wuyi.wyms;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.wuyi.wydp.mapper")
@SpringBootApplication
@EnableScheduling
public class WydpApplication {

    public static void main(String[] args) {
        SpringApplication.run(WydpApplication.class, args);
    }

}
