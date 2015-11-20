package utils;

/**
 * Created by aldazj on 05.11.15.
 */
public class City implements Cloneable{

    private double posX, posY, distanceNextCity = -1;   //Positions d'une ville et la distance à la prochaine ville
    private String label, nextCity = "";                //Label pour la ville courante et la prochaine ville
    private boolean visited = false;                    //Verifie si une ville a été visitée

    /**
     * Création d'un ville
     * @param label : nom de la ville
     * @param posX : position x
     * @param poxY : position y
     */
    public City(String label, double posX, double poxY) {
        this.label = label;
        this.posX = posX;
        this.posY = poxY;
    }

    public double getPosX() {
        return posX;
    }

    public double getPosY() {
        return posY;
    }

    public String getLabel() {
        return label;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public void setDistanceNextCity(double distanceNextCity) {
        this.distanceNextCity = distanceNextCity;
    }

    public double getDistanceNextCity() {
        return distanceNextCity;
    }

    public String getNextCity() {
        return nextCity;
    }

    public void setNextCity(String nextCity) {
        this.nextCity = nextCity;
    }

    public Object clone() {
        Object o = null;
        try {
            o = super.clone();
        } catch(CloneNotSupportedException cnse) {
            cnse.printStackTrace(System.err);
        }
        return o;
    }
}
