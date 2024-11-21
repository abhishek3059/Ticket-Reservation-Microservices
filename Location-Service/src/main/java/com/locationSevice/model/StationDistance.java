package com.locationSevice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StationDistance {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long distanceId;

    @ManyToOne(fetch = FetchType.LAZY) // Correct association
    @JoinColumn(name = "source_station_id", referencedColumnName = "locationId")
    private Location source;

    @ManyToOne(fetch = FetchType.LAZY) // Correct association
    @JoinColumn(name = "destination_station_id", referencedColumnName = "locationId")
    private Location destination;

    @Column(nullable = false)
    private Double distance;
}