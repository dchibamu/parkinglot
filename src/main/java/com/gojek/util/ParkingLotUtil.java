package com.gojek.util;

import com.gojek.domain.Command;
import org.apache.commons.lang3.StringUtils;

public class ParkingLotUtil {

    public static final String PARKING_LOT_CREATED = "Created a parking lot with %s slots";
    public static final String ALLOCATED_SLOT_NUMBER = "Allocated slot number %d";
    public static final String SLOT_NUMBER_IS_FREE = "Slot number %d is free";
    public static final String PARKING_LOT_IS_FULL = "Sorry, parking lot is full";
    public static final String NOT_FOUND = "Not found";
    public static final String EXIT = "exit";
    public static final String IGNORE = "";

    /**
     * Validates each command making sure it makes sense for ParkingLot service
     * @param command
     * @param commandLineArgs
     * @return
     */
    public static boolean isValidCommand(Command command, String[] commandLineArgs){
        if(!StringUtils.isNoneBlank(commandLineArgs))
            return false;
        switch (command){
            case CREATE_PARKING_LOT:
            case LEAVE:
            case REGISTRATION_NUMBERS_FOR_CARS_WITH_COLOUR:
            case SLOT_NUMBERS_FOR_CARS_WITH_COLOR:
            case SLOT_NUMBER_FOR_REGISTRATION_NUMBER:
                return commandLineArgs.length == 2;
            case PARK:
                return commandLineArgs.length == 3;
            case STATUS:
                return commandLineArgs.length == 1;
        }
        return false;
    }
}
