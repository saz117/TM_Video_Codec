package Codec;

import FileManagement.JPEG_Handler;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;

/**
 *
 * @author Adrià Valls, Sebastian Andrade 2022
 */
public class Decoder {

    public Decoder(){}

    public void decode(String inPath, String outPath, int nTiles, int GOP){
        File dir = new File(outPath); //we create the directory in which we will save all the images
        dir.mkdir();

        JPEG_Handler jpeg_handler = new JPEG_Handler();
        File inputFile = new File(inPath);
        File[] file_allPaths = inputFile.listFiles();

        File matchFile = file_allPaths[file_allPaths.length-1];
        System.out.println(matchFile.getName());

        try {
            DataInputStream dis = new DataInputStream(new FileInputStream(matchFile));

            int GOPcount = GOP;
            int baseNum = 0;
            int destNum = 0;

            //Frame Num
            int numberOfFrames = dis.readInt();
            System.out.println("Nº Frames: " + numberOfFrames);

            File baseImgFile = file_allPaths[0];
            File destImgFile;
            BufferedImage baseImg;
            BufferedImage destImg;

            baseImg = jpeg_handler.readImage(baseImgFile.getAbsolutePath());

            int nMatches;

            while (destNum + 1 < file_allPaths.length-1) {

                if (GOPcount == GOP) {
                    GOPcount = 0;
                    //IMG BASE = NEXT IMAGE
                    baseImgFile = file_allPaths[destNum];
                    baseImg = jpeg_handler.readImage(baseImgFile.getAbsolutePath());
                    //System.out.println(file_allPaths[destNum]);
                    nMatches = dis.readInt();
                    jpeg_handler.writeImage(baseImg, outPath + File.separator + destNum + ".jpeg");

                }

                else {
                    GOPcount += 1;
                    //baseImg = jpeg_handler.readImage(file_allPaths[destNum-1].getAbsolutePath()); //FIX after presentation
                    //IMG Dest = NEXT IMAGE
                    destImgFile = file_allPaths[destNum];
                    destImg = jpeg_handler.readImage(destImgFile.getAbsolutePath());
                    //COMPARACION
                    //System.out.println(destImgFile.getName());
                    nMatches = dis.readInt();
                    for(int matchNum=0; matchNum<nMatches; matchNum++){
                        int cellNum = dis.readInt();
                        int xCoord = dis.readInt();
                        int yCoord = dis.readInt();
                        destImg = patchMatches(baseImg, destImg, cellNum, xCoord, yCoord, nTiles);
                    }
                    jpeg_handler.writeImage(destImg, outPath + File.separator + destNum + ".jpeg");
                    baseImg = destImg;
                }
                destNum += 1;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedImage patchMatches(BufferedImage baseImg, BufferedImage destImg, int cellNumber, int xCoord, int yCoord, int nTiles) {
        BufferedImage newDest = destImg;
        int x = 0;
        int y = 0;

        for (int i = 0; i < cellNumber; i++) {
            if (x + nTiles >= destImg.getWidth()) {
                x = 0;
                y += nTiles;
            } else {
                x += nTiles;
            }
        }

        for (int i = 0; i < nTiles; i++) {
            for (int j = 0; j < nTiles; j++) {
                if(x+j<=newDest.getWidth() && y+j<=newDest.getHeight()){
                    newDest.setRGB(x + j, y + i, baseImg.getRGB(xCoord+j, yCoord+i));
                }
            }
        }

        return newDest;
    }
}
