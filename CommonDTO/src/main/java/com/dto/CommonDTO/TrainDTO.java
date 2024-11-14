package com.dto.CommonDTO;

import com.dto.CommonDTO.enums.SeatTypeDTO;
import lombok.*;

import java.util.EnumMap;
import java.util.List;
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
    private Map<Integer, String> trainRoute;
    private Set<String> runningDays;

}
