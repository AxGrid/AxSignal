package com.axgrid.signal.service;

import com.axgrid.signal.ISignalService;
import com.axgrid.signal.dto.AxSignalQueueStatus;
import com.axgrid.signal.dto.AxSignalStatus;
import com.axgrid.signal.dto.AxSignalTask;
import com.axgrid.signal.repository.AxSignalTaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AxSignalExecutorService implements ISignalService, HealthIndicator {

    @Autowired
    AxSignalTaskRepository repository;

    final Map<String, List<IAxSignalListener>> listeners;

    boolean done = true;

    @Autowired(required = false)
    public AxSignalExecutorService(List<IAxSignalListener> listeners) {
        this.listeners = listeners.stream().collect(Collectors.groupingBy(IAxSignalListener::getChannel));
    }

    public AxSignalStatus getStatus(String channel, String trx) {
        var res = repository.getTaskStatus(channel,trx);
        if (res == null) return AxSignalStatus.NotFound;
        else return res.getStatus();
    }

    @PostConstruct
    @Scheduled(fixedDelay = 400)
    public void execute() {
        if (listeners == null) return;
        var unworkedList = repository.getAllTask();
        if (log.isTraceEnabled() && unworkedList.size() > 0) log.trace("Found {} tasks", unworkedList.size());
        boolean errors = false;
        for(AxSignalTask task : unworkedList) {
            try {
                if (log.isTraceEnabled()) log.trace("New task {}", task);
                task.setStatus(AxSignalStatus.Done);
                task.setDoneTime(new Date());
                for(var listener : listeners.get(task.getChannel())) {
                    var status = listener.executeSignal(task);
                    if (status == AxSignalStatus.Error) {
                        task.setStatus(AxSignalStatus.Error);
                        errors = true;
                    }
                    if (status == AxSignalStatus.Continue) {
                        task.setStatus(AxSignalStatus.InQueue);

                        Calendar cal = Calendar.getInstance();
                        cal.setTime(new Date());
                        cal.add(Calendar.MINUTE, 1);
                        task.setCreateTime(cal.getTime());
                        task.setDoneTime(null);
                    }
                }
            } catch (Exception e) {
                errors = true;
                task.setStatus(AxSignalStatus.Error);
                if (log.isErrorEnabled()) log.error("Error in task {}.{}", task.getChannel(), task.getTrx(), e);
            }
            repository.update(task);
        }

        if (unworkedList.size() < 500 && !errors) {
            done = true;
        }

        if (!done) this.execute();
    }

    public void add(AxSignalTask newTask) {
        repository.create(newTask);
        done = false;
    }


    public void add(String channel, Object taskObject) {
        this.add(channel, UUID.randomUUID().toString(), taskObject);
    }

    public void add(String channel, String uuid, Object taskObject) { this.add(channel, uuid, taskObject, new Date()); }

    @Override
    public void add(String channel, String uuid, Object taskObject, Date date) {
        var task = new AxSignalTask();
        task.setChannel(channel);
        task.setCreateTime(date);
        task.setTrx(uuid);
        task.setMessage(taskObject);
        this.add(task);
    }

    @Override
    public Health health() {
        var unworked = repository.getAllUnworked();
        var errors = repository.getAllErrors();
        Map<String, Object> detail = new HashMap<>();

        detail.put("queue", unworked.stream().collect(Collectors.toMap(AxSignalQueueStatus::getChannel, AxSignalQueueStatus::getCount)));
        detail.put("errors", errors.stream().collect(Collectors.toMap(AxSignalQueueStatus::getChannel, AxSignalQueueStatus::getCount)));
        if (unworked.stream().anyMatch(item -> item.getCount() > 50)) {
            return Health.down()
                    .withDetails(detail)
                    .build();
        }

        if (errors.stream().anyMatch(item -> item.getCount() > 10)) {
            return Health.down()
                    .withDetails(detail)
                    .build();
        }

        return Health.up().withDetails(detail).build();
    }
}
