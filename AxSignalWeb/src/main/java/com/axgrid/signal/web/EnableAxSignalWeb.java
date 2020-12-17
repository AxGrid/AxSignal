package com.axgrid.signal.web;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({AxSignalWebConfiguration.class})
public @interface EnableAxSignalWeb {
}
