

function makeQuiz(){
    questions.forEach((currentQuestion,questionNumber)=>{
        output.push(
            `<input type="checkbox" id="question${questionNumber}" name="question${questionNumber}" 
            value="yes">
            <label for="question${questionNumber}">${currentQuestion}</label></br>`);
    });
    quizContainer.innerHTML=output.join('');
}
function showResults(){
    let count = 0;
    questions.forEach( (currentQuestion, questionNumber) => {
        let currentValue = document.getElementById(`question${questionNumber}`);
        if(currentValue.checked === true){
            count++;
        }
    });
    let resultsCode = `<h2>Your Results:</h2><p>Your score was ${count}</p>`;

    if(count<= 1){
        resultsCode += `<p>You are definitely not a cat. </p>`;
    }else if (count <=7){
        resultsCode += `<p>You are not a cat, but you do have some catlike tendencies.</p>`;
    }else if (count <=10){
        resultsCode += `<p>You might be a cat.</p>`;
    }else{
        resultsCode += `<p>You are a cat.</p>`;
    }
    resultsContainer.innerHTML = resultsCode;
    window.scrollBy(000, 400);
}

const quizContainer = document.getElementById('quiz');
console.log(quizContainer);
const resultsContainer = document.getElementById('results');
const submitButton = document.getElementById('submit');
const output=[];
const questions = ['I am quadrupedal.',
'I have around 230-250 bones',
'I have a tail.',
'I spend 12 to 16 hours a day sleeping.',
'I groom myself for a good portion of my waking hours.',
'I have pretty good peripheral and night vision.',
'I have 18 or more toes.',
'I have whiskers.',
'I am more active during the day or dawn.',
'I prefer to stay indoors.',
'I am lactose intolerant.',
'I can not taste sweet things',
'I can drink sea water',
'My heart beats from around 110 to 140 beats a minute.',
]

makeQuiz();
console.log("Made the quiz!");
submitButton.addEventListener('click', showResults);