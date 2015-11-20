package main;

import utils.City;
import utils.ReadFile;
import utils.Utilities;
import utils.WriteDatFile;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by aldazj on 05.11.15.
 */
public class AntSystem {

    private double[][] distances;                           //Distances entre les villes
    private double[][] tau;                                 //Phéronomes
    private double[][] deltaTau;                            //Contribution des phéronomes des fourmis
    private ArrayList<City> cities;                         //Villes existantes
    private int t_max;                                      //Nombre d'itérations
    private int m;                                          //Nombre de fourmis
    private LinkedHashMap<String, Integer> indexDistances;  //Correspondance label d'une ville et l'indice
                                                            //dans la matrice de distances
    ArrayList<City> bestPath = null;                        //Meilleur parcours
    double bestDistance = Double.MAX_VALUE;                 //Distance à minimiser
    private double alpha = 1.0;                             //Paramètre qui favorise les chemins où la
                                                            //quantité de phéromones est importante
    private double beta = 5.0;                              //Paramètre qui influence sur la visibilité
    private double rho = 0.1;                               //Paramètre d'évaporisation de phéromones
    private double Q;                                       //Paramètre de contrôle

    /**
     * AntSystem
     * @param distances : distance entre les distances
     * @param cities : les villes existantes
     * @param t_max : le nombre d'iterations maximales
     * @param nb_ants : le nombre de fourmis
     */
    public AntSystem(double[][] distances, ArrayList<City> cities, int t_max, int nb_ants) {
        this.distances = distances;
        this.cities = cities;
        this.t_max = t_max;
        this.m = nb_ants;
        this.indexDistances = new LinkedHashMap<String, Integer>();
        Utilities.initIndexDistances(this.indexDistances, cities);
        this.tau = new double[distances.length][distances[0].length];
        Utilities.initCities(this.cities);
    }

    /**
     * Main de l'algorithme
     */
    public void main(){
        Utilities.init_tau(tau, 1/Q);
        for (int t = 0; t < t_max; t++) {
            double tmpDistance;
            deltaTau = new double[distances.length][distances.length];

            //Pour chaque fourmis
            for (int k = 0; k < m; k++) {
                ArrayList<City> antPath = new ArrayList<City>();
                cities = Utilities.initCities(cities);
                ArrayList<City> citiesNot_Visited = (ArrayList<City>)cities.clone();

//                System.out.println("citiesNot_Visited");
//                Utilities.printCities(citiesNot_Visited);

                //Choisir une ville aléatoire
                City city = Utilities.getRandomCity(citiesNot_Visited);
                City cityInit = new City(city.getLabel(), city.getPosX(), city.getPosY());
                city.setVisited(true);

//                System.out.println("init: "+city.getLabel());

                //Tant que nous n'avons pas visité toutes les villes
                while (antPath.size() < cities.size()-1){
                    double normalize = computeNormalize(city, citiesNot_Visited, antPath);
                    city = choiceCity(city, citiesNot_Visited, normalize, antPath);
                }
                Utilities.comeBack(city, cityInit, citiesNot_Visited, distances, antPath, indexDistances);

                //Marquer le chemin pris par la fourmis avec sa contribution de phéromones
                tmpDistance = mark_path(antPath);

                //Garder la meilleure solution
                if(tmpDistance < bestDistance){
                    bestDistance = tmpDistance;
                    bestPath = (ArrayList<City>)antPath.clone();
                }
            }
            //Mettre à jour l'apport en phéromone de chaque fourmis
            updateAllPath();
        }
    }

    /**
     * Calcule la longueur total
     * @param city : villes existantes
     * @param citiesNot_Visited : villes pas encore visitées
     * @param antPath : parcours d'une ville
     * @return :
     */
    private double computeNormalize(City city, ArrayList<City> citiesNot_Visited, ArrayList<City> antPath){
        double normalize = 0.0;
        int i = indexDistances.get(city.getLabel());
        for (int l = 0; l < citiesNot_Visited.size(); l++) {
            if(i != l && !citiesNot_Visited.get(l).isVisited()){
                normalize += Math.pow(tau[i][l], alpha)*Math.pow(1/distances[i][l], beta);
            }
        }
        return normalize;
    }

    /**
     * Choisit une ville d'une manière stochastique
     * @param city
     * @param citiesNot_Visited
     * @param normalize
     * @param antPath
     * @return
     */
    private City choiceCity(City city, ArrayList<City> citiesNot_Visited, double normalize, ArrayList<City> antPath){
        int i = indexDistances.get(city.getLabel());
        LinkedHashMap<Integer, Double> probabilities = new LinkedHashMap<Integer, Double>();
        //Calcule la probabilité de séléction
        for (int j = 0; j < citiesNot_Visited.size(); j++) {
            if(i != j && !citiesNot_Visited.get(j).isVisited()){
                probabilities.put(j, ((Math.pow(tau[i][j], alpha) * Math.pow(1 / distances[i][j], beta)) / normalize));
            }
        }
//        Utilities.printHash(probabilities);

        //Choisit l'indice de la prochaine ville
        int j = Utilities.getIndexNextCity(probabilities, citiesNot_Visited);
        City nextCity = citiesNot_Visited.get(j);
        city.setNextCity(nextCity.getLabel());
        city.setDistanceNextCity(distances[i][j]);
        city.setVisited(true);
        antPath.add((City) city.clone());
        city = nextCity;
        return city;
    }

    /**
     *
     * @param antPath
     * @return
     */
    private double mark_path(ArrayList<City> antPath){
        double distance = 0.0;
        int[][] index = new int[antPath.size()][2];
        for (int i = 0; i < antPath.size(); i++) {
            distance += antPath.get(i).getDistanceNextCity();
            index[i] = new int[]{indexDistances.get(antPath.get(i).getLabel()),indexDistances.get(antPath.get(i).getNextCity())};
        }
        double quantityPheronome = Q/distance;
        for (int i = 0; i < index.length; i++) {
            deltaTau[index[i][0]][index[i][1]] += quantityPheronome;
        }
        return distance;
    }

    /**
     * Mise à jour du tableau qui possède toutes les phéronomes du système
     */
    private void updateAllPath(){
        for (int i = 0; i < tau.length; i++) {
            for (int j = 0; j < tau[0].length; j++) {
                tau[i][j] = (1-rho)*tau[i][j]+deltaTau[i][j];
            }
        }
    }

    public void setQ(double q) {
        Q = q;
    }

    public ArrayList<City> getBestPath() {
        return bestPath;
    }

    public double getBestDistance() {
        return bestDistance;
    }

    public static void main(String[] args) {

//        String pathFiles = "src"+ File.separator+"test"+ File.separator;
//        Utilities.generateRandomCityFile(pathFiles);
        int nbExecutions = 10;
//---------------------------------------------------------------------------------
        //Choisir les fichier à exécuter
//        String[] filenames = {"cities.dat", "cities2.dat", "cities50.dat",
//                "cities60.dat", "cities100.dat"};
        String[] filenames = {"cities.dat"};

//---------------------------------------------------------------------------------
        //Choisir le nombre de fourmis à exécuter
//        int[] nb_ants = {2, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110};
        int[] nb_ants = {18};

//---------------------------------------------------------------------------------
        //Choisir le nombre de iterations à exécuter
//        int[] t_max = {2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30};
        int[] t_max = {2};

        long[] tempsExec = new long[nbExecutions];
        long startTime, stopTime, meanTime;
        double tmpMoyenne = Double.MAX_VALUE, bestDistance = 0, moyenneDistanceNNL = 0;
        double variance, moyenne;
        double[] bestFitness = new double[nbExecutions];
        ArrayList<City> bestPath = null;
        ArrayList<String> result2Compare = new ArrayList<String>();

        for (int indexFile = 0; indexFile < filenames.length; indexFile++) {
            System.out.println("###################  NearestNeighbour " + filenames[indexFile] +
                    " ###################");
            for (int i = 0; i < nbExecutions; i++) {
                ReadFile rf = new ReadFile(filenames[indexFile]);
                double[][] distances = rf.getMatrix_D();
                ArrayList<City> cities = rf.getCities();
                startTime = System.currentTimeMillis();
                NearestNeighbourAlgorithm nnl = new NearestNeighbourAlgorithm(cities, distances);
                nnl.main();
                Utilities.printPath(nnl.getPath());
                System.out.println("Distance: " + nnl.getDistance());
                if (nnl.getDistance() < tmpMoyenne) {
                    tmpMoyenne = nnl.getDistance();
                    bestPath = nnl.getPath();
                    bestDistance = nnl.getDistance();
                }
                bestFitness[i] = nnl.getDistance();
                moyenneDistanceNNL += nnl.getDistance();
                stopTime = System.currentTimeMillis();
                tempsExec[i] = stopTime - startTime;
            }
            moyenneDistanceNNL = moyenneDistanceNNL / nbExecutions;
            variance = Utilities.computeVariance(moyenneDistanceNNL, bestFitness);
            meanTime = Utilities.computeMean(tempsExec);
            System.out.println("");
            System.out.println("---------------------------------------");
            Utilities.printPath(bestPath);
            System.out.println("Best distance: " + bestDistance);
            System.out.println("Distance Moyenne: " + moyenneDistanceNNL);
            System.out.println("Execution time mean : " + meanTime);
            System.out.println("Execution time mean : " + Utilities.elapsedToString(meanTime));
            System.out.println("The standard deviation " + Math.sqrt(variance));
            System.out.println("---------------------------------------");
        }


        for (int nbAnts_indice = 0; nbAnts_indice < nb_ants.length; nbAnts_indice++) {
            for (int nbTmax_indice = 0; nbTmax_indice < t_max.length; nbTmax_indice++) {
                for (int indexFile = 0; indexFile < filenames.length; indexFile++) {
                    System.out.println("###################  Ant Systems " + filenames[indexFile] + " t_max:"+
                            t_max[nbTmax_indice]+" nb_ants:"+nb_ants[nbAnts_indice]+" ###################");
                    moyenne = 0.0;
                    bestPath = null;
                    tmpMoyenne = Double.MAX_VALUE; bestDistance = 0;
                    bestFitness = new double[nbExecutions];
                    tempsExec = new long[nbExecutions];
                    for (int i = 0; i < nbExecutions; i++) {
                        ReadFile rf = new ReadFile(filenames[indexFile]);
                        double[][] distances = rf.getMatrix_D();
                        ArrayList<City> cities = rf.getCities();

                        startTime = System.currentTimeMillis();
                        AntSystem as = new AntSystem(distances, cities, t_max[nbTmax_indice], nb_ants[nbAnts_indice]);
                        as.setQ(moyenneDistanceNNL);
                        as.main();
                        Utilities.printPath(as.getBestPath());
                        System.out.println("Distance: "+as.getBestDistance());
                        if (as.getBestDistance() < tmpMoyenne) {
                            tmpMoyenne = as.getBestDistance();
                            bestPath = as.getBestPath();
                            bestDistance = as.getBestDistance();
                        }
                        bestFitness[i] = as.getBestDistance();
                        moyenne += as.getBestDistance();
                        stopTime = System.currentTimeMillis();
                        tempsExec[i] = stopTime - startTime;
                    }
                    moyenne = moyenne / nbExecutions;
                    variance = Utilities.computeVariance(moyenne, bestFitness);
                    meanTime = Utilities.computeMean(tempsExec);
                    System.out.println("");
                    System.out.println("---------------------------------------");
                    Utilities.printPath(bestPath);
                    System.out.println("Best distance: " + bestDistance);
                    System.out.println("Distance Moyenne: " + moyenne);
                    System.out.println("Execution time mean : " + meanTime);
                    System.out.println("Execution time mean : " + Utilities.elapsedToString(meanTime));
                    System.out.println("The standard deviation " + Math.sqrt(variance));
                    System.out.println("---------------------------------------");
//---------------------------------------------------------------------------------
//                    //Pour écrire nos résultats dans un fichier
//                    WriteDatFile writeFile = new WriteDatFile(filenames[indexFile] + "_ANT_"+"t_max_"+
//                            t_max[nbTmax_indice]+"_nb_ants_"+nb_ants[nbAnts_indice]);
//                    writeFile.writeResults(bestPath);
//                    result2Compare.add(filenames[indexFile]+" "+nb_ants[nbAnts_indice]+" "
//                            +t_max[nbTmax_indice]+" "+bestDistance);
                }
            }
        }
//        WriteDatFile writeFile = new WriteDatFile("Results_ANT_compare");
//        writeFile.writeCompareResults(result2Compare);
    }
}
