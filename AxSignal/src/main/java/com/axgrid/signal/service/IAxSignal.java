package com.axgrid.signal.service;

import com.axgrid.signal.dto.AxSignalStatus;
import com.axgrid.signal.dto.AxSignalTask;
import com.axgrid.signal.exception.AxSignalJsonException;

import java.lang.reflect.ParameterizedType;

public abstract class IAxSignal<T> implements IAxSignalListener {

    protected Class<T> clazz;

    protected abstract AxSignalStatus task(T task);

    public AxSignalStatus executeSignal(AxSignalTask task) {
        try {
            return this.task(task.getMessage(clazz));
        } catch (AxSignalJsonException e) {
            e.printStackTrace();
            return AxSignalStatus.Error;
        }
    }

    @SuppressWarnings("unchecked")
    protected IAxSignal() {
        clazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
}
