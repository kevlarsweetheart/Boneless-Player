package hellhound.flamingoplayer;

public class AlbumItem extends MenuItem {

    private long artistId;   //For foreign keys
    private long coverId;   //For foreign keys

    public AlbumItem(String name) {
        super(name);
        this.type = TYPES.ALBUM;
    }

    public long getArtistId() {
        return artistId;
    }

    public void setArtistId(long artistId) {
        this.artistId = artistId;
    }

    public long getCoverId() {
        return coverId;
    }

    public void setCoverId(long coverId) {
        this.coverId = coverId;
    }
}
