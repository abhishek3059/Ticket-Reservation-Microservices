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
    @Column(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_station_id", referencedColumnName = "locationId")
    private Location source;
    @Column(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_station_id", referencedColumnName = "locationId")
    private Location destination;
    @Column(nullable = false)
    private Double distance;
}
