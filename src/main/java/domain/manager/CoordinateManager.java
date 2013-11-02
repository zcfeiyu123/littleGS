package domain.manager;

import domain.entity.Coordinates;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Aaron
 * Date: 13-11-2
 * Time: 上午11:02
 * To change this template use File | Settings | File Templates.
 */
public class CoordinateManager {
    private static CoordinateManager instance = null;
    private HashMap<String, Coordinates> coordinatesHashMap = null;
    private CoordinateManager(){
        this.coordinatesHashMap = new HashMap<String, Coordinates>();
    }

    public static CoordinateManager getInstance()
    {
        if(instance == null)
        {
            instance = new CoordinateManager();
        }
        return instance;
    }

    public Coordinates getCoordinates(double longitude, double latitude)
    {
        String coordinateString = String.valueOf(longitude) + "_" + String.valueOf(latitude);
        return coordinatesHashMap.containsKey(coordinateString) ? coordinatesHashMap.get(coordinateString) : createCoordinates(longitude, latitude);
    }

    private Coordinates createCoordinates(double longitude, double latitude)
    {
        String coordinateString = String.valueOf(longitude) + "_" + String.valueOf(latitude);
        Coordinates coordinatesInstance = new Coordinates(longitude, latitude);
        this.coordinatesHashMap.put(coordinateString, coordinatesInstance);
        return coordinatesInstance;
    }

    public Coordinates getExistCoordinatesByName(double longitude, double latitude)
    {
        String coordinateString = String.valueOf(longitude) + "_" + String.valueOf(latitude);
        return coordinatesHashMap.containsKey(coordinateString) ? coordinatesHashMap.get(coordinateString) : null;
    }
}
