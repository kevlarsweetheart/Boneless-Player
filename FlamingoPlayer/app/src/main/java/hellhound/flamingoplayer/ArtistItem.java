package hellhound.flamingoplayer;

public class ArtistItem extends MenuItem {

    private int albumsCnt = 0;
    public boolean extended = false;

    public ArtistItem(String name) {
        super(name);
        this.type = TYPES.ARTIST;
    }

    public int getAlbumsCnt() {
        return albumsCnt;
    }

    public void setAlbumsCnt(int albumsCnt) {
        this.albumsCnt = albumsCnt;
    }
}
