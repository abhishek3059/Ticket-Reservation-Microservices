package com.dto.CommonDTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StationDistanceDTO {
    private Long distanceId;
    private String sourceStationCode;
    private String destinationStationCode;
    private Double distance;
}
