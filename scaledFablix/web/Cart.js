import $ from 'jquery';
window.jQuery = $;
window.$ = $;
import 'bootstrap';
import 'bootstrap/dist/css/bootstrap.min.css';
import Vue from 'vue';
import navbar from 'navbar';

new Vue({
    render: h=>h(navbar)
}).$mount("#navbar");

var a;

function handleResult(resultData) {
    a = resultData;
    console.log(a);
    $("#back").attr("href", "list.html?" + resultData[0]["curURL"]);
    resultData = resultData.slice(1),
    new Vue({
        el: "#table",
        data: {
            ret: resultData,
            qty: [],
        },
        created: function(){
            for (let r of this.ret){
                this.qty.push(r);
            }
        },
        methods:{
            total:function(){
                let total = 0
                for (let i = 0; i < this.qty.length; i++){
                    total += this.qty[i]['qty']  * this.qty[i]["price"];
                }
                return total;
            },
            dec: function(i){
                console.log(this.qty[i].qty);
                console.log(this.qty[i].title);
                if (this.qty[i].qty <= 1) return;
                this.qty[i].qty--;
                let command="decrease";
                $.ajax("api/cart",{
                    method: "POST",
                    data:{"data":this.qty[i].title,"command":command},
                });
            },
            inc: function(i){
                console.log("increasing ... \n\t" + this.qty[i].title);
                console.log(this.qty[i].qty);
                this.qty[i].qty++;
                let command="add";
                $.ajax("api/cart",{
                    method: "POST",
                    data:{"data":this.qty[i].title,"command":command},
                });
            },
            updateItem: function(i){
                console.log("updating " + this.qty[i].qty);
                let command= "update";
                $.ajax("api/cart",{
                    method: "POST",
                    data:{"data":this.qty[i].title,"command":command, "qty": this.qty[i].qty},
                    success: function(){
                    }
                });
            },
            delItem: function (i) {
                console.log("deleting....")
                let command = "delete";
                $.ajax("api/cart",{
                    method: "POST",
                    data:{"data":this.qty[i].title,"command":command},
                    success: function(){location.reload();}
                });
            }
        },
        template: `
   <div>
       <table id=star_table class="table table-striped table-hover">
       <thead class="thead-dark">
       <tr>
           <th>Name</th>
           <th>Quantity</th>
           <th>Delete Item</th>
           <th>Price</th>
       </tr>
       </thead>
        <tbody>
            <tr v-for="(r,i) in ret">
                <td>{{r.title}}</td>
                <td>
                    <div class="row">
                        <div class="col-sm-3">
                            <div class="input-group">
                                <button @click="dec(i)" type="button" class="btn btn-default btn-number" data-type="minus">
                                    &#x2212;
                                </button>
                                <input v-model="qty[i].qty" @change="updateItem(i)" type="number" class="form-control input-number" min="1">
                                <button @click="inc(i)" type="button" class="btn btn-default btn-number" data-type="plus">
                                    &#x2b;
                                </button>
                            </div>
                        </div>
                    </div> 
                </td>
                <td>
                    <button class="btn btn-danger btn-lg" type="button" data-placement="top" title="delete item"
                            @click="delItem(i)">
                        &#x274C;❌
                    </button>
                </td>
                <td>{{r.price}}</td>
            </tr>
        </tbody>
       </table>
       <p>in total &#x24;{{total()}}</p>
   </div>
        `
    });
}




jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/cart", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});
