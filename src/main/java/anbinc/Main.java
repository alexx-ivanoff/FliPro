package anbinc;

import anbinc.flickr.FlickrApi;
import anbinc.utils.TasksReader;
import com.flickr4java.flickr.*;
import com.flickr4java.flickr.util.IOUtilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Alex on 15.01.2017.
 */
public class Main {

    public static void main(String[] args) throws IOException, FlickrException {
        TasksReader.readTasksFromXML();
        //FlickrApi flickrApi = new FlickrApi();
    }
}
