package domain.entity;

/**
 * Created with IntelliJ IDEA.
 * User: Aaron
 * Date: 13-10-31
 * Time: 下午9:02
 * To change this template use File | Settings | File Templates.
 */
public class Weapon extends Item {

    private WeaponTypes weaponType;//the type of the weapon
    private int power = 0;//how much damage can this weapon cause
    private int range = 0;//fire range of this weapon

    public Weapon(int weaponId, String weaponName, WeaponTypes weaponType, int range, int power)
    {
        super(weaponId, weaponName);
        this.weaponType = weaponType;
        this.range = range;
        this.power = power;
    }

    /**
     * the toString methods for deliver weapons
     * @return
     */
    public String toProfileString()
    {
        StringBuilder sbd = new StringBuilder();
        sbd.append(id).append("\t");
        sbd.append(name).append("\t");
        sbd.append(weaponType.name()).append("\t");
        sbd.append(range).append("\t");
        sbd.append(power);
        return sbd.toString();
    }

    public int getPower()
    {
        return power;
    }

    public int getRange()
    {
        return range;
    }
}
