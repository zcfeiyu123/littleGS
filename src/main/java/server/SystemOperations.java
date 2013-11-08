package server;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-8
 * Time: 下午5:42
 */
public enum  SystemOperations {
    ReloadServerConfig,ReloadUserConfig,ReloadEventConfig;

    public static boolean isSystemOperation(String operation)
    {
        SystemOperations[] operations = SystemOperations.values();
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
