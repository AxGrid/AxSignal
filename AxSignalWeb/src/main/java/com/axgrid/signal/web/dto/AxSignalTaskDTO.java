package com.axgrid.signal.web.dto;

import com.axgrid.signal.dto.AxSignalTask;
import lombok.Data;

import java.io.Serializable;

@Data
public class AxSignalTaskDTO implements Serializable {
    String channel;
    String trx;
    long createTime;
    Long doneTime;
    String status;
    String data;

    public AxSignalTaskDTO(AxSignalTask task) {
        this.channel = task.getChannel();
        this.trx = task.getTrx();
        this.status = task.getStatus().toString();
        this.data = task.getData();
        this.createTime = task.getCreateTime().getTime();
    }
}
