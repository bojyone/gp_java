package main;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class ImageResizer {

    private final File file;
    private final String dstFolder;

    public ImageResizer(File file, String dstFolder) {
        this.file = file;
        this.dstFolder = dstFolder;
    }

    public void resize() {
        try
        {
            BufferedImage image = ImageIO.read(file);

            int NEW_WIDTH = 36;
            int NEW_HEIGHT = 36;
            BufferedImage newImage = new BufferedImage(
                    NEW_WIDTH, NEW_HEIGHT, BufferedImage.TYPE_INT_RGB
            );

            int widthStep = image.getWidth() / NEW_WIDTH;
            int heightStep = image.getHeight() / NEW_HEIGHT;

            for (int x = 0; x < NEW_WIDTH; x++)
            {
                for (int y = 0; y < NEW_HEIGHT; y++) {
                    int rgb = image.getRGB(x * widthStep, y * heightStep);
                    newImage.setRGB(x, y, rgb);
                }
            }

            File newFile = new File(dstFolder + "/" + file.getName());
            ImageIO.write(newImage, "jpg", newFile);

        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}