package utils;

import database.Database;
import model.Manager;
import model.Tester;
import model.User;

import java.sql.SQLException;
import java.util.NoSuchElementException;

public class DrawMenu {

    private static final int WIDTH = 80;

    public static final String ITALIC = "\033[3m";
    public static final String RESET = "\033[0m";

    public static final String RED = "\033[0;31m";
    public static final String GREEN = "\033[0;32m";
    public static final String YELLOW = "\033[0;33m";
    public static final String BLUE = "\033[0;34m";
    public static final String PURPLE = "\033[0;35m";
    public static final String CYAN = "\033[0;36m";
    public static final String LIGHT_GRAY = "\033[38;5;250m";

    public static final String RED_BOLD = "\033[1;31m";
    public static final String GREEN_BOLD = "\033[1;32m";
    public static final String YELLOW_BOLD = "\033[1;33m";
    public static final String BLUE_BOLD = "\033[1;34m";
    public static final String PURPLE_BOLD = "\033[1;35m";
    public static final String CYAN_BOLD = "\033[1;36m";
    public static final String WHITE_BOLD = "\033[1;37m";

    private static final String TOP_LEFT = "┌";
    private static final String TOP_RIGHT = "┐";
    private static final String BOTTOM_LEFT = "└";
    private static final String BOTTOM_RIGHT = "┘";
    private static final String VERTICAL  = "│";
    private static final String HORIZONTAL  = "─";

    public static void clearConsole(){
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static void printRow(String text) {
        String cleanText = text.replaceAll("\033\\[[;\\d]*m", "");

        int len = cleanText.length();
        int space = WIDTH - 2;
        int padLeft = (space - len) / 2;
        int padRight = space - len - padLeft;

        System.out.print(VERTICAL);
        for (int i = 0; i < padLeft; i++) System.out.print(" ");
        System.out.print(text);
        for (int i = 0; i < padRight; i++) System.out.print(" ");
        System.out.println(VERTICAL);
    }

    public static void printBoxed(String title, String[] content) {
        System.out.print(TOP_LEFT);
        for (int i = 0; i < WIDTH - 2; i++) System.out.print(HORIZONTAL);
        System.out.println(TOP_RIGHT);

        printRow(title.toUpperCase());

        System.out.print("├");
        for (int i = 0; i < WIDTH - 2; i++) System.out.print(HORIZONTAL);
        System.out.println("┤");

        for (String line : content) {
            printRow(line);
        }

        System.out.print(BOTTOM_LEFT);
        for (int i = 0; i < WIDTH - 2; i++) System.out.print(HORIZONTAL);
        System.out.println(BOTTOM_RIGHT);
    }

    public static void printCenter(String text) {
        for(int i = 0; i<(WIDTH - text.length())/2; i++) System.out.print(" ");
        System.out.print(text);
    }

    public static void showLoginScreen() {

        String username = BLUE_BOLD + "Username" + ":" + RESET + LIGHT_GRAY + ITALIC + " write your username" + RESET;
        String password = BLUE_BOLD + "Password" + ":" + RESET + LIGHT_GRAY + ITALIC + " write your password" + RESET;
        String[] contents = {"",username, password, ""};
        printBoxed("Login", contents);

        System.out.println();
        printCenter("Username: ");
        try {
            String usernameInput = Input.getStringInput();
            clearConsole();
            String username2 = BLUE_BOLD + "Username" + ": " + RESET + LIGHT_GRAY + ITALIC + usernameInput + RESET;
            String[] contents2 = {"",username2, password, ""};
            printBoxed("Login", contents2);
            System.out.println();

            printCenter("Username: " + usernameInput);
            System.out.println();
            printCenter("Password: ");
            String passwordInput = Input.getStringInput();
            clearConsole();
            String password2 = BLUE_BOLD + "Password" + ": " + RESET + LIGHT_GRAY + ITALIC + passwordInput + RESET;
            String[] contents3 = {"",username2, password2, ""};
            printBoxed("Login", contents3);

            User user = Database.login(usernameInput, passwordInput);
            if(user == null) {
                clearConsole();
                System.out.println(RED_BOLD + "Username or password is incorrect. Please try again." + RESET);
                showLoginScreen();
            }
            else {
                System.out.println(GREEN_BOLD + "Login is successful, directing..." + RESET);
                Thread.sleep(1000);
                clearConsole();
                switch(user.getRole()) {
                    case "Manager":
                        Manager manager = new Manager();
                        manager.setUserId(user.getUserId());
                        manager.setUsername(user.getUsername());
                        manager.setPassword_hash(user.getPassword_hash());
                        manager.setName(user.getName());
                        manager.setSurname(user.getSurname());
                        manager.setRole(user.getRole());
                        manager.setCreated_at(user.getCreated_at());
                        manager.setUpdated_at(user.getUpdated_at());
                        manager.showUserMenu();
                        break;

                    case "Tester":
                        Tester tester = new Tester();
                        tester.setUserId(user.getUserId());
                        tester.setUsername(user.getUsername());
                        tester.setPassword_hash(user.getPassword_hash());
                        tester.setName(user.getName());
                        tester.setSurname(user.getSurname());
                        tester.setRole(user.getRole());
                        tester.setCreated_at(user.getCreated_at());
                        tester.setUpdated_at(user.getUpdated_at());
                        tester.showUserMenu();
                        break;
                }


            }
        } catch (SQLException e) {
            clearConsole();
            System.out.println(RED_BOLD + "Database Error." + RESET);
            showLoginScreen();
        } catch (InterruptedException e) {
            clearConsole();
            System.out.println(RED_BOLD + "Runtime Error." + RESET);
            showLoginScreen();
        }

    }

    public static void showUserList(User user) {
        String title = user.getName() + " " + user.getSurname();
        String[] contents = {
                CYAN_BOLD + "id: " + user.getUserId() + RESET,
                CYAN_BOLD + "username: " + user.getUsername() + RESET,
                CYAN_BOLD + "password: " + user.getPassword_hash() + RESET,
                CYAN_BOLD + "name: " + user.getName() + RESET,
                CYAN_BOLD + "surname: " + user.getSurname() + RESET,
                CYAN_BOLD + "role: " + user.getRole() + RESET,
                CYAN_BOLD + "created_at: " + user.getCreated_at() + RESET,
                CYAN_BOLD + "updated_at: " + user.getUpdated_at() + RESET
        };

        printBoxed(title, contents);
    }


}

