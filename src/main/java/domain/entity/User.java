package domain.entity;

import domain.manager.UserManager;
import domain.manager.WeaponManager;
import utils.SimpleLogger;

import java.util.*;

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

    /**
     * position infomation
     */
    private Coordinates coordinates = null;

    /**
     * six basic attributes
     */
    private double atr1 = 0;
    private double atr2 = 0;
    private double atr3 = 0;
    private double atr4 = 0;
    private double atr5 = 0;
    private double atr6 = 0;

    /**
     * weapon related parameters
     */
    private HashMap<Integer, Weapon> weaponHashMap = null;
    private HashMap<Integer, Integer> weaponInventoryMap = null;
    private boolean weaponAssigned = false;
    /**
     * events related parameters
     */
    private ArrayList<EventMessage> eventMessageArrayList = null;

    private User(String name)
    {
        this.name = name;
        this.HP = 5;//TODO we must give each user her corresponding hp
        this.weaponAssigned = false;
        this.eventMessageArrayList = new ArrayList<EventMessage>();
        this.weaponHashMap = new HashMap<Integer, Weapon>();
        this.weaponInventoryMap = new HashMap<Integer, Integer>();
    }
    /*---------------------------------------------create user operation----------------------------------------------*/
    public static User createUser(String name)
    {
        return new User(name);
    }

    /*-------------------------------------------refresh user status operation----------------------------------------*/
    /**
     * for this particular user instance, she can not obtain the information of any other users, so the only operation
     * for her is updating her location status, the left parts of update information is left to UserManager
     * @param coordinates
     * @return
     */
    public void refresh(Coordinates coordinates)
    {
        this.registerCoordinates(coordinates);
    }

    /*--------------------------------------------get weapon operation------------------------------------------------*/

    public String getWeapon(ArrayList<Integer> weaponIdList)
    {
        for(int i = 0 ,len = weaponIdList.size(); i < len; i++)
        {
            this.registerWeapon(weaponIdList.get(i));
        }

        this.weaponAssigned = true;

        return this.weaponToString();
    }

    private void registerWeapon(Integer weaponId)
    {
        int inventory = weaponInventoryMap.containsKey(weaponId) ? weaponInventoryMap.get(weaponId) + 1 : 1;
        weaponInventoryMap.put(weaponId, inventory);
    }

    private String weaponToString()
    {
        StringBuilder sbd = new StringBuilder();
        Iterator<Map.Entry<Integer, Integer>> weaponIter = weaponInventoryMap.entrySet().iterator();
        while(weaponIter.hasNext())
        {
            Map.Entry<Integer, Integer> entry = weaponIter.next();
            sbd.append("[").append(entry.getKey()).append("\t").append(entry.getValue()).append("]");
        }
        return sbd.toString();
    }

    /*--------------------------------------------use weapon operation------------------------------------------------*/

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

    /*-----------------------------------------------events related---------------------------------------------------*/
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

    /*---------------------------------coordinates related methods----------------------------------------------------*/
    public Coordinates getCoordinates()
    {
        return this.coordinates;
    }

    public void registerCoordinates(Coordinates coordinates)
    {
        this.coordinates = coordinates;
    }

    /*----------------------------------getters and setters, status checker-------------------------------------------*/
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

    public boolean isWeaponAssigned()
    {
        return this.weaponAssigned;
    }

    /*------------------------------------all kinds of toString methods-----------------------------------------------*/
    /**
     * turn this user to a triple tuple String
     * @return
     */
    public String toTripleTupleString()
    {
        StringBuilder sbd = new StringBuilder();
        sbd.append(name).append("\t");
        sbd.append(coordinates.getLongitude()).append("\t");
        sbd.append(coordinates.getLatitude());

        return sbd.toString();
    }

    /**
     * turn the basic information of the user into a json style string
     * @return
     */
    public String toJsonString()
    {
        StringBuilder sbd = new StringBuilder("userName:").append(name);
        sbd.append(",").append("hp:").append(HP);
        sbd.append(",").append("atr1:").append(atr1);
        sbd.append(",").append("atr2:").append(atr2);
        sbd.append(",").append("atr3:").append(atr3);
        sbd.append(",").append("atr4:").append(atr4);
        sbd.append(",").append("atr5:").append(atr5);
        sbd.append(",").append("atr6:").append(atr6);

        return sbd.toString();
    }
}
