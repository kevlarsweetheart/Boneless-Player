package hellhound.humbleplayer;

public class MenuItem {
    private String name;
    public enum TYPES {HOME, SONG, ALBUM, ARTIST, PATH}
    private TYPES type;
    private static int cnt = 0;
    private int itemId;

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
