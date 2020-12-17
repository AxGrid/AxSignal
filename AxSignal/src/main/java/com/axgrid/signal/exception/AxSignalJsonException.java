package com.axgrid.signal.exception;

public class AxSignalJsonException extends AxSignalException {
    public AxSignalJsonException() {super("Json processing exception");}
    public AxSignalJsonException(Throwable t) {super("Json processing exception", t);}
}
