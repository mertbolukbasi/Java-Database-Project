package model;

import database.Database;
import utils.DrawMenu;
import utils.Input;
import java.sql.*;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.Comparator;


public class Senior extends Junior {
    private ArrayList<undoOperation> undoStack = new ArrayList<>();

    /**
     *user menu creation
     *author: Ege Usug
     * @throws SQLException
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
            default:
                System.out.println("Invalid input. Please enter a number between 1 and 10.");
                showUserMenu();
                break;
        }

    }

    /**
     *Ege Usug
     * gets the user from their id
     * @param contact_id
     * @return related contact with id
     * @throws SQLException
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
     *author: Ege Usug
     * gets info about contact and adds it to database
     * @throws SQLException
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
                while(firstName.isBlank() || !firstName.matches("^[a-zA-Z]+$")) {
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
        if(nickname.equals("exit")) {
            DrawMenu.clearConsole();
            showUserMenu();
        }
        else {
            contact.setNickname(nickname);
        }


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
            String temp = Input.getStringInput();
            //phone = Input.getStringInput();
            if(temp.equals("exit")) {
                DrawMenu.clearConsole();
                showUserMenu();
            }

            if (isValidPhone(temp)) {
                phone = temp;
                contact.setPhonePrimary(phone);
                break;
            }
            else {
                DrawMenu.clearConsole();
                System.out.println(DrawMenu.RED_BOLD + "Invalid phone format!" + DrawMenu.RESET);
            }
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
            String temp = Input.getStringInput();
            //email = Input.getStringInput();
            if(temp.equals("exit")) {
                DrawMenu.clearConsole();
                showUserMenu();
            }
            if (isValidMail(temp)) {
                email = temp;
                contact.setEmail(email);
                break;
            }
            else {
                DrawMenu.clearConsole();
                System.out.println(DrawMenu.RED_BOLD + "Invalid email format!" + DrawMenu.RESET);
            }
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
            DrawMenu.printCenter("Birth Date (YYYY-MM-DD: ");
            String temp = Input.getStringInput();
            //birthDate = Input.getStringInput();
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
            System.out.println(DrawMenu.GREEN_BOLD + "Contact successfully added!" + DrawMenu.RESET);

            undoStack.add(new undoOperation(undoOperation.ActionType.ADD_CONTACT, null, contact));

            showUserMenu();
        }

        catch (SQLException e) {
            DrawMenu.clearConsole();
            System.out.println(DrawMenu.RED_BOLD + "Database Error: Contact could not be added." + DrawMenu.RESET);
            showUserMenu();
        }
    }

    /**
     *Author: Ege Usug
     * controls if date is in expected format
     * @param date
     * @return returns false or true based on the format
     */
    public static boolean isValidBirthDay(String date) {

        date = date.replaceAll("\\s+", "");
        if (date.isEmpty()) {
            return false;
        }
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

    /**
     *Author: Ege Usug
     * checks if the email is in expected format
     * @param email
     * @return returns false or true based on the format
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

    /**
     *Author: Ege Usug
     * Checks if the phone is in the expected format
     * @param phone
     * @return true or false based on result
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
     *Author: Ege Usug
     * gets the contact from their id and deletes it from database
     * @throws SQLException
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

                undoStack.add(new undoOperation(undoOperation.ActionType.DELETE_CONTACT, contact, null));

                db.close();

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

    /**
     *Author: Ege Usug
     * makes undo operation
     */
    public void undoOperation() {
        DrawMenu.clearConsole();

        if (undoStack.isEmpty()) {
            System.out.println(DrawMenu.RED_BOLD + "No operations to undo!" + DrawMenu.RESET);Input.getStringInput();
            return;
        }

        undoOperation last = undoStack.remove(undoStack.size() - 1);

        try (Connection db = Database.openDatabase()) {

            switch (last.getType()) {

                case ADD_CONTACT:
                    // Ekleneni siler
                    Contact added = (Contact) last.getNewData();
                    PreparedStatement ps1 = db.prepareStatement("DELETE FROM contacts WHERE contact_id = ?");
                    ps1.setInt(1, added.getContactId());
                    ps1.executeUpdate();
                    System.out.println(DrawMenu.GREEN_BOLD + "Undo successful: Contact addition reverted." + DrawMenu.RESET);
                    break;


                case DELETE_CONTACT:
                    // Silinen geri eklenir
                    Contact deleted = (Contact) last.getOldData();
                    PreparedStatement ps2 = db.prepareStatement("INSERT INTO contacts (contact_id, first_name, last_name, nickname, phone_primary, email, birth_date, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");

                    ps2.setInt(1, deleted.getContactId());
                    ps2.setString(2, deleted.getFirstName());
                    ps2.setString(3, deleted.getLastName());
                    ps2.setString(4, deleted.getNickname());
                    ps2.setString(5, deleted.getPhonePrimary());
                    ps2.setString(6, deleted.getEmail());
                    ps2.setObject(7, deleted.getBirthDate());
                    ps2.setObject(8, deleted.getCreatedAt());
                    ps2.setObject(9, deleted.getUpdatedAt());
                    ps2.executeUpdate();
                    System.out.println(DrawMenu.GREEN_BOLD + "Undo successful: Contact restored." + DrawMenu.RESET);
                    break;


                case UPDATE_CONTACT:
                    // Yeni veriyi eski veriyle değiştirme
                    Contact oldC = (Contact) last.getOldData();
                    PreparedStatement ps3 = db.prepareStatement("UPDATE contacts SET first_name=?, last_name=?, nickname=?, phone_primary=?, email=?, birth_date=? WHERE contact_id=?");

                    ps3.setString(1, oldC.getFirstName());
                    ps3.setString(2, oldC.getLastName());
                    ps3.setString(3, oldC.getNickname());
                    ps3.setString(4, oldC.getPhonePrimary());
                    ps3.setString(5, oldC.getEmail());
                    ps3.setObject(6, oldC.getBirthDate());
                    ps3.setInt(7, oldC.getContactId());
                    ps3.executeUpdate();
                    System.out.println(DrawMenu.GREEN_BOLD + "Undo successful: Contact update reverted." + DrawMenu.RESET);
                    break;
            }

        }
        catch (SQLException e) {
            System.out.println(DrawMenu.RED_BOLD + "Undo failed due to database error." + DrawMenu.RESET);
        }

        Input.getStringInput();
    }

    /**
     *Author: Ege Usug
     * sorts contacts how user wants
     * @throws SQLException
     */
    public void sortContacts() throws SQLException {
        DrawMenu.clearConsole();

        try {
            ArrayList<Contact> contacts = getAllContacts();
            int choice;
            String str;
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
                //choice = Input.getIntInput();   hata olmaz ise sil
                str = Input.getStringInput().toLowerCase();
                if(str.equals("exit")) {
                    DrawMenu.clearConsole();
                    showUserMenu();
                }
                choice = Integer.parseInt(str);
                if(choice < 1 || choice > 6) {
                    DrawMenu.clearConsole();
                    System.out.println("Invalid choice. Please enter a valid choice.");
                }
                else {
                    break;
                }
            }
            switch(choice) {
                case 1:
                    contacts.sort(Comparator.comparing(Contact::getFirstName));
                    break;
                case 2:
                    contacts.sort(Comparator.comparing(Contact::getFirstName).reversed());
                    break;
                case 3:
                    contacts.sort(Comparator.comparing(Contact::getLastName));
                    break;
                case 4:
                    contacts.sort(Comparator.comparing(Contact::getLastName).reversed());
                    break;
                case 5:
                    contacts.sort(Comparator.comparing(Contact::getCreatedAt).reversed());
                    break;
                case 6:
                    contacts.sort(Comparator.comparing(Contact::getCreatedAt));
                    break;
            }

            boolean loop = true;
            int contactCount = contacts.size();
            int currentIndex = 1;

            while (loop) {

                String inputStr;

                label:
                while (true) {
                    DrawMenu.clearConsole();

                    Contact currentContact = contacts.get(currentIndex - 1);
                    printContact(currentContact);

                    System.out.println();
                    DrawMenu.printCenter(DrawMenu.YELLOW_BOLD + "< " + currentIndex + "/" + contactCount + " >" + DrawMenu.RESET);
                    System.out.println();
                    DrawMenu.printCenter("Previous: A, Next: D, Exit: 'exit'");
                    System.out.println();
                    DrawMenu.printCenter("Your choice: ");

                    inputStr = Input.getStringInput().toLowerCase();

                    switch (inputStr) {
                        case "a":
                            if (currentIndex > 1) {
                                currentIndex--;
                                break label;
                            }
                            else {
                                DrawMenu.clearConsole();
                                printContact(currentContact);
                                System.out.println("\n" + DrawMenu.RED_BOLD + "You reached the start of the list." + DrawMenu.RESET);
                                System.out.println("Press Enter to continue...");
                                Input.getStringInput();
                                break label;
                            }

                        case "d":
                            if (currentIndex < contactCount) {
                                currentIndex++;
                                break label;
                            }
                            else {
                                DrawMenu.clearConsole();
                                printContact(currentContact);
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
                            printContact(currentContact);
                            System.out.println("\n" + DrawMenu.RED_BOLD + "Invalid input. Use A, D or exit." + DrawMenu.RESET);
                            System.out.println("Press Enter to continue...");
                            Input.getStringInput();
                            break label;
                    }
                }
                if (!loop) {
                    DrawMenu.clearConsole();
                    showUserMenu();
                    return;
                }
            }
        }
        catch (SQLException e) {
            DrawMenu.clearConsole();
            System.out.println(DrawMenu.RED_BOLD + "Database Error: Contacts could not be fetched." + DrawMenu.RESET);
            System.out.println("Press Enter to return...");
            Input.getStringInput();
            showUserMenu();
        }
        catch (NumberFormatException e) {
            DrawMenu.clearConsole();
            System.out.println(DrawMenu.RED_BOLD + "Invalid input. Please enter a valid choice.");
            showUserMenu();
        }
    }

    /**
     *Author: Ege Usug
     * prints the contact for sort contact
     * @param c contact object
     */
    //sort contact uses this one
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