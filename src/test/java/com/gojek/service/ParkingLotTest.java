package com.gojek.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@DisplayName("Unit tests assertions for ParkingLot service ")
class ParkingLotTest {

    private static final String EXPECTED_OUTPUT_FROM_CREATE_PARKING_LOT="Created a parking lot with %s slots";
    @Test
    @DisplayName("Should be able to create a parking lot with 10 spaces")
    void shouldCreateParkingLotOfSizeTen(){
        int parkingLotCapacity = 10;
        ParkingLot parkingLot = new ParkingLot();
        assertThat(parkingLot.createParkingLot(parkingLotCapacity), is(equalTo(String.format(EXPECTED_OUTPUT_FROM_CREATE_PARKING_LOT, parkingLotCapacity))));
    }
}
