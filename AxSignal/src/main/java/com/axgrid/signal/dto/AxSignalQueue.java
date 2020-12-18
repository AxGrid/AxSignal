package com.axgrid.signal.dto;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

public class AxSignalQueue<T extends AxSignal> extends ArrayList<T> implements Queue<T> {

    @Override
    public boolean add(T t) {
        var res = super.add(t);
        super.sort(Comparator.comparingLong(AxSignal::getTime));
        return res;
    }

    @Override
    public boolean offer(T t) {
        return this.add(t);
    }

    @Override
    public T remove() {
        return this.poll();
    }

    @Override
    public T poll() {
        T el = this.get(0);
        this.remove(el);
        return el;
    }

    @Override
    public T element() {
        return this.peek();
    }

    @Override
    public T peek() {
        return this.get(0);
    }

    public T poolTime() {
        T el = this.peek();
        if (el != null && el.getTime() <= new Date().getTime())
            return this.poll();
        return null;
    }

    public T peekTime() {
        var date = new Date().getTime();
        T el = this.peek();
        if (el != null && el.getTime() <= date)
            return el;
        return null;
    }

    public int sizeTime() {
        long time = new Date().getTime();
        return (int)this.stream().filter(item -> item.getTime() <= time).count();
    }


    public List<T> getAllTime() {
        long time = new Date().getTime();
        return this.stream().filter(item -> item.getTime() <= time).collect(Collectors.toList());
    }


    @Override
    public void add(int index, T element) {
        this.add(element);

    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        c.forEach(this::add);
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        return this.addAll(c);
    }
}
