package info.trongdat.whisperapp.views.libs;


import java.util.Date;


public class TimelineRow {

    private int id;
    private Date date;
    private String title;
    private String description;
    private String imagePost;
    private String image;
    private int bellowLineColor;
    private int bellowLineSize;
    private int imageSize;
    private int backgroundColor;
    private int backgroundSize;

    public TimelineRow(int id, Date date) {
        this(id, date, /*title*/null, /*desc*/null);
    }

    public TimelineRow(int id, Date date, String title, String description) {
        this(id, date, title, description, /*Bitmap*/null,
                /*line color*/0xFFFFFFFF, /*line size*/25/*dp*/,
                /*image size*/25/*dp*/);
    }

    public TimelineRow(int id, Date date, String title, String description, String image, int bellowLineColor, int bellowLineSize, int imageSize) {
        this(id, date, title, description, image, bellowLineColor, bellowLineSize, imageSize, -1, -1);
    }

    public TimelineRow(int id, Date date, String title, String description, String image, int bellowLineColor, int bellowLineSize, int imageSize, int backgroundColor, int backgroundSize) {
        this.id = id;
        this.date = date;
        this.title = title;
        this.description = description;
        this.image = image;
        this.bellowLineColor = bellowLineColor;
        this.bellowLineSize = bellowLineSize;
        this.imageSize = imageSize;
        this.backgroundColor = backgroundColor;
        this.backgroundSize = backgroundSize;
    }


    public TimelineRow(int id, Date date, String title, String description, String imagePost, String image, int bellowLineColor, int bellowLineSize, int imageSize, int backgroundColor, int backgroundSize) {
        this.id = id;
        this.date = date;
        this.title = title;
        this.imagePost = imagePost;
        this.description = description;
        this.image = image;
        this.bellowLineColor = bellowLineColor;
        this.bellowLineSize = bellowLineSize;
        this.imageSize = imageSize;
        this.backgroundColor = backgroundColor;
        this.backgroundSize = backgroundSize;
    }

    public int getBackgroundSize() {
        return backgroundSize;
    }

    public void setBackgroundSize(int backgroundSize) {
        this.backgroundSize = backgroundSize;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImageSize() {
        return imageSize;
    }

    public void setImageSize(int imageSize) {
        this.imageSize = imageSize;
    }

    public int getBellowLineSize() {
        return bellowLineSize;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBellowLineSize(int bellowLineSize) {
        this.bellowLineSize = bellowLineSize;
    }

    public int getBellowLineColor() {
        return bellowLineColor;
    }

    public void setBellowLineColor(int bellowLineColor) {
        this.bellowLineColor = bellowLineColor;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImagePost() {
        return imagePost;
    }

    public void setImagePost(String imagePost) {
        this.imagePost = imagePost;
    }

    public Date getDate() {
        return date;

    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
