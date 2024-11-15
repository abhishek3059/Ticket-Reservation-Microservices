package com.train_service.model;


import com.dto.CommonDTO.LocationDTO;
import com.train_service.enums.ClassType;
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
        @CollectionTable(name = "seat_allocation",
                joinColumns =   
                @JoinColumn(name = "train_id"))
        @MapKeyEnumerated(EnumType.STRING)
        private Map<ClassType, Map<SeatType, Map<Integer, SeatAllocation>>> seatAllocations = new HashMap<>();


        @ElementCollection(targetClass = Days.class)
        @CollectionTable(name = "train_schedule", joinColumns =
        @JoinColumn(name = "train_id"))
        @Enumerated(EnumType.STRING)
        @Column(name = "running_days")
        private EnumSet<Days> runningDays;
        @ElementCollection
        @CollectionTable(name = "train_route",
            joinColumns = @JoinColumn(name = "train_id"))
        @MapKeyColumn(name = "station_order")
        @AttributeOverrides({
            @AttributeOverride(name = "stationName", column = @Column(name = "station_name")),
            @AttributeOverride(name = "distanceFromStart", column = @Column(name = "distance_from_start"))
    })
    private Map<Integer, RouteDistance> trainRouteDistance = new LinkedHashMap<>();

    }

