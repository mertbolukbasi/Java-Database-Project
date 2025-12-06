package utils;

import java.util.Scanner;

public class Input {

    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Get string input from user.
     * @author Mert Bölükbaşı
     */
    public static String getStringInput() {
        return scanner.nextLine().trim();
    }

    /**
     * Get integer input from user.
     * @author Mert Bölükbaşı
     */
    public static int getIntInput() throws NumberFormatException {
        String input = getStringInput();
        return Integer.parseInt(input);
    }
}