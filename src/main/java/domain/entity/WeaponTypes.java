package domain.entity;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-8
 * Time: 下午4:45
 */
public enum WeaponTypes {
    pointWeapon,lineWeapon,areaWeapon;

    public static boolean isWeaponTypes(String type)
    {
        WeaponTypes[] values = WeaponTypes.values();
        for(int i = 0, len = values.length; i < len; i++)
        {
            if(values[i].name().equals(type))
            {
                return true;
            }
        }
        return false;
    }
}
