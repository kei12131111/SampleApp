const path = require('path');
const webpack = require('webpack');

module.exports = {
    entry: './src/storageAccountSample.js',
    plugins: [
        new webpack.ProvidePlugin({
          $: 'jquery',
          jQuery: 'jquery'
        })
      ]
};