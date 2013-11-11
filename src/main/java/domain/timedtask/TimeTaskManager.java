package domain.timedtask;

import utils.TimeUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-11
 * Time: 下午3:05
 */
public class TimeTaskManager {
    // 时间间隔
    private static final long PERIOD_EACH_MINUTE = 60 * 1000L;
    public TimeTaskManager() {
        // 第一次执行定时任务的时间在载入之后1分钟
        Date taskStartTime = TimeUtils.addMinute(Calendar.getInstance().getTime(), 1);
        //重启服务立即加载
        Timer timer = new Timer();
        // 安排指定的任务在指定的时间开始进行重复的固定延迟执行。
        timer.schedule(new TimedWeaponTask(), taskStartTime, PERIOD_EACH_MINUTE);
    }
}
