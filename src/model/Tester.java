package model;
import utils.DrawMenu;
import utils.Input;

public class Tester extends User {

    @Override
    public void showUserMenu() {
        String title = "Welcome " + this.getName() + " " + this.getSurname() + ", " + this.getRole();
        String[] contents = {
                "",
                DrawMenu.YELLOW_BOLD + "[1] List All Contacts" + DrawMenu.RESET,
                DrawMenu.YELLOW_BOLD + "[2] Search Contact (Single Field)" + DrawMenu.RESET,
                DrawMenu.YELLOW_BOLD + "[3] Search Contact (Multiple Fields)" + DrawMenu.RESET,
                DrawMenu.YELLOW_BOLD + "[4] Change Password" + DrawMenu.RESET,
                DrawMenu.YELLOW_BOLD + "[5] Logout" + DrawMenu.RESET,
                ""
        };
        DrawMenu.printBoxed(title, contents);
        System.out.println();
        DrawMenu.printCenter("Your choice: ");
        int input = 5;
        try {
            input = Input.getIntInput();
        } catch (Exception e) {
            DrawMenu.clearConsole();
            System.out.println("Invalid input. Please enter a number between 1 and 5.");
            showUserMenu();
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
                this.changePassword();
                break;
            case 5:
                this.logout();
                return;
            default:
                DrawMenu.clearConsole();
                System.out.println("Invalid input. Please enter a number between 1 and 5.");
                break;
        }
    }

    public void listAllContacts() {
        //TODO
    }

    public void searchContact() {
        //TODO
    }

    public void searchBySelectedFields() {
        //TODO
    }
}
