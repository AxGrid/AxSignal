package com.axgrid.signal;

import com.axgrid.signal.dto.AxSignal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestSignal implements AxSignal {
    String uuid;
    long time;
    String name;
    int id;

}
