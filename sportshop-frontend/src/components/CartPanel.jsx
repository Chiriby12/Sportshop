import React from 'react'
import { useCart } from '../context/CartContext'
import { useToast } from '../context/ToastContext'
import { sportIcon, fmt } from '../services/api'

export default function CartPanel() {
  const { items, open, setOpen, updateItem, removeItem, purchase, total, loading } = useCart()
  const { show } = useToast()

  const handleQty = async (item, delta) => {
    const qty = item.quantity + delta
    if (qty < 1) { try { await removeItem(item.id) } catch (e) { show(e.message, 'error') } ; return }
    try { await updateItem(item.id, qty) } catch (e) { show(e.message, 'error') }
  }

  const handlePurchase = async () => {
    try { await purchase(); show('¡Compra realizada exitosamente!', 'success') }
    catch (e) { show(e.message, 'error') }
  }

  return (
    <>
      {open && <div style={{ position: 'fixed', inset: 0, background: 'rgba(0,0,0,.2)', zIndex: 85 }} onClick={() => setOpen(false)} />}
      <div className={`cart-panel ${open ? 'open' : ''}`}>
        <div className="cp-header">
          <span className="cp-title"><i className="ti ti-shopping-cart" style={{ marginRight: 6 }} />Mi carrito</span>
          <button className="btn-icon" onClick={() => setOpen(false)}><i className="ti ti-x" /></button>
        </div>

        <div className="cp-items">
          {items.length === 0
            ? <div className="empty"><i className="ti ti-shopping-cart-off" /><p>Tu carrito está vacío</p></div>
            : items.map(item => (
              <div key={item.id} className="cp-item">
                <div className="cp-icon"><i className={`ti ${sportIcon(item.productName)}`} /></div>
                <div className="cp-info">
                  <div className="cp-name">{item.productName}</div>
                  <div className="cp-price">{fmt(item.unitPrice)} c/u</div>
                  <div className="cp-qty">
                    <button className="qty-btn" onClick={() => handleQty(item, -1)}>−</button>
                    <span style={{ fontWeight: 600, minWidth: 18, textAlign: 'center', fontSize: 13 }}>{item.quantity}</span>
                    <button className="qty-btn" onClick={() => handleQty(item, 1)}>+</button>
                    <span style={{ marginLeft: 'auto', fontWeight: 600, fontSize: 13, color: 'var(--blue)' }}>
                      {fmt(item.unitPrice * item.quantity)}
                    </span>
                  </div>
                </div>
                <button className="btn-icon" style={{ alignSelf: 'flex-start' }} onClick={() => removeItem(item.id)}>
                  <i className="ti ti-trash" style={{ fontSize: 14 }} />
                </button>
              </div>
            ))
          }
        </div>

        {items.length > 0 && (
          <div className="cp-footer">
            <div className="cp-total">Total <span>{fmt(total)}</span></div>
            <button className="btn btn-primary btn-full" onClick={handlePurchase} disabled={loading}>
              <i className="ti ti-check" /> {loading ? 'Procesando…' : 'Confirmar compra'}
            </button>
          </div>
        )}
      </div>
    </>
  )
}
