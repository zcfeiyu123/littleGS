package domain.entity;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Aaron
 * Date: 13-10-31
 * Time: 下午9:00
 * To change this template use File | Settings | File Templates.
 */
public class User {

    private String name = null;
    private int HP = 0;

    /**
     * position infomation
     */
    private Coordinates coordinates = null;

    /**
     * six basic attributes
     */
    private int atr1 = 0;
    private int atr2 = 0;
    private int atr3 = 0;
    private int atr4 = 0;
    private int atr5 = 0;
    private int atr6 = 0;

    private User(String name, UserConfig config)
    {
        this.name = name;
        //set up basic parameters
        this.HP = config.getDefaultHP();
        this.atr1 = config.getDefaultAttr1();
        this.atr2 = config.getDefaultAttr2();
        this.atr3 = config.getDefaultAttr3();
        this.atr4 = config.getDefaultAttr4();
        this.atr5 = config.getDefaultAttr5();
        this.atr6 = config.getDefaultAttr6();
    }
    /*---------------------------------------------create user operation----------------------------------------------*/
    public static User createUser(String name, UserConfig config)
    {
        return new User(name, config);
    }

    /*-------------------------------------------refresh user status operation----------------------------------------*/

    public String coordinatesToString()
    {
        if(coordinates == null)
        {
            return "";
        }
        else
        {
            return coordinates.getPositionKey();
        }
    }

    public void registerCoordinates(Coordinates c)
    {
        coordinates = c;
    }

    /*----------------------------------getters and setters, status checker-------------------------------------------*/
    public String getName()
    {
        return name;
    }

    public int getHP()
    {
        return HP;
    }

    public void reduceHP(int damage)
    {
        HP = damage > HP ? 0 : HP-damage;
    }

    public boolean isUserDead()
    {
        return HP <= 0;
    }

    public int getAtr1()
    {
        return atr1;
    }
    public int getAtr2()
    {
        return atr2;
    }
    public int getAtr3()
    {
        return atr3;
    }
    public int getAtr4()
    {
        return atr4;
    }
    public int getAtr5()
    {
        return atr5;
    }
    public int getAtr6()
    {
        return atr6;
    }

    /*------------------------------------all kinds of toString methods-----------------------------------------------*/
    /**
     * turn this user to a triple tuple String
     * @return
     */
    public String toTripleTupleString()
    {
        StringBuilder sbd = new StringBuilder("[");
        sbd.append(name).append("\t");
        sbd.append(coordinates.getLongitude()).append("\t");
        sbd.append(coordinates.getLatitude()).append("]");

        return sbd.toString();
    }

    /**
     * turn the basic information of the user into a json style string
     * @return
     */
    public String toJsonString()
    {
        StringBuilder sbd = new StringBuilder("userName:").append(name);
        sbd.append(",").append("hp:").append(HP);
        sbd.append(",").append("atr1:").append(atr1);
        sbd.append(",").append("atr2:").append(atr2);
        sbd.append(",").append("atr3:").append(atr3);
        sbd.append(",").append("atr4:").append(atr4);
        sbd.append(",").append("atr5:").append(atr5);
        sbd.append(",").append("atr6:").append(atr6);

        return sbd.toString();
    }
}
