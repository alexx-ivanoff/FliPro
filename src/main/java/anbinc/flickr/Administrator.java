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

    public Administrator(List<Task> tasks) throws IOException, FlickrException {
        this.tasks = tasks;
    }

    public Map<String, List<Pair<Picture, String>>> manageGroups(List<String> tasksToRun) {

        int photosProcessedTotal = 0;
        Map<String, List<Pair<Picture, String>>> report = new HashMap<>();

        if (tasksToRun.size() != 0)
            tasks = tasks.stream().filter(t -> tasksToRun.contains(t.getName())).collect(Collectors.toList());

        for (Task task : tasks) {

            report.put(task.getName(), new ArrayList<>());
            System.out.println(String.format("Task '%s' with %s groups and %s photos is started.", task.getName(), task.getGroups().size(), task.getPictures().size()));

            int groupsAmount = task.getGroups().size();
            if (groupsAmount > 0) {

                Random rnd = new Random();
                int groupNumber = rnd.nextInt(groupsAmount);
                int groupsCounter = 0;
                int photosProcessed = 0;

                while (groupsCounter++ != groupsAmount) {
                    groupNumber = getNextNumber(groupNumber, groupsAmount);
                    List<Picture> photosWithoutGroup = FlickrApi.getPhotoIdsWithoutGroup(task.getGroups().get(groupNumber), task.getPictures());

                    String groupId = task.getGroups().get(groupNumber);

                    int photosAmount = photosWithoutGroup.size();

                    if (photosAmount > 0 && photosProcessed < task.getPictures().size()) {
                        int photoNumber = rnd.nextInt(photosAmount);
                        int photosCounter = 0;

                        Picture picture = photosWithoutGroup.get(photoNumber);

                        if (FlickrApi.addPhotoToGroup(picture, groupId)) {
                            report.get(task.getName()).add(new Pair<>(picture, groupId));
                            photosProcessed++;
                        }

                        /*
                        while (photosCounter++ != photosAmount && photosProcessed < groupsAmount && photosProcessed < task.getPictures().size()) {
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

    public Map<String, List<Pair<Picture, String>>> manageGroups() {
        return manageGroups(new ArrayList<>());
    }

    private int getNextNumber(int number, int size) {
        if (number == size - 1)
            return 0;

        return ++number;
    }
}
