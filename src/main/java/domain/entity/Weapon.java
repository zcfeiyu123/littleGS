package domain.entity;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Aaron
 * Date: 13-10-31
 * Time: 下午9:02
 * To change this template use File | Settings | File Templates.
 */
public class Weapon extends Item {

    private int weaponID = -1;//weapon id
    private String weaponName = null;//weapon name
    private int weaponType = 0;//the type of the weapon
    private int power = 0;//how much damage can this weapon cause
    private int range = 0;//fire range of this weapon
    private int timer = 0;//in how much minutes this weapon will fire

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
                targetUser.registerEvents(eventMessage);//register events to this particular user
                targetUserNames.append(targetUser.getName()).append(",");
                totalDamage += power;
            }
        }

        //register events to main user
        targetUserNames.deleteCharAt(targetUserNames.length() - 1);
        EventMessage mainEvent = EventMessage.getInstance(user.getName(), targetUserNames.toString(), weaponName, totalDamage);
        user.registerEvents(mainEvent);
    }

    private EventMessage fireToOneUser(User user, User targetUser)
    {
        EventMessage eventMessage = EventMessage.getInstance(user.getName(), targetUser.getName(), weaponName, power);
        return eventMessage;
    }

}
