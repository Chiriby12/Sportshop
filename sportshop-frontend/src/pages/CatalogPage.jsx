import React, { useState, useEffect } from 'react'
import { useSearchParams } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { useCart } from '../context/CartContext'
import { useToast } from '../context/ToastContext'
import { catalogApi, sportIcon, fmt } from '../services/api'
import CartPanel from '../components/CartPanel'

const CATS = [
  { v: '',           l: 'Todos'      },
  { v: 'FOOTBALL',  l: 'Fútbol'     },
  { v: 'BASKETBALL',l: 'Baloncesto' },
  { v: 'RUNNING',   l: 'Running'    },
  { v: 'NATACION',  l: 'Natación'   },
  { v: 'TENNIS',    l: 'Tenis'      },
  { v: 'BOXEO',     l: 'Boxeo'      },
  { v: 'CYCLING',   l: 'Ciclismo'   },
  { v: 'GOLF',      l: 'Golf'       },
]

// Modal detalle producto
function DetailModal({ product, onClose, onAdd }) {
  const { isAuth } = useAuth()
  return (
    <div className="modal-overlay" onClick={e => e.target === e.currentTarget && onClose()}>
      <div className="modal">
        <div className="modal-header">
          <span className="modal-title">{product.name}</span>
          <button className="btn-icon" onClick={onClose}><i className="ti ti-x" /></button>
        </div>
        <div className="modal-body">
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16 }}>
            <div style={{ background: 'var(--blue-light)', borderRadius: 'var(--radius-lg)', height: 160, display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 64, color: 'var(--blue)' }}>
              <i className={`ti ${sportIcon(product.sport, product.category)}`} />
            </div>
            <div>
              <p style={{ fontSize: 12, fontFamily: 'monospace', color: 'var(--text3)', marginBottom: 6 }}>ID: {product.id}</p>
              <p style={{ fontSize: 13, color: 'var(--text2)', marginBottom: 10, lineHeight: 1.6 }}>{product.description || 'Sin descripción'}</p>
              <p style={{ fontSize: 22, fontWeight: 500, color: 'var(--blue)', marginBottom: 6 }}>{fmt(product.price)}</p>
              {product.stock > 10
                ? <span className="badge badge-ok">En stock ({product.stock})</span>
                : product.stock > 0
                  ? <span className="badge badge-low">Últimas {product.stock} unidades</span>
                  : <span className="badge badge-danger">Agotado</span>
              }
              <div style={{ marginTop: 8, fontSize: 12, color: 'var(--text2)' }}>
                <p><b>Marca:</b> {product.brand}</p>
                <p><b>Categoría:</b> {product.category}</p>
                <p><b>Deporte:</b> {product.sport}</p>
              </div>
            </div>
          </div>
        </div>
        <div className="modal-footer">
          <button className="btn btn-ghost" onClick={onClose}>Cerrar</button>
          {isAuth() && product.stock > 0 && (
            <button className="btn btn-primary" onClick={() => { onAdd(product); onClose() }}>
              <i className="ti ti-shopping-cart-plus" /> Agregar al carrito
            </button>
          )}
        </div>
      </div>
    </div>
  )
}

export default function CatalogPage() {
  const [products, setProducts] = useState([])
  const [loading, setLoading]   = useState(true)
  const [search, setSearch]     = useState('')
  const [cat, setCat]           = useState('')
  const [detail, setDetail]     = useState(null)
  const [params] = useSearchParams()
  const { isAuth } = useAuth()
  const { addItem } = useCart()
  const { show } = useToast()

  useEffect(() => {
    const c = params.get('cat')
    if (c) setCat(c)
  }, [])

  useEffect(() => {
    catalogApi.getActive()
      .then(r => setProducts(r.data || []))
      .catch(() => setProducts([]))
      .finally(() => setLoading(false))
  }, [])

  const filtered = products.filter(p => {
    const matchCat = !cat || p.category === cat || p.sport === cat
    const matchSearch = !search || p.name.toLowerCase().includes(search.toLowerCase()) || (p.brand || '').toLowerCase().includes(search.toLowerCase())
    return matchCat && matchSearch
  })

  const handleAdd = async (product) => {
    if (!isAuth()) { show('Inicia sesión para agregar al carrito', 'error'); return }
    try { await addItem(product.id, 1); show(`${product.name} agregado al carrito`, 'success') }
    catch (e) { show(e.message, 'error') }
  }

  return (
    <div className="page">
      <CartPanel />
      {detail && <DetailModal product={detail} onClose={() => setDetail(null)} onAdd={handleAdd} />}

      <div className="container page-section">
        <div style={{ marginBottom: 16 }}>
          <p style={{ fontSize: 18, fontWeight: 500 }}>Catálogo de productos</p>
          <p style={{ fontSize: 13, color: 'var(--text2)' }}>{filtered.length} producto{filtered.length !== 1 ? 's' : ''} disponible{filtered.length !== 1 ? 's' : ''}</p>
        </div>

        {/* Search */}
        <div className="search-bar">
          <input className="search-input" placeholder="Buscar por nombre o marca…" value={search} onChange={e => setSearch(e.target.value)} />
          <button className="btn btn-primary" onClick={() => {}}>
            <i className="ti ti-search" /> Buscar
          </button>
        </div>

        {/* Filter pills */}
        <div className="pills">
          {CATS.map(c => (
            <button key={c.v} className={`pill ${cat === c.v ? 'on' : ''}`} onClick={() => setCat(c.v)}>{c.l}</button>
          ))}
        </div>

        {/* Grid */}
        {loading
          ? <div className="spinner" />
          : filtered.length === 0
            ? <div className="empty"><i className="ti ti-search-off" /><p>No se encontraron productos</p></div>
            : (
              <div className="prod-grid">
                {filtered.map(p => (
                  <div key={p.id} className="prod-card">
                    <div className="prod-img"><i className={`ti ${sportIcon(p.sport, p.category)}`} /></div>
                    <div className="prod-body">
                      <p className="prod-name">{p.name}</p>
                      <p className="prod-brand">{p.brand}</p>
                      <div className="prod-row">
                        <span className="prod-price">{fmt(p.price)}</span>
                        <span className="prod-stock-txt">Stock: {p.stock}</span>
                      </div>
                      <button
                        className="btn btn-outline btn-full btn-sm"
                        style={{ marginTop: 8 }}
                        onClick={() => setDetail(p)}
                      >
                        Ver detalle
                      </button>
                      {isAuth() && p.stock > 0 && (
                        <button className="btn btn-primary btn-full btn-sm" style={{ marginTop: 6 }} onClick={() => handleAdd(p)}>
                          <i className="ti ti-shopping-cart-plus" /> Agregar
                        </button>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            )
        }
      </div>
    </div>
  )
}
