



(() => {
    document.querySelector("#main").innerHTML = `
        <div id="response-text">
        </div>
        
        <p> Hvad har du på sinde i dag?</p>
    <form id="submit-request">
    
        <select id="city-select">
 
    
</select>


        <input id="request-year" type="number" placeholder="vælg årstal"/>
        <input id="request-text" type="text" placeholder="hvad er dit spørgsmål?"/>
        <button type="submit">Submit</button>
    </form>
    
    

    `
})();

const requestYear = document.querySelector("#request-year");
const form = document.querySelector("#submit-request");
const responseText = document.querySelector("#response-text");
const requestText = document.querySelector("#request-text");

const cities = await loadCities();
const citySelect = document.querySelector("#city-select");

cities.forEach(city => {
    const option = document.createElement("option");
    option.value = city.id;

    option.textContent = city.name;

    citySelect.appendChild(option);
})

form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const body = {
        prompt: requestText.value,
        year: requestYear.value,
        cityCode: 122121
    };

    console.log(requestText.value);
    console.log(body);

    const response = await fetch("/api/openai/chat", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(body),
        credentials: "include"
    });

    const data = await response.text();

    const p = document.createElement("p");
    p.textContent = data;

    responseText.appendChild(p);

})


async function loadCities () {

    const response = await fetch("/api/ds/cities", {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include"
    });

    const data = await response.json();

    return Object.entries(data).map(([id, name]) => ({ id, name }));
}

