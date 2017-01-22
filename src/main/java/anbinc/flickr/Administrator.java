package anbinc.flickr;

import com.flickr4java.flickr.FlickrException;
import javafx.util.Pair;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Alex on 15.01.2017.
 */
public class Administrator {

    private List<Task> tasks;

    public void setFlickrApi(FlickrApi flickrApi) {
        this.flickrApi = flickrApi;
    }

    FlickrApi flickrApi;

    public Administrator(List<Task> tasks, FlickrApi flickrApi) throws IOException, FlickrException {
        this.tasks = tasks;
        this.flickrApi = flickrApi;
    }

    public Administrator(List<Task> tasks) throws IOException, FlickrException {
        this(tasks, new FlickrApi());
    }


    public Map<String, List<Pair<String, String>>> manageGroups(List<String> tasksToRun) {

        int photosProcessedTotal = 0;
        Map<String, List<Pair<String, String>>> report = new HashMap<>();

        if (tasksToRun.size() != 0)
            tasks = tasks.stream().filter(t -> tasksToRun.contains(t.getName())).collect(Collectors.toList());

        for (Task task : tasks) {

            report.put(task.getName(), new ArrayList<>());
            System.out.println(String.format("Task '%s' with %s groups and %s photos is started.", task.getName(), task.getGroups().size(), task.getPhotos().size()));

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

                    if (photosAmount > 0 && photosProcessed < task.getPhotos().size()) {
                        int photoNumber = rnd.nextInt(photosAmount);
                        int photosCounter = 0;

                        String photoId = photosWithoutGroup.get(photoNumber);

                        if (flickrApi.addPhotoToGroup(photoId, groupId)) {
                            report.get(task.getName()).add(new Pair<>(photoId, groupId));
                            photosProcessed++;
                        }

                        /*
                        while (photosCounter++ != photosAmount && photosProcessed < groupsAmount && photosProcessed < task.getPhotos().size()) {
                            if (flickrApi.addPhotoToGroup(photoId, groupId)) {
                                report.get(task.getName()).add(new Pair<>(photoId, groupId));
                                photosProcessed++;
                                break;
                            }

                            photoNumber = getNextNumber(photoNumber, photosAmount);
                            photoId = photosWithoutGroup.get(photoNumber);
                        }
                        */
                    }
                }

                System.out.println(String.format("Task '%s' is finished with %s addings.", task.getName(), photosProcessed));
                photosProcessedTotal += photosProcessed;
            }
        }

        System.out.println(String.format("%s photos were successfully added to groups.", photosProcessedTotal));
        return report;
    }

    public Map<String, List<Pair<String, String>>> manageGroups() {
        return manageGroups(new ArrayList<>());
    }

    private int getNextNumber(int number, int size) {
        if (number == size - 1)
            return 0;

        return ++number;
    }
}
