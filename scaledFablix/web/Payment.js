let card_form = $("#card_form");
function handleResult(resultData) {
    let PaymentTableBodyElement = jQuery("#payment_table_body");
    let total=0;
    let rowHTML = "";
    for (let i = 1; i < resultData.length; i++) {
        total+= resultData[i]["price"]*resultData[i]["qty"];
        console.log(resultData[i]["title"]);
        rowHTML += "<tr>";

        rowHTML += "<td>" + resultData[i]["title"] + "</td>";
        rowHTML += "<td>" + resultData[i]["qty"] + "</td>";
        rowHTML += "<td>" +"$"+ resultData[i]["price"].toString() + "</td>";
        rowHTML += "</tr>";
    }
    PaymentTableBodyElement.append(rowHTML);
    $("#total").text("The Total Price is : $" + total.toString());
    $("#back").attr("href", "list.html?" + resultData[0]["curURL"]);


}

function handlePaymentResult(resultDataString) {
    console.log("handle Payment response");
    a = resultDataString
    let resultDataJson = JSON.parse(resultDataString);
    // let resultDataJson = resultDataString;

    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    // If login succeeds, it will redirect the user to index.html
    if (resultDataJson["status"] === "success") {
        window.location.replace("Confirm.html");
    } else {
        // If login fails, the web page will display
        // error messages on <div> with id "login_error_message"
        console.log("show error message");
        console.log(resultDataJson["message"]);
        $("#payment_error_message").addClass('alert alert-warning');
        $("#payment_error_message").text(`<strong>ERROR!</strong> ${resultDataJson["message"]}`);
    }
}

function submitCardForm(formSubmitEvent) {
    console.log("submit card form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/Payment", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: card_form.serialize(),
            success: handlePaymentResult
        }
    );
}


jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/Payment", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});

card_form.submit(submitCardForm);