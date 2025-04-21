const form = document.getElementById("card-form");
const questionInput = document.getElementById("question");
const answerInput = document.getElementById("answer");
const container = document.getElementById("cards-container");

const API_URL = "http://localhost:8080/api/cards";

function renderCards(cards) {
    container.innerHTML = "";
    cards.forEach((card, index) => {
      const div = document.createElement("div");
      div.className = "card";
      div.innerHTML = `
        <strong>Вопрос:</strong> ${card.question}<br>
        <strong>Ответ:</strong> ${card.answer}<br>
        <button onclick="deleteCard(${index})">Удалить</button>
      `;
      container.appendChild(div);
    });
  }  

async function loadCards() {
  const res = await fetch(API_URL);
  const data = await res.json();
  renderCards(data);
}

form.addEventListener("submit", async (e) => {
  e.preventDefault();

  const card = {
    question: questionInput.value,
    answer: answerInput.value,
  };

  await fetch(API_URL, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(card),
  });

  questionInput.value = "";
  answerInput.value = "";
  loadCards();
});

async function deleteCard(index) {
    await fetch(`${API_URL}/${index}`, {
      method: "DELETE"
    });
    loadCards();
  }
  
// Первоначальная загрузка карточек
loadCards();
