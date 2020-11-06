// Import webpack module
var webpack = require("webpack");
//Import path module
const path = require('path');

const { VueLoaderPlugin } = require('vue-loader')

module.exports = {
    mode: "development",
    resolve: {
        alias: {
            'vue$': 'vue/dist/vue.esm.js',
            "utilJS": path.resolve("web/utils/utils.js"),
            'navbar': path.resolve("web/utils/navbar.vue"),
        },
        extensions: ['*', '.js', '.vue', '.json']
    },
    entry: {
        // "login": { import: "./web/login.js", dependOn: 'shared'},
        login: "./web/login.js",
        logout: "./web/logout.js",
        userinfo: "./web/userinfo.js",
        // "list": { import: "./web/list.js", dependOn: 'shared'},
        list: "./web/list.js",
        // "main": { import: "./web/main.js", dependOn: 'shared'},
        main: "./web/main.js",
        single_movie: "./web/single-movie.js",
        single_star: "./web/single-star.js",
        cart: './web/Cart.js',
        Dashboard:'./web/Dashboard.js',
        EmployeeLogin: './web/EmployeeLogin.js',
        // 'shared': ['vue', 'jquery', 'utilJS',  'bootstrap', 'bootstrap/dist/css/bootstrap.min.css'],
        // 'shared': 'jquery',
    },
    // Resolve to output directory and set file
    output: {
        path: path.resolve("web/utils/dist"),
        filename: "[name].js",
    },
    // optimization:{
    //    splitChunks: {
    //        chunks:'all',
    //        name: false,
    //        cacheGroups: {
    //            node_vendors: {
    //                test: /[\\/]node_modules[\\/]/,
    //                chunks: "async",
    //                priority: 1
    //            }
    //        }
    //    },
    // },
    module: {
        rules: [
            {
                test: /\.css$/,
                use: ['style-loader', 'css-loader']
            },
            {
                test: /\.vue$/,
                use: 'vue-loader'
            },
        ]
    },
    plugins: [
        new webpack.ProvidePlugin({
            $: "jquery",
            jQuery: "jquery"
        }),
        new VueLoaderPlugin(),
    ]
}
