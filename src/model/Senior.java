package model;

import database.Database;
import utils.*;

import java.sql.*;
import java.util.ArrayList;
import java.time.LocalDate;


public class Senior extends Junior {

    /**
     * User menu creation
     * @throws SQLException if a database access error occurs.
     * @author Ege Usuğ
     */
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
            System.out.println("Invalid input. Please enter a number between 1 and 10." + DrawMenu.RESET);
            showUserMenu();
        }
        switch (input) {
            case 1:
                DrawMenu.clearConsole();
                this.changePassword();
                DrawMenu.showLoginScreen();
                break;
            case 2:
                DrawMenu.clearConsole();
                this.listAllContacts();
                break;
            case 3:
                DrawMenu.clearConsole();
                this.searchContact();
                this.showUserMenu();
                break;
            case 4:
                DrawMenu.clearConsole();
                this.searchBySelectedFields();
                this.showUserMenu();
                break;
            case 5:
                DrawMenu.clearConsole();
                this.sortContacts();
                this.showUserMenu();
                break;
            case 6:
                DrawMenu.clearConsole();
                this.addContact();
                break;
            case 7:
                DrawMenu.clearConsole();
                this.updateContact();
                this.showUserMenu();
                break;
            case 8:
                DrawMenu.clearConsole();
                this.deleteContact();
                break;
            case 9:
                if(Undo.getUndoStack().isEmpty()) {
                    DrawMenu.clearConsole();
                    System.out.println(DrawMenu.RED_BOLD + "No undo available" + DrawMenu.RESET);
                } else {
                    UndoData undoData = Undo.getUndoStack().peek();
                    Undo.getUndoStack().clear();
                    try {
                        switch(undoData.getUndoAction()) {
                            case ADD_CONTACT:
                                Undo.addContactUndo(undoData.getOldContact().getNickname());
                                break;
                            case DELETE_CONTACT:
                                Undo.deleteContactUndo(undoData.getOldContact());
                                break;
                            case UPDATE_CONTACT:
                                Undo.updateContactUndo(undoData.getOldContact().getContactId(), undoData.getOldContact());
                                break;
                        }
                        DrawMenu.clearConsole();
                        System.out.println(DrawMenu.GREEN_BOLD + "Undo successful!" + DrawMenu.RESET);
                    } catch (Exception e) {
                        DrawMenu.clearConsole();
                        System.out.println(DrawMenu.RED_BOLD + "Database Error: Undo did not work." + DrawMenu.RESET);
                        System.out.println(e.getMessage());
                    }
                }
                this.showUserMenu();
                break;
            case 10:
                DrawMenu.clearConsole();
                this.logout();
                DrawMenu.showLoginScreen();
                break;
            default:
                System.out.println("Invalid input. Please enter a number between 1 and 10.");
                showUserMenu();
                break;
        }
    }

    /**
     * Gets the user from their id.
     * @param contact_id Contact id
     * @return related contact with id
     * @throws SQLException if a database access error occurs.
     * @author Ege Usuğ
     */
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

    /**
     * Gets info about contact and adds it to database.
     * @throws SQLException if a database access error occurs.
     * @author Ege Usuğ
     */
    public void addContact() throws SQLException {

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
                if(firstName.equals("exit")) {
                    DrawMenu.clearConsole();
                    showUserMenu();
                }
                while(!firstName.matches("^[a-zA-ZçÇğĞıİöÖşŞüÜ]+$")) {
                    DrawMenu.clearConsole();
                    System.out.println(DrawMenu.RED + "First name cannot contain numbers or cannot be empty!" + DrawMenu.RESET);
                    DrawMenu.printBoxed(title, contentTable1);
                    System.out.println();
                    DrawMenu.printCenter("First Name: ");
                    firstName = Input.getStringInput();
                }
                contact.setFirstName(firstName);
                break;
            }
            else {
                DrawMenu.clearConsole();
                System.out.println(DrawMenu.RED_BOLD + "First name cannot be empty!" + DrawMenu.RESET);
            }
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
                if(lastName.equals("exit")) {
                    DrawMenu.clearConsole();
                    showUserMenu();
                }
                while(!lastName.matches("^[a-zA-ZçÇğĞıİöÖşŞüÜ]+$")) {
                    DrawMenu.clearConsole();
                    System.out.println(DrawMenu.RED+ "Last name cannot contain numbers or cannot be empty!" + DrawMenu.RESET);
                    DrawMenu.printBoxed(title, contentTable2);
                    System.out.println();
                    DrawMenu.printCenter("Last Name: ");
                    lastName = Input.getStringInput();
                }
                contact.setLastName(lastName);
                break;
            }
            else {
                DrawMenu.clearConsole();
                System.out.println(DrawMenu.RED_BOLD + "Last name cannot be empty!" + DrawMenu.RESET);
            }
        }

        DrawMenu.clearConsole();
        String[] contentTable3 = {
                "",
                DrawMenu.BLUE_BOLD + "[1] First Name: " + DrawMenu.RESET + firstName,
                DrawMenu.BLUE_BOLD + "[2] Last Name: " + DrawMenu.RESET + lastName,
                DrawMenu.BLUE_BOLD + "[3] Nickname: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "Enter nickname" + DrawMenu.RESET,
                DrawMenu.BLUE_BOLD + "[4] Phone: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "Enter phone number" + DrawMenu.RESET,
                DrawMenu.BLUE_BOLD + "[5] Email: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "Enter email" + DrawMenu.RESET,
                DrawMenu.BLUE_BOLD + "[6] Birth Date: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "YYYY-MM-DD" + DrawMenu.RESET,
                ""
        };
        DrawMenu.printBoxed(title, contentTable3);

        System.out.println();
        DrawMenu.printCenter("Nickname: ");
        do {
            nickname = Input.getStringInput();
            if(nickname.isEmpty()) {
                DrawMenu.clearConsole();
                System.out.println(DrawMenu.RED_BOLD + "Nickname cannot be empty!" + DrawMenu.RESET);
                DrawMenu.printBoxed(title, contentTable3);
                System.out.println();
                DrawMenu.printCenter("Nickname: ");
            }
        } while(nickname.isEmpty());

        if(nickname.equals("exit")) {
            DrawMenu.clearConsole();
            showUserMenu();
        }
        else {
            contact.setNickname(nickname);
        }

        DrawMenu.clearConsole();
        while (true) {
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
            String temp = Input.getStringInput();
            if(temp.equals("exit")) {
                DrawMenu.clearConsole();
                showUserMenu();
            }

            if (isValidPhone(temp)) {
                ArrayList<Contact> contacts = getAllContacts();
                Contact con;
                String phoneControl;

                for (int i = 0; i < contacts.size(); i++) {
                    con = contacts.get(i);
                    phoneControl = con.getPhonePrimary();

                    if (phoneControl.equals(temp)) {
                        System.out.println(DrawMenu.RED + "This phone is already in use! Enter different phone number!" + DrawMenu.RESET);
                        temp = Input.getStringInput();
                    }
                    else
                        break;
                }
                phone = temp;
                contact.setPhonePrimary(phone);
                break;
            }
            else {
                DrawMenu.clearConsole();
                System.out.println(DrawMenu.RED_BOLD + "Invalid phone format!" + DrawMenu.RESET);
            }
        }

        DrawMenu.clearConsole();
        while (true) {
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
            String temp = Input.getStringInput();
            //email = Input.getStringInput();
            if(temp.equals("exit")) {
                DrawMenu.clearConsole();
                showUserMenu();
            }
            if (isValidMail(temp)) {
                ArrayList<Contact> contacts = getAllContacts();
                Contact con;
                String mailControl;

                for (int i = 0; i < contacts.size(); i++) {
                    con = contacts.get(i);
                    mailControl = con.getPhonePrimary();

                    if (mailControl.equals(temp)) {
                        System.out.println(DrawMenu.RED + "This mail is already in use! Enter different mail!" + DrawMenu.RESET);
                        temp = Input.getStringInput();
                    }
                    else
                        break;
                }
                email = temp;
                contact.setEmail(email);
                break;
            }
            else {
                DrawMenu.clearConsole();
                System.out.println(DrawMenu.RED_BOLD + "Invalid email format!" + DrawMenu.RESET);
            }
        }

        DrawMenu.clearConsole();
        while (true) {
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
            DrawMenu.printCenter("Birth Date (YYYY-MM-DD): ");
            String temp = Input.getStringInput();
            if(temp.equals("exit")) {
                DrawMenu.clearConsole();
                showUserMenu();
            }
            if (isValidBirthDay(temp)) {
                birthDate = temp;
                contact.setBirthDate(Date.valueOf(birthDate));
                break;
            }

            else {
                DrawMenu.clearConsole();
                System.out.println(DrawMenu.RED_BOLD + "Invalid date format! or You can not leave here Empty!" + DrawMenu.RESET);
            }
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

            UndoData undoData = new UndoData();
            undoData.setUndoAction(Action.ADD_CONTACT);
            undoData.setOldContact(contact);
            Undo.getUndoStack().add(undoData);

            DrawMenu.clearConsole();
            System.out.println(DrawMenu.GREEN_BOLD + "Contact successfully added!" + DrawMenu.RESET);

            showUserMenu();
        }

        catch (SQLException e) {
            DrawMenu.clearConsole();
            System.out.println(DrawMenu.RED_BOLD + "Database Error: Contact could not be added." + DrawMenu.RESET);
            showUserMenu();
        }
    }

    /**
     * Controls if date is in expected format.
     * @param date Date
     * @return returns false or true based on the format
     * @author Ege Usuğ
     */
    public static boolean isValidBirthDay(String date) {

        if (date.isEmpty()) {
            return false;
        }
        if (!date.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            return false;
        }

        try {
            LocalDate parsedDate = LocalDate.parse(date);

            if (parsedDate.isAfter(LocalDate.now())) {
                return false;
            }
            return true;
        }
        catch (Exception e) {
            System.out.println("Invalid Date! Please enter a valid date in the format YYYY-MM-DD.");
            return false;
        }
    }

    /**
     * Checks if the email is in expected format.
     * @param email Email
     * @return returns false or true based on the format
     * @author Ege Usuğ
     */
    public static boolean isValidMail(String email) {

        if(email.isEmpty())
            return false;
        if(email.contains(" "))
            return false;

        int atIndex = email.indexOf('@');
        if(atIndex <= 0)
            return false;
        if(email.indexOf('@', atIndex + 1) != -1)
            return false;

        String localPart = email.substring(0, atIndex);
        if(!localPart.matches("^[A-Za-z0-9._-]+$"))
            return false;

        String domainPart = email.substring(atIndex + 1);
        if(domainPart.isEmpty())
            return false;

        int lastDot = domainPart.lastIndexOf('.');
        if(lastDot == -1)
            return false;
        if(lastDot == domainPart.length() - 1)
            return false;

        int charsAfterDot = domainPart.length() - lastDot - 1;
        if(charsAfterDot < 2)
            return false;

        return true;
    }


    /**
     * Checks if the phone is in the expected format.
     * @param phone Phone number
     * @return true or false based on result
     * @author Ege Usuğ
     */
    //format 05332100598
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

    /**
     * Gets the contact from their id and deletes it from database.
     * @throws SQLException if a database access error occurs.
     * @author Ege Usuğ
     */
    public void deleteContact() throws SQLException {

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

            DrawMenu.printCenter(DrawMenu.PURPLE_BOLD + "Are you sure you want to delete " + contact.getFirstName() + " " + contact.getLastName() + "? (y/n)" + DrawMenu.RESET);
            System.out.println();
            DrawMenu.printCenter("Your choice: ");
            String confirm;
            while(true) {
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
            }
            else {
                Connection db = Database.openDatabase();
                String sql = "DELETE FROM contacts WHERE contact_id = ?";
                PreparedStatement ps = db.prepareStatement(sql);
                ps.setInt(1, id);
                ps.executeUpdate();

                db.close();

                UndoData undoData = new UndoData();
                undoData.setUndoAction(Action.DELETE_CONTACT);
                undoData.setOldContact(contact);
                Undo.getUndoStack().add(undoData);

                DrawMenu.clearConsole();
                System.out.println(DrawMenu.GREEN_BOLD + "Contact deleted successfully!" + DrawMenu.RESET);
                showUserMenu();
            }
        }
        catch (SQLException e) {
            DrawMenu.clearConsole();
            System.out.println(DrawMenu.RED_BOLD + "Database Error" + DrawMenu.RESET);
            showUserMenu();
        }
        catch (NumberFormatException e) {
            DrawMenu.clearConsole();
            System.out.println(DrawMenu.RED_BOLD + "Invalid id. Please try again." + DrawMenu.RESET);
            showUserMenu();
        }
    }


}