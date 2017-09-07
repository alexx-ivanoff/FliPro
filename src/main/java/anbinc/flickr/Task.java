package anbinc.flickr;

import javafx.util.Pair;

import java.util.List;

/**
 * Created by Alex on 15.01.2017.
 */
public class Task {

    private List<String> groups;
    private List<Pair<Picture, String>> excludes;
    private List<Picture> pictures;
    private String name;

    public Task()   {

    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public List<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Pair<Picture, String>> getExcludes() {
        return excludes;
    }

    public void setExcludes(List<Pair<Picture, String>> excludes) {
        this.excludes = excludes;
    }
}
