package com.train_service.model;


import com.dto.CommonDTO.LocationDTO;
import com.train_service.enums.Days;
import com.train_service.enums.SeatType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.*;


@Entity
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public class Train {
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        @NotNull
        private String trainNumber;
        private String source;
        private String  destination;
        private String name;
        private Double baseFare;

        @ElementCollection
        @CollectionTable(name = "train_route",
                joinColumns = @JoinColumn(name = "train_id"))
        @MapKeyColumn(name = "station_order")  // the key in the Map (Integer)
        @Column(name = "station_name")         // the value in the Map (String)
        private Map<Integer, String> trainRoute;

        @ElementCollection
        @CollectionTable(name = "seat_availability",joinColumns = @JoinColumn(name = "train_id"))
        @MapKeyColumn(name = "seat_type")
        @Column(name = "available_seats")
        private Map<SeatType, Integer> availableSeats = new EnumMap<>(SeatType.class);

        @ElementCollection(targetClass = Days.class)
        @CollectionTable(name = "train_schedule", joinColumns =
        @JoinColumn(name = "train_id"))
        @Enumerated(EnumType.STRING)
        @Column(name = "running_days")
        private EnumSet<Days> runningDays;

    }
