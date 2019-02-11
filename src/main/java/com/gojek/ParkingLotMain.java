package com.gojek;

import com.gojek.service.ParkingLot;
import org.apache.commons.lang3.StringUtils;

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

    private static void runInteractiveParkingLot(){
        do {
            Scanner scanner = new Scanner(System.in);
            String nextCommand = scanner.nextLine();
            if(StringUtils.isNotBlank(nextCommand) && nextCommand.equalsIgnoreCase(EXIT))
                break;
            //TODO: validate command from console
            //TODO: invoke executeCommand(nextCommand) on parkingLot instance
        }while(true);
    }

    private static void runFileParkingLot(Path filePath){
        ParkingLot parkingLot = new ParkingLot();
        try(Stream<String> commandStream = Files.lines(filePath)){
            //TODO: call decision making method from ParkingLot
        }catch (IOException e){
            //correct approach is to log the error, however, for this challenge am not sure what effect that logging has on
            //the automated testing platform.
        }
    }
}
