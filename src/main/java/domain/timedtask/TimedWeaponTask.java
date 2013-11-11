package domain.timedtask;

import domain.log.Logger;
import domain.manager.EventManager;

import java.util.Calendar;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-11
 * Time: 下午3:11
 */
public class TimedWeaponTask extends TimerTask {

    private Calendar calendar = Calendar.getInstance();
    @Override
    public void run() {
        int index = -1;
        try{
            index = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
            EventManager.getManager().runTimedWeaponTask(index);
        }catch (Exception e)
        {
            Logger.getInstance().fatal("Run Timed Weapon Task Wrong with Index = " + index);
            e.printStackTrace();
        }
    }
}
