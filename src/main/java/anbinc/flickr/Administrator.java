package anbinc.flickr;

import com.flickr4java.flickr.FlickrException;
import javafx.util.Pair;

import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * Created by Alex on 15.01.2017.
 */
public class Administrator {

    private List<Task> tasks;
    FlickrApi flickrApi;

    public Administrator(List<Task> tasks) throws IOException, FlickrException {
        this.tasks = tasks;

        flickrApi = new FlickrApi();
    }

    public void manageGroups() {
        for (Task task : tasks) {

            int groupsAmount = task.getGroups().size();

            Random rnd = new Random();
            int groupNumber = rnd.nextInt(groupsAmount);
            int groupsCounter = 0;

            while (groupsCounter++ != groupsAmount)  {
                groupNumber = getNextNumber(groupNumber, groupsAmount);
                List<String> photosWithoutGroup = flickrApi.getPhotoIdsWithoutGroup(task.getGroups().get(groupNumber), task.getPhotos());

                String groupId = task.getGroups().get(groupNumber);

                int photosAmount = photosWithoutGroup.size();
                int photoNumber = rnd.nextInt(photosAmount);
                int photosCounter = 0;

                String photoId = photosWithoutGroup.get(groupNumber);

                while (photosCounter++  != photosAmount && !flickrApi.addPhotoToGroup(photoId, groupId))  {
                    photoNumber = getNextNumber(photoNumber, photosAmount);
                    photoId = photosWithoutGroup.get(photoNumber);
                }
            }
        }
    }

    private int getNextNumber(int number, int size) {
        if (number == size - 1)
            return 0;

        return number++;
    }
}
