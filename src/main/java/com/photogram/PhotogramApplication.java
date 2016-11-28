package com.photogram;

import com.photogram.Storage.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class PhotogramApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhotogramApplication.class, args);

        //infoside
        System.out.println("_______PHOTOGRAM_______");
        System.out.println("Gå til http://localhost:8080 for å se Photogram");
        System.out.println("_______________________");

    }

    @Bean
    CommandLineRunner init(StorageService storageService) {
        return (args) -> {
        };
    }

}