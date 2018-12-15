package com.gss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author gusaishuai
 * @since 2017/4/22
 */
@SpringBootApplication
@ComponentScan({
        "com.gss"
})
public class ApplicationMain {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(ApplicationMain.class, args);
    }

}
