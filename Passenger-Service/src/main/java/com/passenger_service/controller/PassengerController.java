package com.passenger_service.controller;

import com.dto.CommonDTO.PassengerDTO;
import com.passenger_service.service.PassengerService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@AllArgsConstructor
@RequestMapping("api/passenger")
public class PassengerController {

    private final PassengerService passengerService;
    @PostMapping("/user/create")
    public ResponseEntity<Void> createPassenger(PassengerDTO passengerDTO) throws ParseException {
        return passengerService.createPassenger(passengerDTO);
    }
    @GetMapping("/passengerDTO/passenger_details")
    public PassengerDTO sendPassengerDetailsToOtherServices(@RequestParam String passengerId){
        return passengerService.sendPassengerDetailsToOtherServices(passengerId);
    }
    @GetMapping("/passengerDTO/verify")
    public ResponseEntity<Boolean> verifyPassenger(@RequestParam String passengerId){
        return passengerService.verifyPassenger(passengerId);
    }
    @DeleteMapping("/user/delete")
    public void deletePassenger(PassengerDTO passengerDTO) throws ParseException{
         passengerService.deletePassenger(passengerDTO);
    }

}
