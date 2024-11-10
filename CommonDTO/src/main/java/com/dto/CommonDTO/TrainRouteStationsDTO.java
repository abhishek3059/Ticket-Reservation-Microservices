package com.dto.CommonDTO;

import lombok.*;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TrainRouteStationsDTO {

    private String  trainNumber;
    private Long locationId;
    private Integer stationOrder;
    private Integer stoppage;
    private String stationName;


}
