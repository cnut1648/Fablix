import $ from 'jquery';
window.jQuery = $;
window.$ = $;
import 'bootstrap';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'jquery-backstretch';
import Vue from 'vue';
import navbar from "./utils/navbar";

new Vue({
    render: h=>h(navbar)
}).$mount("#navbar");

let search_form = $("#search-form");
let Dashboard_entry = $("#db_entry");

function submitSearchForm(formSubmitEvent) {
    console.log("submit search form");
    let param = $("#search-form :input").filter(
            function(index, element){
                return $(element).val() != '';
            }
        ).serialize()
    formSubmitEvent.preventDefault();
    if (param === ""){
        $("#error_message").addClass('alert alert-warning');
        $("#error_message").text("plase enter one search");
    }
    else {
        param += "&N=25&page=1";
        window.location.replace("list.html?" + param);
    }
}
// Bind the submit action of the form to a handler function
search_form.submit(submitSearchForm);

function handleResult(resultData)
{
    let resultDataJson = JSON.parse(resultData);
    let status=resultDataJson["status"]
    if (status==="yes")
    {
        window.location.replace("Dashboard.html");
    }
    else{
        Console.log("not employee")
    }
}

function submitdb(formSubmitEvent) {
    console.log(" Check dashboard");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();
    $.ajax(
        "api/checkdb", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: Dashboard_entry.serialize(),
            success: (resultData) => handleResult(resultData)
        }
    );
};

Dashboard_entry.submit(submitdb);
$(document).ready(function() {
    $.backstretch([
        "utils/movie1.jpg",
        "utils/movie2.jpg",
        "utils/movie3.jpg",
    ], {
        fade: 750,
        duration: 3000
    });

    $('#genre-btn').click(function(){
        $.ajax(
            'api/allGenres',{
                method: 'GET',
                dataType: 'json',
                success: function(JsonData){
                    new Vue({
                      el: '#genre-body',
                      data: {
                          ret: JsonData,
                      },
                        methods:{
                          genreLink: function(g){
                              return './list.html?genre=' + g + "&N=25&page=1";
                          }
                        },
                      template: `<div class="row">
    <a class="col-md-4 stretched-link" v-for="r in ret" :key="r.id" :href="genreLink(r.genre)" :class="'genre'+r.id" >{{r.genre}}</a>
</div>`
                    });
                    $("#genre-lucky").click(function(){
                        let g = JsonData[Math.floor(JsonData.length * Math.random())];
                        let h = $('.genre' + g['id']).attr('href');
                        window.location.href = h;
                    })
                }
            }
        )
    })

    $('#title-btn').click(function(){
        let v = new Vue({
            // el: "#title-body",
            methods: {
                f: () => "*ABCDEFGHIJKLMNOPQRSTUVWXYZ".split(""),
                genreLink: function(b){
                    return './list.html?begin=' + b + "&N=25&page=1";
                }
            },
            template: `<div class='row'>
        <a class="col-md-4 stretched-link" v-for="i in f()" :href="genreLink(i)" :class="'title' + i">
            {{i}}
        </a></div>`
        }).$mount("#title-body");

        $("#title-lucky").click(function(){
            let a = v.f();
            let g = a[Math.floor(a.length * Math.random())];
            let h = $('.title' + g).attr('href');
            window.location.href = h;
        })
    })
});
