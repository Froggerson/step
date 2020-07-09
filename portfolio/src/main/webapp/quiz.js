function makeQuiz(quizContainer, questions) {
  let output = [];
  questions.forEach((currentQuestion, questionNumber) => {
    output.push(
      `<input type="checkbox" id="question${questionNumber}" name="question${questionNumber}" 
            value="yes">
            <label for="question${questionNumber}">${currentQuestion}</label></br>`
    );
  });
  quizContainer.innerHTML = output.join("");
}
function showResults() {
  let count = 0;
  const resultHeader = document.getElementById("result-header");
  resultHeader.style = "visibility:visible;";
  questions.forEach((currentQuestion, questionNumber) => {
    if (document.getElementById(`question${questionNumber}`).checked === true) {
      count++;
    }
  });
  document.getElementById(
    "score"
  ).textContent = `Congratulations! Your score was ${count}.`;

  if (count <= 1) {
    document.getElementById("score-comment").textContent =
      "You are definitely not a cat.";
  } else if (count <= 7) {
    document.getElementById("score-comment").textContent =
      "You are not a cat, but you do have some catlike tendencies.";
  } else if (count <= 10) {
    document.getElementById("score-comment").textContent =
      "You might be a cat or some other animal.";
  } else {
    document.getElementById("score-comment").textContent =
      "Meow. You are a cat.";
  }
  const resultsDiv = document.getElementById("results");
  resultsDiv.scrollIntoView();
}

const quizContainer = document.getElementById("quiz");
const resultsContainer = document.getElementById("results");
const submitButton = document.getElementById("submit");
const questions = [
  "I am quadrupedal.",
  "I have around 230-250 bones",
  "I have a tail.",
  "I spend 12 to 16 hours a day sleeping.",
  "I groom myself for a good portion of my waking hours.",
  "I have pretty good peripheral and night vision.",
  "I have 18 or more toes.",
  "I have whiskers.",
  "I am more active during the day or dawn.",
  "I prefer to stay indoors.",
  "I am lactose intolerant.",
  "I can not taste sweet things",
  "I can drink sea water",
  "My heart beats from around 110 to 140 beats a minute.",
];

makeQuiz(quizContainer, questions);
submitButton.addEventListener("click", showResults);