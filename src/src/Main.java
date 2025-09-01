import java.awt.*;
import java.util.Scanner;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;


class Pair {
    double distance;
    String hexcode;

    Pair(double distance, String hexcode) {
        this.distance = distance;
        this.hexcode = hexcode;
    }
}

public class Main {
    public static void main(String[] args) throws IOException {
        String[] AvaliableHexCodes = {"010100","3d3d3c","797978","ababaa"
                ,"d2d3d3","fefeff","610019","a40e1f"
                ,"ed1c24","fb8073","e45d1b","ff7e26"
                ,"f6ab08","f8dd3a","fffbbd","9d8430"
                ,"c5ac30","e8d45f","4a6a3b","5b954a"
                ,"84c573","0fb868","13e67b","87fe5f"
                ,"0d806e","11aea7","13e1be","0e799e"
                ,"60f7f2","bbfaf2","28509e","4093e4"
                ,"7cc7fe","4d30b9","6b50f6","6b50f6"
                ,"4a4284","7a70c5","b5aff0","780d98"
                ,"ab38b8","e19ef9","ca007b","ec1e81"
                ,"f28ca9","9a5248","d08079","fbb7a4"
                ,"684634","94682b","dba463","7b6352"
                ,"9d846a","d7b494","d08151","f9b377"
                ,"ffc4a4","6c653f","948d6a","cdc59e"
                ,"333840","6d748c","b3b8d0"
        };

        Scanner scanner = new Scanner(System.in);
        System.out.println("Give me the file path: ");
        String file_path = scanner.nextLine();

        try {
            File input_file = new File(file_path);
            BufferedImage original = ImageIO.read(input_file);

            // Create a new ARGB image to ensure full 32-bit color
            BufferedImage image = new BufferedImage(
                    original.getWidth(), original.getHeight(),
                    BufferedImage.TYPE_INT_ARGB
            );

            // Copy pixels from original image to the new ARGB image
            for (int x = 0; x < original.getWidth(); x++) {
                for (int y = 0; y < original.getHeight(); y++) {
                    image.setRGB(x, y, original.getRGB(x, y));
                }
            }

            int width = image.getWidth();
            int height = image.getHeight();

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    int color = image.getRGB(i, j);

                    // Extract RGBA properly
                    int alpha = (color >> 24) & 0xFF;
                    int red   = (color >> 16) & 0xFF;
                    int green = (color >> 8) & 0xFF;
                    int blue  = color & 0xFF;

                    // Find the closest palette color
                    Pair closest = null;
                    double minDistance = Double.MAX_VALUE;

                    for (String hex : AvaliableHexCodes) {
                        double distance = ColorCompersion.PercentageCalculator(
                                new int[]{red, green, blue}, hex
                        );
                        if (distance < minDistance) {
                            minDistance = distance;
                            closest = new Pair(distance, hex);
                        }
                    }

                    // Convert hex to RGB
                    int[] newRGB = ColorCompersion.HexToRgb(closest.hexcode);

                    // Create new Color with alpha preserved
                    Color newColor = new Color(newRGB[0], newRGB[1], newRGB[2], alpha);
                    image.setRGB(i, j, newColor.getRGB());
                }
            }

            System.out.println("What would you like the file to be named?: ");
            String file_name = scanner.nextLine();


            ImageIO.write(image, "png", new File("./output/" + file_name + ".png"));

        } catch (IOException e) {
            System.out.println("A system beszart valamiért: " + e);
        }
    }
}