package org.example.vs_lab1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VsLab1Application {

    public static void main(String[] args) {
        SpringApplication.run(VsLab1Application.class, args);
        System.out.println("Websocket запущен на localhost 8080");
    }

}
