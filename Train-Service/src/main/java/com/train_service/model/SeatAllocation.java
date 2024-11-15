package com.train_service.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class SeatAllocation {
    private Integer seatNumber;
    private Boolean isBooked;
    private String bookingId;  // null if not booked
}
