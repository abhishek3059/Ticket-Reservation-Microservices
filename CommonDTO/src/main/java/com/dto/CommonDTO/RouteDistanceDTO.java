package com.dto.CommonDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RouteDistanceDTO {
    private String stationName;
    private Double distanceFromStart;

}
