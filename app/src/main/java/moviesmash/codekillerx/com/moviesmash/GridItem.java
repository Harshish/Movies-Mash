package moviesmash.codekillerx.com.moviesmash;

public class GridItem {
    private String image;
    private String title;
    private String id;

    public GridItem() {
        super();
    }

    public String getImage() {
        return image;
    }

    public String getId(){ return id; }

    public void setId(String id){ this.id = id; }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
