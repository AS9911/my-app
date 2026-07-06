package com.akiyama.backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@MapperScan("com.akiyama.backend.domain.mapper")
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);


        BCryptPasswordEncoder encoder =
                new BCryptPasswordEncoder();

        System.out.println(
                encoder.encode("password123")
        );
	}

}
