package com.example.sweater;

import com.example.sweater.domain.Role;
import com.example.sweater.domain.User;
import com.example.sweater.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.Collections;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

//    @Qualifier("ds")
//    @Bean
//    public DataSource dataSource() {
//        DriverManagerDataSource ds = new DriverManagerDataSource();
//        ds.setDriverClassName("com.mysql.jdbc.Driver");
//        ds.setUrl("jdbc:mysql://localhost:3306/db_letscode?useSSL=false");
//        ds.setUsername("springuser");
//        ds.setPassword("password");
//        return ds;
//    }

//    @Bean
//    public CommandLineRunner load(UserRepository userRepository) {
//        return (args)->{
//            userRepository.save(new User("user", "user", Collections.singleton(Role.USER)));
//        };
//    }
}
