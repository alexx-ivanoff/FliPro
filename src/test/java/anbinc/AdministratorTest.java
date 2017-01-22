package anbinc;

import anbinc.flickr.Administrator;
import anbinc.flickr.FlickrApi;
import anbinc.flickr.Task;
import com.flickr4java.flickr.FlickrException;
import javafx.util.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

/**
 * Created by ivaa on 2017-01-21.
 */
@RunWith(MockitoJUnitRunner.class)
public class AdministratorTest {

    private List<String> groupIds = new ArrayList<>();
    private List<String> photoIds = new ArrayList<>();

    @Test
    public void manageGroupsTestPhotosMoreThanGroups() throws IOException, FlickrException {

        int groupsNum = 2;
        int photosNum = 4;
        List<Task> tasks = getTasks(groupsNum, photosNum);

        FlickrApi flickrApi = mock(FlickrApi.class);
        when(flickrApi.addPhotoToGroup(any(String.class), any(String.class))).thenReturn(true);
        when(flickrApi.getPhotoIdsWithoutGroup(any(), any())).thenReturn(photoIds);
        Administrator admin = new Administrator(tasks, flickrApi);

        Map<String, List<Pair<String, String>>> report =  admin.manageGroups();
        assertThat("Report size is not as expected", report.size(), equalTo(1));
        assertThat("Photos/groups amount is not as expected", report.get("t1").size(), equalTo(2));
    }

    @Test
    public void manageGroupsTestGroupsMoreThanPhotos() throws IOException, FlickrException {

        int groupsNum = 4;
        int photosNum = 2;
        List<Task> tasks = getTasks(groupsNum, photosNum);

        FlickrApi flickrApi = mock(FlickrApi.class);
        when(flickrApi.addPhotoToGroup(any(String.class), any(String.class))).thenReturn(true);
        when(flickrApi.getPhotoIdsWithoutGroup(any(), any())).thenReturn(photoIds);
        Administrator admin = new Administrator(tasks, flickrApi);

        Map<String, List<Pair<String, String>>> report =  admin.manageGroups();
        assertThat("Report size is not as expected", report.size(), equalTo(1));
        assertThat("Photos/groups amount is not as expected", report.get("t1").size(), equalTo(2));
    }

    @Test
    public void manageGroupsTestWithGroupReject() throws IOException, FlickrException {

        int groupsNum = 3;
        int photosNum = 3;
        List<Task> tasks = getTasks(groupsNum, photosNum);

        FlickrApi flickrApi = mock(FlickrApi.class);
        when(flickrApi.addPhotoToGroup(any(String.class), any(String.class))).thenReturn(true).thenReturn(false).thenReturn(true);
        when(flickrApi.getPhotoIdsWithoutGroup(any(), any())).thenReturn(photoIds);
        Administrator admin = new Administrator(tasks, flickrApi);

        Map<String, List<Pair<String, String>>> report =  admin.manageGroups();
        assertThat("Report size is not as expected", report.size(), equalTo(1));
        assertThat("Photos/groups amount is not as expected", report.get("t1").size(), equalTo(2));
    }

    private List<Task> getTasks(int groupsNum, int photosNum)   {
        List<Task> tasks = new ArrayList<>();

        Task task = new Task();
        task.setName("t1");

        for (int g=1; g<=groupsNum; g++)
            groupIds.add("g" + g);

        for (int p=1; p<=photosNum; p++)
            photoIds.add("p" + p);

        task.setGroups(groupIds);
        task.setPhotos(photoIds);
        tasks.add(task);

        return tasks;
    }
}
