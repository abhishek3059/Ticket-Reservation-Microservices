package com.dto.CommonDTO;

import lombok.*;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PassengerDTO {

    private String id;
    private String dateOfBirth;
    private String firstName;
    private String lastName;
    private String email;
    private Set<Long> bookings;
}
