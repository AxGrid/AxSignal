package com.axgrid.signal;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ComponentScan(basePackages = "com.axgrid.signal")
@EnableScheduling
public class AxSignalConfiguration {

}
