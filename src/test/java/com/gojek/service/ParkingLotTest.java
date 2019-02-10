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
        int parkingLotCapacity = 4;
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.createParkingLot(parkingLotCapacity);
        String registrationNumber = "JPX-876-GP";
        String color = "FFFXDX0";
        Car car = new Car(registrationNumber, color);
        String outcome = parkingLot.parkCar(car);
        //assertThat(parkingLot.getParkingSpaces().size(), is(1));
        assertEquals(String.format(EXPECTED_OUTPUT_WHEN_NEW_SLOT_IS_ALLOCATED, 1), outcome);
    }
}
