package com.locationSevice.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long locationId;
    @Column(nullable = false)
    private String stationName;

    @Column(unique = true, nullable = false)
    private String stationCode;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;


}
