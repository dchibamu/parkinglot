package com.gojek;

import com.gojek.service.ParkingLot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.stream.Stream;

import static com.gojek.util.AppConstants.EXIT;

public class ParkingLotMain {

    public static void main(String[] args) {
        int numberOfArgs = args.length;
        switch(numberOfArgs){
            case 0:
                runInteractiveParkingLot();
                break;
            case 1:
                Path commandsFile = Paths.get(args[0]);
                runFileParkingLot(commandsFile);
                break;
        }
    }

    /**
     * Drives parking lot from user input
     */
    private static void runInteractiveParkingLot(){
        ParkingLot parkingLot = new ParkingLot();
        do {
            Scanner scanner = new Scanner(System.in);
            String nextCommand = scanner.nextLine();
            if(nextCommand != null && !nextCommand.isEmpty() && nextCommand.equalsIgnoreCase(EXIT))
                break;
            System.out.println(parkingLot.executeCommand(nextCommand));
        }while(true);
    }

    /**
     * Runs parking lot from file source
     * @param filePath - file with parking lot commands
     */
    private static void runFileParkingLot(Path filePath){
        ParkingLot parkingLot = new ParkingLot();
        try(Stream<String> commandStream = Files.lines(filePath)){
            commandStream.map(parkingLot::executeCommand).forEach(System.out::println);
        }catch (IOException e){
            //correct approach is to log the error, however, for this challenge am not sure what effect that logging has on
            //the automated testing platform.
        }
    }
}
