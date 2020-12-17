package com.axgrid.signal.web.dto;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.data.domain.Sort;

import java.io.Serializable;

@Data
public class AxSignalSort implements Serializable {

    String field;
    Sort.Direction order = Sort.Direction.ASC;

    final static ObjectMapper objectMapper = new ObjectMapper().configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);

    public Sort toSort() {
        return Sort.by(order, field);
    }

    public static AxSignalSort from(String data) {
        if (data == null) return null;
        try {
            return objectMapper.readValue(data, AxSignalSort.class);
        }catch (JsonProcessingException ignore) {
            return null;
        }
    }
}
