package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by aldazj on 05.11.15.
 */
public class Utilities {

    public static String PATH_DIRECTORY = "src"+ File.separator+"data"+ File.separator;
    /**
     * Initialisation une matrice tout à zero
     * @param matrix
     */
    public static void init_matrix(double[][] matrix){
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                matrix[i][j] = 0.0;
            }
        }
    }

    /**
     * Calcul de la distance d'euclidienne
     * @param nextCityPosX : prochaine position x
     * @param nextCityPosY : prochaine position y
     * @param localPosX : position local x
     * @param localPosY : position local y
     * @return : distace de ha
     */
    public static double dEuclidian(double nextCityPosX, double nextCityPosY, double localPosX, double localPosY){
        return Math.sqrt(Math.pow(nextCityPosX-localPosX, 2)+Math.pow(nextCityPosY-localPosY, 2));
    }

    /**
     * Affiche la matrice de distances
     * @param distances
     */
    public static void printDistances(double[][] distances){
        System.out.println("############  Distances  ############");
        for (int i = 0; i < distances.length; i++) {
            for (int j = 0; j < distances[0].length; j++) {
                System.out.print(distances[i][j] + "\t");
            }
            System.out.println("");
        }
    }

    /**
     * Affiche les fourmis
     * @param cities
     */
    public static void printCities(ArrayList<City> cities){
        System.out.println("############  Cities  ############");
        for (int i = 0; i < cities.size(); i++) {
            System.out.print(cities.get(i).getLabel() + " ");
        }
        System.out.println("");
    }

    /**
     * Génère une valeur aléatoire entre zéro et max
     * @param max
     * @return
     */
    public static int randomIndex(int max){
        return 0 + (int)(Math.random() * ((max - 0) + 1));
    }

    /**
     * Obtient une fourmis aléatoirement
     * @param cities
     * @return
     */
    public static City getRandomCity(ArrayList<City> cities){
        return cities.get(randomIndex(cities.size()-1));
    }

    /**
     * Crée une correspondance entre les noms de villes et les indices par rappport
     * à la matrice de distaces
     * @param indexDistances
     * @param cities
     */
    public static void initIndexDistances(LinkedHashMap<String, Integer> indexDistances, ArrayList<City> cities){
        for (int i = 0; i < cities.size(); i++) {
            indexDistances.put(cities.get(i).getLabel(), i);
        }
    }

    /**
     * Retourne le nom d'un ville à partir d'un indice
     * @param indexCity : indice d'une ville
     * @param citiesNamed : correspondance entre le nom d'une ville et sa position
     * @return
     */
    public static String getHashNameCity(int indexCity, LinkedHashMap<String, Integer> citiesNamed){
        for (Map.Entry<String, Integer> entry : citiesNamed.entrySet()) {
            if(entry.getValue() == indexCity){
                return entry.getKey();
            }
        }
        return "";
    }

    /**
     * Affiche la correspondance entre un nom d'une ville et son indice dans la matrice de distances
     * @param indexDistances
     */
    public static void printHash(LinkedHashMap<Integer, Double> indexDistances){
        for (Map.Entry<Integer, Double> entry : indexDistances.entrySet()) {
            System.out.println(entry.getKey()+":"+entry.getValue());
        }
    }

    /**
     * Récupère une ville
     * @param city : nom de la ville
     * @param cities : ensemble de villes
     * @return : une ville
     */
    public static City getCity(String city, ArrayList<City> cities){
        for (int i = 0; i < cities.size(); i++) {
            if(city.equals(cities.get(i).getLabel())){
                return  cities.get(i);
            }
        }
        return null;
    }

    /**
     * Crée une solution random aléatoire
     * @param cities
     * @return
     */
    public static ArrayList<City> randomInitSolution(ArrayList<City> cities){
        ArrayList<City> init_sol = new ArrayList<City>(cities);
        Collections.shuffle(init_sol);
        return  init_sol;
    }

    /**
     * Trouve la ville plus proche
     * @param currentCity : ville courante
     * @param cities : ensemble de villes
     * @param distances : matrice de distances
     * @param path : les chemins actuels parcourus
     * @param indexDistances : correspondance entre le nom d'une ville et sa position
     * @return
     */
    public static City getNextCity(City currentCity, ArrayList<City> cities,
                                   double[][] distances, ArrayList<City> path,
                                   LinkedHashMap<String, Integer> indexDistances) {

        int index_DistSource = indexDistances.get(currentCity.getLabel());
        double tmpDistance = Double.MAX_VALUE;
        City nextCity = null;
        for (int index_DistDest = 0; index_DistDest < distances[index_DistSource].length; index_DistDest++) {
            if (index_DistDest != index_DistSource) {
                if (distances[index_DistSource][index_DistDest] < tmpDistance) {

                    City city = getCity(getHashNameCity(index_DistDest, indexDistances), cities);
                    if (!city.isVisited()) {
                        tmpDistance = distances[index_DistSource][index_DistDest];
                        nextCity = city;
                    }
                }
            }
        }
        currentCity.setDistanceNextCity(tmpDistance);
        currentCity.setNextCity(nextCity.getLabel());
        path.add(currentCity);
        update_CityVisited(currentCity, cities);
        return nextCity;
    }

    /**
     * Met à jour si une ville est visitée
     * @param currentCity : ville courante
     * @param cities : ensemble de villes
     */
    public static void update_CityVisited(City currentCity, ArrayList<City> cities){
        for (int i = 0; i < cities.size(); i++) {
            if(currentCity.getLabel().equals(cities.get(i).getLabel())){
                cities.get(i).setVisited(true);
            }
        }
    }

    /**
     * Retourn vers la ville de départ
     * @param currentCity : ville courante
     * @param nextCity : prochaine ville
     * @param cities : ensemble de villes
     * @param distances : distances entre les villes
     * @param path : chemin actuel parcouru
     * @param indexDistances : correspondance entre le nom d'une ville et sa position
     */
    public static void comeBack(City currentCity, City nextCity, ArrayList<City> cities,
                                double[][] distances, ArrayList<City> path,
                                LinkedHashMap<String, Integer> indexDistances){

        int index_DistSource = indexDistances.get(currentCity.getLabel());
        int index_DistDest = indexDistances.get(nextCity.getLabel());
        double tmpDistance = distances[index_DistSource][index_DistDest];
        currentCity.setDistanceNextCity(tmpDistance);
        currentCity.setNextCity(nextCity.getLabel());
        path.add(currentCity);
        update_CityVisited(currentCity, cities);
    }

    /**
     * Calcule de la distance
     * @param path
     * @return
     */
    public static double computeDistance(ArrayList<City> path){
        double distance = 0.0;
        for (int i = 0; i < path.size(); i++) {
            distance += path.get(i).getDistanceNextCity();
        }
        return distance;
    }

    /**
     * Initialisation de la matrice tau avec un taux de phéromone initial
     * @param tau : matrice de phéromones
     * @param tau0 : valeur initiale de phéromone
     */
    public static void init_tau(double[][] tau, double tau0){
        for (int i = 0; i < tau.length; i++) {
            for (int j = 0; j < tau[0].length; j++) {
                    tau[i][j] = tau0;
            }
        }
    }

    /**
     * Affiche une matrice
     * @param matrix
     */
    public static void printMatrix(double[][] matrix){
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println("");
        }
    }

    /**
     * Initialisation des villes à "pas visitées"
     * @param cities : les villes
     */
    public static ArrayList<City> initCities(ArrayList<City> cities){
        for (int i = 0; i < cities.size(); i++) {
            cities.get(i).setNextCity("");
            cities.get(i).setDistanceNextCity(-1);
            cities.get(i).setVisited(false);
        }
        return cities;
    }

    /**
     * Récuère l'indice de la prochaine ville d'une manière stochastique
     * @param probabilities : probabilités pour aller aux prochaines villes non visitées
     * @param citiesNot_Visited : les villes pas encore visitées
     * @return : l'indice de la prochaine ville
     */
    public static int getIndexNextCity(LinkedHashMap<Integer, Double> probabilities, ArrayList<City> citiesNot_Visited){
        String [] probaAccumulatives = new String[probabilities.size()];

        //Calcule les probabilités accumulatives
        double probaAccumulValue = 0;
        double delta = Double.MIN_VALUE;
        double tmp = 0;
        int j = 0;
        for (Map.Entry<Integer, Double> entry : probabilities.entrySet()) {
            int index_notVisited = entry.getKey();
            if(!citiesNot_Visited.get(index_notVisited).isVisited()){
                probaAccumulValue += entry.getValue();
                probaAccumulatives[j] = tmp+";"+probaAccumulValue+";"+index_notVisited;
                tmp = probaAccumulValue+delta;
            }
            j++;
        }
        double r = Math.random();
        return Utilities.recherche_dichotomique(r, probaAccumulatives);
    }

    /**
     * Recherche dichotonique. Nous division l'interval de recherche par deux
     * à chaque étape
     * @param value : valeur à trouver
     * @param probaAccumulatives : probabilités accumulatives
     * @return : l'évènement trouvé ou pas
     */
    public static int recherche_dichotomique(double value, String[] probaAccumulatives){
        int start = 0;
        int end = probaAccumulatives.length;
        boolean found = false;
        int pointer;
        do{
            pointer = ((start+end)/2);
            if(is_in_probaAccumulatives(value, probaAccumulatives[pointer])){
                found = true;
                pointer = Integer.parseInt(probaAccumulatives[pointer].split(";")[2]);
            }else if(is_greater_than(value, probaAccumulatives[pointer])){
                start = pointer+1;
            }else if(is_less_than(value, probaAccumulatives[pointer])){
                end = pointer-1;
            }
        }while (found == false && start <= end);

        if(found){
            return pointer;
        }else{
            return -1;
        }
    }

    /**
     * Détermine si une valeur se trouve dans un interval de deux valeurs
     * Exemple:
     * Intervale sur la forme: "0.25;0.50"
     * @param value : valeur aléatoire
     * @param interval : interval
     * @return
     */
    private static boolean is_in_probaAccumulatives(double value, String interval){
        String[] myInterval = interval.split(";");
        double startInterval = Double.parseDouble(myInterval[0]);
        double endInterval = Double.parseDouble(myInterval[1]);
        return  value >= startInterval && value <= endInterval;
    }

    /**
     * Détermine si une valeur est plus grande que les deux valeurs existantes dans un interval
     * Exemple:
     * Intervale sur la forme: "0.25;0.50"
     * @param value : valeur aléatoire
     * @param interval : interval
     * @return
     */
    private static boolean is_greater_than(double value, String interval){
        String[] myInterval = interval.split(";");
        double startInterval = Double.parseDouble(myInterval[0]);
        double endInterval = Double.parseDouble(myInterval[1]);
        return  value > startInterval && value > endInterval;
    }

    /**
     * Détermine si une valeur est plus petite que les deux valeurs existantes dans un interval
     * Exemple:
     * Intervale sur la forme: "0.25;0.50"
     * @param value : valeur aléatoire
     * @param interval : interval
     * @return
     */
    private static boolean is_less_than(double value, String interval){
        String[] myInterval = interval.split(";");
        double startInterval = Double.parseDouble(myInterval[0]);
        double endInterval = Double.parseDouble(myInterval[1]);
        return  value < startInterval && value < endInterval;
    }

    /**
     * Affiche le chemin parcouru
     * @param path : chemin parcouru
     */
    public static void printPath(ArrayList<City> path){
        System.out.print("[");
        for (int i = 0; i < path.size(); i++) {
            System.out.print(path.get(i).getLabel()+"->" + path.get(i).getNextCity()+" ");
        }
        System.out.print("]");
    }

    /**
     * Calcule la variance
     * @param mean : moyenne
     * @param fitness : fitness
     * @return : valeur de la variance
     */
    public static double computeVariance(double mean, double[] fitness){
        double var = 0.0;
        for (int i = 0; i < fitness.length; i++) {
            var += Math.pow(fitness[i]-mean, 2);
        }
        return (var/(fitness.length));
    }

    /***
     * Calcule la variance
     * @param timesExec : time to exec
     * @return
     */
    public static long computeMean(long[] timesExec){
        long mean = 0;
        for (int i = 0; i < timesExec.length; i++) {
            mean += timesExec[i];
        }
        return  mean/timesExec.length;
    }

    /**
     * Affiche un elapsedTimeMillis timer sous la forme hh:mm:ss
     * @param elapsedTimeMillis
     * @return
     */
    public static String elapsedToString(long elapsedTimeMillis) {
        long seconds = (elapsedTimeMillis + 500) / 1000; // round
        long minutes = seconds / 60;
        long hours = minutes / 60;
        return String.format("%1$02d:%2$02d:%3$02d",
                hours,
                minutes % 60,
                seconds % 60);
    }

    /**
     * Genère une matrice aléatoire et l'écrit dans un fichier
     * @param pathFiles
     */
    public static void generateRandomCityFile(String pathFiles){
        int[] dimension = {80};
        for (int i = 0; i < dimension.length; i++) {
            Utilities.generateFile(pathFiles+"cities"+dimension[i]+".dat", dimension[i]);
        }
    }

    /**
     * Génère un fichier de villes
     * @param filename
     * @param size
     */
    public static void generateFile(String filename, int size){
        try {
            String c = "a";
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filename)));
            for (int i = 0; i < size; i++) {
                bw.write(c+i+" "+String.valueOf(randomIndex(size))+" "+String.valueOf(randomIndex(size)));
                bw.write('\n');
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
