package FileManagement;

import java.io.*;

/**
 *
 * @author Adrià Valls, Sebastian Andrade 2022
 */
public class MatchReader {

    /**
     * @param matchFile the file that contains the Matched Cell, and its coordinates
     *
     * this function reads MatchFile.txt to interpret the encoded information.
     */
    public MatchReader(File matchFile){
        try{
            DataInputStream dis = new DataInputStream(new FileInputStream(matchFile));

            int numFrames = dis.readInt();
            System.out.print("Nuber of frames: " + numFrames +"\n");

            for(int photoNum=0; photoNum<numFrames; photoNum++){

                int nMatches = dis.readInt();
                System.out.print("Matches in frame " + photoNum + ": " +nMatches+"\n");

                for(int matchNum=0; matchNum<nMatches; matchNum++){
                    int cNumber = dis.readInt();
                    int x = dis.readInt();
                    int y = dis.readInt();

                    System.out.print("Cell number: "+cNumber+"\n");
                    System.out.print("X coord: "+x +"\n");
                    System.out.print("Y coord: "+y +"\n");
                }
            }
            dis.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
