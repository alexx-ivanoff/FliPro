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

        int photosProcessedTotal = 0;
        for (Task task : tasks) {

            int groupsAmount = task.getGroups().size();
            if (groupsAmount > 0) {

                Random rnd = new Random();
                int groupNumber = rnd.nextInt(groupsAmount);
                int groupsCounter = 0;
                int photosProcessed = 0;

                while (groupsCounter++ != groupsAmount) {
                    groupNumber = getNextNumber(groupNumber, groupsAmount);
                    List<String> photosWithoutGroup = flickrApi.getPhotoIdsWithoutGroup(task.getGroups().get(groupNumber), task.getPhotos());

                    String groupId = task.getGroups().get(groupNumber);

                    int photosAmount = photosWithoutGroup.size();

                    if (photosAmount > 0) {
                        int photoNumber = rnd.nextInt(photosAmount);
                        int photosCounter = 0;

                        String photoId = photosWithoutGroup.get(photoNumber);

                        while (photosCounter != photosAmount && photosProcessed<groupsAmount) {
                            if (flickrApi.addPhotoToGroup(photoId, groupId)) {
                                photosProcessed++;
                                break;
                            }

                            photoNumber = getNextNumber(photoNumber, photosAmount);
                            photoId = photosWithoutGroup.get(photoNumber);
                            photosCounter++;
                        }
                    }
                }

                photosProcessedTotal += photosProcessed;
            }
        }

        System.out.println(String.format("%s photos were successfully added to groups.", photosProcessedTotal));
    }

    private int getNextNumber(int number, int size) {
        if (number == size - 1)
            return 0;

        return ++number;
    }
}
