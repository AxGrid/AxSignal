package com.axgrid.signal.repository;

import com.axgrid.signal.dto.AxSignalQueueStatus;
import com.axgrid.signal.dto.AxSignalTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Repository
public class AxSignalTaskRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public void create(AxSignalTask task) {
        jdbcTemplate.update("INSERT INTO ax_signal (`channel`,trx,create_time,done_time,`status`,data) VALUES (?,?,?,?,?,?)",
                task.getChannel(), task.getTrx(), task.getCreateTime(), task.getDoneTime(), task.getStatus().getValue(), task.getData());
    }

    public void update(AxSignalTask task) {
        jdbcTemplate.update("UPDATE ax_signal SET done_time=?, status=? WHERE `channel`=? AND trx=?",
                task.getDoneTime(), task.getStatus().getValue(), task.getChannel(), task.getTrx());
    }

    public List<AxSignalTask> getAllUnWorked() {
        return jdbcTemplate.query("SELECT * FROM ax_signal WHERE status=1 ORDER BY create_time LIMIT 500", new AxSignalTask.Mapper());
    }

    public List<AxSignalQueueStatus> getAllUnworked() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR, -1);
        Date oneHourBack = cal.getTime();
        return jdbcTemplate.query("SELECT channel, count(*) FROM ax_signal WHERE status=1 AND create_time<? GROUP BY channel;",
                new AxSignalQueueStatus.Mapper(), oneHourBack);
    }

    public List<AxSignalQueueStatus> getAllErrors() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR, -1);
        Date oneHourBack = cal.getTime();
        return jdbcTemplate.query("SELECT channel, count(*) FROM ax_signal WHERE status=500 AND create_time>? GROUP BY channel;",
                new AxSignalQueueStatus.Mapper(), oneHourBack);
    }

}


//    create_time datetime not null,
//        done_time datetime,
//        `status` int default 0 not null,
//        data longtext,
//        index ax_signal_channel(`status`, `channel`),
//        index ax_signal_clean(`status`, done_time),
//        index ax_signal_alarm(`status`, create_time),
//        primary key (`channel`, trx)
