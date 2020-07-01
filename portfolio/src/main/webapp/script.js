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

/**
 * Adds a random cat to the page.
 */
function addCat() {
  const cats = [
    "/images/bowl_cat.gif",
    "/images/fidget_cat.gif",
    "/images/fluffy_cat.gif",
    "/images/nail_cat.gif",
    "/images/paw_cat.gif",
    "/images/scarf_cat.jpeg",
    "/images/vacuum_cat.gif",
    "/images/turtle_cat.jpeg",
    "/images/bongo_cat.gif",
    "/images/cat_nerd.jpeg",
    "/images/cat_phone.gif",
    "/images/cat_stare.jpg",
    "/images/cool_cat.jpg",
    "/images/crying_cat.jpg",
    "/images/disgust_cat.jpeg",
    "/images/polite_cat.jpg",
    "/images/relaxed_cat.jpg",
    "/images/shark_cat.jpeg",
    "/images/swag_cat.jpg",
  ];

  // Pick a random greeting.
  const cat = cats[Math.floor(Math.random() * cats.length)];

  // Add it to the page.
  const catPic = document.getElementById("cat-pic");
  catPic.src = cat;
}

function createCommentElement(comment) {
  console.log("finding comment....");
  const commentElement = document.createElement("div");
  commentElement.className = "comment";

  const titleElement = document.createElement("h1");
  titleElement.innerText = comment[2];
  const usernameElement = document.createElement("p");
  usernameElement.innerText = comment[1];
  const messageElement = document.createElement("p");
  messageElement.innerText = comment[3];

  commentElement.appendChild(titleElement);
  commentElement.appendChild(usernameElement);
  commentElement.appendChild(messageElement);
  return commentElement;
}

function loadComments() {
  console.log("Loading comments now!");
  fetch("/data")
    .then((response) => response.json())
    .then((comments) => {
      const commentElement = document.getElementById("comments-container");
      comments.forEach((comment) => {
        commentElement.appendChild(createCommentElement(comment));
        console.log("Comment successfully inserted!");
      });
    });
}
