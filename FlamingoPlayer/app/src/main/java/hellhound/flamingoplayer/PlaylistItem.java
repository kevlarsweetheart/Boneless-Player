package hellhound.flamingoplayer;

import java.util.ArrayList;

public class PlaylistItem extends MenuItem {

    ArrayList<TrackItem> tracks;
    private int currentTrack = 0;

    public PlaylistItem(String name) {
        super(name);
        this.type = TYPES.PLAYLIST;
        tracks = new ArrayList<>();
    }

    public PlaylistItem(String name, ArrayList<MenuItem> items) {
        super(name);
        this.type = TYPES.PLAYLIST;
        setTracks(items);
    }

    public PlaylistItem(String name, ArrayList<MenuItem> tracks, int currentTrack) {
        super(name);
        this.type = TYPES.PLAYLIST;
        setTracks(tracks);
        this.currentTrack = currentTrack;
    }

    public int setTracks(ArrayList<MenuItem> items){
        if (tracks.size() > 0){
            tracks.clear();
        }
        tracks = filterTracks(items);
        return tracks.size();
    }

    private ArrayList<TrackItem> filterTracks(ArrayList<MenuItem> items){
        ArrayList<TrackItem> result = new ArrayList<>();
        for (MenuItem item : items){
            if(item.getType() == TYPES.TRACK){
                result.add((TrackItem) item);
            }
        }
        return result;
    }

    public TrackItem getTrack(int position){
        if (position < tracks.size() && position >= 0){
            return tracks.get(position);
        } else {
            return null;
        }
    }

    public ArrayList<TrackItem> getTracks() {
        return tracks;
    }
}
