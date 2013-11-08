package server;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-8
 * Time: 下午5:39
 */
public enum UserOperations {
    Create,Refresh,GetWeapon,UserInstantWeapon,UseDelayedWeapon,icon;

    public static boolean isUserOperation(String operation)
    {
        UserOperations[] operations = UserOperations.values();
        for(int i = 0; i < operations.length; i++)
        {
            if(operation.equals(operations[i].name()))
            {
                return true;
            }
        }
        return false;
    }
}
