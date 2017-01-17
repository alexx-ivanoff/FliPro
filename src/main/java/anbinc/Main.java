package anbinc;

import anbinc.flickr.Administrator;
import anbinc.flickr.FlickrApi;
import anbinc.flickr.Task;
import anbinc.utils.TasksReader;
import com.flickr4java.flickr.*;
import com.flickr4java.flickr.util.IOUtilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by Alex on 15.01.2017.
 */
public class Main {

    public static void main(String[] args) throws IOException, FlickrException {
        //TasksReader.readTasksFromXML();
        FlickrApi flickrApi = new FlickrApi();
        Administrator admin = new Administrator(getTasks());
        admin.manageGroups();
    }

    private static List<Task> getTasks()   {

        List<String> groupIds = new ArrayList<>();
        List<String> photoIds = new ArrayList<>();

        groupIds.add("52242377700@N01");
        photoIds.add("30546702814");
        photoIds.add("31253207701");
        photoIds.add("31367978415");
        photoIds.add("31223969212");

        Task task = new Task();
        task.setGroups(groupIds);
        task.setPhotos(photoIds);

        List<Task> tasks = new ArrayList<>();
        tasks.add(task);

        return tasks;
    }
}
