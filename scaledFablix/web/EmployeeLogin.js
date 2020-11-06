import $ from 'jquery';
window.jQuery = $;
window.$ = $;
import 'bootstrap';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'jquery-backstretch';


let login_form = $("#login-form");
/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleLoginResult(resultDataString) {
    console.log(resultDataString);
    console.log("handle login response");
    let resultDataJson = JSON.parse(resultDataString);
    // let resultDataJson = resultDataString;

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
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitLoginForm(formSubmitEvent) {
    console.log("submit login form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();
    $.ajax(
        "api/Elogin", {
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
    login_form.submit(submitLoginForm);
});