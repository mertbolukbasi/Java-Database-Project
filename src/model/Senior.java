package model;

import database.Database;
import utils.DrawMenu;
import utils.Input;
import java.sql.*;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.Comparator;


public class Senior extends Junior {

    @Override
    public void showUserMenu() throws SQLException {
        String title = "Welcome " + this.getName() + " " + this.getSurname() + ", " + this.getRole();
        String[] contents = {
                "",
                DrawMenu.YELLOW_BOLD + "[1] Change Password" + DrawMenu.RESET,
                DrawMenu.YELLOW_BOLD + "[2] List All Contacts" + DrawMenu.RESET,
                DrawMenu.YELLOW_BOLD + "[3] Search Contacts (Single Field)" + DrawMenu.RESET,
                DrawMenu.YELLOW_BOLD + "[4] Search Contacts (Multiple Fields)" + DrawMenu.RESET,
                DrawMenu.YELLOW_BOLD + "[5] Sort Contacts" + DrawMenu.RESET,
                DrawMenu.YELLOW_BOLD + "[6] Add New Contact" + DrawMenu.RESET,
                DrawMenu.YELLOW_BOLD + "[7] Update Existing Contact" + DrawMenu.RESET,
                DrawMenu.YELLOW_BOLD + "[8] Delete Existing Contact" + DrawMenu.RESET,
                DrawMenu.YELLOW_BOLD + "[9] Undo Last Operation" + DrawMenu.RESET,
                DrawMenu.YELLOW_BOLD + "[10] Logout" + DrawMenu.RESET,
                ""
        };
        DrawMenu.printBoxed(title, contents);
        System.out.println();
        DrawMenu.printCenter("Your choice: ");
        int input = 10;
        try {
            input = Input.getIntInput();
        } catch (Exception e) {
            DrawMenu.clearConsole();
            System.out.println("Invalid input. Please enter a number between 1 and 10.");
            showUserMenu();
        }
        switch (input) {
            case 1:
                this.changePassword();
                break;
            case 2:
                this.listAllContacts();
                break;
            case 3:
                this.searchContact();
                break;
            case 4:
                this.searchBySelectedFields();
                break;
            case 5:
                this.sortContacts();
                break;
            case 6:
                this.addContact();
                break;
            case 7:
                this.updateContact();
                break;
            case 8:
                this.deleteContact();
                break;
            case 9:
                this.undoOperation();
                break;
            case 10:
                this.logout();
                break;
        }

    }

    public Contact getContactById(int contact_id) throws SQLException {
        String query = "SELECT * FROM contacts WHERE contact_id = " + contact_id;
        Connection dbConnection = Database.openDatabase();
        Statement statement = dbConnection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        Contact contact = new Contact();

        if (resultSet.next()) {
            contact.setContactId(resultSet.getInt("contact_id"));
            contact.setFirstName(resultSet.getString("first_name"));
            contact.setLastName(resultSet.getString("last_name"));
            contact.setNickname(resultSet.getString("nickname"));
            contact.setPhonePrimary(resultSet.getString("phone_primary"));
            contact.setEmail(resultSet.getString("email"));
            contact.setBirthDate(resultSet.getDate("birth_date"));
            contact.setCreatedAt(resultSet.getDate("created_at"));//
            contact.setUpdatedAt(resultSet.getDate("updated_at"));//
        }

        dbConnection.close();
        return contact;
    }

    public void addContact() {

        String title = "Add New Contact";

        String firstName = "";
        String lastName = "";
        String nickname = "";
        String phone = "";
        String email = "";
        String birthDate = "";

        Contact contact = new Contact();

        while (true) {
            String[] contentTable1 = {
                    "",
                    DrawMenu.BLUE_BOLD + "[1] First Name: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + (firstName.isEmpty() ? "Enter first name" : firstName) + DrawMenu.RESET,
                    DrawMenu.BLUE_BOLD + "[2] Last Name: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "Enter last name" + DrawMenu.RESET,
                    DrawMenu.BLUE_BOLD + "[3] Nickname: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "Optional" + DrawMenu.RESET,
                    DrawMenu.BLUE_BOLD + "[4] Phone: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "Enter phone number" + DrawMenu.RESET,
                    DrawMenu.BLUE_BOLD + "[5] Email: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "Enter email" + DrawMenu.RESET,
                    DrawMenu.BLUE_BOLD + "[6] Birth Date: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "YYYY-MM-DD" + DrawMenu.RESET,
                    ""
            };
            DrawMenu.printBoxed(title, contentTable1);

            System.out.println();
            DrawMenu.printCenter("First Name: ");
            firstName = Input.getStringInput();

            if (!firstName.isEmpty()) {
                contact.setFirstName(firstName);
                break;
            }

            DrawMenu.clearConsole();
            System.out.println(DrawMenu.RED_BOLD + "First name cannot be empty!" + DrawMenu.RESET);
        }

        while (true) {
            DrawMenu.clearConsole();
            String[] contentTable2 = {
                    "",
                    DrawMenu.BLUE_BOLD + "[1] First Name: " + DrawMenu.RESET + firstName,
                    DrawMenu.BLUE_BOLD + "[2] Last Name: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + (lastName.isEmpty() ? "Enter last name" : lastName) + DrawMenu.RESET,
                    DrawMenu.BLUE_BOLD + "[3] Nickname: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "Optional" + DrawMenu.RESET,
                    DrawMenu.BLUE_BOLD + "[4] Phone: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "Enter phone number" + DrawMenu.RESET,
                    DrawMenu.BLUE_BOLD + "[5] Email: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "Enter email" + DrawMenu.RESET,
                    DrawMenu.BLUE_BOLD + "[6] Birth Date: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "YYYY-MM-DD" + DrawMenu.RESET,
                    ""
            };
            DrawMenu.printBoxed(title, contentTable2);

            System.out.println();
            DrawMenu.printCenter("Last Name: ");
            lastName = Input.getStringInput();

            if (!lastName.isEmpty()) {
                contact.setLastName(lastName);
                break;
            }

            DrawMenu.clearConsole();
            System.out.println(DrawMenu.RED_BOLD + "Last name cannot be empty!" + DrawMenu.RESET);
        }

        DrawMenu.clearConsole();
        String[] contentTable3 = {
                "",
                DrawMenu.BLUE_BOLD + "[1] First Name: " + DrawMenu.RESET + firstName,
                DrawMenu.BLUE_BOLD + "[2] Last Name: " + DrawMenu.RESET + lastName,
                DrawMenu.BLUE_BOLD + "[3] Nickname: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + (nickname.isEmpty() ? "Optional" : nickname) + DrawMenu.RESET,
                DrawMenu.BLUE_BOLD + "[4] Phone: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "Enter phone number" + DrawMenu.RESET,
                DrawMenu.BLUE_BOLD + "[5] Email: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "Enter email" + DrawMenu.RESET,
                DrawMenu.BLUE_BOLD + "[6] Birth Date: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "YYYY-MM-DD" + DrawMenu.RESET,
                ""
        };
        DrawMenu.printBoxed(title, contentTable3);

        System.out.println();
        DrawMenu.printCenter("Nickname (optional): ");
        nickname = Input.getStringInput();
        contact.setNickname(nickname);


        while (true) {
            DrawMenu.clearConsole();
            String[] contentTable4 = {
                    "",
                    DrawMenu.BLUE_BOLD + "[1] First Name: " + DrawMenu.RESET + firstName,
                    DrawMenu.BLUE_BOLD + "[2] Last Name: " + DrawMenu.RESET + lastName,
                    DrawMenu.BLUE_BOLD + "[3] Nickname: " + DrawMenu.RESET + nickname,
                    DrawMenu.BLUE_BOLD + "[4] Phone: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + (phone.isEmpty() ? "0(___)___ __ __" : phone) + DrawMenu.RESET,
                    DrawMenu.BLUE_BOLD + "[5] Email: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "Enter email" + DrawMenu.RESET,
                    DrawMenu.BLUE_BOLD + "[6] Birth Date: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "YYYY-MM-DD" + DrawMenu.RESET,
                    ""
            };
            DrawMenu.printBoxed(title, contentTable4);

            System.out.println();
            DrawMenu.printCenter("Phone: ");
            phone = Input.getStringInput();

            if (isValidPhone(phone)) {
                contact.setPhonePrimary(phone);
                break;
            }

            DrawMenu.clearConsole();
            System.out.println(DrawMenu.RED_BOLD + "Invalid phone format!" + DrawMenu.RESET);
        }

        while (true) {
            DrawMenu.clearConsole();
            String[] contentTable5 = {
                    "",
                    DrawMenu.BLUE_BOLD + "[1] First Name: " + DrawMenu.RESET + firstName,
                    DrawMenu.BLUE_BOLD + "[2] Last Name: " + DrawMenu.RESET + lastName,
                    DrawMenu.BLUE_BOLD + "[3] Nickname: " + DrawMenu.RESET + nickname,
                    DrawMenu.BLUE_BOLD + "[4] Phone: " + DrawMenu.RESET + phone,
                    DrawMenu.BLUE_BOLD + "[5] Email: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + (email.isEmpty() ? "example@mail.com" : email) + DrawMenu.RESET,
                    DrawMenu.BLUE_BOLD + "[6] Birth Date: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "YYYY-MM-DD" + DrawMenu.RESET,
                    ""
            };
            DrawMenu.printBoxed(title, contentTable5);

            System.out.println();
            DrawMenu.printCenter("Email: ");
            email = Input.getStringInput();

            if (isValidMail(email)) {
                contact.setEmail(email);
                break;
            }

            DrawMenu.clearConsole();
            System.out.println(DrawMenu.RED_BOLD + "Invalid email format!" + DrawMenu.RESET);
        }

        while (true) {
            DrawMenu.clearConsole();
            String[] contentTable6 = {
                    "",
                    DrawMenu.BLUE_BOLD + "[1] First Name: " + DrawMenu.RESET + firstName,
                    DrawMenu.BLUE_BOLD + "[2] Last Name: " + DrawMenu.RESET + lastName,
                    DrawMenu.BLUE_BOLD + "[3] Nickname: " + DrawMenu.RESET + nickname,
                    DrawMenu.BLUE_BOLD + "[4] Phone: " + DrawMenu.RESET + phone,
                    DrawMenu.BLUE_BOLD + "[5] Email: " + DrawMenu.RESET + email,
                    DrawMenu.BLUE_BOLD + "[6] Birth Date: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + (birthDate.isEmpty() ? "YYYY-MM-DD" : birthDate) + DrawMenu.RESET,
                    ""
            };
            DrawMenu.printBoxed(title, contentTable6);

            System.out.println();
            DrawMenu.printCenter("Birth Date (YYYY-MM-DD or blank): ");
            birthDate = Input.getStringInput();

            if (!birthDate.isEmpty()) {
                if (isValidBirthDay(birthDate)) {
                    contact.setBirthDate(Date.valueOf(birthDate));
                    break;
                }
            }

            DrawMenu.clearConsole();
            System.out.println(DrawMenu.RED_BOLD + "Invalid date format!" + DrawMenu.RESET);
        }

        try {
            Connection dbConnection = Database.openDatabase();

            String sql = "INSERT INTO contacts (first_name, last_name, nickname, phone_primary, email, birth_date, created_at) " + "VALUES (?, ?, ?, ?, ?, ?, NOW())";

            PreparedStatement ps = dbConnection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, contact.getFirstName());
            ps.setString(2, contact.getLastName());
            ps.setString(3, contact.getNickname());
            ps.setString(4, contact.getPhonePrimary());
            ps.setString(5, contact.getEmail());
            ps.setObject(6, contact.getBirthDate());

            ps.executeUpdate();
            dbConnection.close();
        }

        catch (SQLException e) {
            DrawMenu.clearConsole();
            System.out.println(DrawMenu.RED_BOLD + "Database Error: Contact could not be added." + DrawMenu.RESET);
            showUserMenu();
            return;
        }
    }

    public static boolean isValidBirthDay(String date) {

        date = date.replaceAll("\\s+", "");
        if(date.length() < 10)
            return false;
        if((date.charAt(4) != '-') || (date.charAt(7) != '-'))
            return false;
        try {
            LocalDate.parse(date);
            return true;
        } catch (Exception e) {
            System.out.println("Invalid Date! Please enter a valid date in the format YYYY-MM-DD.");
            return false;
        }
    }
    public static boolean isValidMail(String email) {
        if(email.isEmpty())
            return false;
        if(email.contains(" "))
            return false;

        int atIndex = email.indexOf('@');

        if(atIndex == 0)
            return false;
        if(email.indexOf('@', atIndex + 1) != -1)
            return false;

        String domainPart = email.substring(atIndex + 1);

        if (domainPart.isEmpty())
            return false;
        if(domainPart.length()-1==domainPart.lastIndexOf('.'))
            return false;

        int charsAfterDot = domainPart.length() - domainPart.lastIndexOf('.') - 1;

        if(charsAfterDot < 2)
            return false;

        return true;
    }
    public static boolean isValidPhone(String phone) {
        if(phone.isEmpty())
            return false;

        phone = phone.replace("(","");
        phone = phone.replace(")","");
        phone = phone.replaceAll("\\s+", "");

        for (char p : phone.toCharArray()) {
            if (!Character.isDigit(p)) {
                return false;
            }
        }
        if (phone.length() != 11)
            return false;
        if (phone.charAt(0) != '0')
            return false;
        return true;
    }

    public void deleteContact() {

        DrawMenu.clearConsole();
        try {
            int contactId;
            String titleDelete = "Delete Contact, " + this.getName() + " " + this.getSurname() + ", " + this.getRole();

            String[] contentsDelete = {
                    "",
                    DrawMenu.YELLOW_BOLD + "Contact ID: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "Enter contact id to delete" + DrawMenu.RESET,
                    ""
            };
            Contact contact;
            while(true) {
                DrawMenu.printBoxed(titleDelete, contentsDelete);
                System.out.println();
                DrawMenu.printCenter("Contact ID: ");
                contactId = Input.getIntInput();

                contact = getContactById(contactId);

                if (contact.getContactId() == 0) {
                    DrawMenu.clearConsole();
                    System.out.println(DrawMenu.RED_BOLD + "Contact not found. Please try again." + DrawMenu.RESET);
                }
                else
                    break;
            }
            String[] contentsDelete2 = {
                    "",
                    DrawMenu.YELLOW_BOLD + "Contact ID: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + contactId + DrawMenu.RESET,
                    ""
            };
            DrawMenu.clearConsole();
            DrawMenu.printBoxed(titleDelete, contentsDelete2);
            System.out.println();

            DrawMenu.printCenter(
                    DrawMenu.PURPLE_BOLD + "Are you sure you want to delete " + contact.getFirstName() + " " + contact.getLastName() + "? (y/n)" + DrawMenu.RESET
            );
            System.out.println();
            DrawMenu.printCenter("Your choice: ");
            String confirm;
            while(true) {

                DrawMenu.printCenter("Are you sure you want to delete " + contact.getFirstName() + " " + contact.getLastName() + "? (y/n): ");
                confirm = Input.getStringInput().toLowerCase();
                if (confirm.equals("y") || confirm.equals("n")) {
                    break;
                }
                else {
                    DrawMenu.clearConsole();
                    System.out.println(DrawMenu.RED_BOLD + "Invalid input. Please enter (y/n)!");
                }
            }

            int id=contact.getContactId();

            if (confirm.equals("n")) {
                DrawMenu.clearConsole();
                System.out.println("Deletion cancelled.");
                showUserMenu();
                return;
            }
            else {
                Connection db = Database.openDatabase();
                String sql = "DELETE FROM contacts WHERE contact_id = ?";
                PreparedStatement ps = db.prepareStatement(sql);
                ps.setInt(1, id);
                ps.executeUpdate();
                db.close();

                DrawMenu.clearConsole();
                System.out.println(DrawMenu.GREEN_BOLD + "Contact deleted successfully!" + DrawMenu.RESET);
                showUserMenu();
            }
        } catch (Exception e) {
            DrawMenu.clearConsole();
            System.out.println(DrawMenu.RED_BOLD + "Database Error");
            showUserMenu();
        }
    }
    public void undoOperation() {
        // TODO
    }
    public void sortContacts() throws SQLException {

        int choice;
        while(true) {
            DrawMenu.clearConsole();
            String[] contents = {
                    "",
                    DrawMenu.YELLOW_BOLD + "[1]" + DrawMenu.RESET + " Sort by First Name (A-Z)",
                    DrawMenu.YELLOW_BOLD + "[2]" + DrawMenu.RESET + " Sort by First Name (Z-A)",
                    DrawMenu.YELLOW_BOLD + "[3]" + DrawMenu.RESET + " Sort by Last Name (A-Z)",
                    DrawMenu.YELLOW_BOLD + "[4]" + DrawMenu.RESET + " Sort by Last Name (Z-A)",
                    DrawMenu.YELLOW_BOLD + "[5]" + DrawMenu.RESET + " Sort by Created Date (Newest First)",
                    DrawMenu.YELLOW_BOLD + "[6]" + DrawMenu.RESET + " Sort by Created Date (Oldest First)",
                    ""
            };
            DrawMenu.printBoxed("Sort Contacts", contents);
            System.out.println();
            DrawMenu.printCenter("Your choice: ");
            choice = Input.getIntInput();
            if(choice < 1 || choice > 6) {
                DrawMenu.clearConsole();
                System.out.println("Invalid choice. Please enter a valid choice.");
            }
            else {
                break;
            }
        }

        ArrayList<Contact> list = getAllContacts();

        switch(choice) {
            case 1:
                list.sort(Comparator.comparing(Contact::getFirstName));
                break;
            case 2:
                list.sort(Comparator.comparing(Contact::getFirstName).reversed());
                break;
            case 3:
                list.sort(Comparator.comparing(Contact::getLastName));
                break;
            case 4:
                list.sort(Comparator.comparing(Contact::getLastName).reversed());
                break;
            case 5:
                list.sort(Comparator.comparing(Contact::getCreatedAt).reversed());
                break;
            case 6:
                list.sort(Comparator.comparing(Contact::getCreatedAt));
                break;
        }
        DrawMenu.clearConsole();
        for (Contact c : list) {
            printContact(c);
            System.out.println();
        }
        System.out.println();
        DrawMenu.printCenter("Press 1 to go user menu: ");
        while(true) {
            String intput = Input.getStringInput();
            if (intput.equals("1")) {
                DrawMenu.clearConsole();
                showUserMenu();
                break;
            } else {
                System.out.println("Invalid input. Please try again.");
            }
        }
    }
    private void printContact(Contact c) {
        String title = c.getFirstName() + " " + c.getLastName();

        String[] content = {
                DrawMenu.CYAN_BOLD + "ID: " + DrawMenu.RESET + c.getContactId(),
                DrawMenu.CYAN_BOLD + "First Name: " + DrawMenu.RESET + c.getFirstName(),
                DrawMenu.CYAN_BOLD + "Last Name: " + DrawMenu.RESET + c.getLastName(),
                DrawMenu.CYAN_BOLD + "Nickname: " + DrawMenu.RESET + c.getNickname(),
                DrawMenu.CYAN_BOLD + "Phone: " + DrawMenu.RESET + c.getPhonePrimary(),
                DrawMenu.CYAN_BOLD + "Email: " + DrawMenu.RESET + c.getEmail(),
                DrawMenu.CYAN_BOLD + "Birth Date: " + DrawMenu.RESET + c.getBirthDate(),
                DrawMenu.CYAN_BOLD + "Created At: " + DrawMenu.RESET + c.getCreatedAt(),
                DrawMenu.CYAN_BOLD + "Updated At: " + DrawMenu.RESET + c.getUpdatedAt()
        };

        DrawMenu.printBoxed(title, content);
    }
}