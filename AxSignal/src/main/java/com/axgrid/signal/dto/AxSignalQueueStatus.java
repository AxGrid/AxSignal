package com.axgrid.signal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AxSignalQueueStatus {

    String channel;
    int count;

    public static class Mapper implements RowMapper<AxSignalQueueStatus> {

        @Override
        public AxSignalQueueStatus mapRow(ResultSet resultSet, int i) throws SQLException {
            return new AxSignalQueueStatus(
                    resultSet.getString(1),
                    resultSet.getInt(2)
            );
        }
    }

}
