package model;

import database.Database;
import utils.DrawMenu;
import utils.Input;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Junior extends Tester {

    @Override
    public void showUserMenu() {
        while (true) {
            String title = "Welcome " + this.getName() + " " + this.getSurname() + ", " + this.getRole();
            String[] contents = {
                    "",
                    DrawMenu.YELLOW_BOLD + "[1] List All Contacts" + DrawMenu.RESET,
                    DrawMenu.YELLOW_BOLD + "[2] Search Contact (Single Field)" + DrawMenu.RESET,
                    DrawMenu.YELLOW_BOLD + "[3] Search Contact (Multiple Fields)" + DrawMenu.RESET,
                    DrawMenu.YELLOW_BOLD + "[4] Update Contact" + DrawMenu.RESET,
                    DrawMenu.YELLOW_BOLD + "[5] Change Password" + DrawMenu.RESET,
                    DrawMenu.YELLOW_BOLD + "[6] Logout" + DrawMenu.RESET,
                    ""
            };
            DrawMenu.printBoxed(title, contents);
            System.out.println();
            DrawMenu.printCenter("Your choice: ");

            int input;
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
                    DrawMenu.clearConsole();
                    this.updateContact();
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

    public void updateContact() {
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

                String idText = Input.getStringInput().trim().toLowerCase();
                DrawMenu.clearConsole();

                if (idText.equals("exit")) {
                    return;
                }

                boolean allDigits = true;
                for (int i = 0; i < idText.length(); i++) {
                    if (!Character.isDigit(idText.charAt(i))) {
                        allDigits = false;
                        break;
                    }
                }
                if (!allDigits) {
                    System.out.println(DrawMenu.RED_BOLD + "Invalid id. Use numbers only." + DrawMenu.RESET);
                    continue;
                }

                if (idText.isEmpty() || idText.length() > 11) {
                    System.out.println(DrawMenu.RED_BOLD + "Invalid id length. It must be between 1 and 11 digits." + DrawMenu.RESET);
                    continue;
                }

                int id;
                try {
                    id = Integer.parseInt(idText);
                } catch (NumberFormatException e) {
                    System.out.println(DrawMenu.RED_BOLD + "Invalid id. Please enter a valid number." + DrawMenu.RESET);
                    continue;
                }

                if (id <= 0) {
                    System.out.println(DrawMenu.RED_BOLD + "Invalid id. Id must be a positive number." + DrawMenu.RESET);
                    continue;
                }

                Connection conn = null;
                PreparedStatement ps = null;
                ResultSet rs = null;

                String firstName;
                String lastName;
                String nickname;
                String phone;
                String email;
                String birthDate;

                try {
                    conn = Database.openDatabase();
                    String sql = "SELECT contact_id, first_name, last_name, nickname, " +
                            "phone_primary, email, birth_date FROM contacts WHERE contact_id = ?";
                    ps = conn.prepareStatement(sql);
                    ps.setInt(1, id);
                    rs = ps.executeQuery();

                    if (!rs.next()) {
                        System.out.println(DrawMenu.RED_BOLD + "No contact found with id: " + id + DrawMenu.RESET);
                        if (rs != null) rs.close();
                        if (ps != null) ps.close();
                        if (conn != null) conn.close();
                        continue;
                    }

                    firstName = rs.getString("first_name");
                    lastName = rs.getString("last_name");
                    nickname = rs.getString("nickname");
                    phone = rs.getString("phone_primary");
                    email = rs.getString("email");
                    birthDate = rs.getString("birth_date");

                    if (rs != null) rs.close();
                    if (ps != null) ps.close();

                    String line = String.format(
                            "ID=%d | %s %s (%s) | Phone=%s | Email=%s | BirthDate=%s",
                            id,
                            safe(firstName),
                            safe(lastName),
                            safe(nickname),
                            safe(phone),
                            safe(email),
                            safe(birthDate)
                    );

                    System.out.println("Current contact:");
                    System.out.println(line);
                    System.out.println();
                    DrawMenu.printCenter("Please enter the new values for the user with ID = " + id);
                    System.out.println();
                    System.out.println("Leave input empty to keep the current value.");
                    System.out.println();

                    while (true) {
                        DrawMenu.printCenter("New FIRST NAME (current: " + safe(firstName) + "): ");
                        String newFirstName = Input.getStringInput();
                        if (newFirstName.trim().isEmpty()) {
                            break;
                        }
                        if (!isValidFirstName(newFirstName.trim())) {
                            System.out.println("Invalid first name. Use only letters, spaces or '-' and at least 2 characters.");
                        } else {
                            firstName = newFirstName.trim();
                            break;
                        }
                    }

                    while (true) {
                        DrawMenu.printCenter("New LAST NAME (current: " + safe(lastName) + "): ");
                        String newLastName = Input.getStringInput();
                        if (newLastName.trim().isEmpty()) {
                            break;
                        }
                        if (!isValidLastName(newLastName.trim())) {
                            System.out.println("Invalid last name. Use only letters or '-' (no spaces) and at least 2 characters.");
                        } else {
                            lastName = newLastName.trim();
                            break;
                        }
                    }

                    DrawMenu.printCenter("New NICKNAME (current: " + safe(nickname) + "): ");
                    String newNickname = Input.getStringInput();
                    if (!newNickname.trim().isEmpty()) {
                        nickname = newNickname.trim();
                    }

                    while (true) {
                        DrawMenu.printCenter("New PRIMARY PHONE (current: " + safe(phone) + " - format 05XX-XXX-XX-XX)");
                        System.out.println();
                        DrawMenu.printCenter("Enter last 9 digits (or leave empty to keep current): ");
                        String phoneTail = Input.getStringInput().trim();

                        if (phoneTail.isEmpty()) {
                            break;
                        }

                        if (!isValidPhoneTail(phoneTail)) {
                            System.out.println("Invalid phone. Please enter exactly 9 digits (numbers only).");
                        } else {
                            phone = "05" + phoneTail;
                            break;
                        }
                    }

                    while (true) {
                        DrawMenu.printCenter("New EMAIL (current: " + safe(email) + "): ");
                        String newEmail = Input.getStringInput();
                        if (newEmail.trim().isEmpty()) {
                            break;
                        }
                        if (!isValidEmail(newEmail.trim())) {
                            System.out.println("Invalid email. Example: user@example.com");
                        } else {
                            email = newEmail.trim();
                            break;
                        }
                    }

                    while (true) {
                        DrawMenu.printCenter("New BIRTH DATE (current: " + safe(birthDate) + ", format YYYY-MM-DD): ");
                        String newBirthDate = Input.getStringInput();
                        if (newBirthDate.trim().isEmpty()) {
                            break;
                        }
                        if (!isValidBirthDate(newBirthDate.trim())) {
                            System.out.println("Invalid date. Use format YYYY-MM-DD, it must be a real date and cannot be in the future.");
                        } else {
                            birthDate = newBirthDate.trim();
                            break;
                        }
                    }

                    String updateSql = "UPDATE contacts SET " +
                            "first_name = ?, " +
                            "last_name = ?, " +
                            "nickname = ?, " +
                            "phone_primary = ?, " +
                            "email = ?, " +
                            "birth_date = ?, " +
                            "updated_at = CURRENT_TIMESTAMP " +
                            "WHERE contact_id = ?";

                    PreparedStatement updatePs = conn.prepareStatement(updateSql);
                    updatePs.setString(1, firstName);
                    updatePs.setString(2, lastName);
                    updatePs.setString(3, nickname);
                    updatePs.setString(4, phone);

                    if (email == null || email.trim().isEmpty()) {
                        updatePs.setNull(5, Types.VARCHAR);
                    } else {
                        updatePs.setString(5, email);
                    }

                    if (birthDate == null || birthDate.trim().isEmpty()) {
                        updatePs.setNull(6, Types.VARCHAR);
                    } else {
                        updatePs.setString(6, birthDate);
                    }

                    updatePs.setInt(7, id);

                    int rows = updatePs.executeUpdate();

                    if (rows > 0) {
                        System.out.println();
                        System.out.println("Contact updated successfully. New values:");
                        String newLine = String.format(
                                "ID=%d | %s %s (%s) | Phone=%s | Email=%s | BirthDate=%s",
                                id,
                                safe(firstName),
                                safe(lastName),
                                safe(nickname),
                                safe(phone),
                                safe(email),
                                safe(birthDate)
                        );
                        System.out.println(newLine);
                    } else {
                        System.out.println(DrawMenu.RED_BOLD + "No rows were updated. Please check the ID." + DrawMenu.RESET);
                    }

                    updatePs.close();
                    conn.close();

                    System.out.println();
                    DrawMenu.printCenter("Press ENTER to go back to menu...");
                    Input.getStringInput();
                    DrawMenu.clearConsole();
                    return;

                } catch (SQLException e) {
                    System.out.println(DrawMenu.RED_BOLD + "Database Error: contact could not be updated." + DrawMenu.RESET);
                    System.out.println();
                    DrawMenu.printCenter("Press ENTER to go back to menu...");
                    try {
                        Input.getStringInput();
                    } catch (Exception ignored) { }
                    DrawMenu.clearConsole();
                    return;
                }
            } catch (Exception e) {
                System.out.println(DrawMenu.RED_BOLD + "Unexpected error. Please try again." + DrawMenu.RESET);
            }
        }
    }

    private String safe(String value) {
        if (value == null) {
            return "(none)";
        }
        return value;
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
        if (name.contains(" ")) {
            return false;
        }
        return true;
    }

    private boolean isValidPhoneTail(String phoneTail) {
        if (phoneTail.length() != 9) {
            return false;
        }
        for (int i = 0; i < phoneTail.length(); i++) {
            if (!Character.isDigit(phoneTail.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        email = email.trim();
        if (email.isEmpty()) {
            return false;
        }
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


