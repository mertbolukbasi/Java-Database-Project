package model;

import database.Database;
import utils.DrawMenu;
import utils.Input;

import java.sql.*;
import java.util.ArrayList;

public class Tester extends User {

    @Override
    public void showUserMenu() throws SQLException {
        while (true) {
            String title = "Welcome " + this.getName() + " " + this.getSurname() + ", " + this.getRole();
            String[] contents = {
                    "",
                    DrawMenu.YELLOW_BOLD + "[1] List All Contacts" + DrawMenu.RESET,
                    DrawMenu.YELLOW_BOLD + "[2] Search Contact (Single Field)" + DrawMenu.RESET,
                    DrawMenu.YELLOW_BOLD + "[3] Search Contact (Multiple Fields)" + DrawMenu.RESET,
                    DrawMenu.YELLOW_BOLD + "[4] Change Password" + DrawMenu.RESET,
                    DrawMenu.YELLOW_BOLD + "[5] Logout" + DrawMenu.RESET,
                    ""
            };
            DrawMenu.printBoxed(title, contents);
            System.out.println();
            DrawMenu.printCenter("Your choice: ");

            int input = 0;
            try {
                input = Input.getIntInput();
            } catch (Exception e) {
                DrawMenu.clearConsole();
                System.out.println("Invalid input. Please enter a number between 1 and 5.");
                continue;
            }

            switch (input) {
                case 1:
                    DrawMenu.clearConsole();
                    this.listAllContacts();
                    break;
                case 2:
                    DrawMenu.clearConsole();
                    this.searchContact();
                    break;
                case 3:
                    DrawMenu.clearConsole();
                    this.searchBySelectedFields();
                    break;
                case 4:
                    this.changePassword();
                    break;
                case 5:
                    this.logout();
                    return;
                default:
                    DrawMenu.clearConsole();
                    System.out.println("Invalid selection. Please enter a number between 1 and 5.");
                    break;
            }
        }
    }

    public ArrayList<Contact> getAllContacts() throws SQLException {
        ArrayList<Contact> contactList = new ArrayList<>();
        String query = "SELECT * FROM contacts";

        Connection dbConnection = Database.openDatabase();
        Statement statement = dbConnection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        while (resultSet.next()) {
            Contact c = new Contact();
            c.setContactId(resultSet.getInt("contact_id"));
            c.setFirstName(resultSet.getString("first_name"));
            c.setLastName(resultSet.getString("last_name"));
            c.setPhonePrimary(resultSet.getString("phone_primary"));
            c.setEmail(resultSet.getString("email"));
            c.setBirthDate(resultSet.getDate("birth_date"));
            c.setCreatedAt(resultSet.getDate("created_at"));
            c.setUpdatedAt(resultSet.getDate("updated_at"));

            contactList.add(c);
        }
        dbConnection.close();
        return contactList;
    }

    public void listAllContacts() {
        DrawMenu.clearConsole();
        try {
            ArrayList<Contact> contacts = this.getAllContacts();

            if (contacts.isEmpty()) {
                System.out.println(DrawMenu.RED_BOLD + "No contacts found in the database!" + DrawMenu.RESET);
                System.out.println("Press Enter to return...");
                Input.getStringInput();
                return;
            }

            boolean loop = true;
            int contactCount = contacts.size();
            int currentContactIndex = 1;

            while(loop) {
                String inputStr;

                label:
                while(true) {
                    DrawMenu.clearConsole();

                    Contact currentContact = contacts.get(currentContactIndex - 1);

                    printContactDetails(currentContact);

                    System.out.println();
                    DrawMenu.printCenter("< " + currentContactIndex + "/" + contactCount + " >");
                    System.out.println();
                    DrawMenu.printCenter("Previous: A, Next: D, Exit: Type 'exit'");
                    System.out.println();
                    DrawMenu.printCenter("Your choice: ");

                    inputStr = Input.getStringInput().toLowerCase();

                    switch (inputStr) {
                        case "a":
                            if (currentContactIndex > 1) {
                                currentContactIndex--;
                                break label;
                            } else {
                                DrawMenu.clearConsole();
                                printContactDetails(currentContact);
                                System.out.println("\n" + DrawMenu.RED_BOLD + "You reached the start of the list." + DrawMenu.RESET);
                                System.out.println("Press Enter to continue...");
                                Input.getStringInput();
                                break label;
                            }
                        case "d":
                            if (currentContactIndex < contactCount) {
                                currentContactIndex++;
                                break label;
                            } else {
                                DrawMenu.clearConsole();
                                printContactDetails(currentContact);
                                System.out.println("\n" + DrawMenu.RED_BOLD + "You reached the end of the list." + DrawMenu.RESET);
                                System.out.println("Press Enter to continue...");
                                Input.getStringInput();
                                break label;
                            }
                        case "exit":
                            loop = false;
                            break label;
                        default:
                            DrawMenu.clearConsole();
                            printContactDetails(currentContact);
                            System.out.println("\n" + DrawMenu.RED_BOLD + "Invalid input. Use A, D or exit." + DrawMenu.RESET);
                            System.out.println("Press Enter to continue...");
                            Input.getStringInput();
                            break label;
                    }
                }
            }
        } catch (SQLException e) {
            DrawMenu.clearConsole();
            System.out.println(DrawMenu.RED_BOLD + "Database Error: Contacts could not be fetched. " + e.getMessage() + DrawMenu.RESET);
            System.out.println("Press Enter to return...");
            Input.getStringInput();
        }
        DrawMenu.clearConsole();
    }

    private void printContactDetails(Contact c) {
        String title = c.getFirstName() + " " + c.getLastName();
        String[] contents = {
                "",
                DrawMenu.YELLOW_BOLD + "ID: " + DrawMenu.RESET + c.getContactId(),
                DrawMenu.YELLOW_BOLD + "Phone: " + DrawMenu.RESET + c.getPhonePrimary(),
                DrawMenu.YELLOW_BOLD + "Email: " + DrawMenu.RESET + (c.getEmail() != null ? c.getEmail() : "N/A"),
                DrawMenu.YELLOW_BOLD + "Birth Date: " + DrawMenu.RESET + (c.getBirthDate() != null ? c.getBirthDate() : "N/A"),
                ""
        };
        DrawMenu.printBoxed(title, contents);
    }

    public void searchContact() {
        java.util.Scanner sc = new java.util.Scanner(System.in);

        while (true) {
            DrawMenu.clearConsole();
            String title = "Search Contact (Single Field)";
            String[] options = {
                    "",
                    DrawMenu.YELLOW_BOLD + "[1] Search by First Name" + DrawMenu.RESET,
                    DrawMenu.YELLOW_BOLD + "[2] Search by Last Name" + DrawMenu.RESET,
                    DrawMenu.YELLOW_BOLD + "[3] Search by Phone Number" + DrawMenu.RESET,
                    DrawMenu.YELLOW_BOLD + "[4] Return to Menu" + DrawMenu.RESET,
                    ""
            };
            DrawMenu.printBoxed(title, options);
            System.out.println();
            DrawMenu.printCenter("Select a field: ");

            String choiceStr = sc.nextLine().trim();
            int choice;
            try {
                choice = Integer.parseInt(choiceStr);
            } catch (Exception e) {
                System.out.println(DrawMenu.RED_BOLD + "Invalid input. Please enter a number between 1 and 4." + DrawMenu.RESET);
                System.out.println("Press Enter to try again...");
                sc.nextLine();
                continue;
            }

            String dbColumn = "";
            String displayName = "";

            switch (choice) {
                case 1:
                    dbColumn = "first_name";
                    displayName = "First Name";
                    break;
                case 2:
                    dbColumn = "last_name";
                    displayName = "Last Name";
                    break;
                case 3:
                    dbColumn = "phone_primary";
                    displayName = "Phone Number";
                    break;
                case 4:
                    DrawMenu.clearConsole();
                    return;
                default:
                    System.out.println(DrawMenu.RED_BOLD + "Invalid input. Please enter a number between 1 and 4." + DrawMenu.RESET);
                    System.out.println("Press Enter to try again...");
                    sc.nextLine();
                    continue;
            }


            System.out.println();
            DrawMenu.printCenter("Enter " + displayName + " to search: ");
            String searchTerm = sc.nextLine().trim();

            ArrayList<Contact> results = new ArrayList<>();
            String query = "SELECT * FROM contacts WHERE " + dbColumn + " LIKE '%" + searchTerm + "%' COLLATE utf8mb4_turkish_ci";

            try {
                Connection dbConnection = Database.openDatabase();
                Statement statement = dbConnection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);

                while (resultSet.next()) {
                    Contact c = new Contact();
                    c.setContactId(resultSet.getInt("contact_id"));
                    c.setFirstName(resultSet.getString("first_name"));
                    c.setLastName(resultSet.getString("last_name"));
                    c.setPhonePrimary(resultSet.getString("phone_primary"));
                    c.setEmail(resultSet.getString("email"));
                    c.setBirthDate(resultSet.getDate("birth_date"));
                    results.add(c);
                }
                dbConnection.close();
            } catch (SQLException e) {
                System.out.println(DrawMenu.RED_BOLD + "Error: " + e.getMessage() + DrawMenu.RESET);
                sc.nextLine();
                return;
            }


            if (results.isEmpty()) {
                System.out.println(DrawMenu.RED_BOLD + "No contacts found matching '" + searchTerm + "'!" + DrawMenu.RESET);
                System.out.println("Press Enter to try again...");
                sc.nextLine();
                continue;
            }

            boolean loop = true;
            int resultCount = results.size();
            int currentIndex = 1;

            while (loop) {
                String inputStr;
                label:
                while (true) {
                    DrawMenu.clearConsole();

                    Contact currentContact = results.get(currentIndex - 1);

                    System.out.println(DrawMenu.GREEN_BOLD + "SEARCH RESULTS FOR: '" + searchTerm + "' : " + resultCount + DrawMenu.RESET);

                    printContactDetails(currentContact);

                    System.out.println();
                    DrawMenu.printCenter("< " + currentIndex + "/" + resultCount + " >");
                    System.out.println();
                    DrawMenu.printCenter("Previous: A, Next: D, Exit: Type 'exit'");
                    System.out.println();
                    DrawMenu.printCenter("Your choice: ");

                    inputStr = sc.nextLine().toLowerCase().trim();

                    switch (inputStr) {
                        case "a":
                            if (currentIndex > 1) {
                                currentIndex--;
                                break label;
                            } else {
                                System.out.println(DrawMenu.RED_BOLD + "You reached the start of the search results." + DrawMenu.RESET);
                                System.out.println("Press Enter to continue...");
                                sc.nextLine();
                                break label;
                            }
                        case "d":
                            if (currentIndex < resultCount) {
                                currentIndex++;
                                break label;
                            } else {
                                System.out.println(DrawMenu.RED_BOLD + "You reached the end of the search results." + DrawMenu.RESET);
                                System.out.println("Press Enter to continue...");
                                sc.nextLine();
                                break label;
                            }
                        case "exit":
                            loop = false;
                            break label;
                        default:
                            System.out.println(DrawMenu.RED_BOLD + "Invalid input. Use A, D or exit." + DrawMenu.RESET);
                            System.out.println("Press Enter to continue...");
                            sc.nextLine();
                            break label;
                    }
                }
            }
        }
    }

    public void searchBySelectedFields() {
        java.util.Scanner sc = new java.util.Scanner(System.in);
        while (true) {
            DrawMenu.clearConsole();
            String title = "Search Contact (Multiple Fields)";
            String[] info = {
                    "",
                    DrawMenu.YELLOW_BOLD + "INSTRUCTIONS:" + DrawMenu.RESET,
                    "Enter criteria for the fields you want to search.",
                    "Press " + DrawMenu.YELLOW_BOLD + "[ENTER]" + DrawMenu.RESET + " to skip a field (leave it empty).",
                    "Type " + DrawMenu.RED_BOLD + "'exit'" + DrawMenu.RESET + " in any field to return menu.",
                    ""
            };
            DrawMenu.printBoxed(title, info);
            System.out.println();

            System.out.print(DrawMenu.BLUE_BOLD + "First Name: " + DrawMenu.RESET);
            String fName = Input.getStringInput().trim();
            if (fName.equalsIgnoreCase("exit")) {
                DrawMenu.clearConsole();
                return;
            }

            System.out.print(DrawMenu.BLUE_BOLD + "Last Name:  " + DrawMenu.RESET);
            String lName = Input.getStringInput().trim();
            if (lName.equalsIgnoreCase("exit")) {
                DrawMenu.clearConsole();
                return;
            }

            System.out.print(DrawMenu.BLUE_BOLD + "Phone:      " + DrawMenu.RESET);
            String phone = Input.getStringInput().trim();
            if (phone.equalsIgnoreCase("exit")) {
                DrawMenu.clearConsole();
                return;
            }

            System.out.print(DrawMenu.BLUE_BOLD + "Email:      " + DrawMenu.RESET);
            String email = Input.getStringInput().trim();
            if (email.equalsIgnoreCase("exit")) {
                DrawMenu.clearConsole();
                return;
            }

            if (fName.isEmpty() && lName.isEmpty() && phone.isEmpty() && email.isEmpty()) {
                System.out.println(DrawMenu.RED_BOLD + "\nYou didn't enter any criteria!" + DrawMenu.RESET);
                System.out.println("Press Enter to try again...");
                sc.nextLine();
                continue;
            }

            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM contacts WHERE 1=1");

            if (!fName.isEmpty()) queryBuilder.append(" AND first_name LIKE '%").append(fName).append("%' COLLATE utf8mb4_turkish_ci");
            if (!lName.isEmpty()) queryBuilder.append(" AND last_name LIKE '%").append(lName).append("%' COLLATE utf8mb4_turkish_ci");
            if (!phone.isEmpty()) queryBuilder.append(" AND phone_primary LIKE '%").append(phone).append("%' COLLATE utf8mb4_turkish_ci");
            if (!email.isEmpty()) queryBuilder.append(" AND email LIKE '%").append(email).append("%' COLLATE utf8mb4_turkish_ci");

            ArrayList<Contact> results = new ArrayList<>();
            try {
                Connection dbConnection = Database.openDatabase();
                Statement statement = dbConnection.createStatement();
                ResultSet resultSet = statement.executeQuery(queryBuilder.toString());

                while (resultSet.next()) {
                    Contact c = new Contact();
                    c.setContactId(resultSet.getInt("contact_id"));
                    c.setFirstName(resultSet.getString("first_name"));
                    c.setLastName(resultSet.getString("last_name"));
                    c.setPhonePrimary(resultSet.getString("phone_primary"));
                    c.setEmail(resultSet.getString("email"));
                    c.setBirthDate(resultSet.getDate("birth_date"));
                    results.add(c);
                }
                dbConnection.close();
            } catch (SQLException e) {
                System.out.println(DrawMenu.RED_BOLD + "Database Error: " + e.getMessage() + DrawMenu.RESET);
                sc.nextLine();
                return;
            }

            DrawMenu.clearConsole();
            if (results.isEmpty()) {
                System.out.println(DrawMenu.RED_BOLD + "No contacts found matching your criteria." + DrawMenu.RESET);
                System.out.println("Press Enter to try again...");
                sc.nextLine();
                continue;
            }

            boolean loop = true;
            int resultCount = results.size();
            int currentIndex = 1;

            while (loop) {
                String inputStr;
                label:
                while (true) {
                    DrawMenu.clearConsole();

                    Contact currentContact = results.get(currentIndex - 1);

                    System.out.println(DrawMenu.GREEN_BOLD + "MULTI-SEARCH RESULTS (" + resultCount + " matches)" + DrawMenu.RESET);

                    printContactDetails(currentContact);

                    System.out.println();
                    DrawMenu.printCenter("< " + currentIndex + "/" + resultCount + " >");
                    System.out.println();
                    DrawMenu.printCenter("Previous: A, Next: D, Exit: Type 'exit'");
                    System.out.println();
                    DrawMenu.printCenter("Your choice: ");

                    inputStr = sc.nextLine().toLowerCase().trim();

                    switch (inputStr) {
                        case "a":
                            if (currentIndex > 1) {
                                currentIndex--;
                                break label;
                            } else {
                                System.out.println(DrawMenu.RED_BOLD + "You reached the start of the search results." + DrawMenu.RESET);
                                System.out.println("Press Enter to continue...");
                                sc.nextLine();
                                break label;
                            }
                        case "d":
                            if (currentIndex < resultCount) {
                                currentIndex++;
                                break label;
                            } else {
                                System.out.println(DrawMenu.RED_BOLD + "You reached the end of the search results." + DrawMenu.RESET);
                                System.out.println("Press Enter to continue...");
                                sc.nextLine();
                                break label;
                            }
                        case "exit":
                            loop = false;
                            break label;
                        default:
                            System.out.println(DrawMenu.RED_BOLD + "Invalid input. Use A, D or exit." + DrawMenu.RESET);
                            System.out.println("Press Enter to continue...");
                            sc.nextLine();
                            break label;
                    }
                }
            }
        }
    }
}
