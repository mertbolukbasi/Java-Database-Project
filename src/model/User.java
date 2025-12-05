package model;

import java.sql.Date;
import java.sql.SQLException;

public abstract class User {

    private int userId;
    private String username;
    private String password_hash;
    private String name;
    private String surname;
    private String role;
    private Date created_at;
    private Date updated_at;

    public abstract void showUserMenu() throws SQLException;

    /**
     * Handles the password change process interactively.
     * Checks the current password, enforces length rules, and saves the update to the database.
     * @author Oğuzhan Aydın
     */
    public void changePassword() {
        utils.DrawMenu.clearConsole();
        String[] header = {
                "",
                utils.DrawMenu.YELLOW_BOLD + "CHANGE PASSWORD OPERATION" + utils.DrawMenu.RESET,
                ""
        };
        utils.DrawMenu.printBoxed("User Settings", header);

        String currentDbHash = null;
        try {
            java.sql.Connection conn = database.Database.openDatabase();
            java.sql.PreparedStatement stmt = conn.prepareStatement("SELECT password_hash FROM users WHERE user_id = ?");
            stmt.setInt(1, this.userId);
            java.sql.ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                currentDbHash = rs.getString("password_hash");
                this.password_hash = currentDbHash;
            }
            conn.close();
        } catch (Exception e) {
            System.out.println(utils.DrawMenu.RED_BOLD + "Error fetching user data: " + e.getMessage() + utils.DrawMenu.RESET);
            utils.Input.getStringInput();
            utils.DrawMenu.clearConsole();
            return;
        }

        if (currentDbHash == null) {
            System.out.println(utils.DrawMenu.RED_BOLD + "User not found or database error!" + utils.DrawMenu.RESET);
            utils.Input.getStringInput();
            utils.DrawMenu.clearConsole();
            return;
        }

        System.out.println();
        utils.DrawMenu.printCenter("Enter Current Password: ");
        String currentInput = utils.Input.getStringInput();

        String hashedInput = utils.PasswordHash.hash(currentInput);

        if (!hashedInput.equals(currentDbHash)) {
            System.out.println("\n" + utils.DrawMenu.RED_BOLD + "Incorrect current password!" + utils.DrawMenu.RESET);
            System.out.println("Operation cancelled. Press Enter to return main menu...");
            utils.Input.getStringInput();
            utils.DrawMenu.clearConsole();
            return;
        }

        System.out.println();
        utils.DrawMenu.printCenter("Enter New Password: ");
        String newPass = utils.Input.getStringInput();

        if (newPass == null || newPass.length() < 2 || newPass.length() > 32) {
            System.out.println("\n" + utils.DrawMenu.RED_BOLD + "Password must be between 2 and 32 characters!" + utils.DrawMenu.RESET);
            System.out.println("Press Enter to return main menu...");
            utils.Input.getStringInput();
            utils.DrawMenu.clearConsole();
            return;
        }

        utils.DrawMenu.printCenter("Confirm New Password: ");
        String confirmPass = utils.Input.getStringInput();

        if (!newPass.equals(confirmPass)) {
            System.out.println("\n" + utils.DrawMenu.RED_BOLD + "Passwords do not match!" + utils.DrawMenu.RESET);
            System.out.println("Operation cancelled. Press Enter to return main menu...");
            utils.Input.getStringInput();
            utils.DrawMenu.clearConsole();
            return;
        }

        String hashPassword = utils.PasswordHash.hash(newPass);

        if (hashPassword.equals(currentDbHash)) {
            System.out.println("\n" + utils.DrawMenu.RED_BOLD + "New password cannot be the same as the old password!" + utils.DrawMenu.RESET);
            System.out.println("Press Enter to return main menu...");
            utils.Input.getStringInput();
            utils.DrawMenu.clearConsole();
            return;
        }

        String query = "UPDATE users SET password_hash = ? WHERE user_id = ?";

        try {
            java.sql.Connection conn = database.Database.openDatabase();
            java.sql.PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setString(1, hashPassword);
            stmt.setInt(2, this.userId);

            int affectedRows = stmt.executeUpdate();
            conn.close();

            if (affectedRows > 0) {
                this.password_hash = hashPassword;
                System.out.println("\n" + utils.DrawMenu.GREEN_BOLD + "Password changed successfully!" + utils.DrawMenu.RESET);
            } else {
                System.out.println("\n" + utils.DrawMenu.RED_BOLD + "Failed to update password in database." + utils.DrawMenu.RESET);
            }

        } catch (java.sql.SQLException e) {
            System.out.println("\n" + utils.DrawMenu.RED_BOLD + "Database Error: " + e.getMessage() + utils.DrawMenu.RESET);
        }

        System.out.println("Press Enter to return main menu...");
        utils.Input.getStringInput();
        utils.DrawMenu.clearConsole();
    }

    /**
     * Logs out the user with a see you message.
     * @author Oğuzhan Aydın
     */
    public void logout() {
        utils.DrawMenu.clearConsole();

        System.out.println("\n" + utils.DrawMenu.YELLOW_BOLD + "Logging out..." + utils.DrawMenu.RESET);
        System.out.println("See you soon, " + this.getName() + "!");

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {

        }
        utils.DrawMenu.clearConsole();
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword_hash() {
        return password_hash;
    }

    public void setPassword_hash(String password_hash) {
        this.password_hash = password_hash;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
