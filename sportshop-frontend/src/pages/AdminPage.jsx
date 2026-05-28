import React, { useState, useEffect } from 'react'
import { useAuth } from '../context/AuthContext'
import { useToast } from '../context/ToastContext'
import { adminApi, catalogApi, sportIcon, fmt } from '../services/api'

// ── Product form modal ─────────────────────────────────────────
function ProdModal({ item, token, onClose, onDone }) {
  const [f, setF] = useState(item || { name: '', description: '', brand: '', category: '', sport: '', price: '', stock: '', active: true })
  const [busy, setBusy] = useState(false)
  const { show } = useToast()
  const set = e => setF(p => ({ ...p, [e.target.name]: e.target.type === 'checkbox' ? e.target.checked : e.target.value }))

  const submit = async (e) => {
    e.preventDefault(); setBusy(true)
    try {
      const data = { ...f, price: Number(f.price), stock: Number(f.stock) }
      if (item?.id) await adminApi.updateProd(item.id, data, token)
      else await adminApi.createProd(data, token)
      show(item?.id ? 'Producto actualizado' : 'Producto creado', 'success')
      onDone()
    } catch (err) { show(err.message, 'error') }
    finally { setBusy(false) }
  }

  return (
    <div className="modal-overlay" onClick={e => e.target === e.currentTarget && onClose()}>
      <div className="modal">
        <div className="modal-header">
          <span className="modal-title">{item?.id ? 'Editar producto' : 'Nuevo producto'}</span>
          <button className="btn-icon" onClick={onClose}><i className="ti ti-x" /></button>
        </div>
        <form onSubmit={submit}>
          <div className="modal-body">
            <div className="grid-2">
              <div className="field"><label>Nombre</label><input className="input" name="name" value={f.name} onChange={set} required /></div>
              <div className="field"><label>Marca</label><input className="input" name="brand" value={f.brand} onChange={set} required /></div>
            </div>
            <div className="field"><label>Descripción</label><input className="input" name="description" value={f.description} onChange={set} /></div>
            <div className="grid-2">
              <div className="field"><label>Categoría</label><input className="input" name="category" placeholder="RUNNING, FOOTBALL…" value={f.category} onChange={set} required /></div>
              <div className="field"><label>Deporte</label><input className="input" name="sport" placeholder="ATLETISMO, FUTBOL…" value={f.sport} onChange={set} required /></div>
            </div>
            <div className="grid-2">
              <div className="field"><label>Precio (COP)</label><input className="input" type="number" name="price" value={f.price} onChange={set} required /></div>
              <div className="field"><label>Stock</label><input className="input" type="number" name="stock" value={f.stock} onChange={set} required /></div>
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginTop: 2 }}>
              <input type="checkbox" id="active" name="active" checked={f.active} onChange={set} />
              <label htmlFor="active" style={{ fontSize: 13, color: 'var(--text2)' }}>Producto activo</label>
            </div>
          </div>
          <div className="modal-footer">
            <button type="button" className="btn btn-ghost" onClick={onClose}>Cancelar</button>
            <button type="submit" className="btn btn-primary" disabled={busy}>{busy ? 'Guardando…' : 'Guardar'}</button>
          </div>
        </form>
      </div>
    </div>
  )
}

// ── User form modal ────────────────────────────────────────────
function UserModal({ item, token, onClose, onDone }) {
  const [f, setF] = useState(item || { name: '', email: '', password: '', document: '', telephone: '', age: '', role: 'USER' })
  const [busy, setBusy] = useState(false)
  const { show } = useToast()
  const set = e => setF(p => ({ ...p, [e.target.name]: e.target.value }))

  const submit = async (e) => {
    e.preventDefault(); setBusy(true)
    try {
      const data = { ...f, age: Number(f.age) || null }
      if (item?.document) await adminApi.updateUser(item.document, data, token)
      else await adminApi.createUser(data, token)
      show(item?.document ? 'Usuario actualizado' : 'Usuario creado', 'success')
      onDone()
    } catch (err) { show(err.message, 'error') }
    finally { setBusy(false) }
  }

  return (
    <div className="modal-overlay" onClick={e => e.target === e.currentTarget && onClose()}>
      <div className="modal">
        <div className="modal-header">
          <span className="modal-title">{item?.document ? 'Editar usuario' : 'Nuevo usuario'}</span>
          <button className="btn-icon" onClick={onClose}><i className="ti ti-x" /></button>
        </div>
        <form onSubmit={submit}>
          <div className="modal-body">
            <div className="grid-2">
              <div className="field"><label>Nombre</label><input className="input" name="name" value={f.name} onChange={set} required /></div>
              <div className="field"><label>Documento</label><input className="input" name="document" value={f.document} onChange={set} required disabled={!!item?.document} /></div>
            </div>
            <div className="field"><label>Email</label><input className="input" type="email" name="email" value={f.email} onChange={set} required /></div>
            {!item?.document && <div className="field"><label>Contraseña</label><input className="input" type="password" name="password" value={f.password} onChange={set} required /></div>}
            <div className="grid-2">
              <div className="field"><label>Teléfono</label><input className="input" name="telephone" value={f.telephone} onChange={set} /></div>
              <div className="field"><label>Edad</label><input className="input" type="number" name="age" value={f.age} onChange={set} /></div>
            </div>
            <div className="field">
              <label>Rol</label>
              <select className="input select" name="role" value={f.role} onChange={set}>
                <option value="USER">Usuario</option>
                <option value="ADMIN">Administrador</option>
              </select>
            </div>
          </div>
          <div className="modal-footer">
            <button type="button" className="btn btn-ghost" onClick={onClose}>Cancelar</button>
            <button type="submit" className="btn btn-primary" disabled={busy}>{busy ? 'Guardando…' : 'Guardar'}</button>
          </div>
        </form>
      </div>
    </div>
  )
}

// ── Products tab ───────────────────────────────────────────────
function ProductsTab({ token }) {
  const [products, setProducts] = useState([])
  const [loading, setLoading] = useState(true)
  const [modal, setModal]   = useState(null)
  const [search, setSearch] = useState('')
  const { show } = useToast()

  const load = () => {
    setLoading(true)
    adminApi.getProducts(token)
      .then(r => setProducts(r.data || []))
      .catch(() => setProducts([]))
      .finally(() => setLoading(false))
  }
  useEffect(load, [])

  const del = async (p) => {
    if (!confirm(`¿Eliminar "${p.name}"?`)) return
    try { await adminApi.deleteProd(p.id, token); show('Producto eliminado', 'success'); load() }
    catch (e) { show(e.message, 'error') }
  }

  const list = products.filter(p => !search || p.name.toLowerCase().includes(search.toLowerCase()) || (p.brand || '').toLowerCase().includes(search.toLowerCase()))

  return (
    <>
      {modal !== null && <ProdModal item={modal === 'new' ? null : modal} token={token} onClose={() => setModal(null)} onDone={() => { setModal(null); load() }} />}

      <div className="admin-header">
        <h2>Gestión de productos</h2>
        <button className="btn btn-primary btn-sm" onClick={() => setModal('new')}>
          <i className="ti ti-plus" /> Nuevo producto
        </button>
      </div>

      <div className="search-bar">
        <input className="search-input" placeholder="Buscar producto…" value={search} onChange={e => setSearch(e.target.value)} />
      </div>

      <div className="card">
        <div className="tbl-wrap">
          {loading ? <div className="spinner" /> : list.length === 0 ? <div className="empty"><i className="ti ti-package-off" /><p>No hay productos</p></div> : (
            <table>
              <thead>
                <tr>
                  <th>Producto</th><th>Marca</th><th>Categoría</th>
                  <th>Precio</th><th>Stock</th><th>Estado</th><th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {list.map(p => (
                  <tr key={p.id}>
                    <td>
                      <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                        <span style={{ fontSize: 18, color: 'var(--blue)' }}><i className={`ti ${sportIcon(p.sport, p.category)}`} /></span>
                        <span style={{ fontWeight: 500 }}>{p.name}</span>
                      </div>
                    </td>
                    <td>{p.brand}</td>
                    <td><span className="badge badge-info">{p.category}</span></td>
                    <td style={{ fontWeight: 500, color: 'var(--blue)' }}>{fmt(p.price)}</td>
                    <td>
                      {p.stock > 10
                        ? <span className="badge badge-ok">{p.stock}</span>
                        : p.stock > 0
                          ? <span className="badge badge-low">{p.stock}</span>
                          : <span className="badge badge-danger">0</span>
                      }
                    </td>
                    <td>{p.active ? <span className="badge badge-ok">Activo</span> : <span className="badge badge-gray">Inactivo</span>}</td>
                    <td>
                      <div style={{ display: 'flex', gap: 5 }}>
                        <button className="btn btn-ghost btn-sm" onClick={() => setModal(p)}>
                          <i className="ti ti-pencil" /> Editar
                        </button>
                        <button className="btn btn-danger btn-sm" onClick={() => del(p)}>
                          <i className="ti ti-trash" /> Eliminar
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>
    </>
  )
}

// ── Users tab ──────────────────────────────────────────────────
function UsersTab({ token }) {
  const [users, setUsers]   = useState([])
  const [loading, setLoading] = useState(true)
  const [modal, setModal]   = useState(null)
  const [search, setSearch] = useState('')
  const { show } = useToast()

  const load = () => {
    setLoading(true)
    adminApi.getUsers(token)
      .then(r => setUsers(r.data || []))
      .catch(() => setUsers([]))
      .finally(() => setLoading(false))
  }
  useEffect(load, [])

  const del = async (u) => {
    if (!confirm(`¿Eliminar al usuario "${u.name}"?`)) return
    try { await adminApi.deleteUser(u.document, token); show('Usuario eliminado', 'success'); load() }
    catch (e) { show(e.message, 'error') }
  }

  const list = users.filter(u => !search ||
    (u.name || '').toLowerCase().includes(search.toLowerCase()) ||
    (u.email || '').toLowerCase().includes(search.toLowerCase()) ||
    (u.document || '').includes(search)
  )

  return (
    <>
      {modal !== null && <UserModal item={modal === 'new' ? null : modal} token={token} onClose={() => setModal(null)} onDone={() => { setModal(null); load() }} />}

      <div className="admin-header">
        <h2>Gestión de usuarios</h2>
        <button className="btn btn-primary btn-sm" onClick={() => setModal('new')}>
          <i className="ti ti-user-plus" /> Nuevo usuario
        </button>
      </div>

      <div className="search-bar">
        <input className="search-input" placeholder="Buscar por nombre, email o documento…" value={search} onChange={e => setSearch(e.target.value)} />
      </div>

      <div className="card">
        <div className="tbl-wrap">
          {loading ? <div className="spinner" /> : list.length === 0 ? <div className="empty"><i className="ti ti-users-group" /><p>No hay usuarios</p></div> : (
            <table>
              <thead>
                <tr><th>Nombre</th><th>Email</th><th>Documento</th><th>Rol</th><th>Teléfono</th><th>Acciones</th></tr>
              </thead>
              <tbody>
                {list.map(u => (
                  <tr key={u.document}>
                    <td style={{ fontWeight: 500 }}>{u.name}</td>
                    <td style={{ color: 'var(--text2)' }}>{u.email}</td>
                    <td><span style={{ fontFamily: 'monospace', fontSize: 12 }}>{u.document}</span></td>
                    <td>{u.role === 'ADMIN' ? <span className="badge badge-info">Admin</span> : <span className="badge badge-gray">Usuario</span>}</td>
                    <td>{u.telephone || '—'}</td>
                    <td>
                      <div style={{ display: 'flex', gap: 5 }}>
                        <button className="btn btn-ghost btn-sm" onClick={() => setModal(u)}>
                          <i className="ti ti-pencil" /> Editar
                        </button>
                        <button className="btn btn-danger btn-sm" onClick={() => del(u)}>
                          <i className="ti ti-trash" /> Eliminar
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>
    </>
  )
}

// ── AdminPage ──────────────────────────────────────────────────
export default function AdminPage() {
  const { token } = useAuth()
  const [tab, setTab] = useState('products')

  const links = [
    { key: 'products', icon: 'ti-package',    label: 'Productos' },
    { key: 'users',    icon: 'ti-users-group', label: 'Usuarios'  },
  ]

  return (
    <div className="admin-layout">
      {/* Sidebar */}
      <aside className="sidebar">
        <div style={{ padding: '0 14px 16px', borderBottom: '0.5px solid rgba(255,255,255,.1)', marginBottom: 8 }}>
          <div style={{ fontSize: 14, fontWeight: 600, color: '#fff', display: 'flex', alignItems: 'center', gap: 6 }}>
            <i className="ti ti-trophy" /> SportShop
          </div>
          <div style={{ fontSize: 11, color: 'rgba(255,255,255,.4)', marginTop: 2 }}>Panel de administración</div>
        </div>

        <p className="s-title">Gestión</p>
        {links.map(l => (
          <button key={l.key} className={`s-link ${tab === l.key ? 'on' : ''}`} onClick={() => setTab(l.key)}>
            <i className={`ti ${l.icon}`} /> {l.label}
          </button>
        ))}

        <p className="s-title" style={{ marginTop: 16 }}>APIs</p>
        {[
          { href: 'http://localhost:8080/swagger-ui.html', label: 'Auth API' },
          { href: 'http://localhost:8081/swagger-ui.html', label: 'Catalog API' },
          { href: 'http://localhost:8082/swagger-ui.html', label: 'Admin API' },
          { href: 'http://localhost:8083/swagger-ui.html', label: 'Notif API' },
        ].map(a => (
          <a key={a.href} className="s-link" href={a.href} target="_blank" rel="noreferrer">
            <i className="ti ti-external-link" /> {a.label}
          </a>
        ))}
      </aside>

      {/* Main */}
      <main className="admin-main">
        {tab === 'products' && <ProductsTab token={token} />}
        {tab === 'users'    && <UsersTab    token={token} />}
      </main>
    </div>
  )
}
