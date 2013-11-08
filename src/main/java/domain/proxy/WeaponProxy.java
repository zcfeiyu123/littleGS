package domain.proxy;

import domain.entity.Weapon;
import domain.entity.WeaponTypes;
import domain.log.Logger;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-7
 * Time: 下午3:43
 */
public class WeaponProxy {
    private static WeaponProxy weaponProxyInstance = null;
    private WeaponProxy(){}
    public static WeaponProxy getWeaponProxyInstance()
    {
        if(weaponProxyInstance==null)
        {
            weaponProxyInstance = new WeaponProxy();
        }
        return weaponProxyInstance;
    }

    //business part
    private HashMap<Integer, Weapon> weaponHashMap = null;
    private int[] weaponIdArray = null; //store weapon's id in an array
    private int[] weaponInventoryArray = null; //store weapon's inventory in an array
    private double[] weaponProbArray = null; //store the probability of each weapon
    private Random rand = null;

    public void init(){
        //init random
        rand = new Random();
        //TODO load weapons from database
        weaponHashMap = new HashMap<Integer, Weapon>();
        Logger.getInstance().debug("start init weapon proxy");
        //this map is a tmp place to store inventory information of weapon
        //TODO it is a test procedure, we have to modify this
//        HashMap<Integer, Integer> weaponInventoryMap = new HashMap<Integer, Integer>();
        HashMap<Integer, Integer> weaponInventoryMap = makeFakeWeapons();
        //update all arrays
        int numOfTotalWeapons = weaponHashMap.size();
        Logger.getInstance().debug("total " + numOfTotalWeapons + " weapons in hand");
        this.weaponIdArray = new int[numOfTotalWeapons];
        this.weaponInventoryArray = new int[numOfTotalWeapons];
        this.weaponProbArray = new double[numOfTotalWeapons];

        int index = 0;
        double totalInventoryOfWeapons = 0;
        Iterator<Map.Entry<Integer, Integer>> iter = weaponInventoryMap.entrySet().iterator();
        while(iter.hasNext())
        {
            Map.Entry<Integer, Integer> entry = iter.next();
            weaponIdArray[index] = entry.getKey();
            weaponInventoryArray[index] = entry.getValue();
            totalInventoryOfWeapons += entry.getValue();
            index++;
        }

        weaponProbArray[0] = (double)weaponInventoryArray[0] / totalInventoryOfWeapons;
        for(int i = 1; i < numOfTotalWeapons; i++)
        {
            weaponProbArray[i] = (double)weaponInventoryArray[i] / totalInventoryOfWeapons + weaponProbArray[i-1];
        }
        Logger.getInstance().debug("weapon proxy init finish");
    }

    /*------------------------------------deliver weapon to one user--------------------------------------------------*/
    public ArrayList<Integer> deliverWeapon(int numberOfWeapon)
    {
        ArrayList<Integer> weaponIdList = new ArrayList<Integer>();
        for(int i = 0; i < numberOfWeapon; i++)
        {
            int weaponIndex = generateOneWeapon();
            if(weaponIndex < 0)//no weapon left in inventory
            {
                return weaponIdList;
            }
            weaponIdList.add(weaponIdArray[weaponIndex]);
            weaponInventoryArray[weaponIndex] -= 1;//update weapon inventory
        }
        return weaponIdList;
    }

    /**
     * generate index from weapon prob array
     * @return
     */
    private int generateOneWeapon()
    {
        double prob = rand.nextDouble();
        int index = binarySearchIndex(prob);
        if(weaponInventoryArray[index] < 1)
        {
            index = -1;
            for(int i = 0; i < weaponInventoryArray.length; i++)
            {
                if(weaponInventoryArray[i] > 0)
                {
                    return i;
                }
            }
        }
        return index;
    }

    /**
     * method for generating a weapon
     * @param prob
     * @return
     */
    private int binarySearchIndex(double prob)
    {
        int index = -1;
        int numOfWeapons = weaponProbArray.length;
        if(this.weaponProbArray[numOfWeapons - 1] < prob)
        {
            return -1;
        }
        else if(this.weaponProbArray[0] > prob)
        {
            return 0;
        }
        else
        {
            int start = 1;
            int end = numOfWeapons;
            boolean founded = false;
            while(!founded)
            {
                index = (start + end)/2;
                if(weaponProbArray[index - 1] < prob && weaponProbArray[index] > prob)
                {
                    founded = true;
                }
                else if(weaponProbArray[index] < prob)
                {
                    start = index;
                }
                else
                {
                    end = index;
                }
            }
            return index;
        }
    }

    public String getWeaponProfileString(int weaponId)
    {
        return weaponHashMap.get(weaponId).toProfileString();
    }

    /*-----------------------------------------only for test-----------------------------------------------------------*/
    //TODO to delete
    private HashMap<Integer,Integer> makeFakeWeapons()
    {
        HashMap<Integer, Integer> tmpMap = new HashMap<Integer, Integer>();
        int[] ids = {100001,100002};
        String[] names = {"testWeapon1", "testWeapon2"};
        WeaponTypes weaponType = WeaponTypes.lineWeapon;
        int range = 15;
        int power = 2;
        for(int i = 0; i < ids.length; i++)
        {
            Weapon weapon = new Weapon(ids[i], names[i], weaponType, range, power);

            this.weaponHashMap.put(ids[i], weapon);

            tmpMap.put(ids[i], 100);
        }

        return tmpMap;
    }
}
