package hellhound.humbleplayer;

public class ArtistItem extends MenuItem {

    private int coverArtId;

    ArtistItem(String name){
        super(name);
        this.type = TYPES.ARTIST;
        this.coverArtId = -1;
    }

    public int getCoverArtId() {
        return coverArtId;
    }

    public void setCoverArtId(int coverArtId) {
        this.coverArtId = coverArtId;
    }
}
