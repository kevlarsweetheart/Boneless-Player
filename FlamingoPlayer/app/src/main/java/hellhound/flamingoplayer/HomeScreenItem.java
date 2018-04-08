package hellhound.flamingoplayer;

public class HomeScreenItem extends MenuItem {
    private int resourceId;

    public HomeScreenItem(String heading) {
        super(heading);
        this.type = TYPES.HOME;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public int getResourceId() {
        return resourceId;
    }
}
