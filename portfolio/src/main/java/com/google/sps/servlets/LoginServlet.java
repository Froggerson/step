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

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import com.google.gson.Gson;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    ArrayList<Object> loginStatus = new ArrayList<Object>();

    // Only logged-in users can see the form
    UserService userService = UserServiceFactory.getUserService();
    loginStatus.add(userService.isUserLoggedIn());
    if (!userService.isUserLoggedIn()) {
      String loginUrl = userService.createLoginURL("/comments.html");
      loginStatus.add(loginUrl);      
    }else{
      String logoutUrl =  userService.createLogoutURL("/comments.html");
      loginStatus.add(logoutUrl);      
    }
    response.setContentType("application/json;");
    response.getWriter().println(convertJson(loginStatus));
    //response.sendRedirect("/comments.html");

  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    UserService userService = UserServiceFactory.getUserService();

    // Only logged-in users can post messages
    if (!userService.isUserLoggedIn()) {
      response.sendRedirect("/shoutbox");
      return;
    }

    String text = request.getParameter("text");
    String email = userService.getCurrentUser().getEmail();

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity messageEntity = new Entity("Message");
    messageEntity.setProperty("text", text);
    messageEntity.setProperty("email", email);
    messageEntity.setProperty("timestamp", System.currentTimeMillis());
    datastore.put(messageEntity);

    // Redirect to /shoutbox. The request will be routed to the doGet() function above.
    response.sendRedirect("/shoutbox");
  }
  public String convertJson(ArrayList<Object> messages) throws IOException {
    Gson gson = new Gson();
    String json = gson.toJson(messages);
    System.out.println(json);
    return json;

  }
}

