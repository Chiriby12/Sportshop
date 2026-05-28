import React, { createContext, useContext, useState, useEffect } from 'react'

const Ctx = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser]   = useState(null)
  const [token, setToken] = useState(null)
  const [ready, setReady] = useState(false)

  useEffect(() => {
    const t = localStorage.getItem('ss_token')
    const u = localStorage.getItem('ss_user')
    if (t && u) { setToken(t); setUser(JSON.parse(u)) }
    setReady(true)
  }, [])

  const login = (t, u) => {
    localStorage.setItem('ss_token', t)
    localStorage.setItem('ss_user', JSON.stringify(u))
    setToken(t); setUser(u)
  }

  const logout = () => {
    localStorage.removeItem('ss_token')
    localStorage.removeItem('ss_user')
    setToken(null); setUser(null)
  }

  return (
    <Ctx.Provider value={{
      user, token, ready,
      login, logout,
      isAuth: () => !!token,
      isAdmin: () => user?.role === 'ADMIN'
    }}>
      {children}
    </Ctx.Provider>
  )
}

export const useAuth = () => useContext(Ctx)
