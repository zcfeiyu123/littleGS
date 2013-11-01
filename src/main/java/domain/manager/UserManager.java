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
    //TODO
    private HashMap<String, User> locationToUserMap = null; //not really settle down

    private UserManager(){
        userMap = new HashMap<String, User>();
        locationToUserMap = new HashMap<String, User>();
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
        String[] userStr = usersInString.split(",");
        for(int i = 0, len = userStr.length; i < len; i++)
        {
            if(aliveUserMap.containsKey(userStr[i]))
            {
                targetUsers.add(aliveUserMap.get(userStr[i]));
            }
        }

        return targetUsers;
    }

    public ArrayList<User> getTargetUsers(double longitude, long latitude, double range)
    {
        //TODO
        return null;
    }
}
