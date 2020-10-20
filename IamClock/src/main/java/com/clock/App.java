package com.clock;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

/**
 * Hello world!
 *
 */
@SpringBootApplication(scanBasePackages = {"com.clock"})
@RestController
@MapperScan("com.clock.dao")
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello IamClock!" );
        SpringApplication.run(App.class, args);
    }
}
