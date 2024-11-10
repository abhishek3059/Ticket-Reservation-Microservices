package com.train_service.controller;

import com.dto.CommonDTO.TrainDTO;
import com.train_service.service.TrainService;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
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
