package com.axgrid.signal;

import com.axgrid.signal.dto.AxSignalQueue;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

@Slf4j
public class QueueTest {


    @Test
    public void testSortedQueue() {
        AxSignalQueue<TestSignal> signals = new AxSignalQueue<>();
        signals.add(new TestSignal(UUID.randomUUID().toString(), new GregorianCalendar(2014, Calendar.FEBRUARY, 11).getTimeInMillis(), "old", 1 ));
        signals.add(new TestSignal(UUID.randomUUID().toString(), new GregorianCalendar(2050, Calendar.FEBRUARY, 11).getTimeInMillis(), "future", 100 ));
        signals.add(new TestSignal(UUID.randomUUID().toString(), new Date().getTime(), "now", 50 ));

        Assert.assertEquals(signals.size(), 3);
        Assert.assertEquals(signals.sizeTime(), 2);

        Assert.assertEquals(signals.peekTime().getName(), "old");
        Assert.assertEquals(signals.pollTime().getName(), "old");
        Assert.assertEquals(signals.pollTime().getName(), "now");
    }
}
