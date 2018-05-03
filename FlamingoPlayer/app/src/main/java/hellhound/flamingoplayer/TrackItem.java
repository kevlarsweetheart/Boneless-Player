package hellhound.flamingoplayer;

public class TrackItem extends MenuItem {

    int trackNumber;
    String path;
    int albumId;
    int artistId;

    public TrackItem(String name) {
        super(name);
        this.type = TYPES.TRACK;
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(int trackNumber) {
        this.trackNumber = trackNumber;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public int getArtistId() {
        return artistId;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }
}
