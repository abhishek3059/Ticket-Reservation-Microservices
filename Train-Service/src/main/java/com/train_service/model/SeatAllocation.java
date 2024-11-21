package com.train_service.model;

import com.train_service.enums.ClassType;
import com.train_service.enums.SeatType;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class SeatAllocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "train_id")
    private Train train;
    @Enumerated(EnumType.STRING)
    private ClassType classType;
    @Enumerated(EnumType.STRING)
    private SeatType seatType;
    private Integer seatNumber;
    private Boolean isBooked;
    private String bookingId;
}

