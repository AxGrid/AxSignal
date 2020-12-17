package com.axgrid.signal.service;

import com.axgrid.signal.dto.AxSignalStatus;
import com.axgrid.signal.dto.AxSignalTask;

public interface IAxSignalListener {
    AxSignalStatus executeSignal(AxSignalTask task);
    String getChannel();
}
