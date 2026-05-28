import React, { useState, useEffect, useRef } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { useCart } from '../context/CartContext'
import { notifApi, fmtDate } from '../services/api'

export default function Navbar() {
  const { user, token, logout, isAuth, isAdmin } = useAuth()
  const { count, setOpen: openCart } = useCart()
  const nav = useNavigate()
  const loc = useLocation()
  const [notifs, setNotifs]       = useState([])
  const [notifOpen, setNotifOpen] = useState(false)
  const notifRef = useRef()

  const isActive = (p) => loc.pathname === p

  const loadNotifs = async () => {
      if (!isAuth()) return
      try {
          let r
          if (isAdmin()) {
              r = await notifApi.getAll(token)
          } else {
              r = await notifApi.getByUser(user.document, token)
          }
          setNotifs((r.data || []).slice(0, 20))
      } catch {}
  }

  useEffect(() => {
    loadNotifs()
    const id = setInterval(loadNotifs, 15000)
    return () => clearInterval(id)
  }, [token])

  useEffect(() => {
    const fn = (e) => { if (notifRef.current && !notifRef.current.contains(e.target)) setNotifOpen(false) }
    document.addEventListener('mousedown', fn)
    return () => document.removeEventListener('mousedown', fn)
  }, [])

  const markRead = async (id) => {
    try { await notifApi.markRead(id, token); loadNotifs() } catch {}
  }

  const unread = notifs.filter(n => n.status === 'RECEIVED').length

  const handleLogout = () => { logout(); nav('/login') }

  return (
    <nav className="navbar">
      <div className="navbar-logo" onClick={() => nav('/')}>
        <i className="ti ti-trophy" /> SportShop
      </div>

      <div className="navbar-nav">
        {!isAuth() ? (
          <>
            <button className={`nav-a ${isActive('/') ? 'active' : ''}`} onClick={() => nav('/')}>Inicio</button>
            <button className={`nav-a ${isActive('/catalog') ? 'active' : ''}`} onClick={() => nav('/catalog')}>Catálogo</button>
            <button className="nav-btn" onClick={() => nav('/login')}><i className="ti ti-login" /> Iniciar sesión</button>
            <button className="nav-btn" onClick={() => nav('/register')}><i className="ti ti-user-plus" /> Registro</button>
          </>
        ) : isAdmin() ? (
          <>
            <button className={`nav-a ${isActive('/admin') ? 'active' : ''}`} onClick={() => nav('/admin')}>Panel admin</button>

            {/* Notificaciones */}
            <div ref={notifRef} style={{ position: 'relative' }}>
              <button className="nav-btn notif-wrap" onClick={() => setNotifOpen(v => !v)}>
                <i className="ti ti-bell" /> Notificaciones
                {unread > 0 && <span className="notif-dot" />}
              </button>
              <div className={`notif-panel ${notifOpen ? 'open' : ''}`}>
                <div className="np-header">
                  <span className="np-title">Notificaciones</span>
                  <span style={{ fontSize: 11, color: 'var(--text3)' }}>{unread} nuevas</span>
                </div>
                {notifs.length === 0
                  ? <div style={{ padding: '16px', textAlign: 'center', fontSize: 12, color: 'var(--text3)' }}>Sin notificaciones</div>
                  : notifs.map(n => (
                    <div key={n.id} className={`np-item ${n.status === 'RECEIVED' ? 'unread' : ''}`} onClick={() => markRead(n.id)}>
                      <div className="np-item-title">{n.title}</div>
                      <div className="np-item-msg">{n.message}</div>
                      <div className="np-item-time">{fmtDate(n.createdAt)}</div>
                    </div>
                  ))
                }
              </div>
            </div>

            <button className="nav-btn" onClick={handleLogout}><i className="ti ti-logout" /> Salir</button>
          </>
        ) : (
          <>
            <button className={`nav-a ${isActive('/') ? 'active' : ''}`} onClick={() => nav('/')}>Inicio</button>
            <button className={`nav-a ${isActive('/catalog') ? 'active' : ''}`} onClick={() => nav('/catalog')}>Catálogo</button>

            {/* Notificaciones usuario */}
            <div ref={notifRef} style={{ position: 'relative' }}>
              <button className="nav-btn notif-wrap" onClick={() => setNotifOpen(v => !v)}>
                <i className="ti ti-bell" />
                {unread > 0 && <span className="notif-dot" />}
              </button>
              <div className={`notif-panel ${notifOpen ? 'open' : ''}`}>
                <div className="np-header"><span className="np-title">Mis notificaciones</span></div>
                {notifs.length === 0
                  ? <div style={{ padding: '16px', textAlign: 'center', fontSize: 12, color: 'var(--text3)' }}>Sin notificaciones</div>
                  : notifs.map(n => (
                    <div key={n.id} className={`np-item ${n.status === 'RECEIVED' ? 'unread' : ''}`} onClick={() => markRead(n.id)}>
                      <div className="np-item-title">{n.title}</div>
                      <div className="np-item-msg">{n.message}</div>
                      <div className="np-item-time">{fmtDate(n.receivedAt)}</div>
                    </div>
                  ))
                }
              </div>
            </div>

            {/* Carrito */}
            <button className="nav-btn cart-wrap" onClick={() => openCart(true)}>
              <i className="ti ti-shopping-cart" /> Carrito
              {count > 0 && <span className="cart-badge">{count}</span>}
            </button>

            <button className="nav-btn" onClick={handleLogout}><i className="ti ti-logout" /> Salir</button>
          </>
        )}
      </div>
    </nav>
  )
}
