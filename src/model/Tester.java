package model;

import database.Database;
import utils.DrawMenu;
import utils.Input;

import java.sql.*;
import java.util.ArrayList;

public class Tester extends User {

    @Override
    public void showUserMenu() {
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
                    DrawMenu.printCenter(DrawMenu.YELLOW_BOLD + "< " + currentContactIndex + "/" + contactCount + " >" + DrawMenu.RESET);
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
        //TODO
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
                continue;
            }


            String dbColumn = "";
            String displayName = "";

            if (choice == 1) { dbColumn = "first_name"; displayName = "First Name"; }
            else if (choice == 2) { dbColumn = "last_name"; displayName = "Last Name"; }
            else if (choice == 3) { dbColumn = "phone_primary"; displayName = "Phone Number"; }
            else if (choice == 4) { return; }
            else { continue; }


            System.out.println();
            DrawMenu.printCenter("Enter " + displayName + " to search: ");
            String searchTerm = sc.nextLine().trim();

            ArrayList<Contact> results = new ArrayList<>();
            String query = "SELECT * FROM contacts WHERE " + dbColumn + " LIKE '%" + searchTerm + "%'";

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
                    DrawMenu.printCenter(DrawMenu.YELLOW_BOLD + "< " + currentIndex + "/" + resultCount + " >" + DrawMenu.RESET);
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
                                System.out.println(DrawMenu.RED_BOLD + "Start of search results." + DrawMenu.RESET);
                                sc.nextLine();
                                break label;
                            }
                        case "d":
                            if (currentIndex < resultCount) {
                                currentIndex++;
                                break label;
                            } else {
                                System.out.println(DrawMenu.RED_BOLD + "End of search results." + DrawMenu.RESET);
                                sc.nextLine();
                                break label;
                            }
                        case "exit":
                            loop = false;
                            break label;
                        default:
                            System.out.println(DrawMenu.RED_BOLD + "Invalid input. Use A, D or exit." + DrawMenu.RESET);
                            sc.nextLine();
                            break label;
                    }
                }
            }
        }
    }

    public ArrayList<Contact> searchContactsByField(String columnName, String value) throws SQLException {
        ArrayList<Contact> results = new ArrayList<>();

        String query = "SELECT * FROM contacts WHERE " + columnName + " LIKE '%" + value + "%'";

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
        return results;
    }

    public void searchBySelectedFields() {
        //TODO
    }
}
