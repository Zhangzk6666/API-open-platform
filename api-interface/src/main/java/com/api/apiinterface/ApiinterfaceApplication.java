package com.api.apiinterface;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.api.apiinterface.mapper")
public class ApiinterfaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiinterfaceApplication.class, args);
    }

}
