package utils;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class Input {

    private static final Scanner scanner = new Scanner(System.in);

    public static String getStringInput() {
        return scanner.nextLine().trim();
    }

    public static int getIntInput() throws NumberFormatException {
        String input = getStringInput();
        return Integer.parseInt(input);
    }
}