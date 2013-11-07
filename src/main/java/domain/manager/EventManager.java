package domain.manager;

import domain.log.Logger;
import domain.proxy.CoordinatesProxy;
import domain.proxy.UserProxy;
import domain.proxy.WeaponProxy;

import java.util.HashMap;
import java.util.HashSet;

/**
 * this class is used for handling all kinds of events, breaking them into small units, and call the right proxy
 * to finish the job.
 * Author: zhangcen
 * Date: 13-11-1
 * Time: 上午12:05
 */
public class EventManager {

    private static EventManager manager = null;
    private EventManager(){}

    public static EventManager getManager()
    {
        if(manager == null)
        {
            manager = new EventManager();
        }
        return manager;
    }

    //business parts
    //proxies
    private CoordinatesProxy coordinatesProxy = null;
    private UserProxy userProxy = null;
    private WeaponProxy weaponProxy = null;
    //HashMaps
    private HashMap<String, HashMap<Integer, Integer>> userWeaponInventoryMap = null;
    private HashMap<String, HashSet<String>> coordinatesToUserMap = null;
    public void initialization()
    {
        Logger.getInstance().init();
        System.out.println("Current Log Level = " + Logger.getInstance().getPrintLevelName());
        Logger.getInstance().info("start init everything");
        //all proxies init
        coordinatesProxy = CoordinatesProxy.getCoordinatesProxyInstance();
        coordinatesProxy.init();
        userProxy = UserProxy.getUserProxyInstance();
        userProxy.init();
        weaponProxy = WeaponProxy.getWeaponProxyInstance();
        weaponProxy.init();
        Logger.getInstance().info("everything init finish");
        Logger.getInstance().mark();
    }

}
