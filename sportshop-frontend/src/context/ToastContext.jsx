import React, { createContext, useContext, useState, useCallback } from 'react'

const Ctx = createContext(null)

export function ToastProvider({ children }) {
  const [list, setList] = useState([])

  const show = useCallback((msg, type = 'info') => {
    const id = Date.now()
    setList(l => [...l, { id, msg, type }])
    setTimeout(() => setList(l => l.filter(t => t.id !== id)), 3500)
  }, [])

  return (
    <Ctx.Provider value={{ show }}>
      {children}
      <div className="toast-wrap">
        {list.map(t => (
          <div key={t.id} className={`toast ${t.type === 'success' ? 'ok' : t.type === 'error' ? 'err' : ''}`}>
            <i className={`ti ${t.type === 'success' ? 'ti-check' : t.type === 'error' ? 'ti-x' : 'ti-info-circle'}`} />
            {t.msg}
          </div>
        ))}
      </div>
    </Ctx.Provider>
  )
}

export const useToast = () => useContext(Ctx)
