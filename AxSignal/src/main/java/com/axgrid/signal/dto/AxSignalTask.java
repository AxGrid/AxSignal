package com.axgrid.signal.dto;

import com.axgrid.signal.exception.AxSignalJsonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AxSignalTask {
    String trx;
    String channel;
    Date createTime;
    Date doneTime;
    AxSignalStatus status = AxSignalStatus.InQueue;
    String data;

    final static ObjectMapper om = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    public void setMessage(Object message) {
        if (message == null) {
            data = null;
            return;
        }
        try {
            this.data = om.writeValueAsString(message);
        }catch (JsonProcessingException e) {
            if (log.isWarnEnabled()) log.warn("Json processing exception {}", message, e);
        }
    }

    public <T> T getMessage(Class<T> cls) throws AxSignalJsonException {
        if (data == null) return null;
        try {
            T res = om.readValue(data, cls);
            return res;
        } catch (JsonProcessingException e) {
            if (log.isWarnEnabled()) log.warn("Json processing exception {}", this.data, e);
            throw new AxSignalJsonException(e);
        }
    }

    public static class Mapper implements RowMapper<AxSignalTask> {

        @Override
        public AxSignalTask mapRow(ResultSet resultSet, int i) throws SQLException {
            return new AxSignalTask(
                    resultSet.getString("trx"),
                    resultSet.getString("channel"),
                    resultSet.getTime("create_time"),
                    resultSet.getTime("done_time"),
                    AxSignalStatus.fromValue(resultSet.getInt("status")),
                    resultSet.getString("data")
            );
        }
    }

}
