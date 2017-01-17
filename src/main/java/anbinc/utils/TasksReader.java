package anbinc.utils;

import anbinc.flickr.Task;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
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

            NodeList setsList = doc.getElementsByTagName("set");

            for (int setNum = 0; setNum < setsList.getLength(); setNum++) {
                Node setNode = setsList.item(setNum);

                Task task = new Task();
                List<String> groupIds = new ArrayList<>();
                List<String> photoIds = new ArrayList<>();

                if (setNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element setElement = (Element)setNode;
                    NodeList groupsList = setElement.getElementsByTagName("group");
                    NodeList photosList = setElement.getElementsByTagName("photo");

                    //fill groups
                    for (int groupNum = 0; groupNum < groupsList.getLength(); groupNum++) {
                        Node groupNode = groupsList.item(groupNum);

                        if (groupNode.getNodeType() == Node.ELEMENT_NODE)
                            groupIds.add(((Element)groupNode).getAttribute("id"));
                    }

                    //fill photos
                    for (int photoNum = 0; photoNum < photosList.getLength(); photoNum++) {
                        Node photoNode = photosList.item(photoNum);

                        if (photoNode.getNodeType() == Node.ELEMENT_NODE)
                            photoIds.add(((Element)photoNode).getAttribute("id"));
                    }
                }

                task.setGroups(groupIds.stream().distinct().collect(Collectors.toList()));
                task.setPhotos(photoIds.stream().distinct().collect(Collectors.toList()));
                tasks.add(task);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tasks;
    }
}
