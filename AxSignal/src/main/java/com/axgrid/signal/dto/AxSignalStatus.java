package com.axgrid.signal.dto;

public enum AxSignalStatus {
    NotFound(0),
    InQueue(1),
    Continue(2),
    Done(10),
    Error(500);

    final int status;

    public int getValue() {return status;}
    public static AxSignalStatus fromValue(int status) {
        switch (status) {
            default:
                return NotFound;
            case 1:
                return InQueue;
            case 10:
                return Done;
            case 500:
                return Error;
        }
    }

    AxSignalStatus(final int status) {
        this.status = status;
    }
}
