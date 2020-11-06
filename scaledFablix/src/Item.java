public class Item
{
    public String id;
    public String title;
    public Integer qty;


    public Item(String title, String id)
    {
        this.id = id;
        this.title = title;
        this.qty=null;
    }
    public Integer price()
    {
        return this.title.length();
    }

}
