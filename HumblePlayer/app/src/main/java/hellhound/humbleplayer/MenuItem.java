package hellhound.humbleplayer;

public class MenuItem {
    private String heading;
    public enum TYPES {HOME, SONG, ALBUM, ARTIST, PATH}
    private TYPES type;

    MenuItem(String heading){
        this.heading = heading;
    }

    public String getName() {
        return heading;
    }

    public void setName(String name) {
        this.heading = heading;
    }

    public TYPES getType() {
        return type;
    }

    public void setType(TYPES type) {
        this.type = type;
    }
}
