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

            int groupsNum = task.getGroups().size();
            int photosNum = task.getPhotos().size();

            Random rnd = new Random();
            int initialGroupNum = rnd.nextInt(groupsNum - 1);
            int initialPhotoNum = rnd.nextInt(photosNum - 1);

            int currentGroupNum = getNextNumber(initialGroupNum, groupsNum);
            int currentPhotoNum = getNextNumber(initialPhotoNum, photosNum);

            String groupId = task.getGroups().get(initialGroupNum);
            String photoId = task.getPhotos().get(initialGroupNum);

            while (currentPhotoNum != initialPhotoNum && !flickrApi.addPhotoToGroup(photoId, groupId))  {
                currentPhotoNum = getNextNumber(initialPhotoNum, photosNum);

                groupId = task.getGroups().get(currentGroupNum);
                photoId = task.getPhotos().get(currentPhotoNum);
            }


            //if (!flickrApi.addPhotoToGroup(photoId, groupId))
            //    getNextNumber(photoId)
        }
    }

    private int getNextNumber(int number, int size) {
        if (number == size - 1)
            return 0;

        return number++;
    }
}
