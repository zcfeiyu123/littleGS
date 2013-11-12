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
        EventResultCode code = checkUserStatus(userName);
        switch (code)
        {
            case UserNameNullOrEmpty:
                return EventFailMessageBox.getFailMessageWithCode(code);
            case UserNotExist:
                return "{status:success," + userProxy.createUser(userName) + "}";
            default:
                return "{status:success," + userProxy.existUserToJsonString(userName) + "}";
        }
    }

    /*-------------------------------------user refresh operation-----------------------------------------------------*/
    public String refresh(String userName, String longitudeStr, String latitudeStr)
    {
        EventResultCode code = checkUserStatus(userName);
        if(code != EventResultCode.PASS)
        {
            return EventFailMessageBox.getFailMessageWithCode(code);
        }
        //find the corresponding coordinates using position information
        code = updateCoordinates(longitudeStr,latitudeStr);
        if(code != EventResultCode.PASS)
        {
            return EventFailMessageBox.getFailMessageWithCode(code);
        }
        //remove old position information for this user
        String oldPositionKey = userProxy.getUserPositionKey(userName);
        String newPositionKey = longitudeStr + "_" + latitudeStr;
        cleanPositionInformationForUser(userName, oldPositionKey);
        setNewPositionInformationForUser(userName, newPositionKey);
        //add new information for user instance
        Coordinates c = coordinatesProxy.getCoordinatesByName(newPositionKey);
        if(c != null)
        {
            userProxy.registerPosition(userName, c);
        }
        else
        {
            code = EventResultCode.CoordinatesNotExist;
            return EventFailMessageBox.getFailMessageWithCode(code);
        }
        //find users nearby
        int range = userProxy.getUserAttr1(userName);
        LinkedHashSet<String> userNameSet = findUsersNearby(c.getLongitude(), c.getLatitude(), userName, range, numOfPeople);
        if(userNameSet.size() > 1)//more than user himself
        {
            return "{status:success,"+userNameListToString(userNameSet)+"}";
        }
        else
        {
            return EventFailMessageBox.getFailMessageWithCode(EventResultCode.NoUserAround);
        }
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

    private LinkedHashSet<String> findUsersNearby(double longitude, double latitude,  String ownUserName, int range, int numOfPeople)
    {
        LinkedHashSet<String> neighborSet = new LinkedHashSet<String>();
        neighborSet.add(ownUserName);
        int sqrtRange = (int)Math.sqrt(range + 0.0);
        int longitudeOffset = 0;
        int latitudeOffset = 0;
        int size = 1;
        while(longitudeOffset < sqrtRange && latitudeOffset < sqrtRange && size < numOfPeople)
        {
            size = fillNeighborSet(numOfPeople,longitude,latitude,longitudeOffset,latitudeOffset,neighborSet);
            size = fillNeighborSet(numOfPeople,longitude,latitude,0-longitudeOffset,latitudeOffset,neighborSet);
            size = fillNeighborSet(numOfPeople,longitude,latitude,longitudeOffset,0-latitudeOffset,neighborSet);
            size = fillNeighborSet(numOfPeople,longitude,latitude,0-longitudeOffset,0-latitudeOffset,neighborSet);
            if(longitudeOffset != latitudeOffset)//swap longitude and latitude
            {
                size = fillNeighborSet(numOfPeople,longitude,latitude,latitudeOffset,longitudeOffset,neighborSet);
                size = fillNeighborSet(numOfPeople,longitude,latitude,0-latitudeOffset,longitudeOffset,neighborSet);
                size = fillNeighborSet(numOfPeople,longitude,latitude,latitudeOffset,0-longitudeOffset,neighborSet);
                size = fillNeighborSet(numOfPeople,longitude,latitude,0-latitudeOffset,0-longitudeOffset,neighborSet);
            }
            if(longitudeOffset < sqrtRange)
            {
                longitudeOffset++;
            }
            if(longitudeOffset > sqrtRange)
            {
                latitudeOffset++;
                longitudeOffset = latitudeOffset;
            }
        }
        return neighborSet;
    }

    private int fillNeighborSet(int numOfNeighborLimit, double longitude, double latitude, int longitudeOffset, int latitudeOffset, LinkedHashSet<String> neighborSet)
    {
        String positionKey = NumericalUtils.formatDecimal(numberPattern, longitude + stepSize * longitudeOffset)+ "_" + NumericalUtils.formatDecimal(numberPattern, latitude + stepSize * latitudeOffset);
        return fillNeighborSetByPositionKey(numOfNeighborLimit,positionKey,neighborSet);
    }

    private int fillNeighborSetByPositionKey(int numOfNeighborLimit, String positionKey, LinkedHashSet<String> neighborSet)
    {
        int size = neighborSet.size();
        Iterator<String> userNameIterator = coordinatesToUserMap.containsKey(positionKey) ? coordinatesToUserMap.get(positionKey).iterator() : null;
        if(userNameIterator != null)
        {
            while(userNameIterator.hasNext() && size < numOfNeighborLimit)
            {
                String userName = userNameIterator.next();
                if(userProxy.isUserAlive(userName))
                {
                    neighborSet.add(userName);
                    size++;
                }
            }
        }
        return size;
    }

    private String userNameListToString(LinkedHashSet<String> userNameLinkedHashSet)
    {
        StringBuilder sbd = new StringBuilder("users:");
        Iterator<String> userIterator = userNameLinkedHashSet.iterator();
        userIterator.next();//the first one is the user himself
        while(userIterator.hasNext())
        {
            sbd.append(userProxy.userToPositionString(userIterator.next())).append(";");
        }
        return sbd.deleteCharAt(sbd.length()-1).toString();
    }

    /*-----------------------------------------get weapon operation---------------------------------------------------*/
    public String getWeapon(String userName)
    {
        EventResultCode code = checkUserStatus(userName);
        if(code != EventResultCode.PASS)
        {
            return EventFailMessageBox.getFailMessageWithCode(code);
        }
        if(isUserAssignedWeapon(userName))
        {
            return EventFailMessageBox.getFailMessageWithCode(EventResultCode.UserAssignedWeapon);
        }
        int numOfWeapon = userProxy.getUserAttr2(userName);
        ArrayList<Integer> weaponList = getWeaponFromStock(numOfWeapon);
        if(weaponList.size() < 1)
        {
            return EventFailMessageBox.getFailMessageWithCode(EventResultCode.NoWeaponLeft);
        }
        return "{status:success," + registerWeaponForUser(userName, weaponList) + "}";
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
    public String useInstantWeapon(String userName, String targetUsers, String weaponIDStr)
    {
        //test parameters
        EventResultCode code = checkUserStatus(userName);
        if(code != EventResultCode.PASS)
        {
            return EventFailMessageBox.getFailMessageWithCode(code);
        }
        if(!isUserAssignedWeapon(userName))
        {
            return EventFailMessageBox.getFailMessageWithCode(EventResultCode.UserNotAssignedWeapon);
        }
        code = checkWeaponStatus(weaponIDStr);
        if(code != EventResultCode.PASS)
        {
            return EventFailMessageBox.getFailMessageWithCode(code);
        }
        int weaponId = Integer.parseInt(weaponIDStr);
        if(!userWeaponInventoryMap.get(userName).containsKey(weaponId))
        {
            return EventFailMessageBox.getFailMessageWithCode(EventResultCode.UserNotPossessWeapon);
        }
        code = checkTargetUserStatus(targetUsers);
        if(code != EventResultCode.PASS)
        {
            return EventFailMessageBox.getFailMessageWithCode(code);
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
    public String useDelayedWeapon(String userName, String longitudeStr, String latitudeStr, String weaponIDStr, String launchTimeStr)
    {
        EventResultCode code = checkUserStatus(userName);
        if(code != EventResultCode.PASS)
        {
            return EventFailMessageBox.getFailMessageWithCode(code);
        }
        if(!isUserAssignedWeapon(userName))
        {
            return EventFailMessageBox.getFailMessageWithCode(EventResultCode.UserNotAssignedWeapon);
        }
        code = checkWeaponStatus(weaponIDStr);
        if(code != EventResultCode.PASS)
        {
            return EventFailMessageBox.getFailMessageWithCode(code);
        }
        int weaponId = Integer.parseInt(weaponIDStr);
        if(!userWeaponInventoryMap.get(userName).containsKey(weaponId))
        {
            return EventFailMessageBox.getFailMessageWithCode(EventResultCode.UserNotPossessWeapon);
        }
        code = updateCoordinates(longitudeStr, latitudeStr);
        if(code != EventResultCode.PASS)
        {
            return EventFailMessageBox.getFailMessageWithCode(code);
        }
        int launchIndex = getLaunchIndex(launchTimeStr);
        if(launchIndex < 0)
        {
            return EventFailMessageBox.getFailMessageWithCode(EventResultCode.LaunchTimeFormatWrong);
        }
        Coordinates coordinates = coordinatesProxy.getCoordinatesByName(longitudeStr+"_"+latitudeStr);
        return setupTimedWeapon(userName, weaponId, coordinates, launchIndex);
    }

    private String setupTimedWeapon(String userName, int weaponId,Coordinates coordinates, int index)
    {
        TimedWeaponEntity entity = new TimedWeaponEntity(userName, weaponId,coordinates);
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
        LinkedHashSet<String> userNameSet = findUsersNearby(entity.getLongitude(),entity.getLatitude(),entity.getUserName(),weaponProxy.getWeaponRange(entity.getWeaponId()), numOfPeople);
        if(userNameSet.size() > 1)//more than user himself
        {
            Iterator<String> userIterator = userNameSet.iterator();
            userIterator.next();
            while(userIterator.hasNext())
            {
                String targetUserName = userIterator.next();
                if(userProxy.isUserAlive(targetUserName))
                {
                    targetCount++;
                    totalDamage += damage;
                    calcDamageForUser(targetUserName, damage);
                    registerMessageBoxForTargetedUser(entity.getUserName(), targetUserName, weaponName, damage);
                }
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

   /*-----------------------------------------publish events----------------------------------------------------------*/
    public String publishMessages(String userName)
    {
        EventResultCode code = checkUserStatus(userName);
        if(code == EventResultCode.PASS || code == EventResultCode.UserNotAlive)
        {
            String messages = getMessageForUser(userName);
            if(messages.length() > 1)
            {
                return EventFailMessageBox.getFailMessageWithCode(EventResultCode.NoUnpublishedMessage);
            }
            else
            {
                return "{status:success," + messages + "}";
            }
        }
        else
        {
            return EventFailMessageBox.getFailMessageWithCode(code);
        }
    }

    private String getMessageForUser(String userName)
    {
        if(!this.userMessageBoxMap.containsKey(userName))
        {
            return "";
        }
        ArrayList<MessageBox> messageBoxArrayList = userMessageBoxMap.get(userName);
        StringBuilder sbd = new StringBuilder();
        for(int i = 0, len = messageBoxArrayList.size(); i < len; i++)
        {
            if(!messageBoxArrayList.get(i).isPublished())
            {
                sbd.append(messageBoxArrayList.get(i).publish()).append(";");
            }
        }
        //clean message
        if(userProxy.isUserAlive(userName))
        {
            cleanMessages(messageBoxArrayList);
        }
        else
        {
            //this user will not get any new message
            userMessageBoxMap.remove(userName);
        }
        if(sbd.length() > 1)
        {
            return "messages:" + sbd.deleteCharAt(sbd.length()-1).toString();
        }
        return "";
    }
    private void cleanMessages(ArrayList<MessageBox> messageBoxArrayList)
    {
        for(int len = messageBoxArrayList.size(), i = len-1; i > -1; i--)
        {
            if(messageBoxArrayList.get(i).isPublished())
            {
                messageBoxArrayList.remove(i);
            }
        }
    }

    /*---------------------------------------------utility functions--------------------------------------------------*/
    private EventResultCode checkUserStatus(String userName)
    {
        if(StringUtils.isStringNullOrEmpty(userName))
        {
            return EventResultCode.UserNameNullOrEmpty;
        }
        else if(!userProxy.isUserExist(userName))
        {
            return EventResultCode.UserNotExist;
        }
        else if(!userProxy.isUserAlive(userName))
        {
            return EventResultCode.UserNotAlive;
        }
        return EventResultCode.PASS;
    }

    private boolean isUserAssignedWeapon(String userName)
    {
        return userWeaponInventoryMap.containsKey(userName);
    }

    private EventResultCode updateCoordinates(String longitudeStr, String latitudeStr)
    {
        //if position exist, we return pass
        String positionKey = longitudeStr + "_" + latitudeStr;
        if(coordinatesProxy.isCoordinateExist(positionKey))
        {
            return EventResultCode.PASS;
        }
        //if this position does not exist, we need to produce a new one
        double longitude = NumericalUtils.toDouble(longitudeStr);
        if(Double.isNaN(longitude))
        {
            return EventResultCode.LongitudeFormatWrong;
        }
        double latitude = NumericalUtils.toDouble(latitudeStr);
        if(Double.isNaN(latitude))
        {
            return EventResultCode.LatitudeFormatWrong;
        }
        coordinatesProxy.createCoordinates(longitude,latitude);
        return EventResultCode.PASS;
    }

    private EventResultCode checkWeaponStatus(String weaponIdStr)
    {
        if(StringUtils.isStringNullOrEmpty(weaponIdStr))
        {
            return EventResultCode.WeaponIdFormatWrong;
        }
        int weaponId = NumericalUtils.toInteger(weaponIdStr);
        if(weaponId < 0)
        {
            return EventResultCode.WeaponIdFormatWrong;
        }
        else if(!weaponProxy.isWeaponExist(weaponId))
        {
            return EventResultCode.WeaponNotExist;
        }
        return EventResultCode.PASS;
    }

    private EventResultCode checkTargetUserStatus(String targetUserString)
    {
        if(StringUtils.isStringNullOrEmpty(targetUserString))
        {
            return EventResultCode.TargetUserNullOrEmpty;
        }
        return EventResultCode.PASS;
    }

}
