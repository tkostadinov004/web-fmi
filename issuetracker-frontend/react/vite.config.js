import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    // Тук настройваме проксито
    proxy: {
      // Прихващаме всички заявки, които започват с /api
      '/api': {
        target: 'http://localhost:8080', // Адресът на твоя бекенд
        changeOrigin: true, // Променя Origin хедъра на заявката към target URL-а
        secure: false, // Използвай false, ако бекендът ти няма HTTPS (SSL сертификат) локално
        rewrite: (path) => path.replace(/^\/api/, ''), // маха api понеже не се съдържа в пътя
      }
    }
  }
})


// import { defineConfig } from 'vite'
// import react from '@vitejs/plugin-react'

// // https://vite.dev/config/
// export default defineConfig({
//   plugins: [react()],
// })