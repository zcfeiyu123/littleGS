package domain.proxy;

import domain.entity.Coordinates;
import domain.log.Logger;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-7
 * Time: 下午4:10
 */
public class CoordinatesProxy {
    private static CoordinatesProxy coordinatesProxyInstance = null;
    private CoordinatesProxy(){}

    public static CoordinatesProxy getCoordinatesProxyInstance()
    {
        if(coordinatesProxyInstance == null)
        {
            coordinatesProxyInstance = new CoordinatesProxy();
        }
        return coordinatesProxyInstance;
    }

    /*----------------------------------------------business parts----------------------------------------------------*/
    private HashMap<String, Coordinates> coordinatesHashMap = null;

    public void init()
    {
        Logger.getInstance().debug("start init coordinates proxy");
        this.coordinatesHashMap = new HashMap<String, Coordinates>();
        Logger.getInstance().debug("coordinates proxy init finish");
    }

    public boolean isCoordinateExist(String coordinatesString)
    {
        return coordinatesHashMap.containsKey(coordinatesString);
    }

    public void createCoordinates(double longitude, double latitude)
    {
        Coordinates c = new Coordinates(longitude, latitude);
        String key = String.valueOf(longitude) + "_" + String.valueOf(latitude);
        coordinatesHashMap.put(key, c);
    }

    public Coordinates getCoordinatesByName(String key)
    {
        return coordinatesHashMap.get(key);
    }

    public void removeCoordinatesFromHashMap(String key)
    {
        coordinatesHashMap.remove(key);
    }

}
