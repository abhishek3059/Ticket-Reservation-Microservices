package com.train_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
@Data
@Entity
public class Train {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull
    private String trainNumber;
    private LocationDTO source;
    private LocationDTO destination;
    private List<LocationDTO> routeStations;


}
