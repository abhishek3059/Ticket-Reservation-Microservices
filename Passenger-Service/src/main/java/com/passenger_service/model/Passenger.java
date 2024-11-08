package com.passenger_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Passenger {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    @NotNull
    private Date dateOfBirth;
    @Column(nullable = false, unique = true)
    private String username;
    private String firstName;
    private String lastName;
    private String email;




}
