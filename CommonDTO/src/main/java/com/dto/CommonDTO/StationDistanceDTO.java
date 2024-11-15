package com.dto.CommonDTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StationDistanceDTO {
    private Long distanceId;
    private String sourceStationName;
    private String destinationStationName;
    private Double distance;
}
