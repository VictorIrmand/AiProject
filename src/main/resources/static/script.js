(() => {
    document.querySelector("#main").innerHTML =
        <div id="response-text">
        </div>
    <form class="submit-request">
        <input id="request-year" type="number"/>
        <input id="request-text" type="text"/>
    </form>
})();

const requestYear = document.querySelector("#request-year");
const form = document.querySelector("#submit-request");
const responseText = document.querySelector("#request-text");

form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const response = await fetch("/api/openai/chat", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(requestText.value, requestYear.value),
        credentials: "include"
    });

    const data = await response.text();

    const p = document.createElement("p");
    p.textContent = data;

    responseText.appendChild(p);

})