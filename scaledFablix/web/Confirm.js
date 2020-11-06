function handleResult(resultData) {
    let confirmTableBodyElement = jQuery("#confirm_table_body");
    let total=0;
    let rowHTML = "";
    for (let i = 1; i < resultData.length; i++) {
        total+= resultData[i]["price"]*resultData[i]["qty"];
        console.log(resultData[i]["title"]);
        console.log(resultData[i]["qty"]);
        rowHTML += "<tr>";
        rowHTML += "<td>" + resultData[i]["sale_id"] + "</td>";
        rowHTML += "<td>" + resultData[i]["title"] + "</td>";
        rowHTML += "<td>" + resultData[i]["qty"] + "</td>";
        rowHTML += "<td>" + resultData[i]["price"] + "</td>";
        rowHTML += "</tr>";
    }
    confirmTableBodyElement.append(rowHTML);
    $("#total").text("The Total Price is : " + total.toString());
    $("#back").attr("href", "list.html?" + resultData[0]["curURL"]);
}


jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/confirm", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});