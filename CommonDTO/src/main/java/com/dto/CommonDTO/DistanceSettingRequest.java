package com.dto.CommonDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DistanceSettingRequest {
    private String source;
    private String destination;
    private Double distance;
}
