package FileManagement;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Adrià Valls, Sebastian Andrade 2022
 */
public class JPEG_Handler {

    //constructor
    public JPEG_Handler(){

    }
    /**
     * @param str, path to read the image file
     *
     * This method is used to read a single image file, and returns a BufferedImage
     */
    public BufferedImage readImage(String str ){
        BufferedImage img = null;
        try{
            File file = new File(str);
            img = ImageIO.read(file);

            //System.out.println("Image reading: correct");

        }catch (IOException error){
            System.out.println("Error reading image: " + error);
        }
        return img;
    }

    /**
     * @param img, the image you want to save.
     * @param outPath, the file path you want to save the image to
     *
     * This method is used to save an image in the path given, saving it as jpeg
     */
    public void writeImage(BufferedImage img, String outPath){
        try{
            File file = new File(outPath);
            ImageIO.write(img, "jpeg",file);
            //System.out.println("Image writing: correct");

        }catch (IOException error){
            System.out.println("Error writing image: " + error);
        }
    }

    /**
     * @param imgIN, the image you want to change.
     *
     * This method is used to change an image's format to jpeg. This was used during testing. It is no longer
     * necessary since in the encoder we already user the writeImage method.
     */
    public BufferedImage png_to_jpeg(BufferedImage imgIN ){
        BufferedImage Jpeg_Image = null;
        Jpeg_Image = new BufferedImage( imgIN.getWidth(), imgIN.getHeight(), BufferedImage.TYPE_INT_RGB);
        Jpeg_Image.createGraphics().drawImage( imgIN, 0, 0, Color.BLACK, null);
        return Jpeg_Image;
    }
}
