package com.example.vchtcollector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;

//@SpringBootApplication
//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
@SpringBootApplication()
@EnableScheduling
@EnableAsync
public class VchtCollectorApplication {

	public static void main(String[] args) {
		SpringApplication.run(VchtCollectorApplication.class, args);
	}

}
