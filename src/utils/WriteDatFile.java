package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by aldazj on 08.11.15.
 */
public class WriteDatFile {
    private String filename;
    private BufferedWriter bw;
//    private String folder = "src/results";
    private String folder = "src/test";


    public WriteDatFile(String filename) {
        this.filename = folder+"/"+filename+".dat";
    }

    /**
     * Write the results in a file .dat
     * @param path
     */
    public void writeResults(ArrayList<City> path){
        try {
            File file = new File(filename);
            if(!file.exists()){
                file.createNewFile();
            }
            bw = new BufferedWriter(new FileWriter(file));
            for (int i = 0; i < path.size(); i++) {
                bw.write(path.get(i).getLabel()+" ");
                bw.write(path.get(i).getPosX()+" ");
                bw.write(path.get(i).getPosY()+" ");
                bw.write("\n");
            }
            bw.write(path.get(0).getLabel()+" ");
            bw.write(path.get(0).getPosX()+" ");
            bw.write(path.get(0).getPosY()+" ");
            bw.write("\n");
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeCompareResults(ArrayList<String> result2Compare){
        try {
            File file = new File(filename);
            if(!file.exists()){
                file.createNewFile();
            }
            bw = new BufferedWriter(new FileWriter(file));
            for (int i = 0; i < result2Compare.size(); i++) {
                String[] results = result2Compare.get(i).split(" ");
                bw.write(results[0]+" ");
                bw.write(results[1]+" ");
                bw.write(results[2]+" ");
                bw.write(results[3]+" ");
                bw.write("\n");
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
