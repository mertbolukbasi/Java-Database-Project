package utils;

import database.Database;
import model.Contact;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Stack;

public class Undo {

    private static Stack<UndoData> undoStack = new Stack<>();

    /**
     * @param username Username
     * Delete user from database.
     * @author Mert Bölükbaşı
     */
    private static void deleteUser(String username) throws SQLException {
        String query = "DELETE FROM users WHERE username = ?";
        Connection connection = Database.openDatabase();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, username);
        preparedStatement.executeUpdate();
        connection.close();
    }

    /**
     * @param user User Object
     * Add user to database.
     * @author Mert Bölükbaşı
     */
    private static void addUser(User user) throws SQLException {
        String query = "INSERT INTO users (name, surname, username, password_hash, role) VALUES (?, ?, ?, ?, ?)";
        Connection dbConnection = Database.openDatabase();
        PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
        preparedStatement.setString(1, user.getName());
        preparedStatement.setString(2, user.getSurname());
        preparedStatement.setString(3, user.getUsername());
        preparedStatement.setString(4, user.getPassword_hash());
        preparedStatement.setString(5, user.getRole());
        preparedStatement.executeUpdate();
        dbConnection.close();
    }

    /**
     * @param user_id User id
     * @param user User object
     * Update user.
     * @author Mert Bölükbaşı
     */
    private static void updateUser(int user_id, User user) throws SQLException {
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
    }

    /**
     * @param nickname Nickname
     * Delete contact from database.
     * @author Mert Bölükbaşı
     */
    private static void deleteContact(String nickname) throws SQLException {
        String query = "DELETE FROM contacts WHERE nickname = ?";
        Connection connection = Database.openDatabase();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, nickname);
        preparedStatement.executeUpdate();
        connection.close();
    }

    /**
     * @param contact Contact object
     * Add contact to database.
     * @author Mert Bölükbaşı
     */
    private static void addContact(Contact contact) throws SQLException {
        String query = "INSERT INTO contacts (first_name, last_name, nickname, phone_primary, email, birth_date) VALUES (?, ?, ?, ?, ?, ?)";
        Connection dbConnection = Database.openDatabase();
        PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
        preparedStatement.setString(1, contact.getFirstName());
        preparedStatement.setString(2, contact.getLastName());
        preparedStatement.setString(3, contact.getNickname());
        preparedStatement.setString(4, contact.getPhonePrimary());
        preparedStatement.setString(5, contact.getEmail());
        preparedStatement.setDate(6, contact.getBirthDate());
        preparedStatement.executeUpdate();
        dbConnection.close();
    }

    /**
     * @param contact_id Contact id
     * @param contact Contact object
     * Update contact.
     * @author Mert Bölükbaşı
     */
    private static void updateContact(int contact_id, Contact contact) throws SQLException {
        String sql = "UPDATE contacts SET first_name = ?, last_name = ?, nickname = ?, phone_primary = ?, email = ?, birth_date = ? WHERE contact_id = ?";
        Connection dbConnection = Database.openDatabase();

        PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
        preparedStatement.setString(1, contact.getFirstName());
        preparedStatement.setString(2, contact.getLastName());
        preparedStatement.setString(3, contact.getNickname());
        preparedStatement.setString(4, contact.getPhonePrimary());
        preparedStatement.setString(5, contact.getEmail());
        preparedStatement.setDate(6, contact.getBirthDate());

        preparedStatement.executeUpdate();
        dbConnection.close();
    }

    public static void addUserUndo(String username) throws SQLException {
        Undo.deleteUser(username);
    }

    public static void deleteUserUndo(User user) throws SQLException {
        Undo.addUser(user);
    }

    public static void updateUserUndo(int user_id, User user) throws SQLException {
        Undo.updateUser(user_id, user);
    }

    public static void addContactUndo(String nickname) throws SQLException {
        Undo.deleteContact(nickname);
    }

    public static void deleteContactUndo(Contact contact) throws SQLException {
        Undo.addContact(contact);
    }

    public static void updateContactUndo(int contact_id, Contact contact) throws SQLException {
        Undo.updateContact(contact_id, contact);
    }

    public static Stack<UndoData> getUndoStack() {
        return undoStack;
    }
}
