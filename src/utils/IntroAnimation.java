package utils;

import java.util.Arrays;

public class IntroAnimation {
    static final int WIDTH = 80;
    static final int HEIGHT = 40;

    static final String RESET = "\u001B[0m";
    static final String CYAN = "\u001B[36m";
    static final String PURPLE = "\u001B[35m";
    static final String WHITE = "\u001B[37m";
    static final String GREEN = "\u001B[32m";

    static char[] screen = new char[WIDTH * HEIGHT];
    static double[] zBuffer = new double[WIDTH * HEIGHT];
    static int[] colorBuffer = new int[WIDTH * HEIGHT];

    static char[] shades = ".,-~:;=!*#$@".toCharArray();

    public static void drawAnimation() throws InterruptedException {
        double offset = 0;

        for (int frame = 0; frame < 330; frame++) {

            Arrays.fill(screen, ' ');
            Arrays.fill(zBuffer, 0);
            Arrays.fill(colorBuffer, 0);

            for (double i = 0; i < 40; i += 0.3) {
                double angle = offset + (i * 0.3);

                calculateAndDraw(i, Math.sin(angle), Math.cos(angle), 1);
                calculateAndDraw(i, Math.sin(angle + Math.PI), Math.cos(angle + Math.PI), 2);

                if (((int)i) % 2 == 0) {
                    for (double t = 0; t < 1; t += 0.1) {
                        double midX = Math.sin(angle) * (1 - t) + Math.sin(angle + Math.PI) * t;
                        double midZ = Math.cos(angle) * (1 - t) + Math.cos(angle + Math.PI) * t;
                        calculateAndDraw(i, midX, midZ, 3);
                    }
                }
            }

            StringBuilder buffer = new StringBuilder();
            buffer.append("\033[H");

            int lastColor = 0;

            for (int k = 0; k < WIDTH * HEIGHT; k++) {
                if (k % WIDTH == WIDTH - 1) {
                    buffer.append("\n");
                } else {
                    int currentColor = colorBuffer[k];
                    if (currentColor != lastColor) {
                        switch (currentColor) {
                            case 1: buffer.append(CYAN); break;
                            case 2: buffer.append(PURPLE); break;
                            case 3: buffer.append(WHITE); break;
                            default: buffer.append(RESET); break;
                        }
                        lastColor = currentColor;
                    }
                    buffer.append(screen[k]);
                }
            }
            buffer.append(RESET);
            System.out.print(buffer.toString());

            offset -= 0.05;
            Thread.sleep(30);
        }

        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void calculateAndDraw(double yPos, double xRaw, double zRaw, int colorCode) {
        double radius = 15;
        double x = xRaw * radius;
        double z = zRaw * radius + 30;
        double y = yPos * 1.5;

        double ooz = 1 / z;
        int xp = (int) ((double) WIDTH / 2 + (x * ooz * 400) / 10);
        int yp = (int) (5 + y);

        if (xp >= 0 && xp < WIDTH && yp >= 0 && yp < HEIGHT) {
            int idx = xp + yp * WIDTH;
            if (ooz > zBuffer[idx]) {
                zBuffer[idx] = ooz;

                int luminance = (int) ((zRaw + 1) / 2 * (shades.length - 1));
                if (luminance < 0) luminance = 0;
                if (luminance >= shades.length) luminance = shades.length - 1;

                screen[idx] = shades[luminance];
                colorBuffer[idx] = colorCode;
            }
        }
    }
}