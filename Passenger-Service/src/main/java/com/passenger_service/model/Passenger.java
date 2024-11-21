package com.passenger_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Passenger {
    @Id
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

    private Set<Long> bookings = new HashSet<>();

    @PrePersist
    void generateId(){
    Random random = new Random();
    if(this.id == null || this.id.isEmpty()){
        int rand = 10000 + random.nextInt(99999);
        this.id = "P" + rand;
    }
}





}
