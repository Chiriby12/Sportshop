import React from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

const CATS = [
  { icon: 'ti-ball-football', label: 'Fútbol',      cat: 'FOOTBALL'   },
  { icon: 'ti-ball-basketball', label: 'Baloncesto', cat: 'BASKETBALL' },
  { icon: 'ti-run',            label: 'Running',     cat: 'RUNNING'    },
  { icon: 'ti-swimming',       label: 'Natación',    cat: 'NATACION'   },
  { icon: 'ti-tennis',         label: 'Tenis',       cat: 'TENNIS'     },
  { icon: 'ti-boxing-glove',   label: 'Boxeo',       cat: 'BOXEO'      },
  { icon: 'ti-bike',           label: 'Ciclismo',    cat: 'CYCLING'    },
  { icon: 'ti-golf',           label: 'Golf',        cat: 'GOLF'       },
]

export default function HomePage() {
  const nav = useNavigate()
  const { isAuth } = useAuth()

  return (
    <div className="page">
      <div className="container page-section">
        {/* Hero */}
        <div className="hero">
          <h1>Bienvenido a SportShop</h1>
          <p>Tu tienda de artículos deportivos online</p>
          <div className="btn-row">
            <button className="btn btn-primary" onClick={() => nav('/catalog')}>
              <i className="ti ti-shopping-bag" /> Ver catálogo
            </button>
            {!isAuth() && (
              <button className="btn btn-outline" onClick={() => nav('/login')}>
                <i className="ti ti-login" /> Iniciar sesión
              </button>
            )}
          </div>
        </div>

        {/* Categories */}
        <p style={{ fontSize: 13, fontWeight: 500, color: 'var(--text2)', marginBottom: 4 }}>Explora por deporte</p>
        <div className="cats">
          {CATS.map(c => (
            <div key={c.cat} className="cat-card" onClick={() => nav(`/catalog?cat=${c.cat}`)}>
              <div className="icon"><i className={`ti ${c.icon}`} /></div>
              <p>{c.label}</p>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}
