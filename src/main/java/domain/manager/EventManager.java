package domain.manager;

import domain.entity.Coordinates;
import domain.entity.User;
import domain.log.Logger;
import domain.proxy.CoordinatesProxy;
import domain.proxy.UserProxy;
import domain.proxy.WeaponProxy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

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
    //config
    private EventManagerConfig config = null;
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
        //config
        config = EventManagerConfig.getInstance();
        config.load();
        //all proxies init
        CoordinatesProxy.getCoordinatesProxyInstance().init();
        UserProxy.getUserProxyInstance().init();
        WeaponProxy.getWeaponProxyInstance().init();

        //all hash maps
        userWeaponInventoryMap = new HashMap<String, HashMap<Integer, Integer>>();
        coordinatesToUserMap = new HashMap<String, HashSet<String>>();

        Logger.getInstance().info("everything init finish");
        Logger.getInstance().mark();
    }

    /*-------------------------------------create user operation-----------------------------------------------------*/
    /**
     * create a user and return his status
     * @param userName
     * @return
     */
    public String create(String userName)
    {
        if(UserProxy.getUserProxyInstance().isUserExist(userName))
        {
            return UserProxy.getUserProxyInstance().existUserToJsonString(userName);
        }
        else
        {
            return UserProxy.getUserProxyInstance().createUser(userName);
        }
    }

    /*-------------------------------------user refresh operation-----------------------------------------------------*/
    public String refresh(String userName, double longitude, double latitude)
    {
        Logger.getInstance().debug("user name = " + userName);
        UserProxy.getUserProxyInstance().printAllUser();
        Logger.getInstance().mark();
        if(UserProxy.getUserProxyInstance().isUserExist(userName))
        {
            return "{status:fail,reason:user " + userName + " does not exist}";
        }
        //remove old position information for this user
        String oldPositionKey = UserProxy.getUserProxyInstance().getUserPositionKey(userName);
        String newPositionKey = String.valueOf(longitude) + "_" + String.valueOf(latitude);
        if(oldPositionKey.length() > 0 && !oldPositionKey.equals(newPositionKey))
        {
            //which means it is not the first time the user refresh, so we have to remove his old position
            cleanPositionInformationForUser(userName, oldPositionKey);
            setNewPositionInformationForUser(userName, newPositionKey);
        }
        //add new information for user instance
        if(!coordinatesProxy.isCoordinateExist(newPositionKey))
        {
            coordinatesProxy.createCoordinates(longitude,latitude);
        }
        Coordinates c = coordinatesProxy.getCoordinatesByName(newPositionKey);
        UserProxy.getUserProxyInstance().registerPosition(userName, c);
        //find users nearby
        String[] userNameArray = findUsersNearby(newPositionKey);
        return "{status:succeed,"+userNameArrayToUserPositionString(userNameArray)+"}";
    }

    private void cleanPositionInformationForUser(String userName, String oldPositionKey)
    {
        HashSet<String> userNameSet = coordinatesToUserMap.containsKey(oldPositionKey) ? coordinatesToUserMap.get(oldPositionKey) : null;
        if(userNameSet == null)
        {
            return;
        }
        userNameSet.remove(userName);
        coordinatesToUserMap.put(oldPositionKey, userNameSet);
    }

    private void setNewPositionInformationForUser(String userName, String positionKey)
    {
        HashSet<String> userNameSet = coordinatesToUserMap.containsKey(positionKey) ? coordinatesToUserMap.get(positionKey) : new HashSet<String>();
        userNameSet.add(userName);
        coordinatesToUserMap.put(positionKey, userNameSet);
    }

    private String[] findUsersNearby(String positionKey)
    {
        String[] userNameArray = new String[config.getNumOfPeople()];
        Iterator<String> userNameIterator = coordinatesToUserMap.get(positionKey).iterator();
        int index = 0;
        while(userNameIterator.hasNext() && index < userNameArray.length)
        {
            String userName = userNameIterator.next();
            if(UserProxy.getUserProxyInstance().isUserAlive(userName))
            {
                userNameArray[index] = userName;
                index++;
            }
        }

        return userNameArray;
    }

    private String userNameArrayToUserPositionString(String[] userNameArray)
    {
        if(userNameArray[0] == null)//could not find any user around
        {
            return "";
        }
        StringBuilder sbd = new StringBuilder("users:");
        for(int i = 0, length = userNameArray.length; i < length; i++)
        {
            if(userNameArray[i] != null)
            {
                sbd.append(UserProxy.getUserProxyInstance().userToPositionString(userNameArray[i])).append("\3");
            }
        }
        return sbd.deleteCharAt(sbd.length()-1).toString();
    }
}
