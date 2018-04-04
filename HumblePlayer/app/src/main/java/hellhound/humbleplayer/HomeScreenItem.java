package hellhound.humbleplayer;


public class HomeScreenItem extends MenuItem {
    private int resourceId;

    public HomeScreenItem(String heading, int res) {
        super(heading);
        this.type = TYPES.HOME;
        this.resourceId = res;
    }

    public int getResourceId() {
        return resourceId;
    }
}
