package com.axgrid.signal;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({AxSignalConfiguration.class})
public @interface EnableAxSignal {

}
