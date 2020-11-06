import $ from 'jquery';
window.jQuery = $;
window.$ = $;
import 'bootstrap';
import 'bootstrap/dist/css/bootstrap.min.css';
import getParameterByName from 'utilJS';
import Vue from 'vue';
import navbar from 'navbar';

new Vue({
    render: h=>h(navbar)
}).$mount("#navbar");

function handleCartArray(resultDataString)
{
    let resultDataJson = JSON.parse(resultDataString);
    console.log(resultDataJs["title"]);

}

function addtocart(e) {
    console.log(e.title.value);
    e.preventDefault();
    $.ajax("api/Addcart",{
        method: "POST",
        data:{"title":e.title.value,"id":e.id.value},
        success: handleCartArray
    });
    return false;
}
function handleResult(resultData) {

    $("#back").attr("href", "list.html?" + resultData[0]["curURL"]);
    resultData = resultData.slice(1);
    jQuery("#movie_title").append(resultData[0]["title"]);
    let year = resultData[0]["year"];
    let diff = new Date().getFullYear() - year;
    jQuery("#movie_release").append(`Released in ${year} (${diff} years ago)`);
    jQuery("#movie_director").append(`Directed by <em>${resultData[0]["director"]}</em>`);

    jQuery("#rating").text(resultData[0]["rating"]);

    console.log("creating vue");
    new Vue({
        data: {
            rater: resultData[0]["rating"],
        },
        template: `<p v-if="rate()===0">&#x1F60D</p>
        <p v-else-if="rate()===1">&#128566</p>
        <p v-else-if="rate()===2">&#128531</p>
        <p v-else> N/A </p>`,
        methods: {
            rate(){
                console.log(this.rater);
                if (this.rater) {
                    if (this.rater >= 8) return 0;
                    else if (this.rater >= 5) return 1;
                    else return 2;
                }
                else return 3;
            }
        }
    }).$mount("#emoji");

    new Vue({
       el: "#buy",
       data: {
           id: getParameterByName("movieid"),
           title: resultData[0]["title"],
       },
       methods:{
           addtoCart: function () {
               $("#success-buy").modal('show');
               setTimeout(function(){
                   $("#success-buy").modal('hide');
               }, 1200);
               $.ajax("api/Addcart",{
                   method: "POST",
                   data:{"title":this.title,"id":this.id},
               });
               return false;
           },
       },
       template: `
       <div class="text-center">
           <button class="btn btn-outline-success" type="button" data-placement="top" title="add to cart"
                   data-target="#success-buy"
                   @click="addtoCart">
               buy
           </button>
           <!--  modal        -->
           <div class="modal fade" id="success-buy" tabindex="-1">
               <div class="modal-dialog modal-dialog-centered" role="document">
                   <div class="modal-content">
                       <div class="modal-body">
                           successfully buy the {{title}}!
                       </div>
                   </div>
               </div>
           </div> 
       </div>
       `
    });
    let row = "";
    let i = 0;
    let stars = resultData[0]["star_list"].split(",");
    let ids = resultData[0]["star_id"].split(",");
    row += "<div class='row'>";
    for (i = 0; i < stars.length; i++ ){
        row += `<a href="single-star.html?id=${ids[i]}" class="col-md-4 list-group-item list-group-item-action">${stars[i]}</a>`;
    }
    row += "</div>";
    jQuery("#star").append(row);

    row = "";
    for (let g of resultData[0]["genre_list"].split(",")){
        row += "<li class='list-group-item py-2 list-group-item-success rounded'>" +
            "<a href='list.html?genre=" + g + "&N=25&page=1'>" + g + "</a></li>";
    }
    jQuery("#genre").append(row);
}

jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?movieid=" + getParameterByName("movieid"), // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});
