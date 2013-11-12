package domain.manager;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-12
 * Time: 上午11:33
 */
public enum EventResultCode {
    PASS,
    UserNameNullOrEmpty,
    UserNotExist,
    UserNotAlive,
    UserAssignedWeapon,
    UserNotAssignedWeapon,
    UserNotPossessWeapon,
    NoUserAround,
    TargetUserNullOrEmpty,
    WeaponIdFormatWrong,
    WeaponNotExist,
    NoWeaponLeft,
    LongitudeFormatWrong,
    LatitudeFormatWrong,
    CoordinatesNotExist,
    LaunchTimeFormatWrong,
    NoUnpublishedMessage,
    UnKnownError;
}
