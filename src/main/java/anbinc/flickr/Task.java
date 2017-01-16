package anbinc.flickr;

import java.util.List;

/**
 * Created by Alex on 15.01.2017.
 */
public class Task {

    private List<String> groups;
    private List<String> photos;
    private String name;

    public Task()   {

    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }
}
