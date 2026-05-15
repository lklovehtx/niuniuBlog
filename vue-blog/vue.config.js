module.exports = {
  devServer: {
    port: 8081,
    proxy: {
      '/jeecg-boot': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
}
