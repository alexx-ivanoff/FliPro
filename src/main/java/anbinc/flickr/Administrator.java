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

            Random rnd = new Random();
            int initialGroupNum = rnd.nextInt(groupsNum - 1);
            int currentGroupNum = getNextNumber(initialGroupNum, groupsNum);

            while (currentGroupNum != initialGroupNum)  {
                List<String> photosWithoutGroup = flickrApi.getPhotoIdsWithoutGroup(task.getGroups().get(currentGroupNum), task.getPhotos());

                String groupId = task.getGroups().get(currentGroupNum);

                int photosNum = photosWithoutGroup.size();
                int initialPhotoNum = rnd.nextInt(photosNum - 1);
                int currentPhotoNum = getNextNumber(initialPhotoNum, photosNum);

                String photoId = photosWithoutGroup.get(initialGroupNum);

                while (currentPhotoNum != initialPhotoNum && !flickrApi.addPhotoToGroup(photoId, groupId))  {
                    currentPhotoNum = getNextNumber(currentPhotoNum, photosNum);
                    photoId = task.getPhotos().get(currentPhotoNum);
                }

                currentGroupNum = getNextNumber(initialGroupNum, groupsNum);
            }
        }
    }

    private int getNextNumber(int number, int size) {
        if (number == size - 1)
            return 0;

        return number++;
    }
}
