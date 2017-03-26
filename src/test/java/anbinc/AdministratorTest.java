package anbinc;

import anbinc.flickr.Administrator;
import anbinc.flickr.FlickrApi;
import anbinc.flickr.Picture;
import anbinc.flickr.Task;
import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.Transport;
import com.flickr4java.flickr.groups.pools.PoolsInterface;
import com.flickr4java.flickr.photos.*;
import javafx.util.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;

/**
 * Created by ivaa on 2017-01-21.
 */
@RunWith(MockitoJUnitRunner.class)
public class AdministratorTest {

    Flickr flickr;
    PhotosInterface photoInt;
    PoolsInterface poolsInt;
    private List<String> groupIds = new ArrayList<>();
    private List<Picture> pictures = new ArrayList<>();
    private PhotoList<Photo> photos = new PhotoList<>();

    @Test
    public void manageGroupsTestPhotosMoreThanGroups() throws IOException, FlickrException {

        int groupsNum = 2;
        int photosNum = 4;
        List<Task> tasks = getTasks(groupsNum, photosNum);

        Administrator admin = new Administrator(tasks);

        Map<String, List<Pair<Picture, String>>> report =  admin.manageGroups();
        assertThat("Report size is not as expected", report.size(), equalTo(1));
        assertThat("Photos/groups amount is not as expected", report.get("t1").size(), equalTo(2));
    }

    @Test
    public void manageGroupsTestGroupsMoreThanPhotos() throws IOException, FlickrException {

        int groupsNum = 4;
        int photosNum = 2;
        List<Task> tasks = getTasks(groupsNum, photosNum);

        Administrator admin = new Administrator(tasks);

        Map<String, List<Pair<Picture, String>>> report =  admin.manageGroups();
        assertThat("Report size is not as expected", report.size(), equalTo(1));
        assertThat("Photos/groups amount is not as expected", report.get("t1").size(), equalTo(2));
    }

    @Test
    public void manageGroupsTestWithGroupReject() throws IOException, FlickrException {

        int groupsNum = 3;
        int photosNum = 3;
        List<Task> tasks = getTasks(groupsNum, photosNum);

        doNothing().doThrow(new FlickrException("")).doNothing().when(poolsInt).add(anyString(), anyString());
        Administrator admin = new Administrator(tasks);

        Map<String, List<Pair<Picture, String>>> report =  admin.manageGroups();
        assertThat("Report size is not as expected", report.size(), equalTo(1));
        assertThat("Photos/groups amount is not as expected", report.get("t1").size(), equalTo(2));
    }

    private List<Task> getTasks(int groupsNum, int photosNum)   {
        List<Task> tasks = new ArrayList<>();

        Task task = new Task();
        task.setName("t1");

        for (int g=1; g<=groupsNum; g++)
            groupIds.add("g" + g);

        for (int p=1; p<=photosNum; p++) {
            String id = "p" + p;
            pictures.add(new Picture(id));
            Photo photo = new Photo();
            photo.setId(id);
            photos.add(photo);
        }

        task.setGroups(groupIds);
        task.setPictures(pictures);
        tasks.add(task);

        return tasks;
    }

    @Before
    public void setUp() throws Exception {
        flickr = mock(Flickr.class);
        photoInt = mock(PhotosInterface.class);
        when(flickr.getPhotosInterface()).thenReturn(photoInt);
        Photo photo = mock(Photo.class);
        when(photoInt.getPhoto(any())).thenReturn(photo);
        when(photo.getTitle()).thenReturn("");
        FlickrApi.setFlickr(flickr);

        poolsInt = mock(PoolsInterface.class);
        when(flickr.getPoolsInterface()).thenReturn(poolsInt);
        when(poolsInt.getPhotos(anyString(), anyString(), any(), any(), anyInt(), anyInt())).thenReturn(photos);
        PhotoAllContext context = mock(PhotoAllContext.class);
        when(photoInt.getAllContexts(anyString())).thenReturn(context);
        PoolList poolList = new PoolList();
        when(context.getPoolList()).thenReturn(poolList);
    }

    }
