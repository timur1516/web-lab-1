const r_input = document.getElementById("r_text");
const x_checkboxes = document.querySelectorAll(".x_checkbox");

let active_x_checkbox = null;

x_checkboxes.forEach(checkbox => {checkbox.addEventListener("change", () => {
    if(checkbox === active_x_checkbox){
        active_x_checkbox = null;
    }
    else{
        if(active_x_checkbox !== null) active_x_checkbox.checked = false;
        active_x_checkbox = checkbox;
    }
})})

async function submitForm(event) {
    event.preventDefault(); // Предотвращаем перезагрузку страницы

    // Получаем данные формы
    const formData = new FormData(event.target);

    //Извлекаем данные формы
    const x = formData.get("x_check");
    const y = formData.get("y_r");
    const r = formData.get("r_text");

    //Проводим валидацию
    if(x == null || y == null || !(r >= -5 && r <= 5)) return;

    let table_ref = document.getElementById("h_t");
    table_ref.insertRow(-1);

    let newRow = table_ref.insertRow(-1);

    let xCell = newRow.insertCell(0);
    let yCell = newRow.insertCell(1);
    let rCell = newRow.insertCell(2);
    let gotCell = newRow.insertCell(3);

    let xText = document.createTextNode(x.toString());
    let yText = document.createTextNode(y.toString());
    let rText = document.createTextNode(r.toString());
    let gotText = document.createTextNode(true.toString());

    xCell.appendChild(xText);
    yCell.appendChild(yText);
    rCell.appendChild(rText);
    gotCell.appendChild(gotText);

    const queryParams = new URLSearchParams(formData).toString();

    // Отправляем GET запрос на сервер
    const response = await fetch(`/fcgi-bin/server.jar?${queryParams}`);

    // Получаем текстовый ответ
    const responseData = await response.json();

    console.log(responseData.get("answer"));
}

r_input.addEventListener('paste', event => event.preventDefault());
r_input.addEventListener('input', function ()  {
    if (!/^-?\d*\.?\d*$/.test(this.value)) this.value = this.value.slice(0, -1);
});