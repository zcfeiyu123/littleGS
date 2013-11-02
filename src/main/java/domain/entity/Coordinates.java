package domain.entity;

/**
 * Created with IntelliJ IDEA.
 * User: Aaron
 * Date: 13-11-2
 * Time: 上午10:35
 * To change this template use File | Settings | File Templates.
 */
public class Coordinates {

    private double longitude = 0;
    private double latitude = 0;

    public Coordinates (double longitude, double latitude)
    {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public double getLatitude()
    {
        return latitude;
    }

}
