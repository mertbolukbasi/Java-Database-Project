package model;

import database.Database;
import utils.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Stack;


public class Manager extends User {


    /**
     * To return most used name and its count.
     * @author Mert Bölükbaşı
     */
    private static class MostUsedName {
        public String name = "";
        public int count = 0;
    }

    /**
     * @param user_id User id
     * Fetch user with user id from database.
     * @throws SQLException if a database access error occurs.
     * @author Mert Bölükbaşı
     */
    private User getUserById(int user_id) throws SQLException {
        String query = "SELECT * FROM users WHERE user_id = " + user_id;
        Connection dbConnection = Database.openDatabase();
        Statement statement = dbConnection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        User user = new User() {
            @Override
            public void showUserMenu() {

            }
        };
        if(resultSet.next()) {
            int id = resultSet.getInt("user_id");
            String username = resultSet.getString("username");
            String password_hash = resultSet.getString("password_hash");
            String name = resultSet.getString("name");
            String surname = resultSet.getString("surname");
            String role = resultSet.getString("role");

            user.setUserId(id);
            user.setUsername(username);
            user.setPassword_hash(password_hash);
            user.setName(name);
            user.setSurname(surname);
            user.setRole(role);
            dbConnection.close();
            return user;
        }
        return null;
    }

    /**
     * Fetch all users from database.
     * @throws SQLException if a database access error occurs.
     * @author Mert Bölükbaşı
     */
    private ArrayList<User> listAllUsers() throws SQLException {
        ArrayList<User> list = new ArrayList<>();
        String query = "SELECT * FROM users";
        Connection dbConnection = Database.openDatabase();
        Statement statement = dbConnection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        while(resultSet.next()) {
            int id = resultSet.getInt("user_id");
            String username = resultSet.getString("username");
            String password_hash = resultSet.getString("password_hash");
            String name = resultSet.getString("name");
            String surname = resultSet.getNString("surname");
            String role = resultSet.getString("role");
            Date createdAt = resultSet.getDate("created_at");
            Date updatedAt = resultSet.getDate("updated_at");

            User user = new User() {
                @Override
                public void showUserMenu() {

                }
            };
            user.setUserId(id);
            user.setUsername(username);
            user.setPassword_hash(password_hash);
            user.setName(name);
            user.setSurname(surname);
            user.setRole(role);
            user.setCreated_at(createdAt);
            user.setUpdated_at(updatedAt);
            list.add(user);
        }
        return list;
    }

    /**
     * @param user_id User id
     * @param user User object
     * Update user fields in database.
     * @throws SQLException if a database access error occurs.
     * @author Mert Bölükbaşı
     */
    private void updateUser(int user_id, User user, User oldUser) throws SQLException {
        String sql = "UPDATE users SET username = ?, password_hash = ?, name = ?, surname = ?, role = ? WHERE user_id = ?";
        Connection dbConnection = Database.openDatabase();

        PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
        preparedStatement.setString(1, user.getUsername());
        preparedStatement.setString(2, user.getPassword_hash());
        preparedStatement.setString(3, user.getName());
        preparedStatement.setString(4, user.getSurname());
        preparedStatement.setString(5, user.getRole());
        preparedStatement.setInt(6, user_id);
        preparedStatement.executeUpdate();
        dbConnection.close();

        UndoData undoData = new UndoData();
        undoData.setUndoAction(Action.UPDATE_USER);
        undoData.setOldUser(oldUser);
        Undo.getUndoStack().add(undoData);
    }

    /**
     * @param user User object
     * Add new user to database.
     * @throws SQLException if a database access error occurs.
     * @author Mert Bölükbaşı
     */
    private void addUser(User user) throws SQLException {
        String query = "INSERT INTO users (name, surname, username, password_hash, role) VALUES (?, ?, ?, ?, ?)";
        Connection dbConnection = Database.openDatabase();
        PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
        String passwordHash = PasswordHash.hash(user.getPassword_hash());
        preparedStatement.setString(1, user.getName());
        preparedStatement.setString(2, user.getSurname());
        preparedStatement.setString(3, user.getUsername());
        preparedStatement.setString(4, passwordHash);
        preparedStatement.setString(5, user.getRole());
        preparedStatement.executeUpdate();
        dbConnection.close();

        UndoData undoData = new UndoData();
        undoData.setUndoAction(Action.ADD_USER);
        undoData.setOldUser(user);
        Undo.getUndoStack().add(undoData);
    }

    /**
     * @param username User username
     * Controls user exists or not with username.
     * @throws SQLException if a database access error occurs.
     * @author Mert Bölükbaşı
     */
    private boolean checkExistingUser(String username) throws SQLException {
        String query = "SELECT username FROM users WHERE username = ?";
        Connection connection = Database.openDatabase();
        PreparedStatement checkStmt = connection.prepareStatement(query);
        checkStmt.setString(1, username);
        ResultSet checkResult = checkStmt.executeQuery();

        if(checkResult.next()) {
            connection.close();
            return true;
        }
        connection.close();
        return false;
    }

    /**
     * @param id User id
     * Controls user exists or not with user id.
     * @throws SQLException if a database access error occurs.
     * @author Mert Bölükbaşı
     */
    private boolean checkExistingUser(int id) throws SQLException {
        String query = "SELECT user_id FROM users WHERE user_id = ?";
        Connection connection = Database.openDatabase();
        PreparedStatement checkStmt = connection.prepareStatement(query);
        checkStmt.setInt(1, id);
        ResultSet checkResult = checkStmt.executeQuery();


        if(checkResult.next()) {
            connection.close();
            return true;
        }
        connection.close();
        return false;
    }

    /**
     * @param username User username
     * Fetch user from database with username.
     * @throws SQLException if a database access error occurs.
     * @author Mert Bölükbaşı
     */
    private boolean getUserByUsername(String username) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ?";
        Connection connection = Database.openDatabase();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, username);
        ResultSet resultSet = preparedStatement.executeQuery();
        if(resultSet.next()) {
            connection.close();
            return true;
        }
        connection.close();
        return false;
    }

    /**
     * @param id Userid
     * Delete user from database with user id.
     * @throws SQLException if a database access error occurs.
     * @author Mert Bölükbaşı
     */
    private void deleteUser(int id) throws SQLException {
        User oldUser = this.getUserById(id);
        String query = "DELETE FROM users WHERE user_id = ?";
        Connection connection = Database.openDatabase();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
        connection.close();

        UndoData undoData = new UndoData();
        undoData.setUndoAction(Action.DELETE_USER);
        undoData.setOldUser(oldUser);
        Undo.getUndoStack().add(undoData);
    }

    /**
     * Update user menu.
     * @throws SQLException if a database access error occurs.
     * @author Mert Bölükbaşı
     */
    private User updateUserMenu(User user) throws SQLException {
        String updateTitle = "Update User, " + this.getName() + " " + this.getSurname() + ", " + this.getRole();;
        String usernameInput;
        DrawMenu.clearConsole();
        do {
            String[] updateFieldsContents = {
                    "",
                    DrawMenu.BLUE_BOLD + "Username: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "Enter new username" + DrawMenu.RESET,
                    DrawMenu.BLUE_BOLD + "Password: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "Enter new password" + DrawMenu.RESET,
                    DrawMenu.BLUE_BOLD + "Name: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "Enter new surname" + DrawMenu.RESET,
                    DrawMenu.BLUE_BOLD + "Surname: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "Enter new role" + DrawMenu.RESET,
                    DrawMenu.BLUE_BOLD + "Role: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "Enter new role" + DrawMenu.RESET,
                    "",
                    DrawMenu.PURPLE_BOLD + "Leave blank to avoid changes" + DrawMenu.RESET,
                    ""
            };
            DrawMenu.printBoxed(updateTitle, updateFieldsContents);
            System.out.println();
            DrawMenu.printCenter("Username: ");
            usernameInput = Input.getStringInput();
            if(usernameInput.equals("exit")) return null;
            if(getUserByUsername(usernameInput)) {
                DrawMenu.clearConsole();
                System.out.println(DrawMenu.RED_BOLD + "Username already exists. Please try again." + DrawMenu.RESET);
            }
            if(usernameInput.isEmpty()) {
                usernameInput = user.getUsername();
                break;
            };
        } while(getUserByUsername(usernameInput));
        DrawMenu.clearConsole();



        String[] updateFieldsContents2 = {
                "",
                DrawMenu.BLUE_BOLD + "Username: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + usernameInput + DrawMenu.RESET,
                DrawMenu.BLUE_BOLD + "Password: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "Enter new password" + DrawMenu.RESET,
                DrawMenu.BLUE_BOLD + "Name: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "Enter new surname" + DrawMenu.RESET,
                DrawMenu.BLUE_BOLD + "Surname: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "Enter new surname" + DrawMenu.RESET,
                DrawMenu.BLUE_BOLD + "Role: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "Enter new role" + DrawMenu.RESET,
                "",
                DrawMenu.PURPLE_BOLD + "Leave blank to avoid changes" + DrawMenu.RESET,
                ""
        };
        DrawMenu.printBoxed(updateTitle, updateFieldsContents2);
        System.out.println();
        DrawMenu.printCenter("Password: ");
        String passwordInput;
        boolean passwordChange = true;
        do {
            passwordInput = Input.getStringInput();
            if(passwordInput.equals("exit")) return null;
            if(passwordInput.isEmpty()) {
                passwordChange = false;
                passwordInput = user.getPassword_hash();
                break;
            }
            if(passwordInput.length() < 2 || passwordInput.length() > 32) {
                DrawMenu.clearConsole();
                System.out.println(DrawMenu.RED_BOLD + "Password must be between 2 and 32 characters." + DrawMenu.RESET);
                DrawMenu.printBoxed(updateTitle, updateFieldsContents2);
                System.out.println();
                DrawMenu.printCenter("Password: ");
            }
        } while(passwordInput.length() < 2 || passwordInput.length() > 32);
        if(passwordChange) passwordInput = PasswordHash.hash(passwordInput);
        DrawMenu.clearConsole();


        String[] updateFieldsContents3 = {
                "",
                DrawMenu.BLUE_BOLD + "Username: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + usernameInput,
                DrawMenu.BLUE_BOLD + "Password: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "*****" + DrawMenu.RESET,
                DrawMenu.BLUE_BOLD + "Name: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "Enter new surname" + DrawMenu.RESET,
                DrawMenu.BLUE_BOLD + "Surname: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "Enter new surname" + DrawMenu.RESET,
                DrawMenu.BLUE_BOLD + "Role: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "Enter new role" + DrawMenu.RESET,
                "",
                DrawMenu.PURPLE_BOLD + "Leave blank to avoid changes" + DrawMenu.RESET,
                ""
        };
        DrawMenu.printBoxed(updateTitle, updateFieldsContents3);
        System.out.println();
        String nameInput;
        while (true) {
            DrawMenu.printCenter("Name: ");
            nameInput = Input.getStringInput();
            if(nameInput.equals("exit")) return null;

            if (nameInput.isEmpty()) {
                nameInput = user.getName();
                break;
            }

            if (nameInput.matches("^[a-zA-ZçÇğĞıİöÖşŞüÜ\\s]+$")) {
                break;
            } else {
                DrawMenu.clearConsole();
                System.out.println(DrawMenu.RED_BOLD + "Invalid input. Please enter a valid name." + DrawMenu.RESET);
                DrawMenu.printBoxed(updateTitle, updateFieldsContents3);
                System.out.println();
            }
        }
        DrawMenu.clearConsole();


        String[] updateFieldsContents4 = {
                "",
                DrawMenu.BLUE_BOLD + "Username: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + usernameInput,
                DrawMenu.BLUE_BOLD + "Password: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "*****" + DrawMenu.RESET,
                DrawMenu.BLUE_BOLD + "Name: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + nameInput + DrawMenu.RESET,
                DrawMenu.BLUE_BOLD + "Surname: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "Enter new surname" + DrawMenu.RESET,
                DrawMenu.BLUE_BOLD + "Role: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "Enter new role" + DrawMenu.RESET,
                "",
                DrawMenu.PURPLE_BOLD + "Leave blank to avoid changes" + DrawMenu.RESET,
                ""
        };
        DrawMenu.printBoxed(updateTitle, updateFieldsContents4);
        System.out.println();
        String surnameInput;
        while (true) {
            DrawMenu.printCenter("Surname: ");
            surnameInput = Input.getStringInput();
            if(surnameInput.equals("exit")) return null;

            if (surnameInput.isEmpty()) {
                surnameInput = user.getSurname();
                break;
            }

            if (surnameInput.matches("^[a-zA-ZçÇğĞıİöÖşŞüÜ]+$")) {
                break;
            } else {
                DrawMenu.clearConsole();
                System.out.println(DrawMenu.RED_BOLD + "Invalid input. Please enter a valid surname." + DrawMenu.RESET);
                DrawMenu.printBoxed(updateTitle, updateFieldsContents4);
                System.out.println();
            }
        }
        DrawMenu.clearConsole();


        String[] updateFieldsContents5 = {
                "",
                DrawMenu.BLUE_BOLD + "Username: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + usernameInput + DrawMenu.RESET,
                DrawMenu.BLUE_BOLD + "Password: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "*****" + DrawMenu.RESET,
                DrawMenu.BLUE_BOLD + "Name: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + nameInput + DrawMenu.RESET,
                DrawMenu.BLUE_BOLD + "Surname: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + surnameInput + DrawMenu.RESET,
                DrawMenu.BLUE_BOLD + "Role: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "Enter new role" + DrawMenu.RESET,
                "",
                DrawMenu.PURPLE_BOLD + "Leave blank to avoid changes" + DrawMenu.RESET,
                ""
        };
        DrawMenu.printBoxed(updateTitle, updateFieldsContents5);
        System.out.println();
        DrawMenu.printCenter("man: Manager\n");
        DrawMenu.printCenter("sd: Senior Developer\n");
        DrawMenu.printCenter("jd: Junior Developer\n");
        DrawMenu.printCenter("tt: Tester\n\n");
        DrawMenu.printCenter("Role: ");
        String roleInput;
        do {
            roleInput = Input.getStringInput();
            if(roleInput.equals("exit")) return null;
            if(roleInput.isEmpty()) {
                roleInput = user.getRole();
                break;
            }
            if(!roleInput.equals("man") && !roleInput.equals("sd") && !roleInput.equals("jd") && !roleInput.equals("tt") && !roleInput.isEmpty()) {
                DrawMenu.clearConsole();
                System.out.println(DrawMenu.RED_BOLD + "Invalid input. Please enter a valid role as man, sd, jd, tt or empty." + DrawMenu.RESET);
                DrawMenu.printBoxed(updateTitle, updateFieldsContents5);
                System.out.println();
                DrawMenu.printCenter("man: Manager\n");
                DrawMenu.printCenter("sd: Senior Developer\n");
                DrawMenu.printCenter("jd: Junior Developer\n");
                DrawMenu.printCenter("tt: Tester\n\n");
                DrawMenu.printCenter("Role: ");
            }
        } while(!roleInput.equals("man") && !roleInput.equals("sd") && !roleInput.equals("jd") && !roleInput.equals("tt") && !roleInput.isEmpty());

        switch(roleInput) {
            case "man":
                roleInput = "Manager";
                break;
            case "sd":
                roleInput = "Senior Developer";
                break;
            case "jd":
                roleInput = "Junior Developer";
                break;
            case "tt":
                roleInput = "Tester";
                break;
        }

        DrawMenu.clearConsole();
        String[] updateFieldsContents6 = {
                "",
                DrawMenu.BLUE_BOLD + "Username: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + usernameInput + DrawMenu.RESET,
                DrawMenu.BLUE_BOLD + "Password: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "*****" + DrawMenu.RESET,
                DrawMenu.BLUE_BOLD + "Name: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + nameInput + DrawMenu.RESET,
                DrawMenu.BLUE_BOLD + "Surname: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + surnameInput + DrawMenu.RESET,
                DrawMenu.BLUE_BOLD + "Role: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + roleInput + DrawMenu.RESET,
                "",
                DrawMenu.PURPLE_BOLD + "Leave blank to avoid changes" + DrawMenu.RESET,
                ""
        };
        DrawMenu.printBoxed(updateTitle, updateFieldsContents6);
        System.out.println(DrawMenu.GREEN_BOLD + "User updating..." + DrawMenu.RESET);

        User newUser = new User() {
            @Override
            public void showUserMenu() throws SQLException {

            }
        };

        newUser.setUsername(usernameInput);
        newUser.setPassword_hash(passwordInput);
        newUser.setName(nameInput);
        newUser.setSurname(surnameInput);
        newUser.setRole(roleInput);
        return newUser;
    }

    /**
     * Calculates total contact from database.
     * @throws SQLException if a database access error occurs.
     * @author Mert Bölükbaşı
     */
    private int totalContact() throws SQLException {
        String query = "SELECT COUNT(*) FROM contacts";
        Connection dbConnection = Database.openDatabase();
        PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();
        if(resultSet.next()) {
            return resultSet.getInt(1);
        }
        return 0;
    }

    /**
     * Fetch the oldest person from database.
     * @throws SQLException if a database access error occurs.
     * @author Mert Bölükbaşı
     */
    private Contact oldestPerson() throws SQLException {
        String sql = "SELECT * FROM contacts WHERE birth_date IS NOT NULL ORDER BY birth_date ASC LIMIT 1";
        Connection dbConnection = Database.openDatabase();
        Statement statement = dbConnection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        Contact contact = new Contact();
        if(resultSet.next()) {
            contact.setContactId(resultSet.getInt("contact_id"));
            contact.setFirstName(resultSet.getString("first_name"));
            contact.setLastName(resultSet.getString("last_name"));
            contact.setNickname(resultSet.getString("nickname"));
            contact.setPhonePrimary(resultSet.getString("phone_primary"));
            contact.setEmail(resultSet.getString("email"));
            contact.setBirthDate(resultSet.getDate("birth_date"));
            contact.setCreatedAt(resultSet.getDate("created_at"));
            contact.setUpdatedAt(resultSet.getDate("updated_at"));
            return contact;
        }
        return contact;
    }

    /**
     * Fetch the youngest person from database.
     * @throws SQLException if a database access error occurs.
     * @author Mert Bölükbaşı
     */
    private Contact youngestPerson() throws SQLException {
        String sql = "SELECT * FROM contacts WHERE birth_date IS NOT NULL ORDER BY birth_date DESC LIMIT 1";
        Connection dbConnection = Database.openDatabase();
        Statement statement = dbConnection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        Contact contact = new Contact();
        if(resultSet.next()) {
            contact.setContactId(resultSet.getInt("contact_id"));
            contact.setFirstName(resultSet.getString("first_name"));
            contact.setLastName(resultSet.getString("last_name"));
            contact.setNickname(resultSet.getString("nickname"));
            contact.setPhonePrimary(resultSet.getString("phone_primary"));
            contact.setEmail(resultSet.getString("email"));
            contact.setBirthDate(resultSet.getDate("birth_date"));
            contact.setCreatedAt(resultSet.getDate("created_at"));
            contact.setUpdatedAt(resultSet.getDate("updated_at"));
            return contact;
        }
        return contact;
    }

    /**
     * Calculates average age from database.
     * @throws SQLException if a database access error occurs.
     * @author Mert Bölükbaşı
     */
    private double averageAge() throws SQLException {
        String sql = "SELECT AVG(TIMESTAMPDIFF(YEAR, birth_date, CURDATE())) as average_age FROM contacts WHERE birth_date IS NOT NULL";
        Connection dbConnection = Database.openDatabase();
        Statement statement = dbConnection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        if(resultSet.next()) {
            return resultSet.getDouble("average_age");
        }
        return 0.0;
    }

    /**
     * Returns most used name and count.
     * @throws SQLException if a database access error occurs.
     * @author Mert Bölükbaşı
     */
    private MostUsedName mostUsedName() throws SQLException {
        String sql = "SELECT first_name, COUNT(*) as count FROM contacts " +
                "WHERE first_name IS NOT NULL " +
                "GROUP BY first_name " +
                "ORDER BY count DESC LIMIT 1";

        Connection dbConnection = Database.openDatabase();
        Statement statement = dbConnection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        MostUsedName mostUsedName = new MostUsedName();
        if(resultSet.next()) {
            String name = resultSet.getString("first_name");
            int count = resultSet.getInt("count");
            mostUsedName.name = name;
            mostUsedName.count = count;
            return mostUsedName;
        }
        return mostUsedName;
    }

    /**
     * @param birthDate User birthdate.
     * Calculate current age
     * @author Mert Bölükbaşı
     */
    private int calculateAge(Date birthDate) {
        if (birthDate == null) {
            return 0;
        }

        LocalDate birth = birthDate.toLocalDate();

        LocalDate today = LocalDate.now();

        if (birth != null) {
            return Period.between(birth, today).getYears();
        } else {
            return 0;
        }
    }

    /**
     * Shows contact statistics menu.
     * @author Mert Bölükbaşı
     */
    private void showContactStats() {
        String titleStat = "Contacts Statistical Info, " + this.getName() + " " + this.getSurname() + ", " + this.getRole();
        DrawMenu.clearConsole();
        while(true) {
            try {
                String input;
                do {
                    String[] statContent = {
                            "",
                            DrawMenu.BLUE_BOLD + "Total Contact: " + DrawMenu.RESET + this.totalContact() + DrawMenu.LIGHT_GRAY + DrawMenu.RESET,
                            DrawMenu.BLUE_BOLD + "Average Age: " + DrawMenu.RESET + this.averageAge() + DrawMenu.LIGHT_GRAY + DrawMenu.RESET,
                            DrawMenu.BLUE_BOLD + "[1] Oldest Person" + DrawMenu.RESET,
                            DrawMenu.BLUE_BOLD + "[2] Youngest Person" + DrawMenu.RESET,
                            DrawMenu.BLUE_BOLD + "[3] Most frequently used name" + DrawMenu.RESET,
                            ""
                    };
                    DrawMenu.printBoxed(titleStat, statContent);
                    System.out.println();
                    DrawMenu.printCenter("Your choice: ");
                    input = Input.getStringInput();
                    if(input.equals("exit")) {
                        DrawMenu.clearConsole();
                        break;
                    }
                    if(Integer.parseInt(input) != 1 && Integer.parseInt(input) != 2 && Integer.parseInt(input) != 3) {
                        DrawMenu.clearConsole();
                        System.out.println(DrawMenu.RED_BOLD + "Invalid input. Please enter a number between 1 and 3." + DrawMenu.RESET);
                    }
                } while(Integer.parseInt(input) != 1 && Integer.parseInt(input) != 2 && Integer.parseInt(input) != 3);

                switch(Integer.parseInt(input)) {
                    case 1:
                        DrawMenu.clearConsole();
                        Contact oldest = this.oldestPerson();
                        String[] oldestContent = {
                                "",
                                DrawMenu.BLUE_BOLD + "Oldest Age: " + calculateAge(oldest.getBirthDate()) + DrawMenu.LIGHT_GRAY + DrawMenu.RESET,
                                "",
                                DrawMenu.BLUE_BOLD + "ID: " + DrawMenu.LIGHT_GRAY + oldest.getContactId() + DrawMenu.RESET,
                                DrawMenu.BLUE_BOLD + "Nickname: " + DrawMenu.LIGHT_GRAY + oldest.getNickname() + DrawMenu.RESET,
                                DrawMenu.BLUE_BOLD + "Phone: " + DrawMenu.LIGHT_GRAY + oldest.getPhonePrimary() + DrawMenu.RESET,
                                DrawMenu.BLUE_BOLD + "Email: " + DrawMenu.LIGHT_GRAY + oldest.getEmail() + DrawMenu.RESET,
                                DrawMenu.BLUE_BOLD + "Birth Date: " + DrawMenu.LIGHT_GRAY + oldest.getBirthDate() + DrawMenu.RESET,
                                DrawMenu.BLUE_BOLD + "Created At: " + DrawMenu.LIGHT_GRAY + oldest.getCreatedAt() + DrawMenu.RESET,
                                DrawMenu.BLUE_BOLD + "Updated At: " + DrawMenu.LIGHT_GRAY + oldest.getUpdatedAt() + DrawMenu.RESET,
                                ""
                        };
                        DrawMenu.printBoxed(titleStat, oldestContent);
                        break;

                    case 2:
                        DrawMenu.clearConsole();
                        Contact youngest = this.youngestPerson();
                        String[] youngestContent = {
                                "",
                                DrawMenu.BLUE_BOLD + "Oldest Age: " + calculateAge(youngest.getBirthDate()) + DrawMenu.LIGHT_GRAY + DrawMenu.RESET,
                                "",
                                DrawMenu.BLUE_BOLD + "ID: " + DrawMenu.LIGHT_GRAY + youngest.getContactId() + DrawMenu.RESET,
                                DrawMenu.BLUE_BOLD + "Nickname: " + DrawMenu.LIGHT_GRAY + youngest.getNickname() + DrawMenu.RESET,
                                DrawMenu.BLUE_BOLD + "Phone: " + DrawMenu.LIGHT_GRAY + youngest.getPhonePrimary() + DrawMenu.RESET,
                                DrawMenu.BLUE_BOLD + "Email: " + DrawMenu.LIGHT_GRAY + youngest.getEmail() + DrawMenu.RESET,
                                DrawMenu.BLUE_BOLD + "Birth Date: " + DrawMenu.LIGHT_GRAY + youngest.getBirthDate() + DrawMenu.RESET,
                                DrawMenu.BLUE_BOLD + "Created At: " + DrawMenu.LIGHT_GRAY + youngest.getCreatedAt() + DrawMenu.RESET,
                                DrawMenu.BLUE_BOLD + "Updated At: " + DrawMenu.LIGHT_GRAY + youngest.getUpdatedAt() + DrawMenu.RESET,
                                ""
                        };
                        DrawMenu.printBoxed(titleStat, youngestContent);
                        break;

                    case 3:
                        DrawMenu.clearConsole();
                        MostUsedName mostUsed = this.mostUsedName();
                        String[] mostUsedContent = {
                                "",
                                DrawMenu.BLUE_BOLD + "Most frequently used name: " + DrawMenu.LIGHT_GRAY + mostUsed.name + DrawMenu.RESET,
                                DrawMenu.BLUE_BOLD + "Count: " + DrawMenu.LIGHT_GRAY + mostUsed.count + DrawMenu.RESET,
                                ""
                        };
                        DrawMenu.printBoxed(titleStat, mostUsedContent);
                        break;
                }

                System.out.println();
                DrawMenu.printCenter("Press enter to return to menu: ");
                Input.getStringInput();
                DrawMenu.clearConsole();
                break;
            } catch (SQLException e) {
                DrawMenu.clearConsole();
                System.out.println(DrawMenu.RED_BOLD + "Database Error: Contacts could not fetch. Please try again." + DrawMenu.RESET);
            } catch (NumberFormatException e) {
                DrawMenu.clearConsole();
                System.out.println(DrawMenu.RED_BOLD + "Invalid input. Please try again." + DrawMenu.RESET);
            }
        }
        showUserMenu();
    }

    /**
     * @param user User object
     * Prints user list menu and user fields.
     * @author Mert Bölükbaşı
     */
    public void showUserList(User user) {
        String title = user.getName() + " " + user.getSurname();
        String[] contents = {
                DrawMenu.CYAN_BOLD + "id: " + user.getUserId() + DrawMenu.RESET,
                DrawMenu.CYAN_BOLD + "username: " + user.getUsername() + DrawMenu.RESET,
                DrawMenu.CYAN_BOLD + "name: " + user.getName() + DrawMenu.RESET,
                DrawMenu.CYAN_BOLD + "surname: " + user.getSurname() + DrawMenu.RESET,
                DrawMenu.CYAN_BOLD + "role: " + user.getRole() + DrawMenu.RESET,
                DrawMenu.CYAN_BOLD + "created_at: " + user.getCreated_at() + DrawMenu.RESET,
                DrawMenu.CYAN_BOLD + "updated_at: " + user.getUpdated_at() + DrawMenu.RESET
        };
        DrawMenu.printBoxed(title, contents);
    }

    /**
     * Shows manager menu.
     * @author Mert Bölükbaşı
     */
    @Override
    public void showUserMenu() {
        String title = "Welcome " + this.getName() + " " + this.getSurname() + ", " + this.getRole();
        String[] contents = {
                "",
                DrawMenu.YELLOW_BOLD + "[1] Change Password" + DrawMenu.RESET,
                DrawMenu.YELLOW_BOLD + "[2] Contacts Statistical Info" + DrawMenu.RESET,
                DrawMenu.YELLOW_BOLD + "[3] List All Users" + DrawMenu.RESET,
                DrawMenu.YELLOW_BOLD + "[4] Update Existing User" + DrawMenu.RESET,
                DrawMenu.YELLOW_BOLD + "[5] Add New User" + DrawMenu.RESET,
                DrawMenu.YELLOW_BOLD + "[6] Delete Existing User" + DrawMenu.RESET,
                DrawMenu.YELLOW_BOLD + "[7] Undo Last Operation" + DrawMenu.RESET,
                DrawMenu.YELLOW_BOLD + "[8] Logout" + DrawMenu.RESET,
                ""
        };
        DrawMenu.printBoxed(title, contents);
        System.out.println();
        DrawMenu.printCenter("Your choice: ");
        int input = 7;
        try {
            input = Input.getIntInput();
        } catch (Exception e) {
            DrawMenu.clearConsole();
            System.out.println(DrawMenu.RED_BOLD + "Invalid input. Please enter a number between 1 and 7." + DrawMenu.RESET);
            showUserMenu();
        }

        switch (input) {
            case 1:
                this.changePassword();
                DrawMenu.showLoginScreen();
                break;
            case 2:
                this.showContactStats();
                break;
            case 3:
                DrawMenu.clearConsole();
                try {
                    ArrayList<User> users = this.listAllUsers();
                    boolean loop = true;
                    int userCount = users.size();
                    int currentUser = 1;

                    while(loop) {
                        String inputStr;
                        label:
                        while(true) {
                            showUserList(users.get(currentUser - 1));
                            System.out.println();
                            DrawMenu.printCenter("< " + currentUser + "/" + userCount + " >");
                            System.out.println();
                            DrawMenu.printCenter("Previous user: A, Next user: D");
                            System.out.println();
                            System.out.println();
                            DrawMenu.printCenter("Your choice: ");
                            inputStr = Input.getStringInput().toLowerCase();
                            switch (inputStr) {
                                case "a":
                                    if (currentUser > 1 && currentUser <= userCount) {
                                        DrawMenu.clearConsole();
                                        currentUser--;
                                        break label;
                                    } else {
                                        DrawMenu.clearConsole();
                                        System.out.println(DrawMenu.RED_BOLD + "You reached minimum user. Please enter a valid input." + DrawMenu.RESET);
                                    }
                                    break;
                                case "d":
                                    if (currentUser < userCount) {
                                        DrawMenu.clearConsole();
                                        currentUser++;
                                        break label;
                                    } else {
                                        DrawMenu.clearConsole();
                                        System.out.println(DrawMenu.RED_BOLD + "You reached maximum user. Please enter a valid input." + DrawMenu.RESET);
                                    }
                                    break;
                                case "exit":
                                    DrawMenu.clearConsole();
                                    loop = false;
                                    break label;
                                default:
                                    DrawMenu.clearConsole();
                                    System.out.println(DrawMenu.RED_BOLD + "Invalid input. Please try again." + DrawMenu.RESET);
                                    break;
                            }
                        }
                    }
                    showUserMenu();
                } catch (SQLException e) {
                    DrawMenu.clearConsole();
                    System.out.println(DrawMenu.RED_BOLD + "Database Error: Users could not fetch. Please try again." + DrawMenu.RESET);
                    showUserMenu();
                }
            case 4:
                DrawMenu.clearConsole();
                while(true) {
                    try {
                        String updateTitle = "Update User, " + this.getName() + " " + this.getSurname() + ", " + this.getRole();
                        String[] updateIdContents = {
                                "",
                                DrawMenu.BLUE_BOLD + "User ID: " + DrawMenu.RESET + "Enter user id" + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + DrawMenu.RESET,
                                ""
                        };
                        DrawMenu.printBoxed(updateTitle, updateIdContents);
                        System.out.println();
                        DrawMenu.printCenter("User ID: ");
                        String user_id_str = Input.getStringInput();
                        if(user_id_str.equals("exit")) {
                            DrawMenu.clearConsole();
                            this.showUserMenu();
                        }
                        int user_id = Integer.parseInt(user_id_str);
                        if(this.getUserById(user_id) == null) {
                            throw new SQLException();
                        }
                        try {
                            User user = getUserById(user_id);
                            User newUser = updateUserMenu(user);
                            if(newUser == null) {
                                DrawMenu.clearConsole();
                                break;
                            }
                            try {
                                this.updateUser(user_id, newUser, user);
                                DrawMenu.clearConsole();
                                System.out.println(DrawMenu.GREEN_BOLD + "User updated successfully!" + DrawMenu.RESET);
                                break;
                            } catch (SQLException e) {
                                DrawMenu.clearConsole();
                                System.out.println(DrawMenu.RED_BOLD + "Database Error: Users could not update. Please try again." + DrawMenu.RESET);
                            }
                        } catch (SQLException e) {
                            DrawMenu.clearConsole();
                            System.out.println(DrawMenu.RED_BOLD + "Database Error: User could not fetch. Please try again." + DrawMenu.RESET);
                        }
                    } catch (NumberFormatException e) {
                        DrawMenu.clearConsole();
                        System.out.println(DrawMenu.RED_BOLD + "Invalid input. Please try again." + DrawMenu.RESET);
                    } catch (SQLException e) {
                        DrawMenu.clearConsole();
                        System.out.println(DrawMenu.RED_BOLD + "Database Error: User not found. Please try again." + DrawMenu.RESET);
                    }
                }
                this.showUserMenu();
            break;
            case 5:
                DrawMenu.clearConsole();
                try {
                    String titleAdd = "Add User, " + this.getName() + " " + this.getSurname() + ", " + this.getRole();
                    String[] contentsAdd = {
                            "",
                            DrawMenu.BLUE_BOLD + "[1] Username: " + DrawMenu.RESET +  DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "Enter username" + DrawMenu.RESET,
                            DrawMenu.BLUE_BOLD + "[2] Password: " + DrawMenu.RESET +  DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "Enter password" + DrawMenu.RESET,
                            DrawMenu.BLUE_BOLD + "[3] Name: " + DrawMenu.RESET +  DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "Enter name" + DrawMenu.RESET,
                            DrawMenu.BLUE_BOLD + "[4] Surname: " + DrawMenu.RESET +  DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "Enter surname" + DrawMenu.RESET,
                            DrawMenu.BLUE_BOLD + "[5] Role: " + DrawMenu.RESET +  DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "Enter role" + DrawMenu.RESET,
                            ""
                    };
                    DrawMenu.printBoxed(titleAdd, contentsAdd);
                    System.out.println();
                    DrawMenu.printCenter("Username: ");
                    while(true) {
                        String username = Input.getStringInput();
                        if(this.checkExistingUser(username)) {
                            DrawMenu.clearConsole();
                            System.out.println(DrawMenu.RED_BOLD + "Username already exists. Please try again." + DrawMenu.RESET);
                            DrawMenu.printBoxed(titleAdd, contentsAdd);
                            System.out.println();
                            DrawMenu.printCenter("Username: ");
                        } else if(username.isEmpty()) {
                            DrawMenu.clearConsole();
                            System.out.println(DrawMenu.RED_BOLD + "Username cannot be empty. Please try again." + DrawMenu.RESET);
                            DrawMenu.printBoxed(titleAdd, contentsAdd);
                            System.out.println();
                            DrawMenu.printCenter("Username: ");
                        } else if(username.equals("exit")) {
                            DrawMenu.clearConsole();
                            break;
                        } else {
                            DrawMenu.clearConsole();
                            String[] contentsAdd5 = {
                                    "",
                                    DrawMenu.BLUE_BOLD + "[1] Username: " + DrawMenu.RESET +  DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + username + DrawMenu.RESET,
                                    DrawMenu.BLUE_BOLD + "[2] Password: " + DrawMenu.RESET +  DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "Enter password" + DrawMenu.RESET,
                                    DrawMenu.BLUE_BOLD + "[3] Name: " + DrawMenu.RESET +  DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "Enter name" + DrawMenu.RESET,
                                    DrawMenu.BLUE_BOLD + "[4] Surname: " + DrawMenu.RESET +  DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "Enter surname" + DrawMenu.RESET,
                                    DrawMenu.BLUE_BOLD + "[5] Role: " + DrawMenu.RESET +  DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "Enter role" + DrawMenu.RESET,
                                    ""
                            };
                            DrawMenu.printBoxed(titleAdd, contentsAdd5);
                            System.out.println();
                            DrawMenu.printCenter("Password: ");
                            String password;
                            do {
                                password = Input.getStringInput();
                                if(password.length() < 2 || password.length() > 32) {
                                    DrawMenu.clearConsole();
                                    System.out.println(DrawMenu.RED_BOLD + "Password must be between 2 and 32 characters." + DrawMenu.RESET);
                                    DrawMenu.printBoxed(titleAdd, contentsAdd5);
                                    System.out.println();
                                    DrawMenu.printCenter("Password: ");
                                } else if(password.equals("exit")) {
                                    DrawMenu.clearConsole();
                                    break;
                                }
                            } while(password.length() < 2 || password.length() > 32);
                            DrawMenu.clearConsole();
                            String[] contentsAdd2 = {
                                    "",
                                    DrawMenu.BLUE_BOLD + "[1] Username: " + DrawMenu.RESET +  DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + username + DrawMenu.RESET,
                                    DrawMenu.BLUE_BOLD + "[2] Password: " + DrawMenu.RESET +  DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + password + DrawMenu.RESET,
                                    DrawMenu.BLUE_BOLD + "[3] Name: " + DrawMenu.RESET +  DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "Enter name" + DrawMenu.RESET,
                                    DrawMenu.BLUE_BOLD + "[4] Surname: " + DrawMenu.RESET +  DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "Enter surname" + DrawMenu.RESET,
                                    DrawMenu.BLUE_BOLD + "[5] Role: " + DrawMenu.RESET +  DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "Enter role" + DrawMenu.RESET,
                                    ""
                            };
                            DrawMenu.printBoxed(titleAdd, contentsAdd2);
                            System.out.println();
                            String name;
                            while (true) {
                                DrawMenu.printCenter("Name: ");
                                name = Input.getStringInput();

                                if (name.matches("^[a-zA-ZçÇğĞıİöÖşŞüÜ\\s]+$")) {
                                    break;
                                } else if(name.isEmpty()) {
                                    DrawMenu.clearConsole();
                                    System.out.println(DrawMenu.RED_BOLD + "Name cannot be empty. Please try again." + DrawMenu.RESET);
                                    DrawMenu.printBoxed(titleAdd, contentsAdd2);
                                    System.out.println();
                                } else {
                                    DrawMenu.clearConsole();
                                    System.out.println(DrawMenu.RED_BOLD + "Invalid input. Please enter a valid name." + DrawMenu.RESET);
                                    DrawMenu.printBoxed(titleAdd, contentsAdd2);
                                    System.out.println();
                                }
                            }
                            if(name.equals("exit")) {
                                DrawMenu.clearConsole();
                                break;
                            }

                            DrawMenu.clearConsole();
                            String[] contentsAdd3 = {
                                    "",
                                    DrawMenu.BLUE_BOLD + "[1] Username: " + DrawMenu.RESET +  DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + username + DrawMenu.RESET,
                                    DrawMenu.BLUE_BOLD + "[2] Password: " + DrawMenu.RESET +  DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + password + DrawMenu.RESET,
                                    DrawMenu.BLUE_BOLD + "[3] Name: " + DrawMenu.RESET +  DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + name + DrawMenu.RESET,
                                    DrawMenu.BLUE_BOLD + "[4] Surname: " + DrawMenu.RESET +  DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "Enter surname" + DrawMenu.RESET,
                                    DrawMenu.BLUE_BOLD + "[5] Role: " + DrawMenu.RESET +  DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "Enter role" + DrawMenu.RESET,
                                    ""
                            };
                            DrawMenu.printBoxed(titleAdd, contentsAdd3);
                            System.out.println();
                            String surname;
                            while (true) {
                                DrawMenu.printCenter("Surname: ");
                                surname = Input.getStringInput();

                                if (surname.matches("^[a-zA-ZçÇğĞıİöÖşŞüÜ]+$")) {
                                    break;
                                } else if(surname.isEmpty()) {
                                    DrawMenu.clearConsole();
                                    System.out.println(DrawMenu.RED_BOLD + "Surname cannot be empty. Please try again." + DrawMenu.RESET);
                                    DrawMenu.printBoxed(titleAdd, contentsAdd3);
                                    System.out.println();
                                } else {
                                    DrawMenu.clearConsole();
                                    System.out.println(DrawMenu.RED_BOLD + "Invalid input. Please enter a valid surname." + DrawMenu.RESET);
                                    DrawMenu.printBoxed(titleAdd, contentsAdd3);
                                    System.out.println();
                                }
                            }
                            if(surname.equals("exit")) {
                                DrawMenu.clearConsole();
                                break;
                            }
                            DrawMenu.clearConsole();

                            int role;
                            while(true) {
                                try {
                                    String[] contentsAdd4 = {
                                            "",
                                            DrawMenu.CYAN_BOLD + "[1] Manager" + DrawMenu.RESET,
                                            DrawMenu.CYAN_BOLD + "[2] Senior" + DrawMenu.RESET,
                                            DrawMenu.CYAN_BOLD + "[3] Junior" + DrawMenu.RESET,
                                            DrawMenu.CYAN_BOLD + "[4] Tester" + DrawMenu.RESET,
                                            DrawMenu.RED_BOLD + "[5] Return to menu" + DrawMenu.RESET,
                                            ""
                                    };
                                    DrawMenu.printBoxed(title, contentsAdd4);
                                    System.out.println();
                                    DrawMenu.printCenter("Role: ");
                                    role = Input.getIntInput();
                                    if(role >= 1 && role <= 5) break;
                                    else {
                                        DrawMenu.clearConsole();
                                        System.out.println(DrawMenu.RED_BOLD + "Invalid input. Please try again." + DrawMenu.RESET);
                                    }
                                } catch (Exception e) {
                                    DrawMenu.clearConsole();
                                    System.out.println(DrawMenu.RED_BOLD + "Invalid input. Please try again." + DrawMenu.RESET);
                                }
                            }

                            String roleString = "Tester";
                            switch (role) {
                                case 1:
                                    roleString = "Manager";
                                    break;
                                case 2:
                                    roleString = "Senior Developer";
                                    break;
                                case 3:
                                    roleString = "Junior Developer";
                                    break;
                                case 4:
                                    roleString = "Tester";
                                    break;
                                case 5:
                                    break;

                            }
                            if(role == 5) break;
                            User user = new User() {
                                @Override
                                public void showUserMenu() {

                                }
                            };
                            user.setUsername(username);
                            user.setPassword_hash(password);
                            user.setName(name);
                            user.setSurname(surname);
                            user.setRole(roleString);
                            this.addUser(user);
                            DrawMenu.clearConsole();
                            System.out.println(DrawMenu.GREEN_BOLD + "User added successfully!" + DrawMenu.RESET);
                            break;
                        }
                    }
                    this.showUserMenu();
                } catch (SQLException e) {
                    DrawMenu.clearConsole();
                    System.out.println(DrawMenu.RED_BOLD + "Database Error: Users could not fetch. Please try again." + DrawMenu.RESET);
                    showUserMenu();
                } catch (NumberFormatException e) {
                    DrawMenu.clearConsole();
                    System.out.println(DrawMenu.RED_BOLD + "Invalid input. Please try again." + DrawMenu.RESET);
                    showUserMenu();
                }
                break;
            case 6:
                DrawMenu.clearConsole();
                while(true) {
                    try {
                        String titleDelete = "Delete User, " + this.getName() + ", " + this.getSurname() + ", " + this.getRole();
                        String[] contentsDelete = {
                                "",
                                DrawMenu.YELLOW_BOLD + "User ID: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "Enter user id to delete" + DrawMenu.RESET,
                                ""
                        };
                        DrawMenu.printBoxed(titleDelete, contentsDelete);
                        System.out.println();
                        DrawMenu.printCenter("User ID: ");
                        int user_id = Input.getIntInput();
                        if(!this.checkExistingUser(user_id)){
                            DrawMenu.clearConsole();
                            System.out.println(DrawMenu.RED_BOLD + "User not found. Please try again." + DrawMenu.RESET);
                        } else {
                            String[] contentsDelete2 = {
                                    "",
                                    DrawMenu.YELLOW_BOLD + "User ID: " + DrawMenu.RESET + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + user_id+ DrawMenu.RESET,
                                    ""
                            };
                            DrawMenu.clearConsole();
                            DrawMenu.printBoxed(titleDelete, contentsDelete2);
                            System.out.println();
                            DrawMenu.printCenter(DrawMenu.PURPLE_BOLD +  "Are you sure? (y/n)" + DrawMenu.RESET);
                            System.out.println();
                            DrawMenu.printCenter("Your choice: ");
                            String confirmInput = Input.getStringInput();
                            if(confirmInput.equalsIgnoreCase("y")) {
                                DrawMenu.clearConsole();
                                this.deleteUser(user_id);
                                System.out.println(DrawMenu.GREEN_BOLD + "User deleted successfully!" + DrawMenu.RESET);
                                break;
                            } else if(confirmInput.equalsIgnoreCase("n")) {
                                DrawMenu.clearConsole();
                            } else {
                                DrawMenu.clearConsole();
                                System.out.println(DrawMenu.RED_BOLD + "Invalid input. Please try again." + DrawMenu.RESET);
                            }
                        }
                    } catch (NumberFormatException e) {
                        DrawMenu.clearConsole();
                        System.out.println(DrawMenu.RED_BOLD + "Invalid input. Please try again." + DrawMenu.RESET);
                    } catch (Exception e) {
                        DrawMenu.clearConsole();
                        System.out.println(DrawMenu.RED_BOLD + "Database Error: Users could not fetch. Please try again." + DrawMenu.RESET);
                    }
                }
                this.showUserMenu();
                break;
            case 7:
                if(Undo.getUndoStack().isEmpty()) {
                    DrawMenu.clearConsole();
                    System.out.println(DrawMenu.RED_BOLD + "No undo available" + DrawMenu.RESET);
                } else {
                    UndoData undoData = Undo.getUndoStack().peek();
                    Undo.getUndoStack().clear();
                    try {
                        switch(undoData.getUndoAction()) {
                            case ADD_USER:
                                Undo.addUserUndo(undoData.getOldUser().getUsername());
                                break;
                            case DELETE_USER:
                                Undo.deleteUserUndo(undoData.getOldUser());
                                break;
                            case UPDATE_USER:
                                Undo.updateUserUndo(undoData.getOldUser().getUserId(), undoData.getOldUser());
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
            default:
                this.logout();
                DrawMenu.showLoginScreen();
                break;
        }
    }
}
