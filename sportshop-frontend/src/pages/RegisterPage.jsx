import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useToast } from '../context/ToastContext'
import { authApi } from '../services/api'

export default function RegisterPage() {
  const [form, setForm] = useState({ name: '', email: '', password: '', document: '', telephone: '', age: '' })
  const [busy, setBusy] = useState(false)
  const { show } = useToast()
  const nav = useNavigate()
  const set = e => setForm(f => ({ ...f, [e.target.name]: e.target.value }))

  const submit = async (e) => {
    e.preventDefault(); setBusy(true)
    try {
      await authApi.register({ ...form, role: 'USER', age: Number(form.age) || null })
      show('¡Cuenta creada! Inicia sesión.', 'success')
      nav('/login')
    } catch (err) { show(err.message, 'error') }
    finally { setBusy(false) }
  }

  return (
    <div className="auth-wrap">
      <div className="auth-card">
        <div className="auth-top">
          <div className="auth-logo"><i className="ti ti-trophy" /> SportShop</div>
          <div className="auth-sub">Únete hoy</div>
        </div>
        <div className="auth-body">
          <form onSubmit={submit}>
            <div className="field">
              <label>Nombre completo</label>
              <input className="input" name="name" placeholder="Tu nombre" value={form.name} onChange={set} required />
            </div>
            <div className="field">
              <label>Correo electrónico</label>
              <input className="input" type="email" name="email" placeholder="correo@ejemplo.com" value={form.email} onChange={set} required />
            </div>
            <div className="field">
              <label>Contraseña</label>
              <input className="input" type="password" name="password" placeholder="••••••••" value={form.password} onChange={set} required />
              <small style={{ color: 'var(--text2)', fontSize: 11 }}>Mínimo 8 caracteres, una mayúscula, un número y un símbolo (!@#$%)</small>
            </div>
            <div className="grid-2">
              <div className="field">
                <label>Número de documento</label>
                <input className="input" name="document" placeholder="1234567890" value={form.document} onChange={set} required />
              </div>
              <div className="field">
                <label>Edad</label>
                <input className="input" type="number" name="age" placeholder="25" value={form.age} onChange={set} required />
              </div>
            </div>
            <div className="field">
              <label>Teléfono</label>
              <input className="input" name="telephone" placeholder="300 000 0000" value={form.telephone} onChange={set} />
            </div>
            <button className="btn btn-primary btn-full" type="submit" disabled={busy}>
              <i className="ti ti-user-plus" /> {busy ? 'Creando cuenta…' : 'Registrarse'}
            </button>
          </form>
          <p className="auth-link">¿Ya tienes cuenta? <a onClick={() => nav('/login')}>Inicia sesión</a></p>
        </div>
      </div>
    </div>
  )
}
