package com.axgrid.signal.web;

import com.axgrid.signal.EnableAxSignal;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.axgrid.signal.web")
@EnableAxSignal
public class AxSignalWebConfiguration {
}
