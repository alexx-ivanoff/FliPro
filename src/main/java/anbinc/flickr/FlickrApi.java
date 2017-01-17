package anbinc.flickr;

import com.flickr4java.flickr.auth.Auth;
import com.flickr4java.flickr.auth.AuthInterface;
import com.flickr4java.flickr.auth.Permission;
import com.flickr4java.flickr.groups.Group;
import com.flickr4java.flickr.groups.GroupList;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photos.Pool;
import com.flickr4java.flickr.photos.PoolList;
import com.flickr4java.flickr.photosets.Photoset;
import com.flickr4java.flickr.util.IOUtilities;
import org.scribe.model.Token;
import org.scribe.model.Verifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import com.flickr4java.flickr.*;

/**
 * Created by Alex on 15.01.2017.
 */
public class FlickrApi {

    private static Flickr flickr;
    private String userId;

    REST rest;

    RequestContext requestContext;

    public FlickrApi () throws FlickrException, IOException {
        String key = "274e92403cf799fbf4defe5bfcd249fc";
        String secret = "ca332d4db0b1998d";
        String token = "72157679188156715-6fbf117ed9e5c774";
        String tokenSecret = "d2f3ca0bfc01a3d9";

        //getToken();

        String photoId = "29408278120";
        String groupId = "48926546@N00";
        userId = "142254954@N05";

        flickr = new Flickr(key, secret,new REST());

        requestContext = RequestContext.getRequestContext();
        Auth auth = new Auth();
        auth.setPermission(Permission.WRITE);
        auth.setToken(token);
        auth.setTokenSecret(tokenSecret);
        requestContext.setAuth(auth);
        Flickr.debugRequest = false;
        Flickr.debugStream = false;

        List<String> photoIds = new ArrayList<>();
        photoIds.add(photoId);
        getPhotoIdsWithoutGroup(groupId, photoIds);

/*
        GroupList<Group> groups = flickr.getPeopleInterface().getGroups(userId);
        PhotoList<Photo> photos = flickr.getPeopleInterface().getPublicPhotos(userId, new HashSet<String>(), 1000, 1);

        String groupsMapping = "";
        for (Group group : groups)  {
            groupsMapping += String.format("<group>name=\"%s\" id=\"%s\"</group>\n", group.getName(), group.getId());
        }

        String albumName = "India 2016";
        photos = flickr.getPhotosetsInterface().getPhotos(flickr.getPhotosetsInterface().getList(userId).getPhotosets().stream().filter(a->a.getTitle().equals(albumName)).findFirst().get().getId(), 1000, 1);

        String photosMapping = "";

        for (Photoset photoset : flickr.getPhotosetsInterface().getList(userId).getPhotosets()) {
            photos = flickr.getPhotosetsInterface().getPhotos(photoset.getId(), 1000, 1);

            photosMapping += "\n\n>>>>>\n>" + photoset.getTitle() + "\n";

            for (Photo photo : photos)  {
                photosMapping += String.format("<photo>name=\"%s\" id=\"%s\"</photo>\n", photo.getTitle(), photo.getId());
            }
        }
*/

//        Group group = groups.stream().filter(g->g.getName().equals("Nikon DSLR Users")).findFirst().get();
//        addPhotoToGroup(photoId, group.getId());

        int a=0;
    }

    public boolean addPhotoToGroup(String photoId, String groupId) {
        PoolList<Pool> pools = new PoolList<>();
        try {
            pools = flickr.getPhotosInterface().getAllContexts(photoId).getPoolList();
        }
        catch (FlickrException e)   {

        }

        if (!pools.stream().anyMatch(p -> p.getId().equals(groupId)))   {
            try {
                //flickr.getPoolsInterface().add(photoId, groupId);
                System.out.println(String.format("Photo '%s' was successfully added to group '%s'.", photoId, groupId));
                flickr.getPoolsInterface().getGroups();
                return false;
            }
            catch (FlickrException e) {

            }
        }

        System.out.println(String.format("Photo '%s' was not added to group '%s'.", photoId, groupId));
        return false;
    }

    public List<String> getPhotoIdsWithoutGroup(String groupId, List<String> photoIds)   {

        List<String> result = new ArrayList<>();

        try {
            PhotoList<Photo> groupPhotos = flickr.getPoolsInterface().getPhotos(groupId, userId, new String[] {}, new HashSet<String>(), 1000, 1);
            groupPhotos.stream().filter(p -> photoIds.contains(p.getId())).collect(Collectors.toList()).stream().forEach(p -> photoIds.remove(p.getId()));
        }
        catch (FlickrException e)   {

        }

        return photoIds;
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
