package elcg.dina.com.instagramapp;

/**
 * Created by dina on 28/10/16.
 */

public class ImageItem {

    private String imageURL;
    private String imageCaption;
    private Boolean imageIsLiked;
    private int id;



    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getImageCaption() {
        return imageCaption;
    }

    public void setImageCaption(String imageCaption) {
        this.imageCaption = imageCaption;
    }

    public Boolean getImageIsLiked() {
        return imageIsLiked;
    }

    public void setImageIsLiked(Boolean imageIsLiked) {
        this.imageIsLiked = imageIsLiked;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
