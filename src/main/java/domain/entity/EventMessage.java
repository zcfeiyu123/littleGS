package domain.entity;

import utils.SimpleLogger;

/**
 * Created with IntelliJ IDEA.
 * User: Aaron
 * Date: 13-10-31
 * Time: 下午9:36
 * To change this template use File | Settings | File Templates.
 */
public class EventMessage {

    private long timeStamp = 0;
    private String fromUserID = null;
    private String targetUserID = null;
    private String weaponName = null;
    private int damage = 0;

    private EventMessage(String fromUserID, String targetUserID, String weaponName, int damage)
    {
        this.fromUserID = fromUserID;
        this.targetUserID = targetUserID;
        this.weaponName = weaponName;
        this.damage = damage;
        this.timeStamp = System.currentTimeMillis();
    }

    public static EventMessage getInstance(String fromUserID, String targetUserID, String weaponName, int damage)
    {
        if(fromUserID == null)
        {
            SimpleLogger.getLogger().error("fromUserID is null when creating an EventMessage");
            return null;
        }
        else if(targetUserID == null)
        {
            SimpleLogger.getLogger().error("targetUserID is null when creating an EventMessage");
            return null;
        }
        else if(weaponName == null)
        {
            SimpleLogger.getLogger().error("weaponName is null when creating an EventMessage");
            return null;
        }

        return new EventMessage(fromUserID, targetUserID, weaponName, damage);
    }



    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("EventMessage{");
        sb.append("timeStamp=").append(timeStamp);
        sb.append(", fromUserID='").append(fromUserID).append('\'');
        sb.append(", targetUserID='").append(targetUserID).append('\'');
        sb.append(", weaponName='").append(weaponName).append('\'');
        sb.append(", damage=").append(damage);
        sb.append('}');
        return sb.toString();
    }

    /**
     * get time stamp
     * @return
     */
    public long getTimeStamp()
    {
        return this.timeStamp;
    }

    public String getFromUserID()
    {
        return this.fromUserID;
    }

    public String getTargetUserID()
    {
        return this.targetUserID;
    }

    public String getWeaponName()
    {
        return this.weaponName;
    }

    public int getDamage()
    {
        return this.damage;
    }

}
