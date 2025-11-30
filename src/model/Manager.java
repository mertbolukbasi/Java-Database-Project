package model;

import database.Database;
import utils.DrawMenu;
import utils.Input;
import utils.PasswordHash;

import java.sql.*;
import java.util.ArrayList;

public class Manager extends User {

    public ArrayList<Integer> getAllUserId() throws SQLException {
        ArrayList<Integer> idList = new ArrayList<>();
        String query = "SELECT user_id FROM users";

        Connection dbConnection = Database.openDatabase();
        Statement statement = dbConnection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        while(resultSet.next()) {
            idList.add(resultSet.getInt("user_id"));
        }
        dbConnection.close();
        return idList;
    }

    public User getUserById(int user_id) throws SQLException {
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
        }
        dbConnection.close();
        return user;
    }

    public ArrayList<User> listAllUsers() throws SQLException {
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

    public void updateUser(String user_id, User user) throws SQLException {
        StringBuilder query = new StringBuilder("UPDATE users SET ");
        ArrayList<Object> parameters = new ArrayList<>();

        boolean isFirst = true;

        if (user.getUsername() != null && !user.getUsername().isEmpty()) {
            query.append("username = ").append(user.getUsername());
            parameters.add(user.getUsername());
            isFirst = false;
        }

        if (user.getPassword_hash() != null && !user.getPassword_hash().isEmpty()) {
            if (!isFirst) query.append(", ");
            query.append("password_hash = ").append(user.getPassword_hash());
            parameters.add(user.getPassword_hash());
            isFirst = false;
        }

        if (user.getName() != null && !user.getName().isEmpty()) {
            if (!isFirst) query.append(", ");
            query.append("name = ").append(user.getName());
            parameters.add(user.getName());
            isFirst = false;
        }

        if (user.getSurname() != null && !user.getSurname().isEmpty()) {
            if (!isFirst) query.append(", ").append(user.getSurname());
            query.append("surname = ");
            parameters.add(user.getSurname());
            isFirst = false;
        }

        if (user.getRole() != null && !user.getRole().isEmpty()) {
            if (!isFirst) query.append(", ").append(user.getRole());
            query.append("role = ");
            parameters.add(user.getRole());
        }
        query.append(", updated_at = NOW()");
        query.append(" WHERE user_id = ").append(user_id);

        Connection dbConnection = Database.openDatabase();
        PreparedStatement preparedStatement = dbConnection.prepareStatement(query.toString());
        for (int i = 0; i < parameters.size(); i++) {
            preparedStatement.setObject(i + 1, parameters.get(i));
        }
        dbConnection.commit();
        dbConnection.close();

    }

    public void addUser() {
        //TODO
    }

    public void deleteUser() {
        //TODO
    }

    public void showContactStats() {
        //TODO
    }

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
                DrawMenu.YELLOW_BOLD + "[7] Logout" + DrawMenu.RESET,
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
            System.out.println("Invalid input. Please enter a number between 1 and 7.");
            showUserMenu();
        }

        switch (input) {
            case 1:
                this.changePassword();
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
                            DrawMenu.showUserList(users.get(currentUser - 1));
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
                boolean loop = true;
                while(loop) {
                    try {
                        String[] contentsUpdate = {
                                "",
                                DrawMenu.YELLOW_BOLD + "User_Id: " + DrawMenu.RESET + DrawMenu.LIGHT_GRAY + "Enter user id to update" + DrawMenu.RESET,
                                ""
                        };
                        DrawMenu.printBoxed("Update User, " + this.getName() + " " + this.getSurname() + ", " + this.getRole(), contentsUpdate);
                        System.out.println();
                        DrawMenu.printCenter("User Id: ");
                        String user_id;
                            user_id = Input.getStringInput().toLowerCase();
                            DrawMenu.clearConsole();
                            try {
                                ArrayList<Integer> userIds = getAllUserId();
                                if(userIds.contains(Integer.parseInt(user_id))) {
                                    User user = this.getUserById(Integer.parseInt(user_id));
                                    String[] userContents = {
                                            "",
                                            DrawMenu.YELLOW_BOLD + "[1] Username: " + user.getUsername() + DrawMenu.RESET,
                                            DrawMenu.YELLOW_BOLD + "[2] Name: " + user.getName() + DrawMenu.RESET,
                                            DrawMenu.YELLOW_BOLD + "[3] Surname: " + user.getSurname() + DrawMenu.RESET,
                                            DrawMenu.YELLOW_BOLD + "[4] Role: " + user.getRole() + DrawMenu.RESET,
                                            ""
                                    };
                                    DrawMenu.printBoxed(title, userContents);
                                    System.out.println();
                                    DrawMenu.printCenter("Select any field to update: ");
                                    int choice = Input.getIntInput();
                                    switch (choice) {
                                        case 1:
                                            while(true) {
                                                String[] usernameContents = {
                                                        "",
                                                        DrawMenu.YELLOW_BOLD + "Username: " + DrawMenu.ITALIC + DrawMenu.LIGHT_GRAY + "Enter new username",
                                                        ""
                                                };
                                                DrawMenu.printBoxed(title, usernameContents);
                                                System.out.println();
                                                DrawMenu.printCenter("Username: ");
                                                String username = Input.getStringInput();
                                                if(username.equals("exit")) {
                                                    DrawMenu.clearConsole();
                                                    showUserMenu();
                                                    break;
                                                } else if(username.isEmpty()) {
                                                    DrawMenu.clearConsole();
                                                    System.out.println(DrawMenu.RED_BOLD + "Invalid input. Please try again." + DrawMenu.RESET);
                                                } else {
                                                    User user1 = new User() {
                                                        @Override
                                                        public void showUserMenu() {

                                                        }
                                                    };
                                                    user1.setUsername(username);
                                                    this.updateUser(user_id, user1);
                                                    break;
                                                }
                                            }
                                            break;
                                        case 2:
                                        case 3:
                                        case 4:
                                        case 5:
                                        default:
                                            DrawMenu.clearConsole();
                                            System.out.println(DrawMenu.RED_BOLD + "Invalid input. Please try again." + DrawMenu.RESET);
                                            break;
                                    }
                                } else if(user_id.equals("exit")) {
                                    DrawMenu.clearConsole();
                                    loop = false;
                                } else {
                                    DrawMenu.clearConsole();
                                    System.out.println(DrawMenu.RED_BOLD + "Invalid input. Please try again." + DrawMenu.RESET);
                                }
                            } catch (NumberFormatException e) {
                                DrawMenu.clearConsole();
                                System.out.println(DrawMenu.RED_BOLD + "Invalid input. Please try again." + DrawMenu.RESET);
                            } catch (SQLException e) {
                                DrawMenu.clearConsole();
                                System.out.println(DrawMenu.RED_BOLD + "Database Error: Users could not fetch. Please try again." + DrawMenu.RESET);
                            }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                break;
            case 5:
                this.addUser();
                break;
            case 6:
                this.deleteUser();
                break;
            default:
                this.logout();
                break;
        }
    }
}
