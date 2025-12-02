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

    public void addContact() throws SQLException {
        DrawMenu.clearConsole();
        String[] contents = {""};
        DrawMenu.printBoxed("Add New Contact", contents);

        Contact c = new Contact();

        System.out.print("First Name: ");
        c.setFirstName(Input.getStringInput());
        System.out.print("Last Name: ");
        c.setLastName(Input.getStringInput());
        System.out.print("Nickname(optional): ");
        c.setNickname(Input.getStringInput());

        while(true) {
            System.out.print("Phone Number (you have to enter a phone in following format -> 0(___)___ __ __: ");
            String phone = Input.getStringInput();

            if(isValidPhone(phone)) {
                c.setPhonePrimary(phone);
                break;
            }
            else {
                System.out.println("Invalid phone number. Please enter a valid phone number according to format 0(___)___ __ __.");
            }
        }

        while(true) {
            System.out.print("Email: ");
            String email = Input.getStringInput();
            c.setEmail(email);
            if(isValidMail(email)) {
                c.setEmail(email);
                break;
            }
            else {
                System.out.println("Invalid format for email. Please enter a valid email(contains only one @ and at least one dot after @).");
            }
        }

        while(true) {
            System.out.print("Birth Date (YYYY-MM-DD or leave blank): ");
            String birth = Input.getStringInput();
            if (birth.isEmpty()) {
                c.setBirthDate(null);
            }
            else {
                if(isValidBirthDay(birth)) {
                    c.setBirthDate(Date.valueOf(birth));
                    break;
                }
                else{
                    System.out.println("Invalid birth date. Please enter a valid birth date in the format(YYYY-MM-DD).");
                }
            }
        }

        try {
            Connection dbConnection = Database.openDatabase();

            String sql = "INSERT INTO contacts (first_name, last_name, nickname, phone_primary, email, birth_date, created_at) " + "VALUES (?, ?, ?, ?, ?, ?, NOW())";

            PreparedStatement ps = dbConnection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, c.getFirstName());
            ps.setString(2, c.getLastName());
            ps.setString(3, c.getNickname());
            ps.setString(4, c.getPhonePrimary());
            ps.setString(5, c.getEmail());
            ps.setObject(6, c.getBirthDate());

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

    public void deleteContact() throws SQLException {
        try {
            DrawMenu.clearConsole();
            String[] contents = {""};
            DrawMenu.printBoxed("Delete Contact", contents);

            System.out.println();
            Contact contact;
            while(true) {
                DrawMenu.printCenter("Enter Contact ID to delete: ");
                int id = Input.getIntInput();
                contact = getContactById(id);
                if(contact.getContactId() != 0)
                    break;
                else {
                    DrawMenu.clearConsole();
                    System.out.println(DrawMenu.RED_BOLD + "No such contact found!");
                }
            }

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
        }
        catch (Exception e) {
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