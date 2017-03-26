package com.kaim808.transitalarm.view;

/**
 * Created by KaiM on 3/25/17.
 */

public class MenuItem {

    private String Description;
    private int ImageId;

    public MenuItem(String description, int imageId) {
        Description = description;
        ImageId = imageId;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getImageId() {
        return ImageId;
    }

    public void setImageId(int imageId) {
        ImageId = imageId;
    }
}
