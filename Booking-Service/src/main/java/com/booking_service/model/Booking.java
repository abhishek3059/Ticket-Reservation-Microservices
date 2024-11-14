package com.booking_service.model;

import jakarta.persistence.*;
import lombok.*;

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
    private List<String> passengerIds;
    @Column(nullable = false)
    private String trainNumber;
    @Column(nullable = false)
    private String trainName;
    @Column(nullable = false)
    private String boardingStation;
    @Column (nullable = false)
    private String destinationStation;
    @Column(nullable = false)
    private Long ticketPrice;
    @Column(nullable = false)
    private Date date;


    @PrePersist
    public void initializeBookingId(){
        Random random = new Random();
         Integer rand = (Integer) (100000 + random.nextInt(900000));
         this.bookingId = rand.toString();
    }


}
