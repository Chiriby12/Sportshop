import React from 'react'
import { Routes, Route } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import { ToastProvider } from './context/ToastContext'
import { CartProvider } from './context/CartContext'
import { RequireAdmin, GuestOnly } from './components/ProtectedRoute'
import Navbar from './components/Navbar'
import LoginPage    from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import HomePage     from './pages/HomePage'
import CatalogPage  from './pages/CatalogPage'
import AdminPage    from './pages/AdminPage'

function Shell({ children, noNav }) {
  return (
    <>
      {!noNav && <Navbar />}
      {children}
    </>
  )
}

export default function App() {
  return (
    <AuthProvider>
      <ToastProvider>
        <CartProvider>
          <Routes>
            <Route path="/login" element={
              <GuestOnly><Shell noNav><LoginPage /></Shell></GuestOnly>
            } />
            <Route path="/register" element={
              <GuestOnly><Shell noNav><RegisterPage /></Shell></GuestOnly>
            } />
            <Route path="/" element={<Shell><HomePage /></Shell>} />
            <Route path="/catalog" element={<Shell><CatalogPage /></Shell>} />
            <Route path="/admin" element={
              <RequireAdmin><Shell><AdminPage /></Shell></RequireAdmin>
            } />
            <Route path="*" element={
              <Shell>
                <div className="empty" style={{ paddingTop: 80 }}>
                  <i className="ti ti-error-404" style={{ fontSize: 48, color: 'var(--text3)' }} />
                  <p style={{ marginTop: 10 }}>Página no encontrada</p>
                  <a href="/" className="btn btn-primary" style={{ marginTop: 16, display: 'inline-flex' }}>Ir al inicio</a>
                </div>
              </Shell>
            } />
          </Routes>
        </CartProvider>
      </ToastProvider>
    </AuthProvider>
  )
}
