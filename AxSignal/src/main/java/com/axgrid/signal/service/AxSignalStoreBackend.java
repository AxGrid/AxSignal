package com.axgrid.signal.service;

import com.axgrid.signal.dto.AxSignal;

import java.util.Collection;

public interface AxSignalStoreBackend<T extends AxSignal> {
    void add(T task);
    void process(T task);
    void done(T task);
    void remove(T task);
    void clear();
    Collection<T> getAllNewStoredSignals();
    Collection<T> getAllProcessStoredSignals();
}
