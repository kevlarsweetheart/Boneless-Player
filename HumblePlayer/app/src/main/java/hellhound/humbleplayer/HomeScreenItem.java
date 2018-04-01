package hellhound.humbleplayer;


public class HomeScreenItem {
    public enum Types {HSI, FOLDER, MP3};
    private String text;
    private int imgRes;
    private Types type;

    HomeScreenItem(String text, int image, Types type) {
        this.text = text;
        this.imgRes = image;
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public int getImage() {
        return imgRes;
    }

    public Types getType() {
        return type;
    }

}
