package domain.entity;

/**
 * Created with IntelliJ IDEA.
 * User: Aaron
 * Date: 13-10-31
 * Time: 下午9:00
 * To change this template use File | Settings | File Templates.
 */
public abstract class Item {
    private int id;
    private String name;

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

}
