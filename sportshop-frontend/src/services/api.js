// ── Mensajes de error amigables ───────────────────────────────
function mensajeAmigable(err, status) {
  const e = (err || '').toLowerCase()

  if (e.includes('email') && e.includes('exist'))        return 'Ya existe una cuenta con ese correo electrónico.'
  if (e.includes('document') && e.includes('exist'))     return 'Ya existe una cuenta con ese número de documento.'
  if (e.includes('credencial') || e.includes('invalid') || e.includes('bad credentials')) return 'Correo o contraseña incorrectos.'
  if (e.includes('token') || e.includes('jwt') || e.includes('expired'))  return 'Tu sesión expiró. Vuelve a iniciar sesión.'
  if (e.includes('stock insuficiente') || e.includes('stock'))             return 'No hay suficiente stock disponible.'
  if (e.includes('carrito') && e.includes('vacío'))      return 'Tu carrito está vacío.'
  if (e.includes('no existe') && e.includes('producto')) return 'El producto no existe o fue eliminado.'
  if (e.includes('no existe') && e.includes('usuario'))  return 'El usuario no existe.'
  if (e.includes('no existe') && e.includes('carrito'))  return 'El ítem del carrito no existe.'
  if (e.includes('permiso'))                             return 'No tienes permiso para realizar esta acción.'
  if (e.includes('nombre') && e.includes('vacío'))       return 'El nombre del producto no puede estar vacío.'
  if (e.includes('precio'))                              return 'El precio debe ser mayor a 0.'
  if (e.includes('relation') || e.includes('jdbc') || e.includes('sql')) return 'Error de conexión con la base de datos. Contacta al administrador.'
  if (e.includes('connection refused') || e.includes('econnrefused'))     return 'No se puede conectar al servidor. Verifica que los microservicios estén corriendo.'
  if (e.includes('failed to fetch') || e.includes('networkerror'))        return 'Sin conexión al servidor. Revisa tu red o los microservicios.'

  if (status === 400) return err || 'Datos inválidos. Revisa los campos e intenta de nuevo.'
  if (status === 401) return 'Debes iniciar sesión para realizar esta acción.'
  if (status === 403) return 'No tienes permisos para realizar esta acción.'
  if (status === 404) return 'El recurso solicitado no existe.'
  if (status === 409) return 'Ya existe un registro con esos datos.'
  if (status >= 500)  return 'Error interno del servidor. Intenta de nuevo más tarde.'

  return err || 'Ocurrió un error inesperado. Intenta de nuevo.'
}

async function req(method, url, body, token) {
  const headers = { 'Content-Type': 'application/json' }
  if (token) headers['Authorization'] = `Bearer ${token}`

  let res, json
  try {
    res = await fetch(url, {
      method,
      headers,
      ...(body && method !== 'GET' ? { body: JSON.stringify(body) } : {})
    })
    json = await res.json().catch(() => ({}))
  } catch (networkError) {
    throw new Error(mensajeAmigable(networkError.message, 0))
  }

  if (!res.ok) {
      if (res.status === 400 && json.data && typeof json.data === 'object') {
          const campos = Object.values(json.data).join('\n')
          throw new Error(campos)
      }
      const raw = json.mensaje || json.message || json.error || ''
      throw new Error(mensajeAmigable(raw, res.status))
  }
  return json
}

const g  = (u, t)    => req('GET',    u, null, t)
const p  = (u, b, t) => req('POST',   u, b,    t)
const pu = (u, b, t) => req('PUT',    u, b,    t)
const pa = (u, b, t) => req('PATCH',  u, b,    t)
const d  = (u, t)    => req('DELETE', u, null, t)

// ── AUTH :8080 ────────────────────────────────────────────────
export const authApi = {
  login:    (email, password) => p('/api/sportshop/auth/login', { email, password }),
  register: (data)            => p('/api/sportshop/auth/save', data),
  getAll:   (t)               => g('/api/sportshop/auth/all', t),
  getOne:   (doc, t)          => g(`/api/sportshop/auth/get/${doc}`, t),
  update:   (doc, data, t)    => pu(`/api/sportshop/auth/update/${doc}`, data, t),
  delete:   (doc, t)          => d(`/api/sportshop/auth/delete/${doc}`, t),
}

// ── CATALOG :8081 ─────────────────────────────────────────────
export const catalogApi = {
  getActive:    ()            => g('/api/sportshop/catalog/products'),
  getAll:       (t)           => g('/api/sportshop/catalog/products/all', t),
  getById:      (id)          => g(`/api/sportshop/catalog/products/${id}`),
  getByCategory:(cat)         => g(`/api/sportshop/catalog/products/category/${cat}`),
  create:       (data, t)     => p('/api/sportshop/catalog/products', data, t),
  update:       (id, data, t) => pu(`/api/sportshop/catalog/products/${id}`, data, t),
  delete:       (id, t)       => d(`/api/sportshop/catalog/products/${id}`, t),
  getCart:      (t)           => g('/api/sportshop/catalog/cart', t),
  addToCart:    (data, t)     => p('/api/sportshop/catalog/cart', data, t),
  updateCart:   (id, qty, t)  => pu(`/api/sportshop/catalog/cart/${id}?quantity=${qty}`, {}, t),
  removeCart:   (id, t)       => d(`/api/sportshop/catalog/cart/${id}`, t),
  purchase:     (t)           => p('/api/sportshop/catalog/cart/purchase', {}, t),
}

// ── ADMIN :8082 ───────────────────────────────────────────────
export const adminApi = {
  getUsers:    (t)            => g('/api/sportshop/admin/users', t),
  createUser:  (data, t)      => p('/api/sportshop/admin/users', data, t),
  updateUser:  (doc, data, t) => pu(`/api/sportshop/admin/users/${doc}`, data, t),
  deleteUser:  (doc, t)       => d(`/api/sportshop/admin/users/${doc}`, t),
  changeRole:  (doc, role, t) => pa(`/api/sportshop/admin/users/${doc}/role?newRole=${role}`, {}, t),
  getProducts: (t)            => g('/api/sportshop/admin/products', t),
  createProd:  (data, t)      => p('/api/sportshop/admin/products', data, t),
  updateProd:  (id, data, t)  => pu(`/api/sportshop/admin/products/${id}`, data, t),
  deleteProd:  (id, t)        => d(`/api/sportshop/admin/products/${id}`, t),
}

// ── NOTIFICATIONS :8083 ───────────────────────────────────────
export const notifApi = {
  getAll:    (t)      => g('/api/sportshop/notifications', t),
  getByUser: (doc, t) => g(`/api/sportshop/notifications/user/${doc}`, t),
  markRead:  (id, t)  => pa(`/api/sportshop/notifications/${id}/read`, {}, t),
}

// ── Helpers ───────────────────────────────────────────────────
export const sportIcon = (sport = '', cat = '') => {
  const s = (sport + cat).toUpperCase()
  if (s.includes('FUTBOL') || s.includes('FOOTBALL') || s.includes('SOCCER')) return 'ti-ball-football'
  if (s.includes('BASKET') || s.includes('BALONCESTO')) return 'ti-ball-basketball'
  if (s.includes('TENNIS') || s.includes('TENIS'))      return 'ti-tennis'
  if (s.includes('RUNNING') || s.includes('ATLETISMO')) return 'ti-run'
  if (s.includes('NATACION') || s.includes('SWIM'))     return 'ti-swimming'
  if (s.includes('BOXEO') || s.includes('BOX'))         return 'ti-boxing-glove'
  if (s.includes('CICLISMO') || s.includes('CYCLING'))  return 'ti-bike'
  if (s.includes('GOLF'))                               return 'ti-golf'
  if (s.includes('VOLEIBOL') || s.includes('VOLLEYBALL')) return 'ti-ball-volleyball'
  if (s.includes('CAMISETA') || s.includes('SHIRT') || s.includes('ROPA')) return 'ti-shirt'
  return 'ti-trophy'
}

export const fmt = (n) =>
  new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }).format(n)

export const fmtDate = (s) =>
  s ? new Date(s).toLocaleString('es-CO', { dateStyle: 'short', timeStyle: 'short' }) : ''
