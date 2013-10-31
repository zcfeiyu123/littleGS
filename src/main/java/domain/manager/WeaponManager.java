package domain.manager;

import domain.entity.User;
import domain.entity.Weapon;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Aaron
 * Date: 13-11-1
 * Time: 上午12:01
 * To change this template use File | Settings | File Templates.
 */
public class WeaponManager {

    private static WeaponManager instance = null;
    private WeaponManager(){
        this.weaponHashMap = new HashMap<Integer, Weapon>();
        this.weaponInventory = new HashMap<Integer, Integer>();
    }
    private HashMap<Integer, Weapon> weaponHashMap = null; //store weaponId to its instance
    private HashMap<Integer, Integer> weaponInventory = null; //store weaponId to its inventory amount

    public static WeaponManager getInstance()
    {
        if(instance == null)
        {
            instance = new WeaponManager();
        }
        return instance;
    }

    public void initWeaponHashMap()
    {
        //TODO load weapons from database
    }

    public void deliveryWeapon(User user, int numberOfWeapons)
    {
        //TODO
    }

    public Weapon getWeaponById(int weaponId)
    {
        Weapon weapon = weaponHashMap.containsKey(weaponId) ? weaponHashMap.get(weaponId) : null;
        return weapon;
    }

}
