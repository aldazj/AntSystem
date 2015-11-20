package utils;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by aldazj on 19.10.15.
 */
public class ReadFile {

    private BufferedReader br;
    private File file;
    private String path_filename;
    private double[][] matrix_D;
    private ArrayList<City> cities;

    /**
     * File un fichier
     * @param path_filename : path du fichier à lire
     */
    public ReadFile(String path_filename) {
        this.path_filename = path_filename;
        cities = new ArrayList<City>();
        read_data();
    }

    /**
     * Lecture d'un fichier
     */
    public void read_data(){
        file = new File(Utilities.PATH_DIRECTORY+path_filename);
        ArrayList<String[]> data = new ArrayList<String[]>();
        String line = "";
        try {
            br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null) {
                data.add(line.replaceAll(" +", " ").split(" "));
            }
            br.close();
            build_matrix_d(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Construction d'un matrice de distances entre les villes
     * @param data
     */
    private void build_matrix_d(ArrayList<String[]> data){
        matrix_D = new double[data.size()][data.size()];
        Utilities.init_matrix(matrix_D);
        for (int i = 0; i < data.size(); i++) {
            double posX = Double.parseDouble(data.get(i)[1]);
            double posY = Double.parseDouble(data.get(i)[2]);
            cities.add(new City(data.get(i)[0], posX, posY));
            for (int j = 0; j < data.size(); j++) {
                if(i != j){
                    double nextCityPosX = Double.parseDouble(data.get(j)[1]);
                    double nextCityPosY = Double.parseDouble(data.get(j)[2]);
                    double distance = Utilities.dEuclidian(nextCityPosX, nextCityPosY, posX, posY);
                    matrix_D[i][j] = distance;
                }
            }
        }
    }

    /**
     * Récupère une matrice de distances entre les villes
     * @return
     */
    public double[][] getMatrix_D() {
        return matrix_D;
    }

    /**
     * Récupère les villes existantes dans le fichier à lire
     * @return
     */
    public ArrayList<City> getCities() {
        return cities;
    }
}