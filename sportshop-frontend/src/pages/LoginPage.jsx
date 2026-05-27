import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { useToast } from '../context/ToastContext'
import { authApi } from '../services/api'

export default function LoginPage() {
  const [form, setForm] = useState({ email: '', password: '' })
  const [busy, setBusy] = useState(false)
  const { login } = useAuth()
  const { show } = useToast()
  const nav = useNavigate()
  const set = e => setForm(f => ({ ...f, [e.target.name]: e.target.value }))

  const submit = async (e) => {
    e.preventDefault(); setBusy(true)
    try {
      const r = await authApi.login(form.email, form.password)
      login(r.data.token, r.data.user)
      show('¡Bienvenido!', 'success')
      nav(r.data.user.role === 'ADMIN' ? '/admin' : '/')
    } catch (err) { show(err.message, 'error') }
    finally { setBusy(false) }
  }

  return (
    <div className="auth-wrap">
      <div className="auth-card">
        <div className="auth-top">
          <div className="auth-logo"><i className="ti ti-trophy" /> SportShop</div>
          <div className="auth-sub">Accede a tu cuenta</div>
        </div>
        <div className="auth-body">
          <form onSubmit={submit}>
            <div className="field">
              <label>Correo electrónico</label>
              <input className="input" type="email" name="email" placeholder="correo@ejemplo.com" value={form.email} onChange={set} required />
            </div>
            <div className="field">
              <label>Contraseña</label>
              <input className="input" type="password" name="password" placeholder="••••••••" value={form.password} onChange={set} required />
            </div>
            <button className="btn btn-primary btn-full" type="submit" disabled={busy}>
              <i className="ti ti-login" /> {busy ? 'Ingresando…' : 'Ingresar'}
            </button>
          </form>
          <p className="auth-link">¿No tienes cuenta? <a onClick={() => nav('/register')}>Regístrate aquí</a></p>
        </div>
      </div>
    </div>
  )
}
