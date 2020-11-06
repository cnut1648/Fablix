import $ from 'jquery';
window.jQuery = $;
window.$ = $;
import 'bootstrap';
import 'bootstrap/dist/css/bootstrap.min.css';
import Vue from 'vue';
import getParameterByName from 'utilJS';
import navbar from 'navbar';

new Vue({
    render: h=>h(navbar)
}).$mount("#navbar");

function handleMovieResult(resultData) {
    console.log("handleStarResult: populating star table from resultData");

    var bread = new Vue({
        el: "#bread-crumb",
        data:{
            g: false,
        },
        methods:{
           toggleG: function () {
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
           },
          toggleT: function () {
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
          }
        },
        template: `
<div>
    <template v-if="getParameterByName('genre')">
        <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="main.html">Main</a></li>
            <li class="breadcrumb-item"><a @click="toggleG" data-toggle="modal" data-target="#browseGenre">Genre</a></li>
            <li class="breadcrumb-item active" aria-current="page">{{getParameterByName('genre')}}</li>
        </ol>
            <div class="modal fade" id="browseGenre">
                <div class="modal-dialog modal-dialog-centered" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title">Browse by Genres</h5>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <div class="modal-body">
                            <div id="genre-body"></div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                            <button type="button" id="genre-lucky" class="btn btn-primary">I'm Feeling Lucky</button>
                        </div>
                    </div>
                </div>
            </div>
        </nav>
    </template>
    <template v-else-if="getParameterByName('begin')">
        <nav aria-label="breadcrumb">
            <ol class="breadcrumb">
                <li class="breadcrumb-item"><a href="main.html">Main</a></li>
                <li class="breadcrumb-item"><a @click="toggleT" data-toggle="modal" data-target="#browseTitle">Browse</a></li>
                <li class="breadcrumb-item active" aria-current="page">{{getParameterByName('begin')}}</li>
        </ol>
            <div class="modal fade" id="browseTitle">
                <div class="modal-dialog modal-dialog-centered" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title">Browse by Genres</h5>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <div class="modal-body">
                            <div id="title-body"></div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                            <button type="button" id="title-lucky" class="btn btn-primary">I'm Feeling Lucky</button>
                        </div>
                    </div>
                </div>
            </div>
        </nav>
    </template>
    <template v-else>
        <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item active"><a href="main.html">Search</a></li>
        </ol>
        </nav>
    </template>
</div>
`
    })

    var chooseN = new Vue({
        el: "#drop-down",
        data:{
            now : getParameterByName("N"),
        },
        methods:{
            isThis: function(number){
                return this.now == number;
            },
            buildURL: function(number){
                let cur = window.document.location.href;
                return cur.replace("N=" + this.now, "N=" + number);
            },
        },
        template: `<div>
    <div class="dropdown dropleft">
        <button class="btn btn-secondary dropdown-toggle" type="button" id="N-result" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
            N results per page
        </button>
        <div class="dropdown-menu"  aria-labelledby="N-result">
            <a class="btn dropdown-item" :href="buildURL(10)" :class="{disabled: isThis(10)}">10</a>
            <a class="btn dropdown-item" :href="buildURL(25)" :class="{disabled: isThis(25)}">25</a>
            <a class="btn dropdown-item" :href="buildURL(50)" :class="{disabled: isThis(50)}">50</a>
            <a class="btn dropdown-item" :href="buildURL(100)" :class="{disabled: isThis(100)}">100</a>
        </div>
    </div>
    </div>
        `,
    });

    let pagination_top = new Vue({
        el: "#prev-next",
        data: {
            page: parseInt(getParameterByName('page')),
            ret: resultData,
        },
        computed:{
            hasPrev: function(){
                return this.page > 1;
            },
            hasNext: ()=> resultData.length === parseInt(getParameterByName("N")) + 1,
        },
        methods: {
            buildPrev: function(){
                let cur = window.document.location.href;
                return cur.replace("page=" + this.page, "page=" + (this.page - 1));
            },
            buildNext: function(){
                let cur = window.document.location.href;
                return cur.replace("page=" + this.page, "page=" + (this.page + 1));
            }
        },
        template: `
            <nav>
                <ul class="pagination">
                    <li class="page-item"  v-show="hasPrev">
                        <a class="page-link":href="buildPrev()">&laquo;</a>
                    </li>
                    <li class="page-item" v-show="hasNext">
                        <a class="page-link" :href="buildNext()">&raquo;</a>
                    </li>
                </ul>
            </nav>
        `
    });

    let pagination_buttom = new Vue({
        el: "#prev-next-bottom",
        data: {
            page: parseInt(getParameterByName('page')),
            ret: resultData,
        },
        computed:{
            hasPrev: function(){
                return this.page > 1;
            },
            hasNext: ()=> resultData.length === parseInt(getParameterByName("N")) + 1,
        },
        methods: {
            buildPrev: function(){
                let cur = window.document.location.href;
                return cur.replace("page=" + this.page, "page=" + (this.page - 1));
            },
            buildNext: function(){
                let cur = window.document.location.href;
                return cur.replace("page=" + this.page, "page=" + (this.page + 1));
            }
        },
        template: `
            <nav>
                <ul class="pagination">
                    <li class="page-item"  v-show="hasPrev">
                        <a class="page-link":href="buildPrev()">&laquo;</a>
                    </li>
                    <li class="page-item" v-show="hasNext">
                        <a class="page-link" :href="buildNext()">&raquo;</a>
                    </li>
                </ul>
            </nav>
        `
    });

    let sort = new Vue({
       el: "#sort",
        data: {
           cur: document.location.href,
        },
        methods:{
           // 1 & 3: 0: rating, 1: title
           // 2 & 4: 0: asc default, 1: desc
           f: function(first, order1, second, order2){
               let base = this.cur.replace(/&first=.*&second=.*/, '') + "&first=";
               base += (first === 1 ? "title": "rating");
               base += (order1 === 1 ? "1": "0");
               base += "&second=";
               base += (second === 1 ? "title": "rating");
               base += (order2 === 1 ? "1": "0");

               return base;
           },
        },
        template: `
<div>
   <div class="dropdown dropright">
        <button class="btn btn-secondary dropdown-toggle" type="button" id="sort-by" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
            Sort
        </button>
        <div class="dropdown-menu"  aria-labelledby="sort-by">
            <a class="btn dropdown-item" :href="f(0,0,1,0)">Rating ▲ and Title ▲</a>
            <a class="btn dropdown-item" :href="f(0,0,1,1)">Rating ▲ and Title ▼</a>
            <a class="btn dropdown-item" :href="f(0,1,1,0)">Rating ▼ and Title ▲</a>
            <a class="btn dropdown-item" :href="f(0,1,1,1)">Rating ▼ and Title ▼</a>
            <a class="btn dropdown-item" :href="f(1,0,0,0)">Title ▲ and Rating ▲</a>
            <a class="btn dropdown-item" :href="f(1,0,0,1)">Title ▲ and Rating ▼</a>
            <a class="btn dropdown-item" :href="f(1,1,0,0)">Title ▼ and Rating ▲</a>
            <a class="btn dropdown-item" :href="f(1,1,0,1)">Title ▼ and Rating ▼</a>
        </div>
    </div>
</div>`
    });

    let movie_list = new Vue({
        el: "#movie-list",
        data: {
            ret: resultData.length === (parseInt(getParameterByName("N")) + 1) ? resultData.slice(0, resultData.length-1) : resultData
        },
        methods:{
          toMovie: (mid) => "single-movie.html?movieid=" + mid,
          toStar: function(i, s) {
              return "single-star.html?id=" + this.sid(i)[s];
          },
          toGenre: function(i, s){
              return "list.html?genre=" + this.genre(i)[s] + "&N=25&page=1";
          },
          star: function(i) {
              return this.ret[i]["star_list"].split(",")
          },
          sid: function(i) {
              return this.ret[i]["star_id"].split(",")
          },
          genre: function(i){
              return this.ret[i]["genre_list"].split(",");
          },
          addtoCart: function(e, i){
              $("#success-buy-" + i).modal('show');
              setTimeout(function(){
                  $("#success-buy-" + i).modal('hide');
              }, 1200);
              $.ajax("api/Addcart",{
                  method: "POST",
                  data:{"title":e.movie_name,"id":e.movie_id},
              });
              return false;
          }
        },
        template: `
        <tbody>
        <tr v-for="(r,i) in ret">
            <td><a class="tv" :href="toMovie(r.movie_id)">{{r.movie_name}}</a></td>
            <td>{{r.movie_dob}}</td>
            <td>{{r.movie_director}}</td>
            <td>
                <button class="btn btn-outline-info" type="button" data-toggle="collapse"
                    :data-target="'#collapse-content-'+i" data-placement="top" title="Only shows three">
                   in total {{star(i).length}} stars
                </button> 
                <div class="collapse" :id="'collapse-content-' + i">
                 <div class="list-group list-group-flush">
                 <a class="list-group-item" v-for="(s,idx) in star(i)" v-if="idx<3"
                        :href="toStar(i, idx)">{{s}}</a>
                 </div>
                </div>
            </td>
            <td>
                <ul class="list-group">
                    <li v-for="(g,idx) in genre(i)" class="list-group-item py-2 list-group-item-success rounded"
                        v-if="idx < 3"><a :href="toGenre(i, idx)">{{g}}</a></li>
                </ul>
            </td>
                <td v-if="r.movie_rate !== null">{{r.movie_rate}}</td>
                <td v-else>N/A</td>
            <td>
            <button class="btn btn-outline-success" type="button" data-placement="top" title="add to cart"
                :data-target="'#success-buy-' + i"
                @click="addtoCart(r,i)">
                    buy
            </button>
            <!--  modal        -->
            <div class="modal fade" :id="'success-buy-' + i" tabindex="-1">
              <div class="modal-dialog modal-dialog-centered" role="document">
                <div class="modal-content">
                  <div class="modal-body">
                        successfully buy the {{r.movie_name}}!
                  </div>
                </div>
              </div>
            </div>
            </td>
        </tr>
        </tbody>`
    });
}


function buildURL(){
    let base = 'api/movie?';
    if (getParameterByName("genre")){
        base += "genre=" + getParameterByName("genre") + "&";
    }
    else if (getParameterByName("begin")){
        base += "begin=" + getParameterByName("begin") + "&";
    }
    else {
        if (getParameterByName("title")){
            base += "title=" + getParameterByName("title") + "&";
        }
        if (getParameterByName("year")){
            base += "year=" + getParameterByName("year") + "&";
        }
        if (getParameterByName("director")){
            base += "director=" + getParameterByName("director") + "&";
        }
        if (getParameterByName("star")){
            base += "star=" + getParameterByName("star") + "&";
        }
    }
    if (getParameterByName("N")){
        base += "N=" + getParameterByName("N") + "&";
    }
    else {
        base += "N=25&";
    }
    if (getParameterByName("page")){
        base += "page=" + getParameterByName("page") + "&";
    }
    if (getParameterByName("first")){
        base += "first=" + getParameterByName("first") + "&";
    }
    if (getParameterByName("second")){
        base += "second=" + getParameterByName("second") + "&";
    }
    return base.substring(0,base.length-1);
}
Vue.mixin({
    methods:{
        getParameterByName: getParameterByName,
    }
});
// Makes the HTTP GET request and registers on success callback function handleStarResult
$.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: buildURL(), // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});