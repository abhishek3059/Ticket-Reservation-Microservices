package com.dto.CommonDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TrainDTO{
private Long id;

private String trainNumber;
    private String source;
    private String  destination;
    private String name;
    private Map<Integer, String> trainRoute;
}
