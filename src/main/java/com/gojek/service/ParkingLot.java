package com.gojek.service;

import com.gojek.domain.Car;
import com.gojek.domain.Slot;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.gojek.util.ParkingLotUtil.IGNORE;
import static com.gojek.util.ParkingLotUtil.PARKING_LOT_CREATED;

public class ParkingLot {

    private int capacity;
    private Map<Slot, Car> parkingSpaces;

    public String createParkingLot(int size){
        if(size < 1)
            return IGNORE;
        capacity = size;
        parkingSpaces = new LinkedHashMap<>(capacity);
        return String.format(PARKING_LOT_CREATED, capacity);
    }
}
