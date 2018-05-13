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
        this.currentTrack = this.currentTrack - (items.size() - tracks.size());
        return tracks.size();
    }

    public int setTracks(ArrayList<MenuItem> items, int currentTrack){
        this.currentTrack = currentTrack;
        return setTracks(items);
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

    public ArrayList<String> getTracksPaths(){
        ArrayList<String> paths = new ArrayList<>();
        for(TrackItem track : tracks){
            paths.add(track.getPath());
        }
        return paths;
    }

    public void clearTracks(){
        this.tracks.clear();
        currentTrack = 0;
    }

    public int getSize(){
        return tracks.size();
    }

    public int getCurrentTrack() {
        return currentTrack;
    }

    public void setCurrentTrack(int currentTrack) {
        this.currentTrack = currentTrack;
    }
}
