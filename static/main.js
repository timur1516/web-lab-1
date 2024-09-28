// Авто сохранение при обновлении страницы
let tableData = [];
window.addEventListener("load", loadTableData);
// Подобие enum для обработки исключений и вывода сообщений
const message_type = Object.freeze({
    OK: 1,
    EMPTY_FIELDS: 2,
    WRONG_X: 3,
    SOME_SERVER_ERROR: 4
})

// Значение X (храним отдельно так как авто обновляется при вводе)
let x_value = 0;

// Бинарный ввод X ;)
const x_checkboxes = document.getElementsByName("x_checkbox_input");
x_checkboxes.forEach(checkbox => {checkbox.addEventListener("change", () => {
    let str = "0000"
    for(let i = 0; i < x_checkboxes.length; i++){
        str = str.slice(0, i) + (document.getElementById(i).checked ? "1" : "0") + str.slice(i + 1);
    }
    x_value = parseInt(str, 2);
    if(x_value >= 8) x_value -= 16;
    document.getElementById("x_value_label").textContent = x_value;
})});

//Функция отправки запроса на сервер и получения ответа
async function submitForm(event) {
    // Предотвращаем перезагрузку страницы
    event.preventDefault();

    //Извлекаем данные формы
    const formData = new FormData(event.target);
    const x = x_value
    const y = formData.get("y_text_input");
    const r = formData.get("r_radio_input");

    //Проводим валидацию
    let result = validate_data(x, y, r);
    show_user_message(result);
    if(result !== message_type.OK) return;

    //Выполняем запрос
    const queryParams = new URLSearchParams();
    queryParams.append("X", x);
    queryParams.append("Y", y);
    queryParams.append("R", r);

    let responseData;
    try {
        let response = await fetch(`/fcgi-bin/server.jar?${queryParams.toString()}`);
        if(!response.ok){
            show_user_message(message_type.SOME_SERVER_ERROR);
            return;
        }
        responseData = await response.json();

        //Записываем данные в историею
        let execution_time = responseData.executionTime;
        let hit = responseData.hit;
        add_data_to_history(x, y, r, hit, execution_time);

    } catch (error){
        show_user_message(message_type.SOME_SERVER_ERROR);
    }
}

// Метод генерации сообщений пользователю
function show_user_message(message){
    let error_field = document.getElementById("error_field");
    error_field.style.visibility = "visible";
    switch (message){
        case message_type.EMPTY_FIELDS:
            error_field.textContent = "Пожалуйста заполните все поля!";
            break;
        case message_type.WRONG_X:
            error_field.textContent = "Значение X должно быть от -3 до 5";
            break;
        case message_type.SOME_SERVER_ERROR:
            error_field.textContent = "Упс... Произошла ошибка при работе с сервером. Пожалуйста, повторите попытку позже.";
            break;
        default:
            error_field.style.visibility = "hidden";
            error_field.textContent = "";
            break;
    }
}

// Функция валидации данных формы
function validate_data(x, y, r){
    if(x == null || y == null || r == null ||  y === "") return message_type.EMPTY_FIELDS;
    if(x < -3 || x > 5) return message_type.WRONG_X;
    return message_type.OK;
}

// Функция добавления данных в таблицу
function add_data_to_history(x, y, r, hit, execution_time){
    let table_ref = document.querySelector("#history_table tbody");

    let newRow = table_ref.insertRow(0);

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
    let executionTimeText = document.createTextNode(execution_time.toString() + "мкс");

    tableData.push([xText.textContent, yText.textContent, rText.textContent, hitText.textContent, timeText.textContent, executionTimeText.textContent]);
    sessionStorage.setItem("tableData", JSON.stringify(tableData));

    xCell.appendChild(xText);
    yCell.appendChild(yText);
    rCell.appendChild(rText);
    hitCell.appendChild(hitText);
    timeCell.appendChild(timeText);
    executionTimeCell.appendChild(executionTimeText);
}

// Функция для восстановления данных таблицы из sessionStorage
function loadTableData() {
    let savedData = sessionStorage.getItem("tableData");
    if (savedData) {
        tableData = JSON.parse(savedData);
        const table = document.querySelector("#history_table tbody");
        tableData.reverse().forEach(row => {
            let newRow = table.insertRow(-1);
            row.forEach(element => {
                let newCell = newRow.insertCell(-1);
                newCell.appendChild(document.createTextNode(element));
            });
        });
    }
}