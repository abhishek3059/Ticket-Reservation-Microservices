package com.locationSevice.serviceImpl;

import com.dto.CommonDTO.LocationDTO;
import com.locationSevice.customException.StationNotFound;
import com.locationSevice.model.Location;
import com.locationSevice.repository.LocationRepository;
import com.locationSevice.service.LocationService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
@Service
public class LocationServiceImpl implements LocationService {

    private final WebClient client;
    private final LocationRepository locationRepository;

    public LocationServiceImpl(WebClient.Builder client, LocationRepository locationRepository) {
        this.client = client.baseUrl("lb://TRAIN-SERVICE").build();
        this.locationRepository = locationRepository;
    }

    @Override
    public ResponseEntity<LocationDTO> createStation(LocationDTO station) {
        locationRepository.save(convertLocationDTOtoLocation(station));
        return ResponseEntity.ok(station);
    }
    public Location convertLocationDTOtoLocation(LocationDTO dto){
        return Location.builder()
                .locationId(dto.getLocationId())
                .stationName(dto.getStationName())
                .city(dto.getCity())
                .stationCode(dto.getStationCode())
                .state(dto.getState())
                .listOfTrainNumbers(dto.getListOfTrainNumbers())
                .build();
    }

    @Override
    public ResponseEntity<List<LocationDTO>> getAllStations() {
        List<LocationDTO> locationDTOS = locationRepository.findAll().stream()
                                    .map(this::convertLoacationToLocationDTO)
                                            .toList();
        return new ResponseEntity<>(locationDTOS, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<LocationDTO> getLocationById(long id) {
        Location location = locationRepository.findById(id).
                    orElseThrow(() ->
                            new StationNotFound("Station by id:"+id+" not found"));
        return ResponseEntity.ok(convertLoacationToLocationDTO(location));
    }

    @Override
    public LocationDTO getLocationByStationCode(String stationCode) {

        Optional<Location> location =  locationRepository.findByStationCode(stationCode);
        if(location.isPresent()){
            return convertLoacationToLocationDTO(location.get());
        }
        else throw new IllegalArgumentException("no station available by this station code");
    }

    private LocationDTO convertLoacationToLocationDTO(Location location) {
        return LocationDTO.builder()
                .locationId(location.getLocationId())
                .stationCode(location.getStationCode())
                .stationName(location.getStationName())
                .state(location.getState())
                .city(location.getCity())
                .listOfTrainNumbers(location.getListOfTrainNumbers())
                .build();

    }

    @Override
    public LocationDTO sendLocationDetailsToOtherServices(Long locationId) {
          Location location = locationRepository.findById(locationId)
                  .orElseThrow(() -> new StationNotFound("Station requested is not found"));
        if(location != null){
            return convertLoacationToLocationDTO(location);
        }
        else throw new StationNotFound("Station requested not found");
    }

    @Override
    public List<String> fetchTrainsByStationName(String stationName) {
       return client.get().uri("/api/trains/trainDTO/{stationName}",stationName)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->{
                    return Mono.error(new IllegalArgumentException("SomeThing went wrong"));
                })
                .onStatus(HttpStatusCode::is5xxServerError, response ->{
                    return Mono.error(new RuntimeException("Something is wrong with TrainService"));
                })
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {
                })
                .block();


    }

    @Override
    public void addTrainsToStations(String stationName) {
       Location location = locationRepository.findByStationName(stationName).orElseThrow(
                ()-> new StationNotFound("station with station name "+stationName+" not found"));

       List<String> trains = fetchTrainsByStationName(stationName);
       if(trains.isEmpty()){
           throw new NullPointerException("fetched request returned null");
       }
           location.setListOfTrainNumbers(trains);
       locationRepository.save(location);
    }
    @Override
    public ResponseEntity<Boolean> verifyStationDetails(String stationName){
        if(locationRepository.existsByStationName(stationName)){
            throw new IllegalArgumentException("Station "+stationName+" does not exists");
        }
        else{
            return ResponseEntity.ok(true);
        }
    }


}
