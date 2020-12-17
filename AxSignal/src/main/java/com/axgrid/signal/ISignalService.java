package com.axgrid.signal;

import com.axgrid.signal.dto.AxSignalStatus;
import com.axgrid.signal.dto.AxSignalTask;

import java.util.Date;

public interface ISignalService {
    AxSignalStatus getStatus(String channel, String trx);
    void add(AxSignalTask newTask);
    void add(String channel, Object taskObject);
    void add(String channel, String uuid, Object taskObject);
    void add(String channel, String uuid, Object taskObject, Date date);
}

