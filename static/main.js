//Имитация radio используя checkbox
const x_checkboxes = document.getElementsByName("x_checkbox_input");
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

//Защита поля r от вставки и ввода чего-либо кроме чисел
const r_text_input = document.getElementById("r_text");
r_text_input.addEventListener('paste', event => event.preventDefault());
r_text_input.addEventListener('input', function ()  {
    if (!/^-?\d*\.?\d*$/.test(this.value)) this.value = this.value.slice(0, -1);
});

//Функция отправки запроса на сервер и получения ответа
async function submitForm(event) {
    // Предотвращаем перезагрузку страницы
    event.preventDefault();

    //Извлекаем данные формы
    const formData = new FormData(event.target);
    const x = formData.get("x_checkbox_input");
    const y = formData.get("y_radio_input");
    const r = formData.get("r_text_input");

    //Проводим валидацию
    if(!is_valid_data(x, y, r)) return;

    //Выполняем запрос измеряя время
    const queryParams = new URLSearchParams(formData).toString();

    let start_time_point = Date.now();
    const response = await fetch(`/fcgi-bin/server.jar?${queryParams}`);
    const responseData = await response.json();

    let end_time_point = Date.now();

    //Записываем данные в историию
    let execution_time = end_time_point - start_time_point;
    let hit = responseData.answer;
    add_data_to_history(x, y, r, hit, execution_time);
}

//Функция валидации данных формы
function is_valid_data(x, y, r){
    if(x == null || y == null || r === ""){
        alert("Пожалуйста заполните все поля!");
        return false;
    }
    if(r < -5 || r > 5){
        alert("Значение R должно быть в пределах [-5; 5]!");
        return false;
    }
    return true;
}

//Функция добавления данных в таблицу
function add_data_to_history(x, y, r, hit, execution_time){
    let table_ref = document.getElementById("history_table");

    let newRow = table_ref.insertRow(-1);

    let xCell = newRow.insertCell(0);
    let yCell = newRow.insertCell(1);
    let rCell = newRow.insertCell(2);
    let hitCell = newRow.insertCell(3);
    let timeCell = newRow.insertCell(4);
    let executionTimeCell = newRow.insertCell(5);

    let xText = document.createTextNode(x.toString());
    let yText = document.createTextNode(y.toString());
    let rText = document.createTextNode(r.toString());
    let hitText = document.createTextNode(hit.toString());
    let timeText = document.createTextNode(new Date().toLocaleTimeString());
    let executionTimeText = document.createTextNode(execution_time.toString());

    xCell.appendChild(xText);
    yCell.appendChild(yText);
    rCell.appendChild(rText);
    hitCell.appendChild(hitText);
    timeCell.appendChild(timeText);
    executionTimeCell.appendChild(executionTimeText);
}