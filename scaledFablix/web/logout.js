import $ from 'jquery';
window.jQuery = $;
window.$ = $;
import 'bootstrap';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'jquery-backstretch';

$(document).ready(function() {
    $.ajax("api/logout",{
        method: "POST",
        success: function(data){
            console.log(data);
            data = JSON.parse(data);
            console.log(data["name"]);
            let name = data.name;
            if (name !== null){
                name = name.split(",");
                name = name.join(" ");
                $("#name").text(name + ", have a good day!");
            }
            setTimeout(function(){
                window.location.replace("login.html");
                }, 1000);
        }
        }
    )
    $.backstretch([
        "utils/movie1.jpg",
        "utils/movie2.jpg",
        "utils/movie3.jpg",
    ], {
        fade: 750,
        duration: 3000
    });
});
