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
    private Coordinates coordinates;

    public TimedWeaponEntity(String userName, int weaponId, Coordinates coordinates)
    {
        this.userName = userName;
        this.weaponId = weaponId;
        this.coordinates = coordinates;
    }

    public String getUserName() {
        return userName;
    }

    public int getWeaponId() {
        return weaponId;
    }

    public double getLongitude() {
        return coordinates.getLongitude();
    }

    public double getLatitude() {
        return coordinates.getLatitude();
    }
}
