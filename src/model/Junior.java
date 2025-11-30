package model;

import database.Database;
import utils.DrawMenu;
import utils.Input;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class Junior extends Tester {

    private static class ContactData {
        int id;
        String firstName;
        String middleName;
        String lastName;
        String nickname;
        String phonePrimary;
        String phoneSecondary;
        String email;
        String linkedinUrl;
        String birthDate;
    }

    @Override
    public void showUserMenu() {
        //TODO
        while (true) {
            String title = "Welcome " + this.getName() + " " + this.getSurname() + ", " + this.getRole();
            String[] contents = {
                    "",
                    DrawMenu.YELLOW_BOLD + "[1] Change Password" + DrawMenu.RESET,
                    DrawMenu.YELLOW_BOLD + "[2] List All Contacts" + DrawMenu.RESET,
                    DrawMenu.YELLOW_BOLD + "[3] Search by FIRST NAME" + DrawMenu.RESET,
                    DrawMenu.YELLOW_BOLD + "[4] Search by LAST NAME" + DrawMenu.RESET,
                    DrawMenu.YELLOW_BOLD + "[5] Search by PRIMARY PHONE" + DrawMenu.RESET,
                    DrawMenu.YELLOW_BOLD + "[6] Search by MULTIPLE FIELDS" + DrawMenu.RESET,
                    DrawMenu.YELLOW_BOLD + "[7] SORT Contacts" + DrawMenu.RESET,
                    DrawMenu.YELLOW_BOLD + "[8] UPDATE Existing Contact" + DrawMenu.RESET,
                    DrawMenu.YELLOW_BOLD + "[9] Logout" + DrawMenu.RESET,
                    ""
            };
            DrawMenu.printBoxed(title, contents);
            System.out.println();
            DrawMenu.printCenter("Your choice: ");

            int input = 9;
            try {
                input = Input.getIntInput();
            } catch (Exception e) {
                DrawMenu.clearConsole();
                System.out.println(DrawMenu.RED_BOLD +
                        "Invalid input. Please enter a number between 1 and 9." +
                        DrawMenu.RESET);
                continue;
            }

            DrawMenu.clearConsole();

            switch (input) {
                case 1:
                    this.changePassword();
                    break;
                case 2:
                    listAllContacts();
                    break;
                case 3:
                    searchBySingleField("first_name", "FIRST NAME");
                    break;
                case 4:
                    searchBySingleField("last_name", "LAST NAME");
                    break;
                case 5:
                    searchBySingleField("phone_primary", "PRIMARY PHONE");
                    break;
                case 6:
                    searchByMultipleFields();
                    break;
                case 7:
                    sortContacts();
                    break;
                case 8:
                    updateContact();
                    break;
                case 9:
                default:
                    this.logout();
                    return;
            }
        }
    }
    @Override
    public void listAllContacts() {
        try {
            List<ContactData> contacts = loadAllContacts();
            if (contacts.isEmpty()) {
                System.out.println("No contacts found in database.");
            } else {
                System.out.println("All contacts (from database):");
                System.out.println();
                for (ContactData c : contacts) {
                    printContactLine(c);
                }
            }
        } catch (SQLException e) {
            System.out.println(DrawMenu.RED_BOLD +
                    "Database Error: contacts could not be fetched." +
                    DrawMenu.RESET);
        }

        System.out.println();
        DrawMenu.printCenter("Press ENTER to go back to menu...");
        try {
            Input.getStringInput();
        } catch (Exception ignored) {
        }
        DrawMenu.clearConsole();
    }


    private void searchBySingleField(String columnName, String label) {
        try {
            System.out.println("Search by " + label + " (leave empty to cancel).");
            DrawMenu.printCenter(label + ": ");
            String keyword;
            try {
                keyword = Input.getStringInput();
            } catch (Exception e) {
                System.out.println(DrawMenu.RED_BOLD + "Invalid input." + DrawMenu.RESET);
                return;
            }

            if (keyword == null || keyword.trim().isEmpty()) {
                DrawMenu.clearConsole();
                return;
            }

            String sql = "SELECT contact_id, first_name, last_name, nickname, " +
                    "phone_primary, email, birth_date " +
                    "FROM contacts WHERE " + columnName + " LIKE ?";

            Connection conn = Database.openDatabase();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + keyword.trim() + "%");
            ResultSet rs = ps.executeQuery();

            List<ContactData> result = new ArrayList<>();
            while (rs.next()) {
                result.add(mapRowToContact(rs));
            }
            conn.close();

            if (result.isEmpty()) {
                System.out.println("No contacts found for keyword: " + keyword);
            } else {
                System.out.println("Search results for \"" + keyword + "\":");
                System.out.println();
                for (ContactData c : result) {
                    printContactLine(c);
                }
            }
        } catch (SQLException e) {
            System.out.println(DrawMenu.RED_BOLD +
                    "Database Error: contacts could not be fetched." +
                    DrawMenu.RESET);
        }

        System.out.println();
        DrawMenu.printCenter("Press ENTER to go back to menu...");
        try {
            Input.getStringInput();
        } catch (Exception ignored) {
        }
        DrawMenu.clearConsole();
    }

    private void searchByMultipleFields() {
        try {
            System.out.println("Enter search criteria. Leave empty to ignore a field.");
            System.out.println();

            DrawMenu.printCenter("FIRST NAME keyword: ");
            String first = Input.getStringInput();

            DrawMenu.printCenter("LAST NAME keyword: ");
            String last = Input.getStringInput();

            DrawMenu.printCenter("PRIMARY PHONE keyword: ");
            String phone = Input.getStringInput();

            boolean hasFirst = first != null && !first.trim().isEmpty();
            boolean hasLast = last != null && !last.trim().isEmpty();
            boolean hasPhone = phone != null && !phone.trim().isEmpty();

            if (!hasFirst && !hasLast && !hasPhone) {
                System.out.println("No criteria entered. Nothing to search.");
                System.out.println();
                DrawMenu.printCenter("Press ENTER to go back to menu...");
                Input.getStringInput();
                DrawMenu.clearConsole();
                return;
            }

            StringBuilder sql = new StringBuilder(
                    "SELECT contact_id, first_name, last_name, nickname, " +
                            "phone_primary, email, birth_date " +
                            "FROM contacts"
            );
            List<String> params = new ArrayList<>();
            boolean whereAdded = false;

            if (hasFirst) {
                sql.append(whereAdded ? " AND" : " WHERE");
                sql.append(" first_name LIKE ?");
                params.add("%" + first.trim() + "%");
                whereAdded = true;
            }
            if (hasLast) {
                sql.append(whereAdded ? " AND" : " WHERE");
                sql.append(" last_name LIKE ?");
                params.add("%" + last.trim() + "%");
                whereAdded = true;
            }
            if (hasPhone) {
                sql.append(whereAdded ? " AND" : " WHERE");
                sql.append(" phone_primary LIKE ?");
                params.add("%" + phone.trim() + "%");
            }

            Connection conn = Database.openDatabase();
            PreparedStatement ps = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                ps.setString(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();

            List<ContactData> result = new ArrayList<>();
            while (rs.next()) {
                result.add(mapRowToContact(rs));
            }
            conn.close();

            if (result.isEmpty()) {
                System.out.println("No contacts found for the given criteria.");
            } else {
                System.out.println("Search results:");
                System.out.println();
                for (ContactData c : result) {
                    printContactLine(c);
                }
            }

        } catch (Exception e) {
            System.out.println(DrawMenu.RED_BOLD +
                    "Database Error: contacts could not be fetched." +
                    DrawMenu.RESET);
        }

        System.out.println();
        DrawMenu.printCenter("Press ENTER to go back to menu...");
        try {
            Input.getStringInput();
        } catch (Exception ignored) {
        }
        DrawMenu.clearConsole();
    }

    private void sortContacts() {
        try {
            System.out.println("Sort by field:");
            System.out.println("[1] ID");
            System.out.println("[2] FIRST NAME");
            System.out.println("[3] LAST NAME");
            System.out.println("[4] PRIMARY PHONE");
            System.out.println();
            DrawMenu.printCenter("Your choice: ");

            String choice = Input.getStringInput().trim();

            String column;
            switch (choice) {
                case "1":
                    column = "contact_id";
                    break;
                case "2":
                    column = "first_name";
                    break;
                case "3":
                    column = "last_name";
                    break;
                case "4":
                    column = "phone_primary";
                    break;
                default:
                    System.out.println("Invalid field option.");
                    System.out.println();
                    DrawMenu.printCenter("Press ENTER to go back to menu...");
                    Input.getStringInput();
                    DrawMenu.clearConsole();
                    return;
            }

            System.out.println();
            DrawMenu.printCenter("Sort order (A = Ascending, D = Descending): ");
            String orderInput = Input.getStringInput().trim().toUpperCase();
            String order = orderInput.equals("D") ? "DESC" : "ASC";

            String sql = "SELECT contact_id, first_name, last_name, nickname, " +
                    "phone_primary, email, birth_date " +
                    "FROM contacts ORDER BY " + column + " " + order;

            Connection conn = Database.openDatabase();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            List<ContactData> result = new ArrayList<>();
            while (rs.next()) {
                result.add(mapRowToContact(rs));
            }
            conn.close();

            if (result.isEmpty()) {
                System.out.println("No contacts found in database.");
            } else {
                System.out.println("Sorted contacts:");
                System.out.println();
                for (ContactData c : result) {
                    printContactLine(c);
                }
            }

        } catch (Exception e) {
            System.out.println(DrawMenu.RED_BOLD +
                    "Database Error: contacts could not be fetched." +
                    DrawMenu.RESET);
        }

        System.out.println();
        DrawMenu.printCenter("Press ENTER to go back to menu...");
        try {
            Input.getStringInput();
        } catch (Exception ignored) {
        }
        DrawMenu.clearConsole();
    }

    public void updateContact() {
        //TODO
        while (true) {
            try {
                String title = "Update Contact, " + this.getName() + " " + this.getSurname() + ", " + this.getRole();
                String[] contents = {
                        "",
                        DrawMenu.YELLOW_BOLD + "Contact Id: " + DrawMenu.RESET +
                                DrawMenu.LIGHT_GRAY + " Enter contact id to update (or 'exit' to go back)" + DrawMenu.RESET,
                        ""
                };
                DrawMenu.printBoxed(title, contents);
                System.out.println();
                DrawMenu.printCenter("Contact Id: ");

                String idText = Input.getStringInput().toLowerCase().trim();
                DrawMenu.clearConsole();

                if (idText.equals("exit")) {
                    return;
                }

                int id;
                try {
                    id = Integer.parseInt(idText);
                } catch (NumberFormatException e) {
                    System.out.println(DrawMenu.RED_BOLD +
                            "Invalid id. Please enter a number." +
                            DrawMenu.RESET);
                    continue;
                }

                if (id <= 0) {
                    System.out.println(DrawMenu.RED_BOLD +
                            "Invalid id. Id must be a positive number." +
                            DrawMenu.RESET);
                    continue;
                }

                ContactData contact = loadContactById(id);
                if (contact == null) {
                    System.out.println(DrawMenu.RED_BOLD +
                            "No contact found with id: " + id +
                            DrawMenu.RESET);
                    continue;
                }

                System.out.println("Current contact:");
                printContactLine(contact);
                System.out.println();
                System.out.println("Enter NEW values. Leave empty to keep the current value.");
                System.out.println();

                while (true) {
                    DrawMenu.printCenter("New FIRST NAME (current: " + safe(contact.firstName) + "): ");
                    String newFirstName = Input.getStringInput();

                    if (newFirstName.trim().isEmpty()) {
                        break;
                    }

                    if (!isValidFirstName(newFirstName.trim())) {
                        System.out.println("Invalid first name. Use only letters, spaces or '-' and at least 2 characters.");
                    } else {
                        contact.firstName = newFirstName.trim();
                        break;
                    }
                }

                while (true) {
                    DrawMenu.printCenter("New LAST NAME  (current: " + safe(contact.lastName) + "): ");
                    String newLastName = Input.getStringInput();

                    if (newLastName.trim().isEmpty()) {
                        break;
                    }

                    if (!isValidLastName(newLastName.trim())) {
                        System.out.println("Invalid last name. Use only letters or '-' (no spaces) and at least 2 characters.");
                    } else {
                        contact.lastName = newLastName.trim();
                        break;
                    }
                }

                DrawMenu.printCenter("New NICKNAME   (current: " + safe(contact.nickname) + "): ");
                String newNickname = Input.getStringInput();
                if (!newNickname.trim().isEmpty()) {
                    contact.nickname = newNickname.trim();
                }

                while (true) {
                    DrawMenu.printCenter("New PRIMARY PHONE (current: " + safe(contact.phonePrimary) + " - must be 10 digits): ");
                    String newPhone = Input.getStringInput();

                    if (newPhone.trim().isEmpty()) {
                        break;
                    }

                    if (!isValidPhone(newPhone.trim())) {
                        System.out.println("Invalid phone. Please enter exactly 10 digits (numbers only).");
                    } else {
                        contact.phonePrimary = newPhone.trim();
                        break;
                    }
                }

                while (true) {
                    DrawMenu.printCenter("New EMAIL      (current: " + safe(contact.email) + "): ");
                    String newEmail = Input.getStringInput();

                    if (newEmail.trim().isEmpty()) {
                        break;
                    }

                    if (!isValidEmail(newEmail.trim())) {
                        System.out.println("Invalid email. Example: user@example.com");
                    } else {
                        contact.email = newEmail.trim();
                        break;
                    }
                }

                while (true) {
                    DrawMenu.printCenter("New BIRTH DATE (current: " + safe(contact.birthDate) + ", format YYYY-MM-DD): ");
                    String newBirthDate = Input.getStringInput();

                    if (newBirthDate.trim().isEmpty()) {
                        break;
                    }

                    if (!isValidBirthDate(newBirthDate.trim())) {
                        System.out.println("Invalid date. Use format YYYY-MM-DD, it must be a real date and cannot be in the future.");
                    } else {
                        contact.birthDate = newBirthDate.trim();
                        break;
                    }
                }

                updateContactInDatabase(contact);

                System.out.println();
                System.out.println("Contact updated successfully. New values:");
                printContactLine(contact);

                System.out.println();
                DrawMenu.printCenter("Press ENTER to go back to menu...");
                Input.getStringInput();
                DrawMenu.clearConsole();
                return;

            } catch (SQLException e) {
                System.out.println(DrawMenu.RED_BOLD +
                        "Database Error: contact could not be updated." +
                        DrawMenu.RESET);
                System.out.println();
                DrawMenu.printCenter("Press ENTER to go back to menu...");
                try {
                    Input.getStringInput();
                } catch (Exception ignored) {
                }
                DrawMenu.clearConsole();
                return;
            } catch (Exception e) {
                System.out.println(DrawMenu.RED_BOLD +
                        "Unexpected error. Please try again." +
                        DrawMenu.RESET);
            }
        }
    }

    private List<ContactData> loadAllContacts() throws SQLException {
        List<ContactData> list = new ArrayList<>();

        String sql = "SELECT contact_id, first_name, last_name, nickname, " +
                "phone_primary, email, birth_date FROM contacts";

        Connection conn = Database.openDatabase();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            list.add(mapRowToContact(rs));
        }

        conn.close();
        return list;
    }

    private ContactData loadContactById(int id) throws SQLException {
        String sql = "SELECT contact_id, first_name, last_name, nickname, " +
                "phone_primary, email, birth_date " +
                "FROM contacts WHERE contact_id = ?";

        Connection conn = Database.openDatabase();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        ContactData contact = null;
        if (rs.next()) {
            contact = mapRowToContact(rs);
        }

        conn.close();
        return contact;
    }

    private void updateContactInDatabase(ContactData c) throws SQLException {
        String sql = "UPDATE contacts SET " +
                "first_name = ?, " +
                "last_name = ?, " +
                "nickname = ?, " +
                "phone_primary = ?, " +
                "email = ?, " +
                "birth_date = ?, " +
                "updated_at = NOW() " +
                "WHERE contact_id = ?";

        Connection conn = Database.openDatabase();
        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setString(1, c.firstName);
        ps.setString(2, c.lastName);
        ps.setString(3, c.nickname);
        ps.setString(4, c.phonePrimary);
        ps.setString(5, c.email);
        ps.setString(6, c.birthDate);
        ps.setInt(7, c.id);

        ps.executeUpdate();
        conn.commit();
        conn.close();
    }

    private ContactData mapRowToContact(ResultSet rs) throws SQLException {
        ContactData c = new ContactData();
        c.id = rs.getInt("contact_id");
        c.firstName = rs.getString("first_name");
        c.lastName = rs.getString("last_name");
        c.nickname = rs.getString("nickname");
        c.phonePrimary = rs.getString("phone_primary");
        c.email = rs.getString("email");
        c.birthDate = rs.getString("birth_date");
        return c;
    }

    private void printContactLine(ContactData c) {
        String line = String.format(
                "ID=%d | %s %s (%s) | Phone=%s | Email=%s | BirthDate=%s",
                c.id,
                safe(c.firstName),
                safe(c.lastName),
                safe(c.nickname),
                safe(c.phonePrimary),
                safe(c.email),
                safe(c.birthDate)
        );
        System.out.println(line);
    }

    private String safe(String value) {
        return (value == null) ? "(none)" : value;
    }

    private boolean isValidFirstName(String name) {
        if (name.length() < 2) {
            return false;
        }
        for (int i = 0; i < name.length(); i++) {
            char ch = name.charAt(i);
            if (!Character.isLetter(ch) && ch != ' ' && ch != '-') {
                return false;
            }
        }
        return true;
    }

    private boolean isValidLastName(String name) {
        if (name.length() < 2) {
            return false;
        }
        for (int i = 0; i < name.length(); i++) {
            char ch = name.charAt(i);
            if (!Character.isLetter(ch) && ch != '-') {
                return false;
            }
        }
        return !name.contains(" ");
    }

    private boolean isValidPhone(String phone) {
        if (phone.length() != 10) {
            return false;
        }
        for (int i = 0; i < phone.length(); i++) {
            if (!Character.isDigit(phone.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidEmail(String email) {
        if (email == null) return false;
        email = email.trim();
        if (email.isEmpty()) return false;

        for (int i = 0; i < email.length(); i++) {
            if (Character.isWhitespace(email.charAt(i))) {
                return false;
            }
        }

        int atIndex = email.indexOf('@');
        int lastAtIndex = email.lastIndexOf('@');
        if (atIndex <= 0 || atIndex != lastAtIndex) {
            return false;
        }

        int lastDotIndex = email.lastIndexOf('.');
        if (lastDotIndex < atIndex + 2) {
            return false;
        }
        if (lastDotIndex >= email.length() - 1) {
            return false;
        }

        return true;
    }

    private boolean isValidBirthDate(String birthDateText) {
        try {
            LocalDate date = LocalDate.parse(birthDateText);
            LocalDate today = LocalDate.now();
            return !date.isAfter(today);
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}

