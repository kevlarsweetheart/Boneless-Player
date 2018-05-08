package hellhound.flamingoplayer;

public class PlayAllItem extends MenuItem {

    boolean shuffleVisible;
    boolean playAllVisible;
    TYPES spawnedBy;

    public PlayAllItem(TYPES spawnedBy) {
        super(DEFAULT_NAME);
        this.type = TYPES.PLAY_ALL;
        shuffleVisible = true;
        playAllVisible = true;
        this.spawnedBy = spawnedBy;
    }

    public boolean isShuffleVisible() {
        return shuffleVisible;
    }

    public void setShuffleVisible(boolean shuffleVisible) {
        this.shuffleVisible = shuffleVisible;
    }

    public boolean isPlayAllVisible() {
        return playAllVisible;
    }

    public void setPlayAllVisible(boolean playAllVisible) {
        this.playAllVisible = playAllVisible;
    }
}
