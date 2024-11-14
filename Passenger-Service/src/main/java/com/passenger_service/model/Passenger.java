package com.passenger_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Passenger {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    @Column(nullable = false)
    @NotNull(message = "Date of birth cannot be null")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date dateOfBirth;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false , unique = true)
    private String email;
    @ElementCollection
    @CollectionTable(name = "train_bookings", joinColumns =
    @JoinColumn(name = "train_id"))
    @Column(name = "booking_id")
    private Set<Long> bookings;




}
