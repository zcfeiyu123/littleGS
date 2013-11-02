package domain.manager;

/**
 * Created with IntelliJ IDEA.
 * User: zhangcen@youku.com
 * Date: 13-11-1
 * Time: 上午10:09
 * To change this template use File | Settings | File Templates.
 */

import domain.entity.Coordinates;
import domain.entity.User;
import utils.SimpleLogger;
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
    private HashMap<double[], User> locationUserMap = null;

    private UserManager(){
        this.userMap = new HashMap<String, User>();
        this.aliveUserMap = new HashMap<String, User>();
        this.deadUserMap = new HashMap<String, User>();

        //location related information
        locationUserMap = new HashMap<double[], User>();
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
        //can not create such user
        if(userName == null)
        {
            SimpleLogger.getLogger().fatal("userName is null while creating user");
            return "{status:fail, reason:userName is null while creating user}";
        }
        //if the user already exist, we return her basic status
        if(userMap.containsKey(userName))
        {
            return "{status:success," + userMap.get(userName).toJsonString() + "}";
        }
        //we have to create a new user using given name and register her to all the map
        User user = User.createUser(userName);
        registerUser(user);
        registerAliveUser(user);// a new user must be an alive user
        return "{status:success," + user.toJsonString() + "}";
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
        if(user == null)
        {
            return "{status:fail,reason:user " + userName + " does not exist}";
        }
        //first, the user refresh her location status and at this time, we have update the information in locationMap
        Coordinates oldCoordinates = user.getCoordinates();
        Coordinates newCoordinates = CoordinateManager.getInstance().getCoordinates(longitude, latitude);
        user.refresh(newCoordinates);
        /*then we update the nearby users of this particular user, we do not register them to this user, but keep all
         * the information in UserManager
         */
        String response = this.refreshUserStatus(user, oldCoordinates);
        return response;
    }
    /**
     * refresh user status
     * @param user
     */
    private String refreshUserStatus(User user, Coordinates coordinates)
    {
        //TODO have to complete this method asap
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

    private String userListToString(ArrayList<User> userArrayList)
    {
        StringBuilder userNamesBuilder = new StringBuilder();
        for(int i = 0, len = userArrayList.size(); i < len; i++)
        {
            userNamesBuilder.append(userArrayList.get(i).getName()).append(",");
        }
        return userNamesBuilder.deleteCharAt(userNamesBuilder.length() - 1).toString();
    }

    private String userListToTripleTupleString(ArrayList<User> userArrayList)
    {
        StringBuilder userTripeTupleBuilder = new StringBuilder();
        for(int i = 0, len = userArrayList.size(); i < len; i++)
        {
            userTripeTupleBuilder.append(userArrayList.get(i).toTripleTupleString()).append(",");
        }
        return userTripeTupleBuilder.deleteCharAt(userTripeTupleBuilder.length() - 1).toString();
    }

    /*-----------------------------------------common methods---------------------------------------------------------*/

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

    /*----------------------------------user operation proxy parts-----------------------------------------------*/

}
