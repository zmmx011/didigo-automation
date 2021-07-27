const path = require("path");
module.exports = {
  transpileDependencies: [
    'vuetify'
  ],
  outputDir: path.resolve(__dirname, "../static"),
  devServer: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080'
      }
    }
  }
}
