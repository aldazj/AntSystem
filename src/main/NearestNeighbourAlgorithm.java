package main;

import utils.City;
import utils.Utilities;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by aldazj on 06.11.15.
 */
public class NearestNeighbourAlgorithm {

    private double[][] distances;                   //Distances entre les villes
    private ArrayList<City> cities;                 //villes existantes
    private int N;                                  //Taille des villes
    LinkedHashMap<String, Integer> citiesNamed;     //Relation entre le label d'une ville et son indice
                                                    // dans la matrice de distances
    ArrayList<City> path;                           //Chemin parcouru
    double distance;                                //Distance parcourue

    /**
     * Nearest Neighbour Algorithm
     * @param cities : villes existantes
     * @param distances : distances entre les villes
     */
    public NearestNeighbourAlgorithm(ArrayList<City> cities, double[][] distances) {
        this.N = cities.size();
        this.distances = distances;
        this.cities = cities;
        citiesNamed = new LinkedHashMap<String, Integer>();
        Utilities.initIndexDistances(citiesNamed, cities);
    }

    public void main(){
        path = new ArrayList<City>();

        //Solution initiale random
        ArrayList<City> my_cities = Utilities.randomInitSolution(cities);
        City city = my_cities.get(0);

        //Déplacement vers la ville la plus proche
        for (int i = 0; i < N-1; i++) {
            city = Utilities.getNextCity(city, my_cities, distances, path, citiesNamed);
        }

        //Retour à la ville de départ
        Utilities.comeBack(city, my_cities.get(0), my_cities, distances, path, citiesNamed);

        //Calcul de la distance parcourue
        distance = Utilities.computeDistance(path);
    }

    public ArrayList<City> getPath() {
        return path;
    }

    public double getDistance() {
        return distance;
    }
}
