package hellhound.flamingoplayer;

import java.util.ArrayList;
import java.util.Collections;

public class PlaylistItem extends MenuItem {

    ArrayList<TrackItem> tracks;
    ArrayList<Integer> originalOrder;
    private int currentTrack = 0;
    boolean isShuffled = false;

    public PlaylistItem(String name) {
        super(name);
        this.type = TYPES.PLAYLIST;
        tracks = new ArrayList<>();
        originalOrder = new ArrayList<>();
    }

    public PlaylistItem(String name, ArrayList<MenuItem> items) {
        super(name);
        this.type = TYPES.PLAYLIST;
        tracks = new ArrayList<>();
        originalOrder = new ArrayList<>();
        setTracks(items);
    }

    public PlaylistItem(String name, ArrayList<MenuItem> tracks, int currentTrack) {
        super(name);
        this.type = TYPES.PLAYLIST;
        this.tracks = new ArrayList<>();
        originalOrder = new ArrayList<>();
        setTracks(tracks, currentTrack);
    }

    public int setTracks(ArrayList<MenuItem> items){
        if (tracks.size() > 0){
            tracks.clear();
            originalOrder.clear();
        }
        tracks = filterTracks(items);
        for(TrackItem track : tracks){
            originalOrder.add(track.getItemId());
        }
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
        if (tracks.size() > 0) {
            return tracks;
        } else {
            ArrayList<TrackItem> res = new ArrayList<>();
            res.add(new TrackItem("Dummy"));
            return res;
        }
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
        this.originalOrder.clear();
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

    public void shuffle(boolean shuffle){
        if(shuffle){
            isShuffled = true;
            Collections.shuffle(tracks);
        } else {
            isShuffled = false;
            int currInd = 0;
            while (currInd < tracks.size() - 1){
                for(int i = currInd; i < originalOrder.size(); i++){
                    if(originalOrder.get(currInd) == tracks.get(i).getItemId()){
                        Collections.swap(tracks, i, currInd);
                        break;
                    }
                }
                currInd++;
            }
        }
    }
}
