package hellhound.flamingoplayer;

public class ArtistItem extends MenuItem {

    public ArtistItem(String name) {
        super(name);
        this.type = TYPES.ARTIST;
    }
}
