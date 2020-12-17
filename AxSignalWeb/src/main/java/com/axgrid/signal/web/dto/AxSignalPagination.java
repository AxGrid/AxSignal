package com.axgrid.signal.web.dto;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;


@Data
public class AxSignalPagination implements Serializable {
    int page;
    int perPage;

    final static ObjectMapper objectMapper = new ObjectMapper().configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);

    public Pageable toPageable() {
        return PageRequest.of(page-1,perPage);
    }

    public Pageable toPageable(AxSignalSort sort) {
        return PageRequest.of(page-1, perPage, sort.toSort());
    }

    public static AxSignalPagination from(String data) {
        if (data == null) return null;
        try {
            return objectMapper.readValue(data, AxSignalPagination.class);
        }catch (JsonProcessingException ignore) {
            return null;
        }
    }
}
