package com.train_service.nestedDatabaseConverter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.train_service.enums.ClassType;
import com.train_service.enums.SeatType;
import com.train_service.model.SeatAllocation;
import jakarta.persistence.AttributeConverter;
import lombok.AllArgsConstructor;

import java.util.Map;
@AllArgsConstructor
public class SeatAllocationJsonConverter
        implements AttributeConverter<Map<ClassType, Map<SeatType, Map<Integer, SeatAllocation>>>, String> {

    private final ObjectMapper objectMapper ;
    @Override
    public String convertToDatabaseColumn(Map<ClassType, Map<SeatType, Map<Integer, SeatAllocation>>> attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize seat allocations", e);
        }
    }

    @Override
    public Map<ClassType, Map<SeatType, Map<Integer, SeatAllocation>>> convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData,
                    new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize seat allocations", e);
        }
    }
}
