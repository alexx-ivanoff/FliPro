package anbinc.utils;

import anbinc.flickr.FlickrApi;
import anbinc.flickr.Picture;
import anbinc.flickr.Task;
import javafx.util.Pair;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by Alex on 15.01.2017.
 */
public class TasksReader {
    public static List<Task> readTasksFromXML()   {

        List<Task> tasks = new ArrayList<>();

        try {
            String filename = "Tasks.xml";

            InputStream inputStream = new FileInputStream(filename);
            String file = IOUtils.toString(inputStream, "UTF-8").replace("&", "&amp;");
            inputStream.close();

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputSource inputSource = new InputSource(new StringReader(file));

            Document doc = dBuilder.parse(inputSource);

            doc.getDocumentElement().normalize();

            //read groupPacks
            Map<String, List<String>> groupPacks = new HashMap<>();
            NodeList groupPacksList = doc.getElementsByTagName("groupPacks");
            for (int gpsNum = 0; gpsNum < groupPacksList.getLength(); gpsNum++) {
                Node gpsNode = groupPacksList.item(gpsNum);
                if (gpsNode.getNodeType() == Node.ELEMENT_NODE) {
                    NodeList groupPackList = ((Element) gpsNode).getElementsByTagName("groupPack");

                    for (int gpNum = 0; gpNum < groupPackList.getLength(); gpNum++) {
                        Node gpNode = groupPackList.item(gpNum);
                        if (gpNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element gpElement = (Element) gpNode;
                            String gpName = gpElement.getAttribute("name");
                            List<String> groupIds = new ArrayList<>();
                            NodeList groupsList = gpElement.getElementsByTagName("group");

                            for (int groupNum = 0; groupNum < groupsList.getLength(); groupNum++) {
                                Node groupNode = groupsList.item(groupNum);

                                if (groupNode.getNodeType() == Node.ELEMENT_NODE)
                                    groupIds.add(((Element) groupNode).getAttribute("id"));
                            }

                            groupPacks.put(gpName, groupIds.stream().distinct().collect(Collectors.toList()));
                        }
                    }
                }
            }

            //read photoPacks
            Map<String, List<Picture>> photoPacks = new HashMap<>();
            NodeList photoPacksList = doc.getElementsByTagName("photoPacks");
            for (int gpsNum = 0; gpsNum < photoPacksList.getLength(); gpsNum++) {
                Node ppsNode = photoPacksList.item(gpsNum);
                if (ppsNode.getNodeType() == Node.ELEMENT_NODE) {
                    NodeList photoPackList = ((Element) ppsNode).getElementsByTagName("photoPack");

                    for (int ppNum = 0; ppNum < photoPackList.getLength(); ppNum++) {
                        Node ppNode = photoPackList.item(ppNum);
                        if (ppNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element ppElement = (Element) ppNode;
                            String ppName = ppElement.getAttribute("name");
                            List<Picture> pictures = new ArrayList<>();
                            NodeList photosList = ppElement.getElementsByTagName("photo");

                            for (int photoNum = 0; photoNum < photosList.getLength(); photoNum++) {
                                Node photoNode = photosList.item(photoNum);

                                if (photoNode.getNodeType() == Node.ELEMENT_NODE)
                                    pictures.add(new Picture(((Element) photoNode).getAttribute("id")));
                            }

                            photoPacks.put(ppName, pictures.stream().distinct().collect(Collectors.toList()));
                        }
                    }
                }
            }


            //((Element)doc.getElementsByTagName("groupPacks").item(0)).getElementsByTagName("groupPack").getLength()

            // read sets
            NodeList setsList = doc.getElementsByTagName("set");
            for (int setNum = 0; setNum < setsList.getLength(); setNum++) {
                Node setNode = setsList.item(setNum);

                Task task = new Task();
                List<String> groupIds = new ArrayList<>();
                List<Picture> pictures = new ArrayList<>();
                List<Pair<Picture, String>> excludes = new ArrayList<>();

                if (setNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element setElement = (Element)setNode;
                    task.setName(setElement.getAttribute("name"));
                    NodeList groupsList = setElement.getElementsByTagName("group");
                    NodeList photosList = setElement.getElementsByTagName("photo");
                    NodeList groupPackList = setElement.getElementsByTagName("groupPack");
                    NodeList photoPackList = setElement.getElementsByTagName("photoPack");
                    NodeList albumsList = setElement.getElementsByTagName("album");
                    NodeList excludesList = setElement.getElementsByTagName("exclude");

                    //fill groups
                    for (int groupNum = 0; groupNum < groupsList.getLength(); groupNum++) {
                        Node groupNode = groupsList.item(groupNum);

                        if (groupNode.getNodeType() == Node.ELEMENT_NODE)
                            groupIds.add(((Element)groupNode).getAttribute("id"));
                    }

                    //fill groups from groupPacks
                    for (int gpNum = 0; gpNum < groupPackList.getLength(); gpNum++) {
                        Node groupNode = groupPackList.item(gpNum);

                        if (setNum == 14)
                            System.out.println(setNum + "/" + gpNum);
                        if (groupNode.getNodeType() == Node.ELEMENT_NODE)   {
                            String gpName = ((Element)groupNode).getAttribute("name");
                            groupIds.addAll(groupPacks.get(gpName));
                        }
                    }

                    //fill photos
                    for (int photoNum = 0; photoNum < photosList.getLength(); photoNum++) {
                        Node photoNode = photosList.item(photoNum);

                        if (photoNode.getNodeType() == Node.ELEMENT_NODE)
                            pictures.add(new Picture(((Element)photoNode).getAttribute("id")));
                    }

                    //System.out.println(setElement.getAttribute("name") + " / ppacks");
                    //fill photos from photoPacks
                    for (int ppNum = 0; ppNum < photoPackList.getLength(); ppNum++) {
                        Node photoNode = photoPackList.item(ppNum);

                        if (photoNode.getNodeType() == Node.ELEMENT_NODE)   {
                            String ppName = ((Element)photoNode).getAttribute("name");
                            pictures.addAll(photoPacks.get(ppName));
                        }
                    }

                    //fill photos from albums
                    for (int albumNum = 0; albumNum < albumsList.getLength(); albumNum++) {
                        Node albumNode = albumsList.item(albumNum);
                        if (albumNode.getNodeType() == Node.ELEMENT_NODE)   {
                            String albumId = ((Element)albumNode).getAttribute("id");
                            pictures.addAll(FlickrApi.getPhotosFromAlbum(albumId));
                        }

                    }

                    //fill excludes
                    for (int excludeNum = 0; excludeNum < excludesList.getLength(); excludeNum++) {
                        Node excludeNode = excludesList.item(excludeNum);

                        if (excludeNode.getNodeType() == Node.ELEMENT_NODE)
                            excludes.add(new Pair(new Picture(((Element)excludeNode).getAttribute("photoId")), ((Element)excludeNode).getAttribute("groupid")));
                    }
                }

                task.setGroups(groupIds.stream().distinct().collect(Collectors.toList()));
                task.setPictures(pictures.stream().filter(distinctByKey(p -> p.getId())).collect(Collectors.toList()));
                task.setExcludes(excludes);
                tasks.add(task);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
/*
        final int gr = tasks.stream()
                .mapToInt(outer ->
                        outer.getGroups().size())
                .sum();
        final int ph = tasks.stream()
                .flatMapToInt(outer ->
                        outer.getPictures().stream().mapToInt(sub -> sub.length())
                ).sum();
*/
        return tasks;
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object,Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
