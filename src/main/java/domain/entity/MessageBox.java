package domain.entity;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-11
 * Time: 下午2:32
 */
public class MessageBox {
    private String message = null;
    private boolean published = false;

    public MessageBox(String message)
    {
        this.message = message;
    }

    public boolean isPublished()
    {
        return published;
    }

    public String publish()
    {
        published = true;
        return message;
    }
}
