package utils;

public class OutroAnimation {
    private static final int SCREEN_HEIGHT = 20;
    private static final int SCREEN_WIDTH  = 80;

    private static final int[][] STARS = {
            {4, 15}, {4, 30}, {4, 45}, {4, 60},
            {6, 12}, {6, 24}, {6, 36}, {6, 48}, {6, 60},
            {8, 18}, {8, 32}, {8, 50}, {8, 66},
            {10, 14}, {10, 28}, {10, 42}, {10, 56},
            {12, 20}, {12, 34}, {12, 48}, {12, 62},
            {14, 16}, {14, 30}, {14, 44}, {14, 58}
    };

    private static final String[] MOON = {
            "      88888888",
            "    888888888888",
            "   88888888888888",
            "   88888888888888",
            "    888888888888",
            "      88888888"
    };
    private static final int MOON_COL = 6;

    private static final String SMALL_SHIP = "0=<88>=0<E";

    private static final String[] CLOSE_SHIP = {
            "        <>                         )           ",
            "   /-----\\   ---------------    /-\\   /-----\\ ",
            "  |====  |---_____________\\  | (0) |===  ===| ",
            "   \\-----/   ---------------    \\-/   \\-----/ ",
            "        <>                                     "
    };

    private static final String[] BIG_SHIP = {
            "                                                 __0__",
            "                                                 I__   \\",
            "                                   ----------------|    \\",
            "                   ----------------                      \\",
            "-------------------                                       \\",
            "===========================================================(",
            "-----------------------                                   /" ,
            "                       ----------------------------------/   ",
    };

    private static final String BULLET = "____";

    private static final String[][] EXPLOSION = {
            {
                    "    '   '    ",
                    "      *      ",
                    "    '   '    "
            },
            {
                    "  '  '  '  ' ",
                    "    *** *    ",
                    "  '  '  '  ' "
            },
            {
                    "      *      ",
                    "    '   '    ",
                    "             "
            }
    };

    private static final String[] ROBOT_R2 = {
            "      ___      ",
            "    /  () \\     ",
            "  _|_______|_   ",
            " | |  ===  | |  ",
            " |_|   0   |_|  ",
            "  ||   0   ||   ",
            "   |___ ___|   ",
            "  |~ \\___/ ~|   ",
            " /=\\  /=\\  /=\\  ",
            " [ ]  [ ]  [ ]"
    };

    private static final String[] ROBOT_C3_IDLE = {
            "            ___            ",
            "           /   \\           ",
            "          / ( ) \\          ",
            "           \\ = /           ",
            "         ___\\_/___         ",
            "        /   / \\   \\        ",
            "       /   /   \\   \\       ",
            "       \\   \\   /   /       ",
            "        \\   \\_/   /        ",
            "        #|   |   |#        ",
            "         |   |   |         ",
            "         |   |   |         ",
            "        [ ]  |  [ ]        ",
            "        /_\\     /_\\        ",
    };

    private static final String[] ROBOT_C3_TALK2 = {
            "            ___            ",
            "           /   \\           ",
            "          / ( ) \\      #   ",
            "           \\ = /      /   ",
            "         ___\\_/___   /   ",
            "        /   / \\   \\ /      ",
            "       /   /   \\   \\       ",
            "       \\   \\   /   /       ",
            "        \\   \\_/   /        ",
            "        #|   |   |        ",
            "         |   |   |         ",
            "         |   |   |         ",
            "        [ ]  |  [ ]        ",
            "        /_\\     /_\\        ",
    };

    private static final String[] ROBOT_C3_TALK3 = {
            "            ___           ",
            "           /   \\           ",
            "   #      / ( ) \\      #  ",
            "    \\      \\ = /      /   ",
            "     \\   ___\\_/___   /    ",
            "      \\ /   / \\   \\ /      ",
            "       /   /   \\   \\       ",
            "       \\   \\   /   /       ",
            "        \\   \\_/   /        ",
            "         |   |   |        ",
            "         |   |   |         ",
            "         |   |   |         ",
            "        [ ]  |  [ ]        ",
            "        /_\\     /_\\        ",
    };

    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static char[][] createEmptySpace() {
        char[][] screen = new char[SCREEN_HEIGHT][SCREEN_WIDTH];
        for (int r = 0; r < SCREEN_HEIGHT; r++) {
            for (int c = 0; c < SCREEN_WIDTH; c++) {
                screen[r][c] = ' ';
            }
        }
        for (int[] star : STARS) {
            int r = star[0];
            int c = star[1];
            if (r >= 0 && r < SCREEN_HEIGHT && c >= 0 && c < SCREEN_WIDTH) {
                screen[r][c] = '.';
            }
        }
        return screen;
    }

    private static void printScreen(char[][] screen) {
        clearScreen();
        for (int r = 0; r < SCREEN_HEIGHT; r++) {
            System.out.println(new String(screen[r]));
        }
    }

    private static void drawMoon(char[][] screen, int topRow) {
        for (int i = 0; i < MOON.length; i++) {
            int destRow = topRow + i;
            if (destRow < 0 || destRow >= SCREEN_HEIGHT) continue;
            String line = MOON[i];
            for (int j = 0; j < line.length(); j++) {
                int destCol = MOON_COL + j;
                if (destCol < 0 || destCol >= SCREEN_WIDTH) continue;
                char ch = line.charAt(j);
                if (ch != ' ') {
                    screen[destRow][destCol] = ch;
                }
            }
        }
    }

    private static void drawSmallShip(char[][] screen, int row, int col) {
        for (int j = 0; j < SMALL_SHIP.length(); j++) {
            int c = col + j;
            if (row >= 0 && row < SCREEN_HEIGHT && c >= 0 && c < SCREEN_WIDTH) {
                screen[row][c] = SMALL_SHIP.charAt(j);
            }
        }
    }

    private static void drawCloseShip(char[][] screen, int topRow, int col) {
        for (int i = 0; i < CLOSE_SHIP.length; i++) {
            String line = CLOSE_SHIP[i];
            int r = topRow + i;
            if (r < 0 || r >= SCREEN_HEIGHT) continue;
            for (int j = 0; j < line.length(); j++) {
                int c = col + j;
                if (c < 0 || c >= SCREEN_WIDTH) continue;
                char ch = line.charAt(j);
                if (ch != ' ') {
                    screen[r][c] = ch;
                }
            }
        }
    }

    private static void drawBigShip(char[][] screen, int topRow, int col) {
        for (int i = 0; i < BIG_SHIP.length; i++) {
            String line = BIG_SHIP[i];
            int r = topRow + i;
            if (r < 0 || r >= SCREEN_HEIGHT) continue;
            for (int j = 0; j < line.length(); j++) {
                int c = col + j;
                if (c < 0 || c >= SCREEN_WIDTH) continue;
                char ch = line.charAt(j);
                if (ch != ' ') {
                    screen[r][c] = ch;
                }
            }
        }
    }

    private static void drawBullet(char[][] screen, int row, int col) {
        for (int j = 0; j < BULLET.length(); j++) {
            int c = col + j;
            if (row >= 0 && row < SCREEN_HEIGHT && c >= 0 && c < SCREEN_WIDTH) {
                screen[row][c] = BULLET.charAt(j);
            }
        }
    }

    private static void drawExplosion(char[][] screen, int centerRow, int centerCol, int frame) {
        if (frame < 0 || frame >= EXPLOSION.length) return;

        String[] pattern = EXPLOSION[frame];
        int patternHeight = pattern.length;
        int patternWidth  = pattern[0].length();

        int topRow = centerRow - patternHeight / 2;
        int leftCol = centerCol - patternWidth / 2;

        for (int i = 0; i < patternHeight; i++) {
            int r = topRow + i;
            if (r < 0 || r >= SCREEN_HEIGHT) continue;
            String line = pattern[i];
            for (int j = 0; j < line.length(); j++) {
                int c = leftCol + j;
                if (c < 0 || c >= SCREEN_WIDTH) continue;
                char ch = line.charAt(j);
                if (ch != ' ') {
                    screen[r][c] = ch;
                }
            }
        }
    }

    private static void drawSprite(char[][] screen, String[] sprite, int topRow, int col) {
        for (int i = 0; i < sprite.length; i++) {
            int r = topRow + i;
            if (r < 0 || r >= SCREEN_HEIGHT) continue;
            String line = sprite[i];
            for (int j = 0; j < line.length(); j++) {
                int c = col + j;
                if (c < 0 || c >= SCREEN_WIDTH) continue;
                char ch = line.charAt(j);
                if (ch != ' ') {
                    screen[r][c] = ch;
                }
            }
        }
    }

    private static void drawGround(char[][] screen, int row) {
        if (row < 0 || row >= SCREEN_HEIGHT) return;
        for (int c = 0; c < SCREEN_WIDTH; c++) {
            screen[row][c] = '-';
        }
    }

    private static void drawText(char[][] screen, int row, int col, String text) {
        if (row < 0 || row >= SCREEN_HEIGHT) return;
        for (int i = 0; i < text.length(); i++) {
            int c = col + i;
            if (c < 0 || c >= SCREEN_WIDTH) continue;
            screen[row][c] = text.charAt(i);
        }
    }

    private static void playMoonScene() throws InterruptedException {
        int delayMs = 180;
        int startTop = SCREEN_HEIGHT - MOON.length + 2;
        int endTop   = -MOON.length;

        for (int top = startTop; top >= endTop; top--) {
            char[][] screen = createEmptySpace();
            drawMoon(screen, top);
            printScreen(screen);
            Thread.sleep(delayMs);
        }
    }

    private static void playBattleScene() throws InterruptedException {
        int delayMs = 70;

        int smallStartX = SCREEN_WIDTH + 5;
        int bigStartX   = SCREEN_WIDTH + 45;

        int smallY  = 11;
        int bigTopY = 6;

        int smallWidth = SMALL_SHIP.length();
        int bigWidth   = 0;
        for (String line : BIG_SHIP) {
            if (line.length() > bigWidth) bigWidth = line.length();
        }

        for (int step = 0; step < 200; step++) {
            int smallX = smallStartX - step;
            int bigX   = bigStartX   - step;

            char[][] screen = createEmptySpace();
            drawSmallShip(screen, smallY, smallX);
            drawBigShip(screen, bigTopY, bigX);

            printScreen(screen);
            Thread.sleep(delayMs);

            if (smallX + smallWidth < 0 && bigX + bigWidth < 0) {
                break;
            }
        }
    }

    private static void playHitScene() throws InterruptedException {
        int delayMs = 260;

        int shipTopRow = 9;
        int closeWidth = 0;
        for (String line : CLOSE_SHIP) {
            if (line.length() > closeWidth) closeWidth = line.length();
        }
        int shipCol = (SCREEN_WIDTH - closeWidth) / 2;

        int bulletRow = shipTopRow - 1;
        int shipRight = shipCol + closeWidth;

        int startBulletCol  = SCREEN_WIDTH - BULLET.length() - 2;
        int farBulletCol    = shipRight + 8;
        int nearBulletCol   = shipRight + 3;
        int impactBulletCol = shipRight - 3;

        int explosionCenterCol = shipCol + (int)(closeWidth * 0.7);

        for (int frame = 0; frame < 10; frame++) {
            char[][] screen = createEmptySpace();

            drawCloseShip(screen, shipTopRow, shipCol);

            if (frame == 1) {
                drawBullet(screen, bulletRow, startBulletCol);
            } else if (frame == 2) {
                drawBullet(screen, bulletRow, farBulletCol);
            } else if (frame == 3) {
                drawBullet(screen, bulletRow, nearBulletCol);
            } else if (frame == 4) {
                drawBullet(screen, bulletRow, impactBulletCol);
            }

            if (frame >= 4 && frame <= 8) {
                int[] expSeq = {0, 1, 1, 2, 2};
                int idx = frame - 4;
                if (idx >= 0 && idx < expSeq.length) {
                    int expFrame = expSeq[idx];
                    drawExplosion(screen, bulletRow - 1, explosionCenterCol, expFrame);
                }
            }

            printScreen(screen);
            Thread.sleep(delayMs);
        }
    }

    private static void playDroidsScene() throws InterruptedException {
        int groundRow = 18;

        int r2Height = ROBOT_R2.length;
        int c3Height = ROBOT_C3_IDLE.length;

        int r2Top = groundRow - r2Height;
        int c3Top = groundRow - c3Height;

        int r2Col = 6;
        int c3Col = 32;

        int pauseIntro = 1500;
        int pauseTalk  = 2500;

        char[][] screen;

        screen = createEmptySpace();
        drawGround(screen, groundRow);
        drawSprite(screen, ROBOT_R2, r2Top, r2Col);
        drawSprite(screen, ROBOT_C3_IDLE, c3Top, c3Col);
        printScreen(screen);
        Thread.sleep(pauseIntro);

        screen = createEmptySpace();
        drawGround(screen, groundRow);
        drawSprite(screen, ROBOT_R2, r2Top, r2Col);
        drawSprite(screen, ROBOT_C3_IDLE, c3Top, c3Col);
        drawText(screen, 2, 50, "  DID YOU HEAR THAT?  ");
        printScreen(screen);
        Thread.sleep(pauseTalk);

        screen = createEmptySpace();
        drawGround(screen, groundRow);
        drawSprite(screen, ROBOT_R2, r2Top, r2Col);
        drawSprite(screen, ROBOT_C3_TALK2, c3Top, c3Col);
        drawText(screen, 2, 46, " THEY'VE SHUT DOWN ");
        drawText(screen, 3, 46, "   THE MAIN REACTOR. ");
        printScreen(screen);
        Thread.sleep(pauseTalk);

        screen = createEmptySpace();
        drawGround(screen, groundRow);
        drawSprite(screen, ROBOT_R2, r2Top, r2Col);
        drawSprite(screen, ROBOT_C3_TALK3, c3Top, c3Col);
        drawText(screen, 2, 54, "  WE ARE FINISHED!  ");
        printScreen(screen);
        Thread.sleep(pauseTalk);
    }

    public static void drawAnimation() throws InterruptedException {
        playMoonScene();
        playBattleScene();
        playHitScene();
        playDroidsScene();
    }
}

