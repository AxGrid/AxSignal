package com.axgrid.signal;

import com.axgrid.signal.dto.AxSignalTask;
import com.axgrid.signal.service.AxSignalExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest()
@Import(TestApplication.class)
@ActiveProfiles("test")
@TestPropertySource(properties = "debug=true")
@Slf4j
public class SignalTest {

    @Autowired
    TestSignalService signalService;

    @Autowired
    AxSignalExecutorService executorService;

    @Test
    public void send() throws Exception {
        executorService.add("my-test-channel", new TestSignal("test-name", 5));
        Thread.sleep(1100);
        Assert.assertNotNull(signalService.getLastSignal());
        Assert.assertEquals(signalService.getLastSignal().getName(), "test-name");
        Assert.assertEquals(signalService.getLastSignal().getId(), 5);
    }
}
