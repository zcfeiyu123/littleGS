package domain.proxy;

import domain.entity.User;
import domain.entity.Weapon;
import domain.manager.WeaponManager;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Aaron
 * Date: 13-10-31
 * Time: 下午11:57
 * To change this template use File | Settings | File Templates.
 */
public class WeaponProxy {

    private static WeaponProxy instance = null;
    private WeaponProxy(){}

    public static WeaponProxy getInstance()
    {
        if(instance == null)
        {
            instance = new WeaponProxy();
        }
        return instance;
    }

    public void openFire(User user, ArrayList<User> targetUsers, int weaponId)
    {
        Weapon weapon = WeaponManager.getInstance().getWeaponById(weaponId);
        weapon.fire(user, targetUsers);
    }
}
