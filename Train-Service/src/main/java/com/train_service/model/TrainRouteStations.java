package com.train_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity @Table(name = "train_route_stations")
public class TrainRouteStations {
    @Id
    @ManyToOne
    @JoinColumn(name = "train_id")
    private Train train;
    @NotNull (message = "Location id cannot be null")
    private long locationId;
    @NotNull
    private int stationOrder;
    @NotNull
    private int stoppage;


}
