package domain.manager;

import domain.entity.Coordinates;
import domain.entity.User;
import domain.log.Logger;
import domain.proxy.CoordinatesProxy;
import domain.proxy.UserProxy;
import domain.proxy.WeaponProxy;
import utils.NumericalUtils;

import java.util.*;

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
    private int numOfPeople = 0;
    private double stepSize = 0;
    private String numberPattern;
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
        setParameters();
        //all proxies init
        coordinatesProxy = CoordinatesProxy.getCoordinatesProxyInstance();
        coordinatesProxy.init();
        userProxy = UserProxy.getUserProxyInstance();
        userProxy.init();
        weaponProxy = WeaponProxy.getWeaponProxyInstance();
        weaponProxy.init();

        //all hash maps
        userWeaponInventoryMap = new HashMap<String, HashMap<Integer, Integer>>();
        coordinatesToUserMap = new HashMap<String, HashSet<String>>();

        Logger.getInstance().info("everything init finish");
        Logger.getInstance().mark();
    }

    private void setParameters()
    {
        this.numOfPeople = config.getNumOfPeople();
        this.stepSize = config.getStepSize();
        this.numberPattern = config.getNumberPattern();
    }

    public String reloadConfig()
    {
        String retString = config.reload();
        setParameters();
        return retString;
    }

    /*-------------------------------------create user operation-----------------------------------------------------*/
    /**
     * create a user and return his status
     * @param userName
     * @return
     */
    public String create(String userName)
    {
        if(userName == null || userName.length() < 1)
        {
            return "{status:fail,reason:user name is null or empty}";
        }
        if(userProxy.isUserExist(userName))
        {
            return "{status:success," + userProxy.existUserToJsonString(userName) + "}";
        }
        else
        {
            return "{status:success," + userProxy.createUser(userName) + "}";
        }
    }

    /*-------------------------------------user refresh operation-----------------------------------------------------*/
    public String refresh(String userName, double longitude, double latitude)
    {
        if(userName == null || userName.length() < 1)
        {
            return "{status:fail,reason:user is empty or null}";
        }
        if(!userProxy.isUserExist(userName))
        {
            return "{status:fail,reason:user " + userName + " does not exist}";
        }
        //remove old position information for this user
        String oldPositionKey = userProxy.getUserPositionKey(userName);
        String newPositionKey = String.valueOf(longitude) + "_" + String.valueOf(latitude);
        cleanPositionInformationForUser(userName, oldPositionKey);
        setNewPositionInformationForUser(userName, newPositionKey);
        //add new information for user instance
        if(!coordinatesProxy.isCoordinateExist(newPositionKey))
        {
            coordinatesProxy.createCoordinates(longitude,latitude);
        }
        Coordinates c = coordinatesProxy.getCoordinatesByName(newPositionKey);
        if(c != null)
        {
            userProxy.registerPosition(userName, c);
        }
        //find users nearby
        String[] userNameArray = findUsersNearby(longitude, latitude, userName);
        return "{status:success,"+userNameArrayToUserPositionString(userNameArray)+"}";
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
//        Logger.getInstance().debug("positionKey = " + positionKey + " and we are in set new position for user " + userName);
        coordinatesToUserMap.put(positionKey, userNameSet);
    }

    private String[] findUsersNearby(double longitude, double latitude,  String ownUserName)
    {
        int range = userProxy.getUserAttr1(ownUserName);
        int rangeSquare = range * range;
        String[] userNameArray = new String[numOfPeople];
        int index = 0;
        String positionKey;
        //TODO to optimize it in the future
        //process the same location
        positionKey = NumericalUtils.formatDecimal(numberPattern, longitude)+ "_" + NumericalUtils.formatDecimal(numberPattern, latitude);
        Logger.getInstance().debug("positionKey = " + positionKey);
        index = fillUserNameArrayByPositionKey(userNameArray, positionKey, index, ownUserName);
        if(index >= numOfPeople)
        {
            return userNameArray;
        }
        //start iteration
        //first we deal with the problem that one parameter = 0
        for(int k = 1; k < range; k++)
        {
            positionKey = NumericalUtils.formatDecimal(numberPattern, longitude)+ "_" + NumericalUtils.formatDecimal(numberPattern, latitude + stepSize * k);
            Logger.getInstance().debug("positionKey = " + positionKey);
            index = fillUserNameArrayByPositionKey(userNameArray, positionKey, index, ownUserName);
            if(index >= numOfPeople)
            {
                return userNameArray;
            }
            positionKey = NumericalUtils.formatDecimal(numberPattern, longitude)+ "_" + NumericalUtils.formatDecimal(numberPattern, latitude - stepSize * k);
            Logger.getInstance().debug("positionKey = " + positionKey);
            index = fillUserNameArrayByPositionKey(userNameArray, positionKey, index, ownUserName);
            if(index >= numOfPeople)
            {
                return userNameArray;
            }
            positionKey = NumericalUtils.formatDecimal(numberPattern, longitude + stepSize * k)+ "_" + NumericalUtils.formatDecimal(numberPattern, latitude);
            Logger.getInstance().debug("positionKey = " + positionKey);
            index = fillUserNameArrayByPositionKey(userNameArray, positionKey, index, ownUserName);
            if(index >= numOfPeople)
            {
                return userNameArray;
            }
            positionKey = NumericalUtils.formatDecimal(numberPattern, longitude - stepSize * k)+ "_" + NumericalUtils.formatDecimal(numberPattern, latitude);
            Logger.getInstance().debug("positionKey = " + positionKey);
            index = fillUserNameArrayByPositionKey(userNameArray, positionKey, index, ownUserName);
            if(index >= numOfPeople)
            {
                return userNameArray;
            }
        }
        // no parameter = 0
        for(int i = 1; i < range; i++)
        {
            for(int j = i; j < range ; j++)
            {
                if(i*i + j*j < rangeSquare) // a valid position
                {
                    positionKey = NumericalUtils.formatDecimal(numberPattern, longitude + stepSize * i)+ "_" + NumericalUtils.formatDecimal(numberPattern, latitude + stepSize * j);
                    Logger.getInstance().debug("positionKey = " + positionKey);
                    index = fillUserNameArrayByPositionKey(userNameArray, positionKey, index, ownUserName);
                    if(index >= numOfPeople)
                    {
                        return userNameArray;
                    }
                    positionKey = NumericalUtils.formatDecimal(numberPattern, longitude + stepSize * i)+ "_" + NumericalUtils.formatDecimal(numberPattern, latitude - stepSize * j);
                    Logger.getInstance().debug("positionKey = " + positionKey);
                    index = fillUserNameArrayByPositionKey(userNameArray, positionKey, index, ownUserName);
                    if(index >= numOfPeople)
                    {
                        return userNameArray;
                    }
                    positionKey = NumericalUtils.formatDecimal(numberPattern, longitude - stepSize * i)+ "_" + NumericalUtils.formatDecimal(numberPattern, latitude + stepSize * j);
                    Logger.getInstance().debug("positionKey = " + positionKey);
                    index = fillUserNameArrayByPositionKey(userNameArray, positionKey, index, ownUserName);
                    if(index >= numOfPeople)
                    {
                        return userNameArray;
                    }
                    positionKey = NumericalUtils.formatDecimal(numberPattern, longitude - stepSize * i)+ "_" + NumericalUtils.formatDecimal(numberPattern, latitude - stepSize * j);
                    Logger.getInstance().debug("positionKey = " + positionKey);
                    index = fillUserNameArrayByPositionKey(userNameArray, positionKey, index, ownUserName);
                    if(index >= numOfPeople)
                    {
                        return userNameArray;
                    }
                    if(i != j)
                    {
                        positionKey = NumericalUtils.formatDecimal(numberPattern, longitude + stepSize * j)+ "_" + NumericalUtils.formatDecimal(numberPattern, latitude + stepSize * i);
                        Logger.getInstance().debug("positionKey = " + positionKey);
                        index = fillUserNameArrayByPositionKey(userNameArray, positionKey, index, ownUserName);
                        if(index >= numOfPeople)
                        {
                            return userNameArray;
                        }
                        positionKey = NumericalUtils.formatDecimal(numberPattern, longitude + stepSize * j)+ "_" + NumericalUtils.formatDecimal(numberPattern, latitude - stepSize * i);
                        Logger.getInstance().debug("positionKey = " + positionKey);
                        index = fillUserNameArrayByPositionKey(userNameArray, positionKey, index, ownUserName);
                        if(index >= numOfPeople)
                        {
                            return userNameArray;
                        }
                        positionKey = NumericalUtils.formatDecimal(numberPattern, longitude - stepSize * j)+ "_" + NumericalUtils.formatDecimal(numberPattern, latitude + stepSize * i);
                        Logger.getInstance().debug("positionKey = " + positionKey);
                        index = fillUserNameArrayByPositionKey(userNameArray, positionKey, index, ownUserName);
                        if(index >= numOfPeople)
                        {
                            return userNameArray;
                        }
                        positionKey = NumericalUtils.formatDecimal(numberPattern, longitude - stepSize * j)+ "_" + NumericalUtils.formatDecimal(numberPattern, latitude - stepSize * i);
                        Logger.getInstance().debug("positionKey = " + positionKey);
                        index = fillUserNameArrayByPositionKey(userNameArray, positionKey, index, ownUserName);
                        if(index >= numOfPeople)
                        {
                            return userNameArray;
                        }
                    }
                }
            }
        }
        return userNameArray;
    }

    private int fillUserNameArrayByPositionKey(String[] userNameArray, String positionKey, int index, String ownUserName)
    {
        if(index >= numOfPeople)
        {
            return index;
        }
        Iterator<String> userNameIterator = coordinatesToUserMap.containsKey(positionKey) ? coordinatesToUserMap.get(positionKey).iterator() : null;
        if(userNameIterator != null)
        {
            while(userNameIterator.hasNext())
            {
                String userName = userNameIterator.next();
                if(userProxy.isUserAlive(userName) && !userName.equals(ownUserName))
                {
                    userNameArray[index] = userName;
                    index++;
                    if(index >= numOfPeople)
                    {
                        return index;
                    }
                }
            }
        }
        return index;
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
                sbd.append(userProxy.userToPositionString(userNameArray[i])).append(";");
            }
        }
        return sbd.deleteCharAt(sbd.length()-1).toString();
    }

    /*-----------------------------------------get weapon operation---------------------------------------------------*/
    public String getWeapon(String userName)
    {
        if(userName == null || userName.length() < 1)
        {
            return "{status:fail,reason:user name is null or empty}";
        }
        if(!userProxy.isUserExist(userName))
        {
            return "{status:fail,reason:user" + userName +" does not exist}";
        }
        if(!userProxy.isUserAlive(userName))
        {
            return "{status:fail,reason:user" + userName +" is already dead}";
        }
        if(userWeaponInventoryMap.containsKey(userName))
        {
            return "{status:fail,reason:user has got weapon today}";
        }
        int numOfWeapon = userProxy.getUserAttr2(userName);
        ArrayList<Integer> weaponList = getWeaponFromStock(numOfWeapon);
        String retString = registerWeaponForUser(userName, weaponList);
        if(retString.length() < 1)
        {
            return "{status:fail,reason:no weapon left in stock}";
        }
        return "{status:success," + retString +"}";
    }

    private ArrayList<Integer> getWeaponFromStock(int numOfWeapon)
    {
         return weaponProxy.deliverWeapon(numOfWeapon);
    }

    private String registerWeaponForUser(String userName, ArrayList<Integer> weaponList)
    {
        HashMap<Integer, Integer> weaponInventoryMap = new HashMap<Integer, Integer>();
        for(int i = 0, len = weaponList.size(); i < len; i++)
        {
            int weaponId = weaponList.get(i);
            int inventory = weaponInventoryMap.containsKey(weaponId) ? weaponInventoryMap.get(weaponId) + 1 : 1;
            weaponInventoryMap.put(weaponId, inventory);
        }
        userWeaponInventoryMap.put(userName, weaponInventoryMap);
        if(weaponInventoryMap.size() < 1)
        {
            return "";
        }
        StringBuilder sbd = new StringBuilder("weapons:");
        Iterator<Map.Entry<Integer, Integer>> weaponIterator = weaponInventoryMap.entrySet().iterator();
        while(weaponIterator.hasNext())
        {
            Map.Entry<Integer, Integer> entry = weaponIterator.next();
            sbd.append("[").append(weaponProxy.getWeaponProfileString(entry.getKey()));
            sbd.append("\t").append(entry.getValue()).append("]").append(";");
        }
        return sbd.deleteCharAt(sbd.length()-1).toString();
    }
}
