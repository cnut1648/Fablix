<template>
    <nav class="navbar navbar-light" style="background-color: #e3f2fd;">
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNavAltMarkup" aria-controls="navbarNavAltMarkup" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <!--    toggle item in here-->
        <div class="collapse navbar-collapse" id="navbarNavAltMarkup">
            <ul class="navbar-nav">
                <!--        note home is active, page applied directly without clicking-->
                <li class="nav-item" :class="{active: isMain}" >
                    <a class="nav-item nav-link"  href="main.html">Main<span class="sr-only">(current)</span></a>
                </li>
                <li class="nav-item">
                    <a class="nav-item nav-link active" href="Cart.html">Cart</a>
                </li>
                <li class="nav-item">
                    <a class="nav-item nav-link active" href="userinfo.html">User</a>
                </li>
                <li class="nav-item">
                    <a class="nav-item nav-link active" href="logout.html">Logout</a>
                </li>
                <form class="form-inline my-2 my-lg-0">
                    <input v-model="query" id="autocomplete" class="autocomplete-searchbox form-control mr-sm-2" placeholder="movie title">
                    <button @click="search($event)" class="btn btn-outline-success my-2 my-sm-0">Search</button>
<!--                    <button class="btn btn-outline-success my-2 my-sm-0">Search</button>-->
                </form>
            </ul>
        </div>
        <!--      rest of navbar-->
        <a @click="jumpback" class="navbar-brand" href="#">
            <img src="utils/letter-f.png"  width="30" height="30" class="d-inline-block align-top"
                 alt="Fablix"> Fablix</a>
    </nav>
</template>
<script>
    import $ from 'jquery';
    window.jQuery = $;
    window.$ = $;
    import 'devbridge-autocomplete';

    export default {
        name: "navbar",
        data: function(){
           let cur = window.document.location.href;
           return {
               isMain: !!cur.match("list"),
               query : "",
           }
        },
        methods: {
            search: function(e){
                e.preventDefault();
                window.location.replace("list.html?title=" + this.query + "&N=25&page=1");
            },
            jumpback: function () {
                window.location.replace("login.html");
            }
        },
        mounted(){
            $("#autocomplete").autocomplete({
                lookup: function(query, doneCallback){
                    // only call when len(query) >= 3
                    if (query.length < 3) return;
                    let storage = window.localStorage;
                    console.log("init autocomplete");
                    let stored = storage.getItem(query);
                    if (stored !== null){
                        console.log("fetching local storage");
                        let storedJson = JSON.parse(stored);
                        doneCallback({suggestions: storedJson});
                        for (let i of storedJson) console.log(i["value"]);
                    }
                    else{
                        console.log("fetching from server");
                        jQuery.ajax({
                            dataType: "json",
                            method: "GET",
                            url: "api/autoComplete?title=" + escape(query),
                            success: function(data){
                                // slicing data to top 10
                                if (data.length > 10) data = data.slice(0,10);
                                for (let i of data){
                                    console.log(i["value"]);
                                }
                                storage.setItem(query, JSON.stringify(data));
                                // doneCallback to let autocomplete know getting data is done
                                doneCallback({ suggestions : data});
                            },
                            error: function(error){
                                alert(error);
                                console.log("error ajax");
                            }
                        })
                    }
                },
                onSelect: function (suggestion) {
                    let url = "single-movie.html?movieid=" + suggestion["data"]["movieID"];
                    window.location.replace(url);
                },
                deferRequestBy: 300,
            });
            // $("#autocomplete").keypress(function(event){
            //     console.log($("#autocomplete").val());
            //     // enter key
            //     if (event.keyCode === 13){
            //         console.log($("#autocomplete").val());
            //     }
            //     if (event.keyCode == 18){
            //         console.log($("#autocomplete").val());
            //     }
            // })

        }
    }
</script>

<style scoped>
</style>