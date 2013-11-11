package domain.entity;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-11
 * Time: 下午5:04
 */
public class TimedWeaponEntity {
    private String userName = null;
    private int weaponId;
    private double longitude;
    private double latitude;

    public TimedWeaponEntity(String userName, int weaponId, double longitude, double latitude)
    {
        this.userName = userName;
        this.weaponId = weaponId;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getUserName() {
        return userName;
    }

    public int getWeaponId() {
        return weaponId;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
}
