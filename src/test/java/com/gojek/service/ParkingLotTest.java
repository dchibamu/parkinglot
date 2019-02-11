package com.gojek.service;

import com.gojek.domain.Car;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@DisplayName("Unit tests assertions for ParkingLot service ")
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

        registrationNumber = "JKLA-9897-HR";
        color = "BLUE";
        Car car4 = new Car(registrationNumber, color);
        parkingLot.parkCar(car4);

        registrationNumber = "THKAL-7819321";
        color = "BLUE";
        Car car5 = new Car(registrationNumber, color);
        parkingLot.parkCar(car5);

        String outCome = parkingLot.getSlotNumberForRegistrationNumber(givenRegistraionNumber);
        assertThat(outCome, is(equalTo(5)));
    }

}
