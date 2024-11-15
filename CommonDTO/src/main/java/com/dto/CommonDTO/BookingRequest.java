package com.dto.CommonDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequest {
    private String trainNumber;
    private String classType;
    private String seatType;
    private String bookingId;
    private LocalDate bookedAt;
    private Integer seatNumber;
    private String boardingStation;
    private String destination;

}
