package com.dto.CommonDTO;

import lombok.*;

import java.util.List;
import java.util.Map;

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
}
