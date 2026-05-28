import React, { createContext, useContext, useState, useEffect } from 'react'
import { catalogApi } from '../services/api'
import { useAuth } from './AuthContext'

const Ctx = createContext(null)

export function CartProvider({ children }) {
  const { token, isAuth } = useAuth()
  const [items, setItems]   = useState([])
  const [open, setOpen]     = useState(false)
  const [loading, setLoading] = useState(false)

  const load = async () => {
    if (!isAuth()) return
    try { const r = await catalogApi.getCart(token); setItems(r.data || []) } catch { setItems([]) }
  }

  useEffect(() => { load() }, [token])

  const addItem = async (productId, quantity) => {
    setLoading(true)
    try { await catalogApi.addToCart({ productId, quantity }, token); await load(); setOpen(true) }
    finally { setLoading(false) }
  }

  const updateItem = async (id, qty) => {
    await catalogApi.updateCart(id, qty, token); await load()
  }

  const removeItem = async (id) => {
    await catalogApi.removeCart(id, token); await load()
  }

  const purchase = async () => {
    setLoading(true)
    try { await catalogApi.purchase(token); setItems([]); setOpen(false) }
    finally { setLoading(false) }
  }

  const total = items.reduce((s, i) => s + i.unitPrice * i.quantity, 0)
  const count = items.reduce((s, i) => s + i.quantity, 0)

  return (
    <Ctx.Provider value={{ items, open, setOpen, addItem, updateItem, removeItem, purchase, total, count, loading, load }}>
      {children}
    </Ctx.Provider>
  )
}

export const useCart = () => useContext(Ctx)
