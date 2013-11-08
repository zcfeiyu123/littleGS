package domain.proxy;

import domain.entity.Coordinates;
import domain.entity.UserConfig;
import domain.entity.User;
import domain.log.Logger;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-7
 * Time: 下午3:43
 */
public class UserProxy {

    /*--------------------------------------------instance parts------------------------------------------------------*/
    private static UserProxy userProxyInstance = null;
    private UserProxy(){
        allUserMap = new HashMap<String, User>();
        aliveUserMap = new HashMap<String, User>();
        deadUserMap = new HashMap<String, User>();
    }
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
        config.load();
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
        User newUser = User.createUser(userName, config);
        //we need to register this user to hash map
        registerUserToAllUserMap(newUser);
        registerUserToAliveUserMap(newUser);
        return newUser.toJsonString();
    }

    private void registerUserToAllUserMap(User user)
    {
        allUserMap.put(user.getName(), user);
    }

    private void registerUserToAliveUserMap(User user)
    {
        aliveUserMap.put(user.getName(), user);
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

    /*---------------------------------------------user refresh parts-------------------------------------------------*/
    public String getUserPositionKey(String userName)
    {
        return allUserMap.get(userName).coordinatesToString();
    }

    public void registerPosition(String userName, Coordinates c)
    {
        allUserMap.get(userName).registerCoordinates(c);
    }

    public String userToPositionString(String userName)
    {
        return allUserMap.get(userName).toTripleTupleString();
    }

    public int getUserAttr1(String userName)
    {
        return allUserMap.get(userName).getAtr1();
    }

    /*-----------------------------------------------user get weapon part----------------------------------------------*/
    public int getUserAttr2(String userName)
    {
        return aliveUserMap.get(userName).getAtr2();
    }

    //some methods makes testing easy
    public void printAllUser()
    {
        Iterator<String> nameIterator = allUserMap.keySet().iterator();
        while(nameIterator.hasNext())
        {
            System.out.println(nameIterator.next() + " in hash map");
        }
    }
}
