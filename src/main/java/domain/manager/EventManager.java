package domain.manager;

import domain.entity.Coordinates;
import domain.entity.MessageBox;
import domain.entity.TimedWeaponEntity;
import domain.entity.User;
import domain.log.Logger;
import domain.proxy.CoordinatesProxy;
import domain.proxy.UserProxy;
import domain.proxy.WeaponProxy;
import utils.NumericalUtils;
import utils.StringUtils;

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
    private HashMap<String, ArrayList<MessageBox>> userMessageBoxMap = null;
    //timed weapon events list
    private int minutesOfOneDay = 1440;
    private ArrayList<TimedWeaponEntity>[] timedWeaponTaskArrayList;
    //initialization method
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
        userMessageBoxMap = new HashMap<String, ArrayList<MessageBox>>();
        //timed task list
        this.timedWeaponTaskArrayList = new ArrayList[minutesOfOneDay];
        for(int i = 0; i < minutesOfOneDay; i++)
        {
            timedWeaponTaskArrayList[i] = new ArrayList<TimedWeaponEntity>();
        }
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
        int range = userProxy.getUserAttr1(userName);
        String[] userNameArray = findUsersNearby(longitude, latitude, userName, range);
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

    private String[] findUsersNearby(double longitude, double latitude,  String ownUserName, int range)
    {
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

    /*-----------------------------------------use instant weapon-----------------------------------------------------*/
    public String useInstantWeapon(String userName, String targetUsers, String weaponID)
    {
        //test parameters
        if(userName == null || userName.length() < 1)
        {
            return "{status:fail,reason:user name is null or empty}";
        }
        if(!userProxy.isUserExist(userName))
        {
            return "{status:fail,reason:user " + userName +" does not exist}";
        }
        if(!userProxy.isUserAlive(userName))
        {
            return "{status:fail,reason:user " + userName +" is already dead}";
        }
        if(!userWeaponInventoryMap.containsKey(userName))
        {
            return "{status:fail,reason:user " + userName + " has not got weapon}";
        }

        if(weaponID == null || weaponID.length() < 1)
        {
            return "{status:fail,reason:weapon id is null or empty}";
        }
        int weaponId;
        try{
            weaponId = Integer.parseInt(weaponID);
        }catch (Exception e)
        {
            return "{status:fail,reason:weapon id conversion fail}";
        }
        if(!weaponProxy.isWeaponExist(weaponId))
        {
            return "{status:fail,reason:weapon" + weaponID + " does not exist}";
        }
        if(!userWeaponInventoryMap.get(userName).containsKey(weaponId))
        {
            return "{status:fail,reason:user " + userName + " does not possess weapon " + weaponId + " }";
        }

        if(targetUsers == null || targetUsers.length() < 1)
        {
            return "{status:fail,reason:target user is null or empty}";
        }

        //after detection, we start to process
        //we first reduce weapon inventory from user map

        int damage = weaponProxy.getDamage(weaponId);
        String weaponName = weaponProxy.getWeaponName(weaponId);
        int totalDamage = 0;
        int targetCount = 0;
        //TODO we only calculate damage here, the events procedure should be revised later
        String[] targetUserArray = StringUtils.splitStr(targetUsers,',');
        for(int i = 0; i < targetUserArray.length; i++)
        {
            if(userProxy.isUserAlive(targetUserArray[i]))
            {
                targetCount++;
                totalDamage += damage;
                calcDamageForUser(targetUserArray[i], damage);
                registerMessageBoxForTargetedUser(userName, targetUserArray[i], weaponName, damage);
            }
        }
        return String.format("{status:success,targetCount:%d,targetDamage:%d}",targetCount,totalDamage);
    }

    private void calcDamageForUser(String userName, int damage)
    {
        userProxy.calcDamage(userName, damage);
    }

    /**
     * register message for users being attacked
     * @param userName who launched the attack
     * @param targetUserName who is attacked
     * @param weaponName use what kind of weapon
     * @param damage how much damage caused
     */
    private void registerMessageBoxForTargetedUser(String userName, String targetUserName, String weaponName, int damage)
    {
        String message = String.format("[you are attacked by %s using %s, lost %d HP]", userName,weaponName,damage);
        MessageBox messageBox = new MessageBox(message);
        ArrayList<MessageBox> messageBoxArrayList = userMessageBoxMap.containsKey(targetUserName)?userMessageBoxMap.get(targetUserName):new ArrayList<MessageBox>();
        messageBoxArrayList.add(messageBox);
        userMessageBoxMap.put(targetUserName,messageBoxArrayList);
    }

    /*-------------------------------------------timed weapon task----------------------------------------------------*/
    public String useDelayedWeapon(String userName, String longitudeStr, String latitudeStr, String weaponID, String launchTimeStr)
    {
        //test parameters
        if(userName == null || userName.length() < 1)
        {
            return "{status:fail,reason:user name is null or empty}";
        }
        if(!userProxy.isUserExist(userName))
        {
            return "{status:fail,reason:user " + userName +" does not exist}";
        }
        if(!userProxy.isUserAlive(userName))
        {
            return "{status:fail,reason:user " + userName +" is already dead}";
        }
        if(!userWeaponInventoryMap.containsKey(userName))
        {
            return "{status:fail,reason:user " + userName + " has not got weapon}";
        }

        if(weaponID == null || weaponID.length() < 1)
        {
            return "{status:fail,reason:weapon id is null or empty}";
        }
        int weaponId;
        double longitude, latitude;
        try{
            weaponId = Integer.parseInt(weaponID);
            longitude = Double.parseDouble(longitudeStr);
            latitude = Double.parseDouble(latitudeStr);
        }catch (Exception e)
        {
            return "{status:fail,reason:data conversion fail}";
        }
        if(!weaponProxy.isWeaponExist(weaponId))
        {
            return "{status:fail,reason:weapon" + weaponID + " does not exist}";
        }
        if(!userWeaponInventoryMap.get(userName).containsKey(weaponId))
        {
            return "{status:fail,reason:user " + userName + " does not possess weapon " + weaponId + " }";
        }

        int launchIndex = getLaunchIndex(launchTimeStr);
        if(launchIndex < 0)
        {
            return "{status:fail,reason:launch time is not correctly set}";
        }
        return setupTimedWeapon(userName, weaponId, longitude, latitude, launchIndex);
    }

    private String setupTimedWeapon(String userName, int weaponId, double longitude, double latitude, int index)
    {
        TimedWeaponEntity entity = new TimedWeaponEntity(userName, weaponId, longitude, latitude);
        timedWeaponTaskArrayList[index].add(entity);
        return "{status:success}";
    }

    private int getLaunchIndex(String launchTimeStr)
    {
        String[] timeStr = StringUtils.splitStr(launchTimeStr,':');
        if(timeStr.length != 2)
        {
            return -1;
        }
        try{
            int index = Integer.parseInt(timeStr[0]) * 60 + Integer.parseInt(timeStr[1]);
            return index;
        }catch (Exception e)
        {
            return -1;
        }

    }

    public void runTimedWeaponTask(int index)
    {
        ArrayList<TimedWeaponEntity> timedWeaponEntityArrayList = timedWeaponTaskArrayList[index];
        for(int i = 0, len = timedWeaponEntityArrayList.size(); i < len; i++)
        {
            runOneTimedTask(timedWeaponEntityArrayList.get(i));
        }
    }

    private void runOneTimedTask(TimedWeaponEntity entity)
    {
        int targetCount = 0;
        int totalDamage = 0;
        int damage = weaponProxy.getDamage(entity.getWeaponId());
        String weaponName = weaponProxy.getWeaponName(entity.getWeaponId());
        String[] usersNearBy = findUsersNearby(entity.getLongitude(), entity.getLatitude(),entity.getUserName(),weaponProxy.getWeaponRange(entity.getWeaponId()));
        for(int i = 0; i < usersNearBy.length; i++)
        {
            String targetUserName = usersNearBy[i];
            if(userProxy.isUserAlive(targetUserName))
            {
                targetCount++;
                totalDamage += damage;
                calcDamageForUser(targetUserName, damage);
                registerMessageBoxForTargetedUser(entity.getUserName(), targetUserName, weaponName, damage);
            }
        }

        String message = String.format("[you attack %d users with weapon %s, cause total damage %d]", targetCount, weaponName, totalDamage);
        registerMessageBoxForOwnUser(message, entity.getUserName());
    }

   private void registerMessageBoxForOwnUser(String message, String ownUser)
   {
       MessageBox messageBox = new MessageBox(message);
       ArrayList<MessageBox> messageBoxArrayList = userMessageBoxMap.containsKey(ownUser)?userMessageBoxMap.get(ownUser):new ArrayList<MessageBox>();
       messageBoxArrayList.add(messageBox);
       userMessageBoxMap.put(ownUser,messageBoxArrayList);
   }
}
