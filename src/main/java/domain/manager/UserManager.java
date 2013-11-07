package domain.manager;

/**
 * Created with IntelliJ IDEA.
 * User: zhangcen@youku.com
 * Date: 13-11-1
 * Time: 上午10:09
 * To change this template use File | Settings | File Templates.
 */

import domain.config.UserConfig;
import domain.entity.Coordinates;
import domain.entity.User;
import utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * this class keeps all the user instances in a hash map
 * also response for keep every user associate with her location
 */
public class UserManager {

    private static UserManager instance = null;

    private HashMap<String, User> userMap = null;
    private HashMap<String, User> aliveUserMap = null;
    private HashMap<String, User> deadUserMap = null;
    //TODO location to user map should be a position to multi user, the design below MUST be revised!!!!!
    private HashMap<Coordinates, HashMap<String, User>> locationUserMap = null;

    private UserManager(){
        this.userMap = new HashMap<String, User>();
        this.aliveUserMap = new HashMap<String, User>();
        this.deadUserMap = new HashMap<String, User>();

        //location related information
        locationUserMap = new HashMap<Coordinates, HashMap<String, User>>();
    }

    public static UserManager getInstance()
    {
        if(instance == null)
        {
            instance = new UserManager();
        }
        return instance;
    }
    /*---------------------------------------methods for creating a new user-------------------------------------------*/
    /**
     * create a user instance
     * @param userName
     * @return
     */
    public String createUser(String userName)
    {
        //if the user already exist, we return her basic status
        if(userMap.containsKey(userName))
        {
            return userMap.get(userName).toJsonString();
        }
        //we have to create a new user using given name and register her to all the map
        User user = User.createUser(userName);
        registerUser(user);
        registerAliveUser(user);// a new user must be an alive user
        return user.toJsonString();
    }
    /**
     * register the user to all user HashMap
     * @param user
     */
    private void registerUser(User user)
    {
        if(user == null)
        {
            return ;
        }
        this.userMap.put(user.getName(), user);
    }

    /**
     * register the user to all alive user HashMap
     * @param user
     */
    private void registerAliveUser(User user)
    {
        if(user == null)
        {
            return ;
        }
        this.aliveUserMap.put(user.getName(), user);
    }

    /*-------------------------------------------refresh user operation methods---------------------------------------*/
    public String refresh(String userName, double longitude, double latitude)
    {
        User user = getExistUser(userName);
        //first, the user refresh her location status and at this time, we have update the information in locationMap
        Coordinates oldCoordinates = user.getCoordinates();
        Coordinates newCoordinates = CoordinateManager.getInstance().getCoordinates(longitude, latitude);
        user.refresh(newCoordinates);
        /*then we update the nearby users of this particular user, we do not register them to this user, but keep all
         * the information in UserManager
         */
        return this.refreshUserStatus(user, oldCoordinates);
    }
    /**
     * refresh user status
     * @param user
     */
    private String refreshUserStatus(User user, Coordinates oldCoordinates)
    {
        // this user updates her location status
        if(oldCoordinates != null)
        {
            this.removeRegisteredUserByCoordinate(oldCoordinates, user.getName());
        }
        this.registerUserToCoordinates(user);
        // what we return is users nearby
        return refreshUsersNearby(user);
    }

    private void removeRegisteredUserByCoordinate(Coordinates coordinates, String userName)
    {
        if(coordinates == null)
        {
            return;
        }
        HashMap<String, User> userMap = locationUserMap.get(coordinates);
        if(userMap.containsKey(userName))
        {
            userMap.remove(userName);
        }
    }

    private void registerUserToCoordinates(User user)
    {
        HashMap<String, User> userMap = locationUserMap.containsKey(user.getCoordinates()) ? locationUserMap.get(user.getCoordinates()) : new HashMap<String, User>();
        userMap.put(user.getName(), user);
    }

    private String refreshUsersNearby(User user)
    {
        return this.userListToTripleTupleString(selectUsersNearby(user));
    }

    private ArrayList<User> selectUsersNearby(User user)
    {
        //TODO range is not set for this method
        return getUsersNearCoordinate(user.getCoordinates(), 0, UserConfig.getInstance().getNumberLimit());
    }

    //TODO this method below should be revised carefully, with consideration of range and numberLimit
    private ArrayList<User> getUsersNearCoordinate(Coordinates coordinates, double range, int numberLimit)
    {
        ArrayList<User> userList = new ArrayList<User>();
        userList.add(User.createUser("anotherTest"));
        userList.get(0).registerCoordinates(CoordinateManager.getInstance().getCoordinates(333,444));
        return userList;
    }

    private String userListToTripleTupleString(ArrayList<User> userArrayList)
    {
        if(userArrayList.size() < 1)
        {
            return "user:";
        }
        StringBuilder userTripeTupleBuilder = new StringBuilder();
        for(int i = 0, len = userArrayList.size(); i < len; i++)
        {
            userTripeTupleBuilder.append(userArrayList.get(i).toTripleTupleString()).append("\3");
        }
        return "user:" + userTripeTupleBuilder.deleteCharAt(userTripeTupleBuilder.length() - 1).toString();
    }

    /*-----------------------------------methods for getting weapon for user------------------------------------------*/
    public String getWeapon(String userName)
    {
        User user = this.getExistUser(userName);
        //TODO we must set the number of weapons each person could get
        ArrayList<Integer> weaponIdList = WeaponManager.getInstance().deliverWeapon(3);
        return user.getWeapon(weaponIdList);
    }

    /*------------------------------------methods for using weapon----------------------------------------------------*/
    public String useInstantActionWeapon(String userName, String targetUserList, int weaponId)
    {
        WeaponManager.getInstance().useWeapon(weaponId ,userMap.get(userName), userStringToArrayList(targetUserList));
        return "";
    }

    private ArrayList<User> userStringToArrayList(String userString)
    {
        ArrayList<User> userArrayList = new ArrayList<User>();
        String[] userNames = StringUtils.splitStr(userString, ',');
        for(int i = 0, len = userNames.length; i < len; i++)
        {
            if(aliveUserMap.containsKey(userNames[i]))
            {
                userArrayList.add(aliveUserMap.get(userNames[i]));
            }
        }

        return userArrayList;
    }

    public String useDelayedActionWeapon(String userName, String targetTime, int weaponId)
    {
        return "";
    }


    public void processDeadUser(User user)
    {
        aliveUserMap.remove(user.getName());
        deadUserMap.put(user.getName(), user);
    }

    /**
     * get target users from the input string
     * @param usersInString
     * @return
     */
    public ArrayList<User> getTargetUsers(String usersInString)
    {
        ArrayList<User> targetUsers = new ArrayList<User>();


        return targetUsers;
    }

    public ArrayList<User> getTargetUsers(double longitude, double latitude, double range)
    {
        return getUsersAround(longitude, latitude, range, 50);
    }

    public String getNearbyUsers(double longitude, double latitude, double range)
    {
        ArrayList<User> userArrayList = getUsersAround(longitude, latitude, range, 50);
        if(userArrayList != null)
        {
            return this.userListToTripleTupleString(userArrayList);
        }
        return "";
    }

    private ArrayList<User> getUsersAround(double longitude, double latitude, double range, int numLimit)
    {
        double[] position = new double[2];
        position[0] = longitude;
        position[1] = latitude;
        //TODO get users nearby using given longitude and latitude
        ArrayList<User> userArrayList = new ArrayList<User>();
        User user = User.createUser("1234");
        userArrayList.add(user);
        return userArrayList;
    }







    /*-----------------------------------------common methods---------------------------------------------------------*/

    /**
     * detect whether this user exist
     * @param userName
     * @return
     */
    public boolean isUserExist(String userName)
    {
        return this.userMap.containsKey(userName);
    }

    /**
     * detect whether this user has had weapon
     * @param userName
     * @return
     */
    public boolean hasAssignedWeapon(String userName)
    {
        return this.userMap.get(userName).isWeaponAssigned();
    }

    /**
     * get user instance from all user HashMap
     * @param userName
     * @return
     */
    private User getExistUser(String userName)
    {
        return userMap.containsKey(userName) ? userMap.get(userName) : null;
    }

    /**
     * get user instance from all alive user HashMap
     * @param userName
     * @return
     */
    public User getAliveUser(String userName)
    {
        return aliveUserMap.containsKey(userName) ? aliveUserMap.get(userName) : null;
    }


    private String userListToString(ArrayList<User> userArrayList)
    {
        if(userArrayList.size() < 1)
        {
            return "" ;
        }
        StringBuilder userNamesBuilder = new StringBuilder();
        for(int i = 0, len = userArrayList.size(); i < len; i++)
        {
            userNamesBuilder.append(userArrayList.get(i).getName()).append(",");
        }
        return userNamesBuilder.deleteCharAt(userNamesBuilder.length() - 1).toString();
    }
}
