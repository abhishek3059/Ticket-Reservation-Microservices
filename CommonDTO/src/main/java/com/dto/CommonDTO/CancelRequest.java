package com.dto.CommonDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CancelRequest {
    private String trainNumber;
    private String bookingId;


}
