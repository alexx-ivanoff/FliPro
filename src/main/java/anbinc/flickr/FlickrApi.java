package anbinc.flickr;

import com.flickr4java.flickr.auth.Auth;
import com.flickr4java.flickr.auth.AuthInterface;
import com.flickr4java.flickr.auth.Permission;
import com.flickr4java.flickr.groups.Group;
import com.flickr4java.flickr.groups.GroupList;
import com.flickr4java.flickr.photos.*;
import com.flickr4java.flickr.photosets.Photoset;
import org.scribe.model.Token;
import org.scribe.model.Verifier;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.flickr4java.flickr.*;

/**
 * Created by Alex on 15.01.2017.
 */
public class FlickrApi {

    private static Flickr flickr;
    private static String userId;

    REST rest;

    RequestContext requestContext;

    public FlickrApi () throws FlickrException, IOException {
        userId = "142254954@N05";
        String key = "274e92403cf799fbf4defe5bfcd249fc";
        String secret = "ca332d4db0b1998d";
        String token = "72157679188156715-6fbf117ed9e5c774";
        String tokenSecret = "d2f3ca0bfc01a3d9";

        //getToken();

        String photoId = "29408278120";
        String groupId = "48926546@N00";

        flickr = new Flickr(key, secret,new REST());

        requestContext = RequestContext.getRequestContext();
        Auth auth = new Auth();
        auth.setPermission(Permission.WRITE);
        auth.setToken(token);
        auth.setTokenSecret(tokenSecret);
        requestContext.setAuth(auth);
        Flickr.debugRequest = false;
        Flickr.debugStream = false;

        //getIds();
    }

    public static void setFlickr(Flickr _flickr)   {
        flickr = _flickr;
    }

    public static boolean addPhotoToGroup(Picture picture, String groupId) {
        PoolList<Pool> pools = new PoolList<>();
        try {
            pools = flickr.getPhotosInterface().getAllContexts(picture.getId()).getPoolList();
        }
        catch (FlickrException e)   {

        }

        if (!pools.stream().anyMatch(p -> p.getId().equals(groupId)))   {
            try {

                try {
                    flickr.getPoolsInterface().add(picture.getId(), groupId);
                    System.out.println(String.format("Photo '%s' was successfully added to group '%s'.", picture.getName(), groupId));
                    //flickr.getPoolsInterface().getGroups();
                    return true;
                }
                catch (FlickrException e) {
                    System.out.println(String.format("Photo '%s' was not added to group '%s' because of '%s'.", picture.getName(), groupId, e.getMessage()));
                }
            }
            catch (Exception e2)    {
                System.out.println(String.format("Photo '%s' was not added to group '%s' because of non-flickr exception: '%s'.", picture.getId(), groupId, e2.getMessage()));
            }
        }

        return false;
    }

    public static List<Picture> getPhotoIdsWithoutGroup(String groupId, List<Picture> pictures)   {

        List<String> result = new ArrayList<>();
        List<Picture> unaddedPictures = new ArrayList<>();
        //List<String> unaddedPicsIds = new ArrayList<>();
        //pictures.forEach(p -> unaddedPicsIds.add(p.getId()));

        try {
            try {
                List<String> groupPhotoIds = flickr.getPoolsInterface().getPhotos(groupId, userId, new String[]{}, new HashSet<String>(), 1000, 1).stream().map(p -> p.getId()).collect(Collectors.toList());
                unaddedPictures = pictures.stream().filter(p -> !groupPhotoIds.contains(p.getId())).collect(Collectors.toList());
                //groupPhotos.stream().filter(p -> unaddedPicsIds.contains(p.getId())).collect(Collectors.toList()).stream().forEach(id -> unaddedPictures.remove());
            } catch (FlickrException e) {
                System.out.println("Flickr exception wrong during getting pics without group " + groupId + ": " + e.getErrorMessage());
            }
        }
        catch (Exception e2)    {
            System.out.println("Something goes wrong during getting pics without group " + groupId+ ": " + e2.getStackTrace());
        }

        return unaddedPictures;
    }

    public static List<Picture> getPhotosFromAlbum(String albumId) throws FlickrException {
        List<Picture> pictures = new ArrayList<>();
        List<String> photoIds = flickr.getPhotosetsInterface().getPhotos(albumId, 1000, 1).stream().map(p->p.getId()).collect(Collectors.toList());
        photoIds.forEach(p-> pictures.add(new Picture(p)));
        return pictures;
    }

    public static String getPhotoName(String id) {

        int counter = 0;

        while (counter < 3) {
            try {
                String name = flickr.getPhotosInterface().getPhoto(id).getTitle();
                return name;
            } catch (Exception e) {
                System.out.println("Problems during getting name for photoid = " + id);
                counter++;
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ie) {

                }
            }
        }

        return "---unknown name---";
    }

    private void getIds() throws FlickrException {
        GroupList<Group> groups = flickr.getPeopleInterface().getGroups(userId);
        PhotoList<Photo> photos = flickr.getPeopleInterface().getPublicPhotos(userId, new HashSet<String>(), 1000, 1);
        Collection<Photoset> albums = flickr.getPhotosetsInterface().getList(userId).getPhotosets();

        //groups.stream().filter(g->g.getName().toLowerCase().contains("girl")).map(g -> String.format("<group name=\"%s\" id=\"%s\"/>", g.getName(), g.getId())).collect (Collectors.joining ("\n"))

        SearchParameters searchParameters = new SearchParameters();
        List<String> tags = new ArrayList<>(Arrays.asList("urban", "people"));
        final PhotoList<Photo> searchResults = new PhotoList<>();

        for (String tag : tags) {
            searchParameters.setTags(new String[]{tag});
            searchParameters.setUserId(userId);
            flickr.getPhotosInterface().search(searchParameters, 1000, 1).stream().filter(p -> searchResults.contains(p)).forEach(p -> searchResults.remove(p));

        }
        String searchResultsString = searchResults.stream().map(g -> String.format("<photo name=\"%s\" id=\"%s\"/>", g.getTitle(), g.getId())).collect (Collectors.joining ("\n"));


        String groupsMapping = "";
        for (Group group : groups)  {
            groupsMapping += String.format("<group name=\"%s\" id=\"%s\"/>\n", group.getName(), group.getId());
        }

        String albumName = "India 2016";
        String albumsMapping = "";
        for (Photoset album : albums)   {
            albumsMapping += String.format("<album name=\"%s\" id=\"%s\"/>\n", album.getTitle(), album.getId());

        }

        String photosMapping = "";

        for (Photoset photoset : flickr.getPhotosetsInterface().getList(userId).getPhotosets()) {
            photos = flickr.getPhotosetsInterface().getPhotos(photoset.getId(), 1000, 1);

            photosMapping += "\n\n>>>>>\n>" + photoset.getTitle() + "\n";

            for (Photo photo : photos)  {
                photosMapping += String.format("<photo name=\"%s\" id=\"%s\"/>\n", photo.getTitle(), photo.getId());
            }
        }
        int a=0;
    }

    public void getToken() throws IOException, FlickrException {
        Properties properties;
        InputStream in = null;

        String key = "274e92403cf799fbf4defe5bfcd249fc";
        String secret = "ca332d4db0b1998d";

        Flickr flickr = new Flickr(key, secret, new REST());
        Flickr.debugStream = false;
        AuthInterface authInterface = flickr.getAuthInterface();

        Scanner scanner = new Scanner(System.in);

        Token token = authInterface.getRequestToken();
        System.out.println("token: " + token);

        String url = authInterface.getAuthorizationUrl(token, Permission.WRITE);
        System.out.println("Follow this URL to authorise yourself on Flickr");
        System.out.println(url);
        System.out.println("Paste in the token it gives you:");
        System.out.print(">>");

        String tokenKey = scanner.nextLine();
        scanner.close();

        Token requestToken = authInterface.getAccessToken(token, new Verifier(tokenKey));
        System.out.println("Authentication success");

        Auth auth = authInterface.checkToken(requestToken);

        // This token can be used until the user revokes it.
        System.out.println("Token: " + requestToken.getToken());
        System.out.println("Secret: " + requestToken.getSecret());
        System.out.println("nsid: " + auth.getUser().getId());
        System.out.println("Realname: " + auth.getUser().getRealName());
        System.out.println("Username: " + auth.getUser().getUsername());
        System.out.println("Permission: " + auth.getPermission().getType());
    }
}
