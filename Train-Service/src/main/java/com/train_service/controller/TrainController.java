package com.train_service.controller;

import com.dto.CommonDTO.BookingRequest;
import com.dto.CommonDTO.CancelRequest;
import com.dto.CommonDTO.TrainDTO;
import com.train_service.service.TrainService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/trains")
public class TrainController {

    private final TrainService trainService;

    public TrainController(TrainService trainService){
        this.trainService = trainService;
    }

    @GetMapping("/public") @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<List<TrainDTO>> getAll(){
        return trainService.getAll();
    }

    @GetMapping("/trainDTO/available-seats}")
    public ResponseEntity<List<Integer>> getAvailableSeats(@RequestBody BookingRequest request){
        return trainService.getAvailableSeats(request.getTrainNumber(), request.getClassType(), request.getSeatType());
    }


    @PutMapping("trainDTO/Seat-Availability")
    public ResponseEntity<Boolean> bookSeat
            (@RequestParam String trainNumber,
             @RequestParam String classType,
             @RequestParam String seatType,
             @RequestParam  Integer seatNumber,
             @RequestParam String bookingId){
        return trainService.bookSeat(trainNumber, classType,seatType, seatNumber, bookingId);
    }


    @PutMapping("/trainDTO/cancel-booking")
    public void releaseSeat(@RequestParam String trainNumber , @RequestParam String bookingId){
         trainService.releaseSeat(trainNumber, bookingId);
    }


    @GetMapping("/trainDTO/get-train-distance/{trainNumber}")
    public ResponseEntity<Double> sendTrainsTotalDistanceForTrain(@PathVariable String trainNumber){
        return trainService.getTotalDistanceForTrain(trainNumber);
    }

    @GetMapping("/trainDTO/get-distance-between-stations")
    public ResponseEntity<Double> sendDistanceBetweenSourceStationAndDestination
            (@RequestParam String trainNumber, @RequestParam String boardingStation, @RequestParam String destination ){
        return trainService.getTotalDistanceBetweenBoardingAndDestination(trainNumber,boardingStation,destination);
    }

    @GetMapping("/public/{trainNumber}") @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<TrainDTO> getTrainByTrainNo(@PathVariable String trainNumber){
        return trainService.getTrainByTrainNumber(trainNumber);
    }


    @PutMapping("/private/update-train-route/{trainNumber}") @ResponseStatus(HttpStatus.ACCEPTED)
    public void addStationToRoute(@PathVariable String trainNumber, @RequestBody Map<Integer,Long> stationWithOrder)
    {
         trainService.addStationToRoute(trainNumber,stationWithOrder);
    }

   @GetMapping("/public/train-with-route/{trainNumber}")@ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<TrainDTO> getTrainWithRoute(@PathVariable String trainNumber){
        return trainService.getTrainWithFullRoute(trainNumber);
    }


    @PostMapping("/private") @ResponseStatus(HttpStatus.CREATED)
    public void addTrain(@RequestBody TrainDTO train){
        trainService.addTrain(train);

    }


    @GetMapping("/trainDTO/{stationName}") @ResponseStatus(HttpStatus.ACCEPTED)
    public List<String> sendTrainDTOtoOtherServices(@PathVariable String stationName){
        return trainService.sendDataOfTrainsForStationName(stationName);
    }

}
