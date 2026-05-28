import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      '/api/sportshop/auth':          { target: 'http://localhost:8080', changeOrigin: true },
      '/api/sportshop/catalog':       { target: 'http://localhost:8081', changeOrigin: true },
      '/api/sportshop/admin':         { target: 'http://localhost:8082', changeOrigin: true },
      '/api/sportshop/notifications': { target: 'http://localhost:8083', changeOrigin: true }
    }
  },
  define: {
    'import.meta.env.VITE_AUTH_URL':          JSON.stringify(process.env.VITE_AUTH_URL || ''),
    'import.meta.env.VITE_CATALOG_URL':       JSON.stringify(process.env.VITE_CATALOG_URL || ''),
    'import.meta.env.VITE_ADMIN_URL':         JSON.stringify(process.env.VITE_ADMIN_URL || ''),
    'import.meta.env.VITE_NOTIFICATIONS_URL': JSON.stringify(process.env.VITE_NOTIFICATIONS_URL || ''),
  }
})