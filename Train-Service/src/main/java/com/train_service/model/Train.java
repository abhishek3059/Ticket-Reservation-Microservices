package com.train_service.model;


import com.dto.CommonDTO.LocationDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;

    @Data
    @Entity
    public class Train {
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        @NotNull
        private String trainNumber;
        private String source;
        private String  destination;
        private String name;

        @ElementCollection
        @CollectionTable(name = "train_route",
                joinColumns = @JoinColumn(name = "train_id"))
        @MapKeyColumn(name = "station_order")  // the key in the Map (Integer)
        @Column(name = "station_name")         // the value in the Map (String)
        private Map<Integer, String> trainRoute;



    }
