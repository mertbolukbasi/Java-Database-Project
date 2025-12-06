import model.Manager;
import utils.DrawMenu;
import utils.IntroAnimation;
import utils.OutroAnimation;

public class Main {
    public static void main(String[] args) {
        DrawMenu.clearConsole();
        try {
            IntroAnimation.drawAnimation();
            DrawMenu.showLoginScreen();
            OutroAnimation.drawAnimation();
        } catch (Exception e) {
            System.out.println("Runtime Error.");
        }
        DrawMenu.clearConsole();
    }
}