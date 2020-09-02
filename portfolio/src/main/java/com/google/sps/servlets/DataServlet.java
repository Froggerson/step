// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import com.google.gson.Gson;

/** 
  * Servlet that handles comment data.
  * Retrieves data inputted into the form in /comment
  * in order to create a comment.
  * Accesses BlobStore API in order to store images.
  */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  private ArrayList<String> comment = new ArrayList<String>();

  /**
    * Retrieves comment data from datastore and converts
    * it into a JSON format. The comments in JSON format
    * is then returned to the function that requested it.
    */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    int count = 0;

    ArrayList<Object> comments = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      long id = entity.getKey().getId();
      String username = (String) entity.getProperty("username");
      String title = (String) entity.getProperty("title");
      long timestamp = (long) entity.getProperty("timestamp");
      String message = (String) entity.getProperty("message");    
      String image = (String) entity.getProperty("image");
      String email = (String) entity.getProperty("email");


      ArrayList<Object> comment= new ArrayList<Object>();
      comment.add(timestamp);
      comment.add(username);
      comment.add(title);
      comment.add(message);
      comment.add(image);
      comment.add(email);
      comments.add(comment);
    }
    Gson gson = new Gson();
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(comments));
  }
 
  /**
    * Collects the form data from /comment.
    * Stores the data into datastore as a Comment entity.
    * Also calls upon getUploadedFileUrl() in order to get
    * any image url that has been uploaded. 
    */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    long timestamp = System.currentTimeMillis();
    Entity commentEntity = new Entity("Comment");
    String imageUrl = getUploadedFileUrl(request, "image");
    UserService userService = UserServiceFactory.getUserService();
    String email = userService.getCurrentUser().getEmail();
    
    commentEntity.setProperty("username", request.getParameter("username"));
    commentEntity.setProperty("title", request.getParameter("title"));
    commentEntity.setProperty("message", request.getParameter("message"));
    commentEntity.setProperty("timestamp", timestamp);
    commentEntity.setProperty("image", imageUrl);
    commentEntity.setProperty("email", email);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);
    response.sendRedirect("/comments.html");
  }

  /* 
    Converts an ArrayList into a Json format
    */
  public String convertJson(ArrayList<String> messages) throws IOException {
    Gson gson = new Gson();
    String json = gson.toJson(messages);
    System.out.println(json);
    return json;
  }
  
    /**
      * Retrieves an url from BlobStore that would
      * represent the image that a user uploaded.
      */
private String getUploadedFileUrl(HttpServletRequest request, String formInputElementName) {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get("image");

    // User submitted form without selecting a file, so we can't get a URL. (dev server)
    if (blobKeys == null || blobKeys.isEmpty()) {
      return null;
    }

    // Our form only contains a single file input, so get the first index.
    BlobKey blobKey = blobKeys.get(0);

    // User submitted form without selecting a file, so we can't get a URL. (live server)
    BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
    if (blobInfo.getSize() == 0) {
      blobstoreService.delete(blobKey);
      return null;
    }

    // We could check the validity of the file here, e.g. to make sure it's an image file
    // https://stackoverflow.com/q/10779564/873165

    // Use ImagesService to get a URL that points to the uploaded file.
    ImagesService imagesService = ImagesServiceFactory.getImagesService();
    ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(blobKey);

    // To support running in Google Cloud Shell with AppEngine's devserver, we must use the relative
    try {
      URL url = new URL(imagesService.getServingUrl(options));
      return url.getPath();
    } catch (MalformedURLException e) {
      return imagesService.getServingUrl(options);
    }
  }
}
