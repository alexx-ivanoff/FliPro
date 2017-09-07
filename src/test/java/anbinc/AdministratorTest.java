package anbinc;

import anbinc.flickr.Administrator;
import anbinc.flickr.FlickrApi;
import anbinc.flickr.Picture;
import anbinc.flickr.Task;
import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.groups.pools.PoolsInterface;
import com.flickr4java.flickr.photos.*;
import javafx.util.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
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
    private List<Pair<Picture, String>> excludes = new ArrayList<>();
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
    public void manageGroupsTestUsedPhotosMoreThanGroups() throws IOException, FlickrException {

        int groupsNum = 2;
        int photosNum = 4;
        List<Task> tasks = getTasks(groupsNum, photosNum);

        Administrator admin = new Administrator(tasks);
        pictures.get(0).setUsageCount(1);
        pictures.get(1).setUsageCount(1);
        pictures.get(3).setUsageCount(1);

        Picture picture = mock(Picture.class);
        doNothing().when(picture).eraseUsageCount();

        Map<String, List<Pair<Picture, String>>> report =  admin.manageGroups();
        assertThat("Report size is not as expected", report.size(), equalTo(1));
        assertThat("Photos/groups amount is not as expected", report.get("t1").size(), equalTo(1));
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

    @Test
    public void manageUsageInDifferentSets() throws IOException, FlickrException {

        int groupsNumT1 = 2;
        int photosNumT1 = 3;

        int groupsNumT2 = 3;
        int photosNumT2 = 3;

        List<Task> tasks = getTasks(1, groupsNumT1, photosNumT1);
        tasks.addAll(getTasks(2, groupsNumT2, photosNumT2));

        Administrator admin = new Administrator(tasks);
        pictures.get(0).setUsageCount(1);
        pictures.get(1).setUsageCount(1);
        //pictures.get(2).setUsageCount(1);

        Map<String, List<Pair<Picture, String>>> report =  admin.manageGroups();
        assertThat("Report size is not as expected", report.size(), equalTo(2));
        assertThat("Photos/groups amount is not as expected", report.get("t1").size(), equalTo(2));
        assertThat("Photos/groups amount is not as expected", report.get("t2").size(), equalTo(3));
    }

    @Test
    public void testGetPhotoIdsWithoutGroup() throws FlickrException {
        PhotoList<Photo> photoList = new PhotoList<>();
        Photo ph1 = new Photo();
        ph1.setId("id1");
        photoList.add(ph1);

        Photo ph2 = new Photo();
        ph2.setId("id2");
        photoList.add(ph2);

        Photo ph3 = new Photo();
        ph3.setId("id3");
        photoList.add(ph3);

        when(poolsInt.getPhotos(anyString(), anyString(), any(), any(), anyInt(), anyInt())).thenReturn(photoList);
        Photo photo = new Photo();
        photo.setTitle("");
        when(photoInt.getPhoto(anyString())).thenReturn(photo);

        List<Picture> pictures = new ArrayList<>();
        Picture pic1 = new Picture("id1");
        pictures.add(pic1);

        Picture pic2 = new Picture("id22");
        pictures.add(pic2);

        Picture pic3 = new Picture("id3");
        pictures.add(pic3);

        List<Picture> result = FlickrApi.getPhotoIdsWithoutGroup("", pictures);
        assertThat("Result size is not as expected", result.size(), equalTo(1));
        assertThat("Result not as expected.", result.stream().anyMatch(r -> r.getId().equals("id22")), equalTo(true));
    }

        private List<Task> getTasks(int groupsNum, int photosNum) {
        return getTasks(1, groupsNum, photosNum);
    }

        private List<Task> getTasks(int taskNum, int groupsNum, int photosNum)   {
        List<Task> tasks = new ArrayList<>();
        groupIds = new ArrayList<>();
        pictures = new ArrayList<>();
        photos = new PhotoList<>();

        Task task = new Task();
        task.setName("t" + taskNum);

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
        task.setExcludes(new ArrayList<>());
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
