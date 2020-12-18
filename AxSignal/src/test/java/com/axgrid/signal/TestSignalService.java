package com.axgrid.signal;

import com.axgrid.signal.service.AxSignalService;
import org.springframework.stereotype.Service;

@Service
public class TestSignalService extends AxSignalService<TestSignal> {

    protected TestSignalService() {
        super("tasks", "test", false);
    }

}
