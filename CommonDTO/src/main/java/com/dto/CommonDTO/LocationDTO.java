package com.dto.CommonDTO;


import lombok.*;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationDTO {
    private long locationId;
    private String stationName;
    private String stationCode;
    private String city;
    private String state;


}
