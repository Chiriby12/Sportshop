import React from 'react'
import { Navigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export function RequireAuth({ children }) {
  const { isAuth, ready } = useAuth()
  if (!ready) return <div className="spinner" />
  return isAuth() ? children : <Navigate to="/login" replace />
}

export function RequireAdmin({ children }) {
  const { isAuth, isAdmin, ready } = useAuth()
  if (!ready) return <div className="spinner" />
  if (!isAuth()) return <Navigate to="/login" replace />
  if (!isAdmin()) return <Navigate to="/" replace />
  return children
}

export function GuestOnly({ children }) {
  const { isAuth, isAdmin, ready } = useAuth()
  if (!ready) return <div className="spinner" />
  if (isAuth()) return <Navigate to={isAdmin() ? '/admin' : '/'} replace />
  return children
}
