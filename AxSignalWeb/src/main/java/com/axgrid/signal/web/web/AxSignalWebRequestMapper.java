package com.axgrid.signal.web.web;

import com.axgrid.signal.dto.AxSignalTask;
import com.axgrid.signal.repository.AxSignalTaskRepository;
import com.axgrid.signal.web.dto.AxSignalPagination;
import com.axgrid.signal.web.dto.AxSignalSort;
import com.axgrid.signal.web.dto.AxSignalTaskDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/api/signal/")
public class AxSignalWebRequestMapper {

    @Autowired
    AxSignalTaskRepository repository;

    AxSignalTaskDTO preRender(AxSignalTask task) {
        return new AxSignalTaskDTO(task);
    }

    @RequestMapping("/")
    public ResponseEntity<List<AxSignalTaskDTO>> getAll(
            @RequestParam(value = "filter", required = false) String filterString,
            @RequestParam(value = "sort", required = false) String sortString,
            @RequestParam(value = "pagination", required = false) String paginationString
    ) {

        List<AxSignalTaskDTO> data = new ArrayList<AxSignalTaskDTO>();

        AxSignalPagination pagination = AxSignalPagination.from(paginationString);
        AxSignalSort sort = AxSignalSort.from(sortString);
        HttpHeaders headers = new HttpHeaders();

        Pageable p = sort == null ? pagination.toPageable() : pagination.toPageable(sort);
        Page<AxSignalTask> page = repository.findAll(p);
        page.forEach(item -> data.add(preRender(item)));
        headers.add("Content-Total", String.format("%d", page.getTotalElements()));
        headers.add("Content-Pages", String.format("%d", page.getTotalPages()));
        headers.add("Content-Page", String.format("%d", page.getNumber()));
        headers.add("Content-PageSize", String.format("%d", page.getSize()));
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }
}
