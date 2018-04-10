package hellhound.flamingoplayer;

public class MenuItem {
    private String name;
    public enum TYPES {HOME, TRACK, ALBUM, ARTIST, PATH, PLAY_ALL}
    protected TYPES type;
    private static int cnt = -2147483648;
    private int itemId;
    protected static final String DEFAULT_NAME = "DEFAULT";

    MenuItem(String name){
        this.name = name;
        itemId = cnt;
        cnt++;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TYPES getType() {
        return type;
    }

    public int getItemId() {
        return itemId;
    }
}