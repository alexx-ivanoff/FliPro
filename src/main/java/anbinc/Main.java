package anbinc;

import anbinc.flickr.Administrator;
import anbinc.flickr.FlickrApi;
import anbinc.flickr.Picture;
import anbinc.flickr.Task;
import anbinc.utils.TasksReader;
import com.flickr4java.flickr.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Created by Alex on 15.01.2017.
 */
public class Main {

    public static void main(String[] args) throws IOException, FlickrException {
        FlickrApi flickrApi = new FlickrApi();
        List<Task> tasks = TasksReader.readTasksFromXML();
        //tasks = getTasks();
        List<String> tasksToRun = new ArrayList<>();

        Properties properties = System.getProperties();
        if (properties.stringPropertyNames().contains("tasksToRun"))
            tasksToRun = Arrays.asList(properties.getProperty("tasksToRun").split(","));


        Administrator admin = new Administrator(tasks);
        admin.manageGroups(tasksToRun);
    }

    private static void printTasks(List<Task> tasks) {

    }
/*
    private static List<Task> getTasks()   {

        List<String> groupIds = new ArrayList<>();
        List<Picture> pictures = new ArrayList<>();

        groupIds.add("52242377700@N01");
        pictures.add("30546702814");
        pictures.add("31253207701");
        pictures.add("31367978415");
        pictures.add("31223969212");

        Task task = new Task();
        task.setGroups(groupIds);
        task.setPictures(pictures);

        List<Task> tasks = new ArrayList<>();
        tasks.add(task);

        return tasks;
    }*/
}
