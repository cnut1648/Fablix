import $ from 'jquery';
window.jQuery = $;
window.$ = $;
import 'bootstrap';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'jquery-backstretch';


let add_movie_form = $("#movie-form");
let add_star_form = $("#star-form");


function handleMovieResult(resultDataString) {
    console.log("handle Movie response");
    let message=resultDataString["errorMessage"]

    $("#error_message").addClass('text-center');
    $("#error_message").html(message);
    $("#error-msg").modal('show');
    setTimeout(function(){
        $("#error-msg").modal('hide');
    }, 300000);

}

function submitMovieForm(formSubmitEvent) {
    formSubmitEvent.preventDefault();
    console.log("movie ");
    console.log(add_movie_form.serialize());
    $.ajax(
        "api/dashboard", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: add_movie_form.serialize(),
            success: (resultData) => handleMovieResult(resultData)
        }
    );
};

function submitStarForm(formSubmitEvent) {
    formSubmitEvent.preventDefault();
    console.log("star ");
    console.log(add_star_form.serialize());

    $.ajax(
        "api/dashboard", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: add_star_form.serialize(),
            success: (resultData) => handleMovieResult(resultData)
        }
    );
};


add_movie_form.submit(submitMovieForm);
add_star_form.submit(submitStarForm);


$(document).ready(function(){
    $.backstretch([
        "utils/movie1.jpg",
        "utils/movie2.jpg",
        "utils/movie3.jpg",
    ], {
        fade: 750,
        duration: 3000
    });

    $("#meta-btn").click(function(){
        // metadata
        console.log("cccc");
        jQuery.ajax({
            dataType: "json",  // Setting return data type
            method: "GET",// Setting request method
            url: "api/dashboard", // Setting request url, which is mapped by StarsServlet in Stars.java
            success: function(resultData){
                console.log(resultData);
                let TableBodyElement = jQuery("#table_body");
                let rowHTML = "";
                for (let i = 0; i < resultData.length; i++) {

                    // Concatenate the html tags with resultData jsonObject
                    let name = resultData[i]["name"].split(",");
                    let type = resultData[i]["type"].split(",");
                    for (let x = 0; x < name.length - 1; x++) {
                        rowHTML += "<tr>";
                        rowHTML += "<th>" + resultData[i]["tablename"] + "</th>";
                        rowHTML += "<th>" + name[x] + "</th>";
                        rowHTML += "<th>" + type[x] + "</th>";
                        rowHTML += "</tr>";
                    }
                }
                TableBodyElement.append(rowHTML);

            }
        });
    });

});