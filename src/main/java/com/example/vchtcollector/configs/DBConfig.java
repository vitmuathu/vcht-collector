package com.example.vchtcollector.configs;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class DBConfig {

    @Value("db.evtp.username")
    private String evtpUsername;

    @Value("db.evtp.password")
    private String evtpPassword;

    @Value("db.evtp.url")
    private String evtpUrl;


}
