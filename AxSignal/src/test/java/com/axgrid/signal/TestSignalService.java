package com.axgrid.signal;

import com.axgrid.signal.dto.AxSignalStatus;
import com.axgrid.signal.service.IAxSignal;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
public class TestSignalService extends IAxSignal<TestSignal> {

    @Getter
    TestSignal lastSignal;


    @Override
    protected AxSignalStatus task(TestSignal task) {
        lastSignal = task;
        return AxSignalStatus.Done;
    }

    @Override
    public String getChannel() {
        return "my-test-channel";
    }
}
