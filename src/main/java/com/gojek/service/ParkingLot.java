package com.gojek.service;

import com.gojek.domain.Car;
import com.gojek.domain.Slot;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.PriorityQueue;

import static com.gojek.util.ParkingLotUtil.*;

public class ParkingLot {

    private int capacity;
    private Map<Slot, Car> parkingSpaces;
    private int slotNumber;
    private PriorityQueue<Slot> freeSlots = new PriorityQueue<>( (s1, s2) -> s2.getNumber() - s1.getNumber());

    public String createParkingLot(int size){
        if(size < 1)
            return IGNORE;
        capacity = size;
        slotNumber = 0;
        parkingSpaces = new LinkedHashMap<>(capacity);
        return String.format(PARKING_LOT_CREATED, capacity);
    }

    public String parkCar(Car car){

        if(isAlreadyParked(car.getRegistrationNumber())){
            return IGNORE;
        }
        if(!freeSlots.isEmpty()){
            Slot slot = freeSlots.remove();
            slot.setOccupied(true);
            parkingSpaces.put(slot, car);
            return String.format(ALLOCATED_SLOT_NUMBER, slot.getNumber());
        }

        if( slotNumber < capacity){
            Slot slot = new Slot(++slotNumber, true);
            parkingSpaces.put(slot, car);
            return String.format(ALLOCATED_SLOT_NUMBER, slotNumber);
        }else {
            return PARKING_LOT_IS_FULL;
        }
    }

    public String unPark(int slotNumber){
        return String.format(SLOT_NUMBER_IS_FREE, slotNumber);
    }

    public Map<Slot, Car> getParkingSpaces() {
        return parkingSpaces;
    }

    public boolean isParkingLotFull(){
        return freeSlots.isEmpty() && slotNumber == capacity;
    }

    private boolean isAlreadyParked(String registrationNumber){
        return parkingSpaces.values().stream()
                .anyMatch(v->v.getRegistrationNumber().equalsIgnoreCase(registrationNumber));
    }
}
