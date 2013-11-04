package domain.manager;

import domain.entity.User;
import domain.entity.Weapon;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Aaron
 * Date: 13-11-1
 * Time: 上午12:01
 * To change this template use File | Settings | File Templates.
 */
public class WeaponManager {

    private static WeaponManager instance = null;
    private WeaponManager(){
        this.weaponHashMap = new HashMap<Integer, Weapon>();
        this.rand = new Random();
    }
    private HashMap<Integer, Weapon> weaponHashMap = null; //store weaponId to its instance
    private int[] weaponIdArray = null; //store weapon's id in an array
    private int[] weaponInventoryArray = null; //store weapon's inventory in an array
    private double[] weaponProbArray = null; //store the probability of each weapon
    private Random rand = null;

    public static WeaponManager getInstance()
    {
        if(instance == null)
        {
            instance = new WeaponManager();
        }
        return instance;
    }

    /*-------------------------------------load weapon data from database---------------------------------------------*/
    /**
     * load weapon instance from database
     */
    public void initWeaponHashMap()
    {
        //TODO load weapons from database
        //this map is a tmp place to store inventory information of weapon
        HashMap<Integer, Integer> weaponInventoryMap = new HashMap<Integer, Integer>();

        //update all arrays
        int numOfTotalWeapons = weaponHashMap.size();
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

        for(int i = 0; i < numOfTotalWeapons; i++)
        {
            weaponProbArray[i] = (double)weaponInventoryArray[i] / totalInventoryOfWeapons;
        }
    }

    /*------------------------------------deliver weapon to one user--------------------------------------------------*/
    public ArrayList<Integer> devliverWeapon(int numberOfWeapon)
    {
        //TODO finish the algorithm for deliver weapon, the rest part of the method is for test only
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




    public void useWeapon()
    {

    }

    public Weapon getWeaponById(int weaponId)
    {
        Weapon weapon = weaponHashMap.containsKey(weaponId) ? weaponHashMap.get(weaponId) : null;
        return weapon;
    }

}
