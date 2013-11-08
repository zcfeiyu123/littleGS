package domain.entity;

import domain.manager.UserManager;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Aaron
 * Date: 13-10-31
 * Time: 下午9:02
 * To change this template use File | Settings | File Templates.
 */
public class Weapon extends Item {

    private WeaponTypes weaponType;//the type of the weapon
    private int power = 0;//how much damage can this weapon cause
    private int range = 0;//fire range of this weapon

    public Weapon(int weaponId, String weaponName, WeaponTypes weaponType, int range, int power)
    {
        super(weaponId, weaponName);
        this.weaponType = weaponType;
        this.range = range;
        this.power = power;
    }

    /**
     * fire to other users
     * @param user user own this weapon
     * @param targetUsers users will get damage
     */
    public void fire(User user, ArrayList<User> targetUsers)
    {
        StringBuilder targetUserNames = new StringBuilder();
        int totalDamage = 0;
        for(int i = 0, len = targetUsers.size(); i < len; i++)
        {
            User targetUser = targetUsers.get(i);
            EventMessage eventMessage = fireToOneUser(user, targetUser);
            if(eventMessage != null)//succeed in firing
            {

                targetUser.calcDamage(power);
                targetUser.registerEvents(eventMessage);  //register events to this particular user

                if(targetUser.isUserDead())
                {
                    UserManager.getInstance().processDeadUser(targetUser);
                }

                targetUserNames.append(targetUser.getName()).append(",");
                totalDamage += power;
            }
        }

        //register events to main user
        targetUserNames.deleteCharAt(targetUserNames.length() - 1);
        EventMessage mainEvent = EventMessage.getInstance(user.getName(), targetUserNames.toString(), this.getName(), totalDamage);
        user.registerEvents(mainEvent);
    }

    private EventMessage fireToOneUser(User user, User targetUser)
    {
        EventMessage eventMessage = EventMessage.getInstance(user.getName(), targetUser.getName(), this.getName(), power);
        return eventMessage;
    }

    /**
     * the toString methods for deliver weapons
     * @return
     */
    public String toProfileString()
    {
        StringBuilder sbd = new StringBuilder();
        sbd.append(id).append("\t");
        sbd.append(name).append("\t");
        sbd.append(weaponType.name()).append("\t");
        sbd.append(range).append("\t");
        sbd.append(power);
        return sbd.toString();
    }

}
