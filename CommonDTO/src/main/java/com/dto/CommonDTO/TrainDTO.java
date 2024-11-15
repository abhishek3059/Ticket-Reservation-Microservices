package com.dto.CommonDTO;


import lombok.*;

import java.util.Map;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class TrainDTO{
private Long id;

private String trainNumber;
    private String source;
    private String  destination;
    private String name;
    private Map<Integer, RouteDistanceDTO> trainRouteDistance;
    private Set<String> runningDays;

}
