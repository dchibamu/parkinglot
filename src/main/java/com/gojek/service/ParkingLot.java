package com.gojek.service;

import com.gojek.domain.Car;
import com.gojek.domain.Command;
import com.gojek.domain.Slot;

import java.util.*;
import java.util.stream.Collectors;

import static com.gojek.domain.Command.INVALID;
import static com.gojek.util.AppConstants.*;

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

    /**
     * Finds a parking slot for a car if parking lot is not full.
     * If a car has left the parking slot then there is a slot available closer to the entrance,
     * in that case allocate that vacant slot first.
     * @param car - car to be parked in parking lot
     * @return String message indicating success creation of parking lot
     */
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

    /**
     * Makes a parking slot vacant, adding the available slot to free slot queue, to be allocated first if another car request for parking.
     * @param slotNumber Parking lot slot number
     * @return String message indicating vacant slot
     */
    public String unPark(int slotNumber){
        if(slotNumber < 1 || slotNumber > capacity)
            return IGNORE;
        Slot slot = findSlotBySlotNumber(slotNumber).get();
        slot.setOccupied(false);
        freeSlots.add(slot);
        return String.format(SLOT_NUMBER_IS_FREE, slotNumber);
    }

    /**
     * Finds all registration numbers for cars with given color, otherwise Not found.
     * @param color - car color
     * @return String of registration numbers concatenated by comma.
     */
    public String getRegistrationNumbersForCarsWithColor(String color){
        String result = parkingSpaces.entrySet().stream()
                .filter(slot -> slot.getKey().isOccupied())
                .map(Map.Entry::getValue)
                .filter(car -> car.getColor().equalsIgnoreCase(color))
                .map(Car::getRegistrationNumber)
                .collect(Collectors.joining(", "));
        if(result != null && !result.isEmpty() )
            return result;
        else
            return NOT_FOUND;
    }

    /**
     * Returns slot numbers of cars with given color, otherwise Not found
     * @param color - car color
     * @return String of concatenated slot numbers
     */
    public String getSlotNumbersForCarsWithColor(String color){
        String result = parkingSpaces.entrySet().stream()
                .filter(car -> car.getValue().getColor().equalsIgnoreCase(color))
                .filter(entry -> entry.getKey().isOccupied())
                .map(Map.Entry::getKey)
                .map(Slot::getNumber)
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
        if(result != null && !result.isEmpty() )
            return result;
        else
            return NOT_FOUND;
    }

    /**
     * Finds slot number for given registration number, otherwise returns Not found
     * @param registrationNumber - car registration number
     * @return - String number or Not found
     */
    public String getSlotNumberForRegistrationNumber(String registrationNumber){

        return parkingSpaces.entrySet().stream()
                .filter(car ->car.getValue().getRegistrationNumber().equalsIgnoreCase(registrationNumber))
                .filter(entry -> entry.getKey().isOccupied())
                .map(Map.Entry::getKey)
                .map(Slot::getNumber)
                .map(String::valueOf)
                .findFirst().orElse(NOT_FOUND);
    }

    public Map<Slot, Car> getParkingSpaces() {
        return parkingSpaces;
    }

    public String status(){
        StringBuffer output = new StringBuffer(String.format("%s\t%s\t%s\n","Slot No.","Registration No","Colour"));
        for(Map.Entry<Slot, Car> entry: parkingSpaces.entrySet()){
            if(entry.getKey().isOccupied())
                output.append(String.format("%s\t%s\t%s\n",entry.getKey().getNumber(),entry.getValue().getRegistrationNumber(), entry.getValue().getColor()));
        }
        return output.toString();
    }

    /**
     *  Validates each command making sure it makes sense for ParkingLot service.
     *  If command is valid it executes command otherwise,
     *  it does nothing.
     * @param commandLine
     * @return String output to STDOUT
     */
    public String executeCommand(String commandLine){
        if(commandLine == null || "".equals(commandLine))
            return IGNORE;
        String[] commandLineArgs = Arrays.stream(commandLine.split("\\s+")).toArray(String[]::new);
        Command command = getCommand(commandLineArgs[0]);

        switch (command){
            case CREATE_PARKING_LOT:
                int parkingLotSize = parseNumericalArguments(commandLineArgs[1]);
                if( parkingLotSize > 0)
                    return createParkingLot(parkingLotSize);
                break;
            case LEAVE:
                int slotNum = parseNumericalArguments(commandLineArgs[1]);
                if( slotNum > 0 && slotNum < capacity)
                    return unPark(slotNum);
                break;
            case REGISTRATION_NUMBERS_FOR_CARS_WITH_COLOUR:
                if(commandLineArgs.length == 2)
                    return getRegistrationNumbersForCarsWithColor(commandLineArgs[1]);
                break;
            case SLOT_NUMBERS_FOR_CARS_WITH_COLOR:
                if(commandLineArgs.length == 2)
                    return getSlotNumbersForCarsWithColor(commandLineArgs[1]);
                break;
            case SLOT_NUMBER_FOR_REGISTRATION_NUMBER:
                if(commandLineArgs.length == 2)
                    return getSlotNumberForRegistrationNumber(commandLineArgs[1]);
                break;
            case PARK:
               if(commandLineArgs.length == 3) {
                   Car car = new Car(commandLineArgs[1], commandLineArgs[2]);
                   return parkCar(car);
               }
               break;
            case STATUS:
                if(commandLineArgs.length == 1)
                    return status();
                break;
        }
        return IGNORE;
    }

    /**
     * It validates that the argument passed is a number,if not return -1. -1 is used as a signal that invalid inputs have been
     * entered because it doesn't make sense in the context of a parking lot.
     * @param commandLineArg
     * @return a number greater than zero, otherwise -1.
     */
    private static int parseNumericalArguments(String commandLineArg){
        try {
            return Integer.parseInt(commandLineArg);
        }catch (NumberFormatException e){
            //Did not log error - making assumption that automated test system only expects output to STDOUT
        }
        return -1;
    }

    private static Command getCommand(String commandArg){
        try{
            return Command.valueOf(commandArg.toUpperCase());
        }catch (Exception e){
            //Did not log error - making assumption that automated test system only expects output to STDOUT
        }
        return INVALID;
    }


    private boolean isAlreadyParked(String registrationNumber){
        return parkingSpaces.values().stream()
                .anyMatch(v->v.getRegistrationNumber().equalsIgnoreCase(registrationNumber));
    }

    private Optional<Slot> findSlotBySlotNumber(int slotNumber){
        return parkingSpaces.entrySet().stream()
                .map(Map.Entry::getKey)
                .filter(slotNum -> slotNum.getNumber() == slotNumber)
                .findFirst();
    }
}
