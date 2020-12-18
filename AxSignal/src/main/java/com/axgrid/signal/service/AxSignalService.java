package com.axgrid.signal.service;

import com.axgrid.signal.dto.AxSignal;
import com.axgrid.signal.dto.AxSignalQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public abstract class AxSignalService<T extends AxSignal> implements Queue<T>, HealthIndicator {

    final AxSignalQueue<T> queue = new AxSignalQueue<>();
    final Class<T> persistentClass;
    final AxSignalStoreBackend<T> storage;

    @Value("${ax.signal.alarm:100}")
    int alarmSize;

    public int size() { return queue.size(); }

    public List<AxSignal> getAllSignals() {
        return new ArrayList<>(queue);
    }

    public void process(T t) {
        storage.process(t);
    }

    public void done(T t) {
        storage.done(t);
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return queue.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return queue.getAllTime().iterator();
    }

    @Override
    public Object[] toArray() {
        return queue.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return queue.toArray(a);
    }

    @Override
    public boolean add(T t) {
        storage.add(t);
        return queue.add(t);
    }

    @Override
    public boolean offer(T t) {
        return queue.offer(t);
    }

    @Override
    public T remove() {
        return this.poll();
    }

    @Override
    public T poll() {
        T item = queue.pollTime();
        if (item != null)
            storage.done(item);
        return item;
    }

    @Override
    public T element() {
        return peek();
    }

    @Override
    public T peek() {
        T item = queue.peekTime();
        if (item != null)
            storage.process(item);
        return item;
    }

    @Override
    public boolean remove(Object o) {
        if (o != null)
            storage.remove((T)o);
        return queue.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return queue.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        c.forEach(this::add);
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return !c.stream().noneMatch(this::remove);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return queue.retainAll(c);
    }

    @Override
    public void clear() {
        queue.clear();
        storage.clear();
    }

    private void load(boolean loadProcess) {
        if (loadProcess)
            this.queue.addAll(Stream.concat(storage.getAllNewStoredSignals().stream(), storage.getAllProcessStoredSignals().stream())
                    .sorted(Comparator.comparingLong(AxSignal::getTime)).collect(Collectors.toList()));
        else
            this.queue.addAll(storage.getAllNewStoredSignals());
    }

    @SuppressWarnings("unchecked")
    protected AxSignalService(AxSignalStoreBackend<T> storage, boolean loadProcess) {
        this.persistentClass = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
        log.debug("persistentClass {}", persistentClass);
        this.storage = storage;
        load(loadProcess);
    }

    @SuppressWarnings("unchecked")
    protected AxSignalService(String basePath, String channel, boolean loadProcess) {
        this.persistentClass = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
        this.storage = new AxSignalFileStoreBackend<T>(basePath, channel, persistentClass);
        load(loadProcess);
    }

    @Override
    public Health health() {
        if (this.queue.sizeTime() > alarmSize) {
            return Health.down()
                    .withDetail("Queue", this.size())
                    .build();
        }
        return Health.up().build();
    }
}
