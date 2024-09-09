package com.cleanersbooking.service.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.LocalTime;

@Configuration
@Getter
public class AppConfig {


    @Value("${cleaner.working-start-time}")
    private LocalTime workingStartTime;

    @Value("${cleaner.working-end-time}")
    private LocalTime workingEndTime;

}
