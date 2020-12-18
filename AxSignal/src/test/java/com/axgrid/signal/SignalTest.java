package com.axgrid.signal;

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

import java.util.Date;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest()
@Import(TestApplication.class)
@ActiveProfiles("test")
@TestPropertySource(properties = "debug=true")
@Slf4j
public class SignalTest {

    @Autowired
    TestSignalService testSignalService;


    @Test
    public void send() throws Exception {
        testSignalService.clear();
        testSignalService.add(new TestSignal(
                UUID.randomUUID().toString(),
                new Date().getTime(),
                "test",
                5
        ));
        TestSignal t = testSignalService.peek();
        Assert.assertNotNull(t);
        Assert.assertEquals(testSignalService.size(), 1);
        Assert.assertEquals(t.getName(), "test");
        t = testSignalService.poll();
        Assert.assertEquals(testSignalService.size(), 0);
        Assert.assertNotNull(t);
        Assert.assertEquals(t.getName(), "test");
        t = testSignalService.poll();
        Assert.assertNull(t);
    }
}
