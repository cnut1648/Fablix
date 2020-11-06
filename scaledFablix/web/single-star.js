import $ from 'jquery';
window.jQuery = $;
window.$ = $;
import 'bootstrap';
import 'bootstrap/dist/css/bootstrap.min.css';
import getParameterByName from 'utilJS';
import Vue from "vue";
import navbar from 'navbar';

new Vue({
    render: h=>h(navbar)
}).$mount("#navbar");


function handleResult(resultData) {
    console.log("handleResult: populating star info from resultData");
    $("#back").attr("href", "list.html?" + resultData[0]["curURL"]);
    resultData = resultData.slice(1);


    // populate the star info h3
    // find the empty h3 body by id "star_info"
    let starInfoElement = jQuery("#star_info");
    let query_dob = resultData[0]["star_dob"];
    let dob = (query_dob == null) ? "N/A": query_dob;
    // append two html <p> created to the h3 body, which will refresh the page
    starInfoElement.append("<p>Star Name: " + resultData[0]["star_name"] + "</p>" +
        "<p>Date Of Birth: " + dob + "</p>");
    console.log("handleResult: populating movie table from resultData");

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += `<th><a class="tv" href='single-movie.html?movieid=${resultData[i]["movie_id"]}'>
${resultData[i]["movie_title"]}</a></th>`;
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }

    let back_row='';
    jQuery("#back").append(back_row);
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let starId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-star?id=" + starId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});