package com.locationSevice.model;

import com.dto.CommonDTO.TrainDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
    @ElementCollection // Maps the list of strings as a collection in JPA
    @Column(name = "train_number") // You can specify the column name for items in the list
    private List<String> listOfTrainNumbers;

}
