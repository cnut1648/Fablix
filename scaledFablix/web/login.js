import $ from 'jquery';
window.jQuery = $;
window.$ = $;
import 'bootstrap';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'jquery-backstretch';

let login_form = $("#login-form");

function handleLoginResult(resultDataString) {
    console.log("handle login response");
    let resultDataJson = JSON.parse(resultDataString);

    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    // If login succeeds, it will redirect the user to list.html
    if (resultDataJson["status"] === "success") {
        $("#welcome").modal("show");
        setTimeout(function () {
            window.location.replace("main.html");
        }, 1000);
    } else {
        // If login fails, the web page will display
        // error messages on <div> with id "login_error_message"
        $("#login_error_message").addClass('alert alert-warning');
        $("#login_error_message").html(resultDataJson["message"]);
        grecaptcha.reset();
    }
}

function submitLoginForm() {
    console.log("submit login form");

    console.log("checking grecaptcha")
    if (grecaptcha.getResponse().length === 0){

        $("#login_error_message").addClass('alert alert-warning');
        $("#login_error_message").html("Please check the reCAPTCHA.");
        return;
    }
    $.ajax(
        "api/login", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: login_form.serialize(),
            success: handleLoginResult
        }
    );
};

$(document).ready(function() {
    $.backstretch([
        "utils/movie1.jpg",
        "utils/movie2.jpg",
        "utils/movie3.jpg",
    ], {
        fade: 750,
        duration: 3000
    });

    $('#reset-button').click(function(){
        $('#login-form')[0].reset();
    })
// Bind the submit action of the form to a handler function
    login_form.submit(function(formSubmitEvent){
        formSubmitEvent.preventDefault();
        console.log("checking grecaptcha")
        if (grecaptcha.getResponse().length === 0){

            $("#login_error_message").addClass('alert alert-warning');
            $("#login_error_message").html("Please check the reCAPTCHA.");
        }
        else{
            submitLoginForm();
        }
    });
});
