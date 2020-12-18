package com.axgrid.signal.dto;

import java.io.Serializable;

public interface AxSignal extends Serializable {
    long getTime();
    String getUuid();
}
