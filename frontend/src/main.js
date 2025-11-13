import "./style.css"


document.querySelector('#app').innerHTML = `

<div class="bg-[#1e1e1e] text-[#f8f8ff] w-screen h-screen text-white flex flex-col items-center">
  <h1 class=" text-[#f8f8ff] font-sans mt-10 text-3xl">Danmarks boligpriser gennem tiden</h1>
  
  
  <div class=" flex flex-col gap-3 h-3/5 w-4/5 border-white border-[0.5px] overflow-y-auto mt-10 rounded-md p-5" id="chat-box">
  
</div>
  
  <div class="flex w-4/5 mt-3">
  <div class="flex flex-row gap-2 w-full items-center justify-center">
    <select
      id="area-select"
      class="bg-[#2a2b32] text-[#f8f8ff] border border-[#3e3f4b] rounded-lg px-3 py-2
             focus:outline-none focus:ring-2 focus:ring-[#10a37f] transition-all duration-150"
    >
      <option value="">Vælg et område</option>
    </select>

    <select
      id="start-year-select"
      class="bg-[#2a2b32] text-[#f8f8ff] border border-[#3e3f4b] rounded-lg px-3 py-2
             focus:outline-none focus:ring-2 focus:ring-[#10a37f] transition-all duration-150"
    >
      <option value="">Start-år</option>
    </select>
    
    <span class="text-[#f8f8ff] text-sm">→</span>
    
    <select
      id="end-year-select"
      class="bg-[#2a2b32] text-[#f8f8ff] border border-[#3e3f4b] rounded-lg px-3 py-2
             focus:outline-none focus:ring-2 focus:ring-[#10a37f] transition-all duration-150"
    >
      <option value="">Slut-år</option>
    </select>
<form class="flex flex-grow" id="prompt-form">
  <input
    class="flex-grow bg-[#2a2b32] text-[#f8f8ff] border border-[#3e3f4b] rounded-lg px-3 py-2
           focus:outline-none focus:ring-2 focus:ring-[#10a37f] transition-all duration-150 w-full"
    type="text"
    id="prompt-input"
    disabled
    placeholder="Vælg område, start-år og slut-år for at bruge chatten."
  />
</form>
  </div>
</div>

</div>
`
// dom elements
const startYearSelect = document.querySelector("#start-year-select");
const endYearSelect = document.querySelector("#end-year-select");
const areaSelect = document.querySelector("#area-select");
const chatBox = document.querySelector("#chat-box");
const promptInput = document.querySelector("#prompt-input");
const promptForm = document.querySelector("#prompt-form");

// dom control


// url
const BASE = "http://localhost:8080";

// init methods
setAvailableYears();
await setAvailableAreas();

// eventlisteners
startYearSelect.addEventListener("change", async (e) => {
    e.preventDefault();
    await provideEj67Context()
})
endYearSelect.addEventListener("change", async (e) => {
    e.preventDefault();
    await provideEj67Context()
})
areaSelect.addEventListener("change", async (e) => {
    e.preventDefault();
    await provideEj67Context()
})

promptForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    await askAi();
})


// helper methods
function setAvailableYears() {

    for (let i = 1993; i <= 2024; i++) {
        const opt1 = document.createElement("option");
        opt1.value = i;
        opt1.textContent = i;

        const opt2 = document.createElement("option");
        opt2.value = i;
        opt2.textContent = i;

        startYearSelect.appendChild(opt1);
        endYearSelect.appendChild(opt2);
    }
}

async function setAvailableAreas() {
    const url = BASE + "/api/ds/areas";
    console.log("Henter fra:", url);

    const res = await fetch(url, {
        method: "GET",
        credentials: "include",
        headers: {
            "Accept": "application/json",
        },
    });

    const data = await res.json();

    Object.entries(data).forEach(([id, text]) => { // for hver key value par der er med ID og Text i JSON inde i Mono.
        const option = document.createElement("option");
        option.textContent = text;
        option.value = id;
        areaSelect.appendChild(option);
    });
}


async function provideEj67Context() {
    if (
        areaSelect.value.trim() !== "" &&
        startYearSelect.value.trim() !== "" &&
        endYearSelect.value.trim() !== ""
    ) {
        promptInput.placeholder = "Venter på svar fra Danmarks Statistik...";
        const url = BASE + `/api/ds/areas/${areaSelect.value}/ej67`;
        console.log("Henter fra:", url);

        const dto = {
            startYear: startYearSelect.value,
            endYear: endYearSelect.value,
            ejendomsKategori: ["0111"] // hardcoded midlertidigt til "Ejerlejligheder" koden, men kan også være andre kategorier.
        }

        const res = await fetch(url, {
            method: "POST",
            credentials: "include",
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json",
            },
            body: JSON.stringify(dto),
        });

        if (res.ok) {
            promptInput.disabled = false;
            promptInput.placeholder = "Spørg kunstig intelligens om boligpriser ud fra de valgte år og område.";
        } else {
            promptInput.disabled = true;
            promptInput.placeholder = "Kunne ikke hente data fra Danmarks Statistik.";
        }

    } else {
        promptInput.disabled = true;
    }
}

async function askAi() {
    chatBox.appendChild(buildChatElementUser(promptInput.value));
    const loadingText = buildChatElementAi("Tænker over dit svar...");
    chatBox.appendChild(loadingText);

    const url = BASE + `/api/openai/chat`;
    console.log("Henter fra:", url);

    const res = await fetch(url, {
        method: "POST",
        credentials: "include",
        headers: {
            "Content-Type": "application/json",
            "Accept": "application/json",
        },
        body: JSON.stringify(promptInput.value),
    });

    const responseText = await res.text();

    chatBox.removeChild(loadingText); // ryder midlertidig loading tekst.
    chatBox.appendChild(buildChatElementAi(responseText)); // tilføjer ny prompt.

    promptInput.value = ""; // ryder input feltet.

}


function buildChatElementUser(promptText) {
    const promptDiv = document.createElement("div");
    promptDiv.classList.value = "self-start flex flex-col items-start justify-center max-w-[60%] gap-1"
    const navn = document.createElement("p");
    navn.textContent = "Du spurgte:"
    promptDiv.appendChild(navn);

    const container = document.createElement("div");
    container.classList.value = "self-start bg-gray-700 rounded-md p-2 w-fit "


    const promptP = document.createElement("p");
    promptP.textContent = promptText;
    container.appendChild(promptP);
    promptDiv.appendChild(container);

    return promptDiv;
}

function buildChatElementAi(promptText) {
    const promptDiv = document.createElement("div");
    promptDiv.classList.value = "self-end flex flex-col items-start justify-center max-w-[60%] gap-1"
    const navn = document.createElement("p");
    navn.textContent = "AI svarede:"
    promptDiv.appendChild(navn);

    const container = document.createElement("div");
    container.classList.value = "self-end bg-gray-700 rounded-md p-2 w-fit "


    const promptP = document.createElement("p");
    promptP.textContent = promptText;
    container.appendChild(promptP);
    promptDiv.appendChild(container);

    return promptDiv;
}


