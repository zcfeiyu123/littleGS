package server;

/**
 * Created with IntelliJ IDEA.
 * User: Aaron
 * Date: 13-11-1
 * Time: 下午10:57
 * To change this template use File | Settings | File Templates.
 */
public class LittleServiceConstants {

    public class UserOperations
    {
        public static final String create = "Create";
        public static final String refresh = "Refresh";
        public static final String getWeapon = "GetWeapon";
        public static final String useWeapon = "UseWeapon";
    }

    public class SystemOperations
    {
        public static final String reloadUserConfig = "ReloadUserConfig";
    }

    public class commonOperations
    {
        public static final String icon = "favicon.ico";
    }

    public static boolean isUserOperation(String operation)
    {
        return operation.equals(UserOperations.create) || operation.equals(UserOperations.refresh)
                || operation.equals(UserOperations.getWeapon) || operation.equals(UserOperations.useWeapon);
    }

    public static boolean isSystemOperation(String operation)
    {
        return operation.equals(SystemOperations.reloadUserConfig);
    }

}
