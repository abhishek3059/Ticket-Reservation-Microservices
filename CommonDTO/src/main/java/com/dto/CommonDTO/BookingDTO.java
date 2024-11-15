package com.dto.CommonDTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
@Builder
@AllArgsConstructor
@Getter
@Setter
public class BookingDTO {

    private String bookingId;
    private String passengerId;
    private String trainNumber;
    private String trainName;
    private String boardingStation;
    private String destinationStation;
    private Double ticketPrice;
    private LocalDate date;
}
