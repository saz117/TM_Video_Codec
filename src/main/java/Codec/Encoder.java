package Codec;

import FileManagement.JPEG_Handler;
import FileManagement.MatchWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.PublicKey;
import java.util.HashMap;

/**
 *
 * @author Adrià Valls, Sebastian Andrade 2022
 */
public class Encoder {
    private HashMap<Integer, Double> qualityValue;
    /** Encoder Class constructor  */
    public Encoder() {
        /*
        We use this table to make a proportional assesment of the Quality input given by the user.
        Since we use the Euclidean Distance to determine the similarity, and we know how high these distances can be,
        we created these values and assigned it to what we think corresponds to a reasonable quality value.
         */
        qualityValue = new HashMap<>();
        qualityValue.put(10, 1.0);
        qualityValue.put(9, 2.0);
        qualityValue.put(8, 5.0);
        qualityValue.put(7, 8.0);
        qualityValue.put(6, 10.0);
        qualityValue.put(5, 12.0);
        qualityValue.put(4, 15.0);
        qualityValue.put(3, 18.0);
        qualityValue.put(2, 20.0);
        qualityValue.put(1, 25.0);

    }

    /**
     * @param inPath the unzipped directory that contains all the images
     * @param outPath the unzipped directory that will contain all the encoded images
     * @param nTiles the number of tiles given by the user to define the size of the cell
     * @param seekRange the maximum cell-search distance given by the user
     * @param GOP an int that defines the number of images in between 2 frames
     * @param quality an int 1-10 that defines the quality of the process
     *
     * In this method, we save the Base image in the encoded directory, and we iterate ove the rest of the files,
     * We use the GOPcount to determine when to update the base image. Here we call the matchFinder method,
     * where we divide each image into cells (grid).
     */
    public void encode(String inPath, String outPath, int nTiles, int seekRange, int GOP, int quality) {

        System.out.println("Encoding Files :)");

        File dir = new File(outPath); //we create the directory in which we will save all the images
        dir.mkdir();

        JPEG_Handler jpeg_handler = new JPEG_Handler();
        File inputFile = new File(inPath);
        File[] file_allPaths = inputFile.listFiles();

        System.out.println("Estimated time: " + (double) 0.0658 * file_allPaths.length + "s");

        if (file_allPaths.length == 0) {
            throw new IllegalArgumentException("This file is empty " + inputFile.getAbsolutePath());
        }
        //Create matchFile and Save num of frames
        String fileName = "\\MatchFile.txt";
        File matchFile = new File(outPath + fileName);
        boolean fileCreated;
        try {
            fileCreated = matchFile.createNewFile();
            if (fileCreated) {
                //System.out.print("Match file Created");
                DataOutputStream dos = new DataOutputStream(new FileOutputStream(matchFile, true));
                dos.writeInt(file_allPaths.length);
                dos.close();
            }
        } catch (IOException e) {
            System.out.print("Cannot create MatchFile");
            e.printStackTrace();
        }


        int GOPcount = 0;
        int baseNum = 0;
        int destNum = 0;

        BufferedImage baseImg;
        BufferedImage destImg;

        //Save first Base Image
        File baseImgFile = file_allPaths[baseNum];
        File destImgFile = file_allPaths[destNum];

        baseImg = jpeg_handler.readImage(baseImgFile.getAbsolutePath());
        jpeg_handler.writeImage(baseImg, outPath + File.separator + "00.jpeg");

        MatchWriter matches = new MatchWriter();
        matches.saveToFile(matchFile);

        String encode_progressBar = new String(new char[file_allPaths.length]).replace('\0', '_');

        while (destNum + 1 < file_allPaths.length) {
            destNum += 1;
            encode_progressBar = encode_progressBar.substring(0, destNum) + "=" + encode_progressBar.substring(destNum+1,encode_progressBar.length());
            System.out.print(encode_progressBar + "\r");
            matches.clearData();
            if (GOPcount == GOP) {
                GOPcount = 0;
                baseNum = destNum;
                //IMG BASE = NEXT IMAGE
                baseImgFile = file_allPaths[baseNum];
                baseImg = jpeg_handler.readImage(baseImgFile.getAbsolutePath());
                //SAVE IMAGE TAL CUAL
                jpeg_handler.writeImage(baseImg, outPath + File.separator + baseImgFile.getName().substring(0, destImgFile.getName().length()-4) + ".jpeg");
                //SAVE MATCHES
                matches.saveToFile(matchFile);


            } else {
                GOPcount += 1;
                //IMG Dest = NEXT IMAGE
                baseImg = jpeg_handler.readImage(file_allPaths[destNum-1].getAbsolutePath()); //FIX after presentation
                destImgFile = file_allPaths[destNum];
                destImg = jpeg_handler.readImage(destImgFile.getAbsolutePath());
                //COMPARACION
                //System.out.println("Image num "+destImg);
                destImg = matchFinder(baseImg, destImg, nTiles, seekRange, quality, matches);
                jpeg_handler.writeImage(destImg, outPath + File.separator + destImgFile.getName().substring(0, destImgFile.getName().length()-4) + ".jpeg");
                matches.saveToFile(matchFile);
            }
            matches.clearData();
        }

    }

    /**
     * @param baseImg the image that depends on the COP count. The image is not modified
     * @param destImg the image that corresponds to the iteration (next frame)
     * @param nTiles the number of tiles given by the user to define the size of the cell
     * @param seekRange the maximum cell-search distance given by the user
     * @param quality an int 1-10 that defines the quality of the process
     * @param matches the intance of the class MatchWriter that will allow us to se the information of the encoding process
     *
     * In this method, we create the grid of the image to get the coordinates for each cell, to do the cell matching.
     */
    public BufferedImage matchFinder(BufferedImage baseImg, BufferedImage destImg, int nTiles, int seekRange, int quality, MatchWriter matches) {
        BufferedImage newDest = destImg;
        int cellNum = 0;
        int xCell = 0;
        int yCell = 0;

        for (int y = 0; y*nTiles < destImg.getHeight(); y ++) {
            yCell = y*nTiles;
            for (int x = 0; x*nTiles < destImg.getWidth(); x ++) {
                xCell = x*nTiles;
                //System.out.println("Cell coords: "+xCell+" "+yCell);
                newDest = cellMatching(baseImg, newDest, xCell, yCell, nTiles, seekRange, quality, matches, cellNum);
                cellNum++;
            }
        }
        return newDest;
    }

    /**
     * @param baseImg the image that depends on the COP count. The image is not modified
     * @param destImg the image that corresponds to the iteration (next frame)
     * @param Xcoord the x coordinate that corresponds to the cell
     * @param Ycoord the y coordinate that corresponds to the cell
     * @param nTiles the number of tiles given by the user to define the size of the cell
     * @param seekRange the maximum cell-search distance given by the user
     * @param quality an int 1-10 that defines the quality of the process
     * @param matches the intance of the class MatchWriter that will allow us to se the information of the encoding process
     * @param cellNum the cell number we are at, used for the MatchFile.txt.
     *
     * In this method, we create the actual cell, using the given coordinates and the getSubimage method. We call the method
     * tessleComparator to get the euclidean distance, and determine whether it is a match or not.
     */
    public BufferedImage cellMatching(BufferedImage baseImg, BufferedImage destImg, int Xcoord, int Ycoord, int nTiles, int seekRange, int quality, MatchWriter matches, int cellNum) {
        //System.out.println("Entered cell num "+cellNum);
        boolean matchFound = false;
        BufferedImage newDest = destImg;
        int centerX = Xcoord;
        int centerY = Ycoord;
        int w = nTiles;
        int h = nTiles;

        //Subdivision Tessela
        BufferedImage tesselaDes = destImg.getSubimage(centerX, centerY, nTiles, nTiles);
        BufferedImage tesselaBase = baseImg.getSubimage(centerX, centerY, nTiles, nTiles);

        int range = 0;

        if (tessleComparator(tesselaBase, tesselaDes, quality)) {
            matchFound = true;
        }
        boolean outOfRange = false;
        int x = Xcoord;
        int y = Ycoord;
        int test = 0;
        while (!matchFound && !outOfRange) {

            x = centerX-range;
            y = centerY-range;
            int width = range*2+1;
            int height = range*2+1;
            for(int wit=0; wit<width;wit++){
                x = x+wit;
                w = x+nTiles;
                y = centerY-range;
                if(x>=0 && w<destImg.getWidth()){
                    for(int hit=0; hit<height ;hit++){
                        y = y+hit;
                        h = y+nTiles;
                        if(y>=0 && h<destImg.getHeight()){
                            //generar subimagen
                            tesselaBase = baseImg.getSubimage(x, y, nTiles, nTiles);
                            //comparar las dos subimagenes
                            if (tessleComparator(tesselaBase, tesselaDes, quality)) {
                                matchFound = true;
                                break;
                            }
                        }
                    }

                }
                if(matchFound){
                    break;
                }
                range++;
            }
            if (range == seekRange) {
                outOfRange = true;
            }

        }
        if(matchFound){
           applyAverage(newDest, Xcoord, Ycoord, nTiles);
           matches.addMatch(cellNum,x,y);
        }
        return newDest;
    }

    /**
     * @param baseTessle the cell of the base image
     * @param destTessle the cell of the image we are comparing it to
     * @param quality an int 1-10 that defines the quality of the process
     *
     * In this method, we get the euclidean distance, to see how similar the dest cell is to the cell in the base image,
     * and determine if it is a match.
     */
    public boolean tessleComparator(BufferedImage baseTessle, BufferedImage destTessle, int quality){

        boolean isMatch =true;
        int r, g, b;
        float diff = 0;
        int count = 0;

        for (int i = 0; i < baseTessle.getWidth(); i++){
            for (int j = 0; j < baseTessle.getHeight(); j++){
                //System.out.println("coordenada i:"+i+" j:"+j);
                Color destPixel = new Color(destTessle.getRGB(i, j));
                Color basePixel = new Color(baseTessle.getRGB(i, j));

                r = destPixel.getRed() - basePixel.getRed();
                g = destPixel.getGreen() - basePixel.getGreen();
                b = destPixel.getBlue() - basePixel.getBlue();
                diff += Math.sqrt(r*r+b*b+g*g)/3;
                count++;

            }
        }
        float distance = diff/count;
        //System.out.println("diff: " + distance);
        if(distance > this.qualityValue.get(quality)){
            isMatch = false;
        }

        return isMatch;
    }

    /**
     * @param destImg the cell of the image
     * @param xCoord the x coordinate of the cell
     * @param yCoord the y coordinate of the cell
     * @param nTiles the number of tiles given by the user to define the size of the cell
     *
     * In this method, we get the average color of the cell and apply this new color to the entire cell.
     */
    public BufferedImage applyAverage(BufferedImage destImg, int xCoord, int yCoord, int nTiles){

        BufferedImage newDest = destImg;
        //System.out.println("x: " + xCoord +"|" + "y: " + yCoord);
        //get the average color
        int r, g, b; //we will be adding the value to calculate the average
        r = g = b = 0;
        int count = 0;

        for (int i = xCoord; i < xCoord + nTiles; i++){
            for (int j = yCoord; j < yCoord + nTiles; j++){

                Color pixel = new Color(destImg.getRGB(i, j));
                r = r + pixel.getRed();
                g = g + pixel.getGreen();
                b = b + pixel.getBlue();

                count++;
            }
        }
        //get average of each channel
        r = r / count;
        g = g / count;
        b = b / count;

        Color avgColor = new Color(r, g, b);
        //now that we have the average color, we can apply the color to the new image
        //TODO-check if coords are correct, what is numPixels for?
        for (int x = xCoord; x < xCoord + nTiles; x++){
            for (int y = yCoord; y < yCoord + nTiles; y++){
                newDest.setRGB(x, y, avgColor.getRGB());
            }
        }
        return newDest;
    }
}
