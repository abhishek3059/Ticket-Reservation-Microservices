package com.dto.CommonDTO;

import lombok.Data;

@Data
public class BookingCreationRequest {
   private PassengerDTO passengerDTO;
   private BookingRequest bookingDTO;
}
