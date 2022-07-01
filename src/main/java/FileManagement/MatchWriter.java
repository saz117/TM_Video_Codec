package FileManagement;


import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//Each
/**
 *
 * @author Adrià Valls, Sebastian Andrade 2022
 */
public class MatchWriter {
    private List<Integer> cellList;
    private List<Integer> xList;
    private List<Integer> yList;
    private int matchNumber;

    //TODO: for decoder, initialize match list from the file
    public MatchWriter()  {
        cellList = new ArrayList<>();
        xList = new ArrayList<>();
        yList = new ArrayList<>();
        matchNumber = 0;
    }
    public void clearData(){
        cellList.clear();
        xList.clear();
        yList.clear();
        matchNumber = 0;
    }

    /**
     * @param cellNumber the number of the individual cell of the grid
     * @param xCoord the x coordinate of the cell
     * @param yCoord the y coordinate of the cell
     *
     * this function adds the match information to each corresponding global lists
     */
    public void addMatch(int cellNumber, int xCoord, int yCoord){
        cellList.add(cellNumber);
        xList.add(xCoord);
        yList.add(yCoord);
        matchNumber += 1;
    }
    public void printMatches(){
        System.out.println(matchNumber);
    }

    /**
     * @param matchFile the number of the individual cell of the grid
     *
     * this function saves the information of each global list into the MatchFile.txt
     */
    public void saveToFile(File matchFile){

        try {
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(matchFile, true));

            dos.writeInt(matchNumber);

            for(int i=0; i<matchNumber; i++){
                //Cell number on the destiny image
                dos.writeInt(cellList.get(i));
                //X coordinate on the base image
                dos.writeInt(xList.get(i));
                //Y coordinate on the base image
                dos.writeInt(yList.get(i));
            }
            dos.close();

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }


}
