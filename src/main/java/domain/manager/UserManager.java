package domain.manager;

/**
 * Created with IntelliJ IDEA.
 * User: zhangcen@youku.com
 * Date: 13-11-1
 * Time: 上午10:09
 * To change this template use File | Settings | File Templates.
 */

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
    private HashMap<User, double[]> userLocationMap = null;

    private UserManager(){
        this.userMap = new HashMap<String, User>();
        this.aliveUserMap = new HashMap<String, User>();
        this.deadUserMap = new HashMap<String, User>();

        //location related information
        locationUserMap = new HashMap<double[], User>();
        userLocationMap = new HashMap<User, double[]>();
    }

    public static UserManager getInstance()
    {
        if(instance == null)
        {
            instance = new UserManager();
        }
        return instance;
    }

    private void registerUser(User user)
    {
        if(user == null)
        {
            return ;
        }
        this.userMap.put(user.getName(), user);
    }

    private void registerAliveUser(User user)
    {
        if(user == null)
        {
            return ;
        }
        this.aliveUserMap.put(user.getName(), user);
    }

    public void refreshUserStatus(User user)
    {
        //new position
        if(userLocationMap.containsKey(user))
        {
            double[] position = userLocationMap.get(user);
            if(locationUserMap.containsKey(position))
            {
                locationUserMap.remove(position);
            }
            position[0] = user.getLongitude();
            position[1] = user.getLatitude();
            userLocationMap.put(user, position);
            locationUserMap.put(position, user);
        }
        else
        {
            double[] position = new double[2];
            position[0] = user.getLongitude();
            position[1] = user.getLatitude();
            userLocationMap.put(user, position);
            locationUserMap.put(position,user);
        }
    }

    /**
     * create a user instance
     * @param userName
     * @return
     */
    public User createUser(String userName)
    {
        if(userName == null)
        {
            SimpleLogger.getLogger().error("User ID is null while creating user");
            return null;
        }
        if(userMap.containsKey(userName))
        {
            return userMap.get(userName);
        }
        User user = User.getInstance(userName);
        registerUser(user);
        registerAliveUser(user);
        SimpleLogger.getLogger().debug("creating user " + userName + " succeed");
        return user;
    }

    public User getExistUser(String userName)
    {
        return userMap.containsKey(userName) ? userMap.get(userName) : null;
    }

    public User getAliveUser(String userName)
    {
        return aliveUserMap.containsKey(userName) ? aliveUserMap.get(userName) : null;
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
        User user = User.getInstance("1234");
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
}
