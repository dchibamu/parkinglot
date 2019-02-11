package com.gojek.service;

import com.gojek.domain.Car;
import com.gojek.domain.Slot;
import org.junit.Ignore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@DisplayName("<== ParkingLot service ==>")
class ParkingLotTest {

    private static final String EXPECTED_OUTPUT_FROM_CREATE_PARKING_LOT = "Created a parking lot with %s slots";
    private static final String EXPECTED_OUTPUT_WHEN_PARKINGLOT_IS_FULL = "Sorry, parking lot is full";
    private static final String EXPECTED_OUTPUT_WHEN_SLOT_NUMBER_IS_FREE = "Slot number %d is free";
    private static final String EXPECTED_OUTPUT_WHEN_NEW_SLOT_IS_ALLOCATED = "Allocated slot number %d";
    private static final String EXPECTED_OUTPUT_WHEN_NOT_FOUND = "Not found";
    private static final String EXPECTED_EMPTY_STRING="";

    @Test
    @DisplayName("Should be able to create a parking lot with 10 spaces")
    void shouldCreateParkingLotOfSizeTen(){
        int parkingLotCapacity = 10;
        ParkingLot parkingLot = new ParkingLot();
        assertThat(parkingLot.createParkingLot(parkingLotCapacity), is(equalTo(String.format(EXPECTED_OUTPUT_FROM_CREATE_PARKING_LOT, parkingLotCapacity))));
    }

    @Test
    @DisplayName("Parking lot size should only be of size 1 or more")
    void shouldReturnNothingIfParkingLotSizeIsLessThanOne(){
        int parkingLotCapacity = 0;
        ParkingLot parkingLot = new ParkingLot();
        assertThat(parkingLot.createParkingLot(parkingLotCapacity), is(not(equalTo(String.format(EXPECTED_OUTPUT_FROM_CREATE_PARKING_LOT, parkingLotCapacity)))));
    }

    @Test
    @DisplayName("Should return allocated slot number message")
    void shouldParkACarInParkingLot(){
        int parkingLotCapacity = 1;
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.createParkingLot(parkingLotCapacity);
        String registrationNumber = "JPX-876-GP";
        String color = "BLUE";
        Car car = new Car(registrationNumber, color);
        String outcome = parkingLot.parkCar(car);
        assertThat(parkingLot.getParkingSpaces().size(), is(1));
        assertEquals(String.format(EXPECTED_OUTPUT_WHEN_NEW_SLOT_IS_ALLOCATED, 1), outcome);
    }

    @Test
    @DisplayName("Should return empty string")
    void shouldReturnNothingIfSameCarIsAttemptedToBeParkedIfAlreadyInParkingLot(){
        int parkingLotCapacity = 1;
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.createParkingLot(parkingLotCapacity);

        String registrationNumber = "JPX-876-GP";
        String color = "BLUE";
        Car car = new Car(registrationNumber, color);
        parkingLot.parkCar(car);
        String outCome = parkingLot.parkCar(car);
        assertEquals(EXPECTED_EMPTY_STRING, outCome);
    }

    @Test
    @DisplayName("Should return parking lot is full message")
    void shouldNotParkACarInParkingLot(){
        int parkingLotCapacity = 1;
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.createParkingLot(parkingLotCapacity);

        String registrationNumber = "JPX-876-GP";
        String color = "BLUE";
        Car car = new Car(registrationNumber, color);
        parkingLot.parkCar(car);

        registrationNumber = "DFX-999-GP";
        color = "GREEN";
        Car car2 = new Car(registrationNumber, color);
        String outcome2 = parkingLot.parkCar(car2);
        assertEquals(EXPECTED_OUTPUT_WHEN_PARKINGLOT_IS_FULL, outcome2);
    }

    @Test
    @DisplayName("Should park car in previously occupied slot first if now empty")
    void shouldParkACarInPreviouslyOccupiedParkingSpace(){
        int parkingLotCapacity = 4;
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.createParkingLot(parkingLotCapacity);

        String registrationNumber = "JPX-876-GP";
        String color = "BLUE";
        Car car = new Car(registrationNumber, color);
        parkingLot.parkCar(car);

        registrationNumber = "DFX-999-GP";
        color = "GREEN";
        Car car2 = new Car(registrationNumber, color);
        String outcome2 = parkingLot.parkCar(car2);

        registrationNumber = "ASK-515-WP";
        color = "PURPLE";
        Car car3 = new Car(registrationNumber, color);
        String outcome3 = parkingLot.parkCar(car3);
        //assertEquals(EXPECTED_OUTPUT_WHEN_PARKINGLOT_IS_FULL, outcome2);

        //Call 2 should leave making slot 2 vacant
        int slotNumber = 2;
        parkingLot.unPark(slotNumber);
        //car 4 should park in slot 2
        registrationNumber = "WDW-787-MP";
        color = "WHITE";
        Car car4 = new Car(registrationNumber, color);
        String outcome4 = parkingLot.parkCar(car4);
        assertThat(outcome4, is(equalTo(String.format(EXPECTED_OUTPUT_WHEN_NEW_SLOT_IS_ALLOCATED, slotNumber))));
    }

    @Test
    @DisplayName("Should make previously occupied parking space empty")
    void shouldUnParkACar(){
        int parkingLotCapacity = 3;
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.createParkingLot(parkingLotCapacity);

        String registrationNumber = "JPX-876-GP";
        String color = "BLUE";
        Car car = new Car(registrationNumber, color);
        parkingLot.parkCar(car);

        registrationNumber = "DFX-999-GP";
        color = "GREEN";
        Car car2 = new Car(registrationNumber, color);
        String outcome2 = parkingLot.parkCar(car2);

        registrationNumber = "ASK-515-WP";
        color = "PURPLE";
        Car car3 = new Car(registrationNumber, color);
        parkingLot.parkCar(car3);

        //Call 2 should leave making slot 2 vacant
        int slotNumber = 2;
        String leaveSlotOutcome = parkingLot.unPark(slotNumber);
        assertThat(leaveSlotOutcome, is(equalTo(String.format(EXPECTED_OUTPUT_WHEN_SLOT_NUMBER_IS_FREE, slotNumber))));
    }

    @Test
    @DisplayName("Should return empty string if slot number provided is outside the range 1 to capacity")
    void unParkWithInvalidSlotNumber(){
        int parkingLotCapacity = 3;
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.createParkingLot(parkingLotCapacity);

        String registrationNumber = "JPX-876-GP";
        String color = "BLUE";
        Car car = new Car(registrationNumber, color);
        parkingLot.parkCar(car);

        int slotNumber = 12;
        String leaveSlotOutcome = parkingLot.unPark(slotNumber);
        assertThat(leaveSlotOutcome, is(equalTo(String.format(EXPECTED_EMPTY_STRING, slotNumber))));
    }

    @Test
    @DisplayName("Should return registration numbers with color blue")
    void getRegistrationNumbersForCarsWithColor_Blue(){
        String givenColor = "BLUE";
        String expectedREgistrationNumbers = "JPX-876-GP, JKLA-9897-HR";
        int parkingLotCapacity = 3;
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.createParkingLot(parkingLotCapacity);

        String registrationNumber = "JPX-876-GP";
        String color = "BLUE";
        Car car = new Car(registrationNumber, color);
        parkingLot.parkCar(car);

        registrationNumber = "HJK-970-DN";
        color = "YELLOW";
        Car car2 = new Car(registrationNumber, color);
        parkingLot.parkCar(car2);

        registrationNumber = "JKLA-9897-HR";
        color = "BLUE";
        Car car3 = new Car(registrationNumber, color);
        parkingLot.parkCar(car3);

        String outCome = parkingLot.getRegistrationNumbersForCarsWithColor(givenColor);
        assertThat(outCome, is(equalTo(expectedREgistrationNumbers)));
    }

    @Test
    @DisplayName("Should return zero registration numbers for cars with colorless color")
    void getRegistrationNumbersForCarsWithColor_Colorless(){
        String givenColor = "colorless";
        int parkingLotCapacity = 3;
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.createParkingLot(parkingLotCapacity);

        String registrationNumber = "JPX-876-GP";
        String color = "BLUE";
        Car car = new Car(registrationNumber, color);
        parkingLot.parkCar(car);

        registrationNumber = "HJK-970-DN";
        color = "YELLOW";
        Car car2 = new Car(registrationNumber, color);
        parkingLot.parkCar(car2);

        registrationNumber = "JKLA-9897-HR";
        color = "BLUE";
        Car car3 = new Car(registrationNumber, color);
        parkingLot.parkCar(car3);

        String outCome = parkingLot.getRegistrationNumbersForCarsWithColor(givenColor);
        assertThat(outCome, is(equalTo(EXPECTED_OUTPUT_WHEN_NOT_FOUND)));
    }

    @Test
    @DisplayName("Should return slot numbers for green cars")
    void getSlotNumbersForCarsWithColor_Green(){
        String givenColor = "GREEN";
        String expectedSlotNumbers = "2";
        int parkingLotCapacity = 3;
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.createParkingLot(parkingLotCapacity);

        String registrationNumber = "JPX-876-GP";
        String color = "BLUE";
        Car car = new Car(registrationNumber, color);
        parkingLot.parkCar(car);

        registrationNumber = "HJK-970-DN";
        color = "GREEN";
        Car car2 = new Car(registrationNumber, color);
        parkingLot.parkCar(car2);

        registrationNumber = "JKLA-9897-HR";
        color = "BLUE";
        Car car3 = new Car(registrationNumber, color);
        parkingLot.parkCar(car3);

        String outCome = parkingLot.getSlotNumbersForCarsWithColor(givenColor);
        assertThat(outCome, is(equalTo(expectedSlotNumbers)));
    }

    @Test
    @DisplayName("Should return slot numbers for blue cars")
    void getSlotNumbersForCarsWithColor_Blue(){
        String givenColor = "Blue";
        String expectedSlotNumbers = "1, 3";
        int parkingLotCapacity = 3;
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.createParkingLot(parkingLotCapacity);

        String registrationNumber = "JPX-876-GP";
        String color = "BLUE";
        Car car = new Car(registrationNumber, color);
        parkingLot.parkCar(car);

        registrationNumber = "HJK-970-DN";
        color = "GREEN";
        Car car2 = new Car(registrationNumber, color);
        parkingLot.parkCar(car2);

        registrationNumber = "JKLA-9897-HR";
        color = "BLUE";
        Car car3 = new Car(registrationNumber, color);
        parkingLot.parkCar(car3);

        String outCome = parkingLot.getSlotNumbersForCarsWithColor(givenColor);
        assertThat(outCome, is(equalTo(expectedSlotNumbers)));
    }

    @Test
    @DisplayName("Should return Not found for invalid color")
    void getSlotNumbersForCarsWithColor_NonExistentColor(){
        String givenColor = "*^&%&^jkjk787nbj";
        int parkingLotCapacity = 4;
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.createParkingLot(parkingLotCapacity);

        String registrationNumber = "JPX-876-GP";
        String color = "BLUE";
        Car car = new Car(registrationNumber, color);
        parkingLot.parkCar(car);

        registrationNumber = "HJK-970-DN";
        color = "GREEN";
        Car car2 = new Car(registrationNumber, color);
        parkingLot.parkCar(car2);

        registrationNumber = "JKLA-9897-HR";
        color = "BLUE";
        Car car3 = new Car(registrationNumber, color);
        parkingLot.parkCar(car3);

        String outCome = parkingLot.getSlotNumbersForCarsWithColor(givenColor);
        assertThat(outCome, is(equalTo(EXPECTED_OUTPUT_WHEN_NOT_FOUND)));
    }


    @Test
    @DisplayName("Should return valid slot number for given registration number")
    void shouldReturnSlotNumberForRegistrationNumber(){
        String givenRegistraionNumber = "THKAL-7819321";
        int parkingLotCapacity = 5;
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.createParkingLot(parkingLotCapacity);

        String registrationNumber = "JPX-876-GP";
        String color = "BLUE";
        Car car = new Car(registrationNumber, color);
        parkingLot.parkCar(car);

        registrationNumber = "HJK-970-DN";
        color = "YELLOW";
        Car car2 = new Car(registrationNumber, color);
        parkingLot.parkCar(car2);

        registrationNumber = "JKLA-9897-HR";
        color = "BLUE";
        Car car3 = new Car(registrationNumber, color);
        parkingLot.parkCar(car3);

        registrationNumber = "IHJO-121-NW";
        color = "BLUE";
        Car car4 = new Car(registrationNumber, color);
        parkingLot.parkCar(car4);

        registrationNumber = "THKAL-7819321";
        color = "BLUE";
        Car car5 = new Car(registrationNumber, color);
        parkingLot.parkCar(car5);

        String outCome = parkingLot.getSlotNumberForRegistrationNumber(givenRegistraionNumber);
        assertThat(outCome, is(equalTo("5")));
    }

    @Test
    @DisplayName("Should return Not found slot number for given registration number")
    void shouldReturnNotFoundSlotNumberGivenRegistrationNumber(){
        String givenRegistraionNumber = "THKAL-7819321";
        int parkingLotCapacity = 5;
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.createParkingLot(parkingLotCapacity);

        String registrationNumber = "JPX-876-GP";
        String color = "BLUE";
        Car car = new Car(registrationNumber, color);
        parkingLot.parkCar(car);

        registrationNumber = "HJK-970-DN";
        color = "YELLOW";
        Car car2 = new Car(registrationNumber, color);
        parkingLot.parkCar(car2);

        String outCome = parkingLot.getSlotNumberForRegistrationNumber(givenRegistraionNumber);
        assertThat(outCome, is(equalTo(EXPECTED_OUTPUT_WHEN_NOT_FOUND)));
    }

    @Test
    @DisplayName("Should display formatted parking lot status")
    void shouldDisplayFormattedParkingLotStatusOutput() {
        String expectedOutput = "Slot No.\tRegistration No\tColour\n" +
                "1\tK89I-83121-KLD89\tWhite\n" +
                "2\tCKI-8DAA1-HDHAS\tBlue\n" +
                "3\tHJFAH-824832-JKJ\tWhite\n" +
                "4\tFGH-8977-K05GHK\tArmy Green\n" +
                "5\tDADS-532432-HDG\tBlue\n"+
                "6\tGHHFG-232-75645\tArmy Green\n" +
                "7\tFHGD-GSFDS-12132\tYELLOW\n";
        ParkingLot parkingLot = new ParkingLot();
        int capacity = 10;
        parkingLot.createParkingLot(capacity);
        Car car1 = new Car("K89I-83121-KLD89", "White");
        Car car2 = new Car("CKI-8DAA1-HDHAS", "Blue");
        Car car3 = new Car("2121-TWRW-HDGD", "Red");
        Car car4 = new Car("FGH-8977-K05GHK", "Army Green");
        Car car5 = new Car("HJFAH-824832-JKJ", "White");
        Car car6 = new Car("DADS-532432-HDG", "Blue");
        Car car7 = new Car("GHHFG-232-75645", "Army Green");
        Car car8 = new Car("FHGD-GSFDS-12132", "YELLOW");
        parkingLot.parkCar(car1);
        parkingLot.parkCar(car2);
        parkingLot.parkCar(car3);
        parkingLot.parkCar(car4);
        parkingLot.unPark(3);
        parkingLot.parkCar(car5);
        parkingLot.parkCar(car6);
        parkingLot.parkCar(car7);
        parkingLot.parkCar(car8);
        assertThat(parkingLot.status(), is(equalTo(expectedOutput)));
    }

    @Test
    @DisplayName("Should validate create_parking_lot command")
    void shouldValidateCreateParkingLotCommand(){

        ParkingLot parkingLot = new ParkingLot();
        int capacity = 10;
        String expectedResult =String.format(EXPECTED_OUTPUT_FROM_CREATE_PARKING_LOT, capacity);
        String command = "create_parking_lot 10";
        String parkingLotCreated = parkingLot.executeCommand(command);
        assertThat(parkingLotCreated, is(equalTo(expectedResult)));
    }

    @Test
    @DisplayName("Should validate and execute leave command")
    void shouldValidateAndCallLeaveCommand(){
        ParkingLot parkingLot = new ParkingLot();
        int capacity = 3;
        parkingLot.executeCommand(String.format("create_parking_lot %d", capacity));
        parkingLot.parkCar(new Car("K89I-83121-KLD89", "White"));
        parkingLot.parkCar(new Car("CKI-8DAA1-HDHAS", "Blue"));
        parkingLot.parkCar(new Car("2121-TWRW-HDGD", "Red"));

        String command = "leave 2";
        String expectedResult = String.format(EXPECTED_OUTPUT_WHEN_SLOT_NUMBER_IS_FREE, 2);
        assertThat(parkingLot.executeCommand(command), is(equalTo(expectedResult)));
    }

    @Test
    @DisplayName("Should validate and call registration_numbers_for_cars_with_color command")
    void shouldValidateAndCallGetRegistrationNumbersForCarsWithColorCommand(){
        ParkingLot parkingLot = new ParkingLot();
        int capacity = 4;
        parkingLot.executeCommand(String.format("create_parking_lot %d", capacity));
        parkingLot.parkCar(new Car("K89I-83121-KLD89", "Green"));
        parkingLot.parkCar(new Car("CKI-8DAA1-HDHAS", "Blue"));
        parkingLot.parkCar(new Car("2121-TWRW-HDGD", "Red"));
        parkingLot.parkCar(new Car("FGH-8977-K05GHK", "Green"));
        String expectedResult = "K89I-83121-KLD89, FGH-8977-K05GHK";
        String command = "registration_numbers_for_cars_with_colour Green";
        assertThat(parkingLot.executeCommand(command), is(equalTo(expectedResult)));
    }

    @Test
    @DisplayName("Should validate empty command line arguments")
    void shouldValidateInvalidCommandArgument(){
        ParkingLot parkingLot = new ParkingLot();
        int capacity = 4;
        String command = null;
        assertThat(parkingLot.executeCommand(command), is(isEmptyOrNullString()));
    }


    @Test
    @DisplayName("Should validate and execute slot_numbers_for_cars_with_colour command")
    void shouldValidateSlotNumbersForCarsWithColorCommand(){
        ParkingLot parkingLot = new ParkingLot();
        int capacity = 4;
        parkingLot.createParkingLot(capacity);
        String expectedResult = "2, 4";
        String command = "slot_numbers_for_cars_with_color Blue";
        parkingLot.parkCar(new Car("K89I-83121-KLD89", "Green"));
        parkingLot.parkCar(new Car("CKI-8DAA1-HDHAS", "Blue"));
        parkingLot.parkCar(new Car("2121-TWRW-HDGD", "Purple"));
        parkingLot.parkCar(new Car("FGH-8977-K05GHK", "Blue"));
        assertThat(parkingLot.executeCommand(command), is(equalTo(expectedResult)));
    }

    @Test
    @DisplayName("Should validate and execute slot_number_for_registration_number command")
    void shouldValidateSlotNumberForRegistrationNumberCommand(){
        ParkingLot parkingLot = new ParkingLot();
        int capacity = 4;
        parkingLot.createParkingLot(capacity);
        String expectedResult = "4";
        String command = "slot_number_for_registration_number FGH-8977-K05GHK";
        parkingLot.parkCar(new Car("K89I-83121-KLD89", "Green"));
        parkingLot.parkCar(new Car("CKI-8DAA1-HDHAS", "Blue"));
        parkingLot.parkCar(new Car("2121-TWRW-HDGD", "Purple"));
        parkingLot.parkCar(new Car("FGH-8977-K05GHK", "Blue"));
        assertThat(parkingLot.executeCommand(command), is(equalTo(expectedResult)));
    }

    @Test
    @DisplayName("Should validate and execute park command")
    void shouldValidateParkCommand(){
        ParkingLot parkingLot = new ParkingLot();
        int capacity = 4;
        parkingLot.createParkingLot(capacity);
        String expectedResult = String.format(EXPECTED_OUTPUT_WHEN_NEW_SLOT_IS_ALLOCATED, 1);
        String registrationNumber="K89I-83121-KLD89";
        String color = "Green";
        String command = String.format("park %s %s", registrationNumber, color);
        assertThat(parkingLot.executeCommand(command), is(equalTo(expectedResult)));
    }
    @Test
    @DisplayName("Should validate and execute status command")
    void shouldValidateAndExecuteStatusCommand(){
        ParkingLot parkingLot = new ParkingLot();
        int capacity = 8;
        parkingLot.createParkingLot(capacity);
        parkingLot.parkCar(new Car("K89I-83121-KLD89", "White"));
        parkingLot.parkCar(new Car("CKI-8DAA1-HDHAS", "Blue"));
        parkingLot.parkCar(new Car("HJFAH-824832-JKJ", "White"));
        parkingLot.parkCar(new Car("KSAS-809DA-WAAA", "pink"));
        parkingLot.parkCar(new Car("FGH-8977-K05GHK", "Green"));
        parkingLot.parkCar(new Car("FHGD-GSFDS-12132", "Yellow"));
        parkingLot.unPark(4);
        String expectedResult = "Slot No.\tRegistration No\tColour\n" +
                "1\tK89I-83121-KLD89\tWhite\n" +
                "2\tCKI-8DAA1-HDHAS\tBlue\n" +
                "3\tHJFAH-824832-JKJ\tWhite\n" +
             //   "4\tKSAS-809DA-WAAA\tpink\n" +
                "5\tFGH-8977-K05GHK\tGreen\n" +
                "6\tFHGD-GSFDS-12132\tYellow\n";
        String command = "status";
        assertThat(parkingLot.executeCommand(command), is(equalTo(expectedResult)));
    }

    @Test
    @DisplayName("Should ignore invalid entered command")
    void shouldIgnoreInvalidCommandAndExecuteNothing(){
        ParkingLot parkingLot = new ParkingLot();
        String command = "non-existent";
        assertThat(parkingLot.executeCommand(command), is(isEmptyString()));
    }
}
