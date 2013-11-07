package domain.proxy;

import domain.config.UserConfig;
import domain.entity.User;
import domain.log.Logger;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-7
 * Time: 下午3:43
 */
public class UserProxy {

    /*--------------------------------------------instance parts------------------------------------------------------*/
    private static UserProxy userProxyInstance = null;
    private UserProxy(){}
    public static UserProxy getUserProxyInstance()
    {
        if(userProxyInstance == null)
        {
            userProxyInstance = new UserProxy();
        }
        return userProxyInstance;
    }

    /*-------------------------------------------business parts-------------------------------------------------------*/
    //config
    private UserConfig config = null;
    //user maps
    private HashMap<String, User> allUserMap = null;
    private HashMap<String, User> aliveUserMap = null;
    private HashMap<String, User> deadUserMap = null;

    //init everything, include config, all kinds of hashMap and so on
    public void init()
    {
        Logger.getInstance().debug("start init user proxy");
        config = UserConfig.getInstance();
        allUserMap = new HashMap<String, User>();
        aliveUserMap = new HashMap<String, User>();
        deadUserMap = new HashMap<String, User>();
        Logger.getInstance().debug("user proxy init finish");
    }

    //detect whether a user exist
    public boolean isUserExist(String userName)
    {
        return allUserMap.containsKey(userName);
    }
    public boolean isUserAlive(String userName)
    {
        return aliveUserMap.containsKey(userName);
    }
    /*----------------------------------------------create new user parts---------------------------------------------*/
    /**
     * create a new user when this user login for first time today
     * @param userName
     * @return
     */
    public String createUser(String userName)
    {
        User newUser = User.createUser(userName);
        //we need to register this user to hash map
        this.allUserMap.put(userName, newUser);
        this.aliveUserMap.put(userName, newUser);
        return newUser.toJsonString();
    }

    /**
     * return a user's json String
     * @param userName
     * @return
     */
    public String existUserToJsonString(String userName)
    {
        return allUserMap.get(userName).toJsonString();
    }
}
