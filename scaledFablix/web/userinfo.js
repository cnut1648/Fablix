import $ from 'jquery';
window.jQuery = $;
window.$ = $;
import 'bootstrap';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'jquery-backstretch';
import Vue from 'vue';

$(document).ready(function() {
    console.log("user getting")
    $.ajax("api/user",{
            method: "GET",
            success: function(data){
                console.log("success!")
                data = JSON.parse(data);
                new Vue({
                    el: "#app",
                    data: {
                        name: data.name,
                        email: data.email,
                        address: data.address,
                        hours: new Date().getHours(),
                    },
                    methods:{
                      back: function () {
                            window.document.location.replace("main.html");
                      }
                    },
                    template: `
                        <section class="container h-100">
                            <div class="d-flex h-100 justify-content-center align-items-center">
                                <div class="jumbotron col-10 col-md-8 col-lg-6">
                                    <h1 class="display-4 text-center" v-if="hours < 12">Good morning!</h1>
                                    <h1 class="display-4 text-center" v-else-if="hours >= 12 && hours < 18">Good afternoon!</h1>
                                    <h1 class="display-4 text-center" v-else>Good evening!</h1>
                                    <p class="lead text-center">Hello &#128540; {{name.split(",").join(" ")}}</p>
                                    <hr class="my-3">
                                    <p class="lead text-center">Your address is {{address}}.</p>
                                    <p class="lead text-center">Your email is {{email}}.</p>
                                    <div class="text-center">
                                        <button @click="back" class="btn btn-primary btn-lg active">Go back</button>
                                    </div>
                                </div>
                            </div>
                        </section> 
                    `
                })
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
