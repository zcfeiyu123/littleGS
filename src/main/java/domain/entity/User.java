package domain.entity;

import domain.manager.UserManager;
import domain.manager.WeaponManager;
import utils.SimpleLogger;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Aaron
 * Date: 13-10-31
 * Time: 下午9:00
 * To change this template use File | Settings | File Templates.
 */
public class User {

    private String name = null;
    private int HP = 0;
    private ArrayList<EventMessage> eventMessageArrayList = null;
    private HashMap<Integer, Weapon> weaponHashMap = null;
    private HashMap<Integer, Integer> weaponInventoryMap = null;

    /**
     * position infomation
     */
    private double longitude = 0;
    private double latitude = 0;

    /**
     * six basic attributes
     */
    private double atr1 = 0;
    private double atr2 = 0;
    private double atr3 = 0;
    private double atr4 = 0;
    private double atr5 = 0;
    private double atr6 = 0;

    private User(String name)
    {
        this.name = name;
        this.HP = 5;//TODO we must give each user her corresponding hp
        this.eventMessageArrayList = new ArrayList<EventMessage>();
        this.weaponHashMap = new HashMap<Integer, Weapon>();
        this.weaponInventoryMap = new HashMap<Integer, Integer>();
    }

    public static User getInstance(String name)
    {
        return new User(name);
    }

    /**
     *
     * @return
     */
    public String refresh(double longitude, double latitude)
    {
        //set new position information
        this.setLatitude(latitude);
        this.setLongitude(longitude);

        //register this information in UserManager
        UserManager.getInstance().refreshUserStatus(this);
        //TODO to be finished
        String nearbyUserNames = UserManager.getInstance().getNearbyUsers(longitude, latitude, 0);
        String response = "{status:success," + nearbyUserNames + "}";
        return response;
    }

    /*-------------------------weapon related----------------------*/
    public void registerWeapon(Weapon weapon)
    {
        int weaponId = weapon.getId();
        if(this.weaponHashMap.containsKey(weaponId))
        {
            int inventory = weaponInventoryMap.get(weaponId)+1;
            weaponInventoryMap.put(weaponId, inventory);
        }
        else
        {
            weaponHashMap.put(weaponId, weapon);
            weaponInventoryMap.put(weaponId, 1);
        }
    }

    public String getWeapon()
    {
        System.out.println("in getting weapon");
        WeaponManager.getInstance().deliveryWeapon(this, 0);
        return "get weapon";
        //TODO finish getting weapon
    }

    public String useWeapon(int weaponId, String targetUserNames)
    {
        ArrayList<User> targetUsers = UserManager.getInstance().getTargetUsers(targetUserNames);
        useWeapon(weaponId, targetUsers);

        return "use weapon";
    }

    public String useWeapon(int weaponId, double longitude, long latitude)
    {
        ArrayList<User> targetUsers = UserManager.getInstance().getTargetUsers(longitude, latitude, 0);
        useWeapon(weaponId, targetUsers);

        return "use weapon";
    }

    private void useWeapon(int weaponId, ArrayList<User> targetUsers)
    {
        if(!weaponHashMap.containsKey(weaponId))
        {
            SimpleLogger.getLogger().error("no weapon with id = " + weaponId + " in room");
            return ;
        }
        if(!weaponInventoryMap.containsKey(weaponId) || weaponInventoryMap.get(weaponId) < 1)
        {
            SimpleLogger.getLogger().error("no weapon with id = " + weaponId + " in stock");
            return ;
        }

        Weapon weapon = weaponHashMap.get(weaponId);
        weapon.fire(this, targetUsers);
        reduceWeaponInventory(weaponId, 1);
    }

    private void reduceWeaponInventory(int weaponId, int count)
    {
        if(!weaponInventoryMap.containsKey(weaponId) || weaponInventoryMap.get(weaponId) < 1)
        {
            SimpleLogger.getLogger().error("no weapon with id = " + weaponId + " in stock");
            return ;
        }
        int inventory = weaponInventoryMap.get(weaponId) - count;
        inventory = inventory < 0 ? 0 : inventory;
        weaponInventoryMap.put(weaponId, inventory);
    }

    /*-------------------------------events related------------------------------------------*/
    public void registerEvents(EventMessage eventMessage)
    {
        this.eventMessageArrayList.add(eventMessage);
    }

    public String publishEventMessages()
    {
        StringBuilder sbd = new StringBuilder();
        while(eventMessageArrayList.size() > 0)
        {
            sbd.append(eventMessageArrayList.get(0).toString());
            eventMessageArrayList.remove(0);
        }
        return sbd.toString();
    }

    /*----------------------------------basic information--------------------------------------*/
    public String getName()
    {
        return name;
    }

    public int getHP()
    {
        return HP;
    }

    public void calcDamage(int damage)
    {
        HP = damage > HP ? 0 : HP-damage;
    }

    public boolean isUserDead()
    {
        return HP > 0;
    }

    public double getLongitude()
    {
        return this.longitude;
    }

    private void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }

    public double getLatitude()
    {
        return this.latitude;
    }

    private void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

    /**
     * turn this user to a triple tuple String
     * @return
     */
    public String toTripleTupleString()
    {
        StringBuilder sbd = new StringBuilder();
        sbd.append("user").append(":");
        sbd.append(name).append("\t");
        sbd.append(longitude).append("\t");
        sbd.append(latitude);

        return sbd.toString();
    }
}
