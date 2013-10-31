package domain.entity;

import domain.manager.WeaponManager;
import domain.proxy.WeaponProxy;

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
    private ArrayList<EventMessage> eventMessageArrayList = null;
    private HashMap<String, Weapon> weaponHashMap = null;

    private User(String name)
    {
        this.name = null;
        this.eventMessageArrayList = new ArrayList<EventMessage>();
        this.weaponHashMap = new HashMap<String, Weapon>();
    }

    public static User getInstance(String name)
    {
        return new User(name);
    }

    public void refresh()
    {
        System.out.println("in fresh");
            //TODO finish refresh
    }

    public void getWeapon()
    {
        System.out.println("in getting weapon");
        WeaponManager.getInstance().deliveryWeapon(this, 0);
        //TODO finish getting weapon
    }

    public void useWeapon(int weaponId, ArrayList<User> targetUsers)
    {
        WeaponProxy.getInstance().openFire(this, targetUsers, 0);
        System.out.println("in using weapon");
        //TODO
    }

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

    public String getName()
    {
        return name;
    }
}
