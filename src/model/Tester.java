package model;

import database.Database;
import utils.DrawMenu;
import utils.Input;

import java.sql.*;
import java.util.ArrayList;
/**
 * Represents the Tester role.
 * Provides list, search, and sort functions.
 * This class extends the User class to inherit some user's functions.
 * @author Oğuzhan Aydın
 */
public class Tester extends User {

    /**
     * Displays the main menu
     * @throws SQLException if a database access error occurs.
     * @author Oğuzhan Aydın
     */
    @Override
    public void showUserMenu() throws SQLException {
        while (true) {
            String title = "Welcome " + this.getName() + " " + this.getSurname() + ", " + this.getRole();
            String[] contents = {
                    "",
                    DrawMenu.YELLOW_BOLD + "[1] List All Contacts" + DrawMenu.RESET,
                    DrawMenu.YELLOW_BOLD + "[2] Search Contact (Single Field)" + DrawMenu.RESET,
                    DrawMenu.YELLOW_BOLD + "[3] Search Contact (Multiple Fields)" + DrawMenu.RESET,
                    DrawMenu.YELLOW_BOLD + "[4] Sort Contacts" + DrawMenu.RESET,
                    DrawMenu.YELLOW_BOLD + "[5] Change Password" + DrawMenu.RESET,
                    DrawMenu.YELLOW_BOLD + "[6] Logout" + DrawMenu.RESET,
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
                System.out.println("Invalid input. Please enter a number between 1 and 6.");
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
                    this.sortContacts();
                    break;
                case 5:
                    this.changePassword();
                    break;
                case 6:
                    this.logout();
                    return;
                default:
                    DrawMenu.clearConsole();
                    System.out.println("Invalid selection. Please enter a number between 1 and 6.");
                    break;
            }
        }
    }

    /**
     * Retrieves all contact records from the database.
     * @return An ArrayList containing all contacts.
     * @throws SQLException if a database access error occurs.
     * @author Oğuzhan Aydın
     */
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
            c.setNickname(resultSet.getString("nickname"));
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

    /**
     * Lists all contacts with pagination (A/D).
     * @author Oğuzhan Aydın
     */
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
                            DrawMenu.clearConsole();
                            this.showUserMenu();
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

    /**
     * Helper method to display a contact's details in a formatted box.
     * @param c The contact object to display.
     * @author Oğuzhan Aydın
     */
    private void printContactDetails(Contact c) {
        String title = c.getFirstName() + " " + c.getLastName();
        String[] contents = {
                "",
                DrawMenu.YELLOW_BOLD + "ID: " + DrawMenu.RESET + c.getContactId(),
                DrawMenu.YELLOW_BOLD + "Nickname: " + DrawMenu.RESET + (c.getNickname() != null ? c.getNickname() : "N/A"),
                DrawMenu.YELLOW_BOLD + "Phone: " + DrawMenu.RESET + c.getPhonePrimary(),
                DrawMenu.YELLOW_BOLD + "Email: " + DrawMenu.RESET + (c.getEmail() != null ? c.getEmail() : "N/A"),
                DrawMenu.YELLOW_BOLD + "Birth Date: " + DrawMenu.RESET + (c.getBirthDate() != null ? c.getBirthDate() : "N/A"),
                DrawMenu.YELLOW_BOLD + "Created At: " + DrawMenu.RESET + (c.getCreatedAt() != null ? c.getCreatedAt() : "N/A"),
                DrawMenu.YELLOW_BOLD + "Updated At: " + DrawMenu.RESET + (c.getUpdatedAt() != null ? c.getUpdatedAt() : "N/A"),
                ""
        };
        DrawMenu.printBoxed(title, contents);
    }

    /**
     * Performs a search based on a single selected field.
     * Displays results with pagination.
     * @author Oğuzhan Aydın
     */
    public void searchContact() {

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

            String choiceStr = Input.getStringInput();

            int choice;
            try {
                choice = Integer.parseInt(choiceStr);
            } catch (Exception e) {
                System.out.println(DrawMenu.RED_BOLD + "Invalid input. Please enter a number between 1 and 4." + DrawMenu.RESET);
                System.out.println("Press Enter to try again...");
                Input.getStringInput();
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
                    Input.getStringInput();
                    continue;
            }


            System.out.println();
            DrawMenu.printCenter("Enter " + displayName + " to search: ");
            String searchTerm = Input.getStringInput();

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
                    c.setNickname(resultSet.getString("nickname"));
                    c.setPhonePrimary(resultSet.getString("phone_primary"));
                    c.setEmail(resultSet.getString("email"));
                    c.setBirthDate(resultSet.getDate("birth_date"));
                    c.setCreatedAt(resultSet.getDate("created_at"));
                    c.setUpdatedAt(resultSet.getDate("updated_at"));
                    results.add(c);
                }
                dbConnection.close();
            } catch (SQLException e) {
                System.out.println(DrawMenu.RED_BOLD + "Error: " + e.getMessage() + DrawMenu.RESET);
                Input.getStringInput();
                return;
            }


            if (results.isEmpty()) {
                System.out.println(DrawMenu.RED_BOLD + "No contacts found matching '" + searchTerm + "'!" + DrawMenu.RESET);
                System.out.println("Press Enter to try again...");
                Input.getStringInput();
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

                    inputStr = Input.getStringInput().toLowerCase();

                    switch (inputStr) {
                        case "a":
                            if (currentIndex > 1) {
                                currentIndex--;
                                break label;
                            } else {
                                System.out.println(DrawMenu.RED_BOLD + "You reached the start of the search results." + DrawMenu.RESET);
                                System.out.println("Press Enter to continue...");
                                Input.getStringInput();
                                break label;
                            }
                        case "d":
                            if (currentIndex < resultCount) {
                                currentIndex++;
                                break label;
                            } else {
                                System.out.println(DrawMenu.RED_BOLD + "You reached the end of the search results." + DrawMenu.RESET);
                                System.out.println("Press Enter to continue...");
                                Input.getStringInput();
                                break label;
                            }
                        case "exit":
                            loop = false;
                            break label;
                        default:
                            System.out.println(DrawMenu.RED_BOLD + "Invalid input. Use A, D or exit." + DrawMenu.RESET);
                            System.out.println("Press Enter to continue...");
                            Input.getStringInput();
                            break label;
                    }
                }
            }
        }
    }

    /**
     * Performs an advanced search using multiple criteria.
     * Supports many search combinations.
     * @author Oğuzhan Aydın
     */
    public void searchBySelectedFields() {

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
            String fName = Input.getStringInput();
            if (fName.equalsIgnoreCase("exit")) {
                DrawMenu.clearConsole();
                return;
            }

            System.out.print(DrawMenu.BLUE_BOLD + "Last Name:  " + DrawMenu.RESET);
            String lName = Input.getStringInput();
            if (lName.equalsIgnoreCase("exit")) {
                DrawMenu.clearConsole();
                return;
            }

            System.out.print(DrawMenu.BLUE_BOLD + "Phone:      " + DrawMenu.RESET);
            String phone = Input.getStringInput();
            if (phone.equalsIgnoreCase("exit")) {
                DrawMenu.clearConsole();
                return;
            }

            System.out.print(DrawMenu.BLUE_BOLD + "Email:      " + DrawMenu.RESET);
            String email = Input.getStringInput();
            if (email.equalsIgnoreCase("exit")) {
                DrawMenu.clearConsole();
                return;
            }

            if (fName.isEmpty() && lName.isEmpty() && phone.isEmpty() && email.isEmpty()) {
                System.out.println(DrawMenu.RED_BOLD + "\nYou didn't enter any criteria!" + DrawMenu.RESET);
                System.out.println("Press Enter to try again...");
                Input.getStringInput();
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
                    c.setNickname(resultSet.getString("nickname"));
                    c.setPhonePrimary(resultSet.getString("phone_primary"));
                    c.setEmail(resultSet.getString("email"));
                    c.setBirthDate(resultSet.getDate("birth_date"));
                    c.setCreatedAt(resultSet.getDate("created_at"));
                    c.setUpdatedAt(resultSet.getDate("updated_at"));
                    results.add(c);
                }
                dbConnection.close();
            } catch (SQLException e) {
                System.out.println(DrawMenu.RED_BOLD + "Database Error: " + e.getMessage() + DrawMenu.RESET);
                Input.getStringInput();
                return;
            }

            DrawMenu.clearConsole();
            if (results.isEmpty()) {
                System.out.println(DrawMenu.RED_BOLD + "No contacts found matching your criteria." + DrawMenu.RESET);
                System.out.println("Press Enter to try again...");
                Input.getStringInput();
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

                    inputStr = Input.getStringInput().toLowerCase();

                    switch (inputStr) {
                        case "a":
                            if (currentIndex > 1) {
                                currentIndex--;
                                break label;
                            } else {
                                System.out.println(DrawMenu.RED_BOLD + "You reached the start of the search results." + DrawMenu.RESET);
                                System.out.println("Press Enter to continue...");
                                Input.getStringInput();
                                break label;
                            }
                        case "d":
                            if (currentIndex < resultCount) {
                                currentIndex++;
                                break label;
                            } else {
                                System.out.println(DrawMenu.RED_BOLD + "You reached the end of the search results." + DrawMenu.RESET);
                                System.out.println("Press Enter to continue...");
                                Input.getStringInput();
                                break label;
                            }
                        case "exit":
                            loop = false;
                            break label;
                        default:
                            System.out.println(DrawMenu.RED_BOLD + "Invalid input. Use A, D or exit." + DrawMenu.RESET);
                            System.out.println("Press Enter to continue...");
                            Input.getStringInput();
                            break label;
                    }
                }
            }
        }
    }

    /**
     * Sorts contacts based on a selected field and order (Ascending/Descending).
     * Supports sorting by ID, Name, Phone, and Dates.
     * @author Oğuzhan Aydın
     */
    public void sortContacts() {

        sortMenuLoop:
        while (true) {
            DrawMenu.clearConsole();
            String title = "Sort Contacts";
            String[] options = {
                    "",
                    DrawMenu.YELLOW_BOLD + "[1] Sort by ID" + DrawMenu.RESET,
                    DrawMenu.YELLOW_BOLD + "[2] Sort by First Name" + DrawMenu.RESET,
                    DrawMenu.YELLOW_BOLD + "[3] Sort by Last Name" + DrawMenu.RESET,
                    DrawMenu.YELLOW_BOLD + "[4] Sort by Phone" + DrawMenu.RESET,
                    DrawMenu.YELLOW_BOLD + "[5] Sort by Created Date" + DrawMenu.RESET,
                    DrawMenu.YELLOW_BOLD + "[6] Sort by Updated Date" + DrawMenu.RESET,
                    DrawMenu.YELLOW_BOLD + "[7] Sort by Birth Date" + DrawMenu.RESET,
                    DrawMenu.YELLOW_BOLD + "[8] Return to Menu" + DrawMenu.RESET,
                    ""
            };
            DrawMenu.printBoxed(title, options);
            System.out.println();
            DrawMenu.printCenter("Select field to sort by: ");

            String inputStr = Input.getStringInput();

            int choice;
            try {
                choice = Integer.parseInt(inputStr);
            } catch (NumberFormatException e) {
                System.out.println(DrawMenu.RED_BOLD + "Invalid input! Please enter a number." + DrawMenu.RESET);
                System.out.println("Press Enter to try again...");
                Input.getStringInput();
                continue;
            }

            String dbColumn = "";
            String displaySortName = "";

            switch (choice) {
                case 1: dbColumn = "contact_id"; displaySortName = "ID"; break;
                case 2: dbColumn = "first_name"; displaySortName = "First Name"; break;
                case 3: dbColumn = "last_name"; displaySortName = "Last Name"; break;
                case 4: dbColumn = "phone_primary"; displaySortName = "Phone"; break;
                case 5: dbColumn = "created_at"; displaySortName = "Created Date"; break;
                case 6: dbColumn = "updated_at"; displaySortName = "Updated Date"; break;
                case 7: dbColumn = "birth_date"; displaySortName = "Birth Date"; break;
                case 8:
                    DrawMenu.clearConsole();
                    return;
                default:
                    System.out.println(DrawMenu.RED_BOLD + "Invalid selection. Please enter 1-8." + DrawMenu.RESET);
                    System.out.println("Press Enter to try again...");
                    Input.getStringInput();
                    continue;
            }

            String sqlOrder = "";
            String displayOrder = "";

            orderSelectionLoop:
            while (true) {
                DrawMenu.clearConsole();
                String[] sortInfo = { "", "Selected Field: " + DrawMenu.YELLOW_BOLD + displaySortName + DrawMenu.RESET, "" };
                DrawMenu.printBoxed("Sort Options", sortInfo);

                System.out.println();
                DrawMenu.printCenter("Order (A: Ascending, D: Descending) or 'exit': ");
                String orderInput = Input.getStringInput().toLowerCase();

                boolean isNumericField = (choice == 1 || choice == 4);
                boolean isBirthDate = (choice == 7);
                boolean isSystemDate = (choice == 5 || choice == 6);

                switch (orderInput) {
                    case "exit":
                        continue sortMenuLoop;

                    case "a":
                        sqlOrder = "ASC";
                        if (isBirthDate) displayOrder = "Ascending (Oldest -> Youngest)";
                        else if (isSystemDate) displayOrder = "Ascending (Oldest -> Newest)";
                        else if (isNumericField) displayOrder = "Ascending (Min -> Max)";
                        else displayOrder = "Ascending (A -> Z)";
                        break orderSelectionLoop;

                    case "d":
                        sqlOrder = "DESC";
                        if (isBirthDate) displayOrder = "Descending (Youngest -> Oldest)";
                        else if (isSystemDate) displayOrder = "Descending (Newest -> Oldest)";
                        else if (isNumericField) displayOrder = "Descending (Max -> Min)";
                        else displayOrder = "Descending (Z -> A)";
                        break orderSelectionLoop;

                    default:
                        System.out.println(DrawMenu.RED_BOLD + "Invalid input! Please enter A, D or exit." + DrawMenu.RESET);
                        System.out.println("Press Enter to try again...");
                        Input.getStringInput();
                        break;
                }
            }

            String query = "SELECT * FROM contacts ORDER BY " + dbColumn + " " + sqlOrder;
            ArrayList<Contact> results = new ArrayList<>();

            try {
                Connection dbConnection = Database.openDatabase();
                Statement statement = dbConnection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);

                while (resultSet.next()) {
                    Contact c = new Contact();
                    c.setContactId(resultSet.getInt("contact_id"));
                    c.setFirstName(resultSet.getString("first_name"));
                    c.setLastName(resultSet.getString("last_name"));
                    c.setNickname(resultSet.getString("nickname"));
                    c.setPhonePrimary(resultSet.getString("phone_primary"));
                    c.setEmail(resultSet.getString("email"));
                    c.setBirthDate(resultSet.getDate("birth_date"));
                    c.setCreatedAt(resultSet.getDate("created_at"));
                    c.setUpdatedAt(resultSet.getDate("updated_at"));
                    results.add(c);
                }
                dbConnection.close();
            } catch (SQLException e) {
                System.out.println(DrawMenu.RED_BOLD + "Database Error: " + e.getMessage() + DrawMenu.RESET);
                Input.getStringInput();
                return;
            }

            if (results.isEmpty()) {
                System.out.println(DrawMenu.RED_BOLD + "No contacts found to sort!" + DrawMenu.RESET);
                System.out.println("Press Enter to return...");
                Input.getStringInput();
                continue;
            }

            boolean loop = true;
            int contactCount = results.size();
            int currentContactIndex = 1;

            while (loop) {
                String navInput;
                label:
                while (true) {
                    DrawMenu.clearConsole();

                    Contact currentContact = results.get(currentContactIndex - 1);

                    System.out.println(DrawMenu.GREEN_BOLD + "SORTED BY: " + displaySortName + " (" + displayOrder + ")" + DrawMenu.RESET);

                    printContactDetails(currentContact);

                    System.out.println();
                    DrawMenu.printCenter("< " + currentContactIndex + "/" + contactCount + " >");
                    System.out.println();
                    DrawMenu.printCenter("Previous: A, Next: D, Exit: Type 'exit'");
                    System.out.println();
                    DrawMenu.printCenter("Your choice: ");

                    navInput = Input.getStringInput().toLowerCase();

                    switch (navInput) {
                        case "a":
                            if (currentContactIndex > 1) {
                                currentContactIndex--;
                                break label;
                            } else {
                                System.out.println(DrawMenu.RED_BOLD + "Start of list." + DrawMenu.RESET);
                                System.out.println("Press Enter to continue...");
                                Input.getStringInput();
                                break label;
                            }
                        case "d":
                            if (currentContactIndex < contactCount) {
                                currentContactIndex++;
                                break label;
                            } else {
                                System.out.println(DrawMenu.RED_BOLD + "End of list." + DrawMenu.RESET);
                                System.out.println("Press Enter to continue...");
                                Input.getStringInput();
                                break label;
                            }
                        case "exit":
                            loop = false;
                            break label;
                        default:
                            System.out.println(DrawMenu.RED_BOLD + "Invalid input. Use A, D or exit." + DrawMenu.RESET);
                            System.out.println("Press Enter to try again...");
                            Input.getStringInput();
                            break label;
                    }
                }
            }
        }
    }
}
