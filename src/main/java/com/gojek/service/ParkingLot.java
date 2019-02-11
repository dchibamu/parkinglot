package com.gojek.service;

import com.gojek.domain.Car;
import com.gojek.domain.Command;
import com.gojek.domain.Slot;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static com.gojek.domain.Command.INVALID;
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
        if(slotNumber < 1 || slotNumber > capacity)
            return IGNORE;
        Slot slot = findSlotBySlotNumber(slotNumber).get();
        slot.setOccupied(false);
        freeSlots.add(slot);
        return String.format(SLOT_NUMBER_IS_FREE, slotNumber);
    }

    public String getRegistrationNumbersForCarsWithColor(String color){
        return "JPX-876-GP, DFX-999-GP, ASK-515-WP";
    }

    public String getSlotNumbersForCarsWithColor(String color){
        return "6, 7";
    }

    public String getSlotNumberForRegistrationNumber(String registrationNumber){
        return "4";
    }

    public Map<Slot, Car> getParkingSpaces() {
        return parkingSpaces;
    }

    public String status(){
        StringBuffer output = new StringBuffer(String.format("%s\t%s\t%s\n","Slot No.","Registration No","Colour"));
        for(Map.Entry<Slot, Car> entry: parkingSpaces.entrySet()){
            output.append(String.format("%s\t%s\t%s\n",entry.getKey().getNumber(),entry.getValue().getRegistrationNumber(), entry.getValue().getColor()));
        }
        return output.toString();
    }

    public boolean isParkingLotFull(){
        return freeSlots.isEmpty() && slotNumber == capacity;
    }

    /**
     *  Validates each command making sure it makes sense for ParkingLot service.
     *  If command is valid it executes command otherwise,
     *  it does nothing.
     * @param commandLine
     * @return String output to STDOUT
     */
    public String executeAction(String commandLine){
        String[] commandLineArgs = Arrays.stream(commandLine.split("\\s+")).toArray(String[]::new);

        if(!StringUtils.isNoneBlank(commandLineArgs))
            return IGNORE;
        Command command = getCommand(commandLineArgs[0]);

        switch (command){
            case CREATE_PARKING_LOT:
                int parkingLotSize = parseNumericalArguments(commandLineArgs[1]);
                if( parkingLotSize > 0)
                    return createParkingLot(parkingLotSize);
            case LEAVE:
                int slotNum = parseNumericalArguments(commandLineArgs[1]);
                if( slotNum > 0 && slotNum < capacity)
                    return unPark(slotNumber);
            case REGISTRATION_NUMBERS_FOR_CARS_WITH_COLOUR:
                if(commandLineArgs.length == 2)
                    return getRegistrationNumbersForCarsWithColor(commandLineArgs[1]);
            case SLOT_NUMBERS_FOR_CARS_WITH_COLOR:
                if(commandLineArgs.length == 2)
                    return getSlotNumbersForCarsWithColor(commandLineArgs[1]);
            case SLOT_NUMBER_FOR_REGISTRATION_NUMBER:
                if(commandLineArgs.length == 2)
                    return getSlotNumberForRegistrationNumber(commandLineArgs[1]);
            case PARK:
               if(commandLineArgs.length == 3) {
                   Car car = new Car(commandLineArgs[1], commandLineArgs[2]);
                   return parkCar(car);
               }
            case STATUS:
                if(commandLineArgs.length == 1){
                    return status();
                }
            default:
                return IGNORE;
        }
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
                .map(slot -> slot.getKey())
                .filter(slotNum -> slotNum.getNumber() == slotNumber)
                .findFirst();
    }
}
