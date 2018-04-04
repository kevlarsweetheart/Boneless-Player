package hellhound.humbleplayer;


public class HomeScreenItem extends MenuItem {
    private int resourseId;

    public HomeScreenItem(String heading, int res) {
        super(heading);
        setType(TYPES.HOME);
        this.resourseId = res;
    }

    public int getResourseId() {
        return resourseId;
    }
}
