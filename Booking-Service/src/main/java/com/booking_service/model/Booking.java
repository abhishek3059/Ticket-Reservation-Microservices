package com.booking_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Booking {

    @Id
    private String bookingId;
    @Column(nullable = false)
    private String passengerId;
    @Column(nullable = false)
    private String trainNumber;
    @Column(nullable = false)
    private String trainName;
    @Column(nullable = false)
    private String boardingStation;
    @Column (nullable = false)
    private String destinationStation;
    @Column(nullable = false)
    private Double ticketPrice;
    @Column(nullable = false)
    private LocalDate date;
    @Column(nullable = false)
    private String paymentId;



    @PrePersist
    public void initializeBookingId(){
        Random random = new Random();
         Integer rand = (Integer) (100000 + random.nextInt(900000));
         this.bookingId = rand.toString();
    }


}
