import { useState } from 'react'
import { AuthProvider } from './context/AuthContext'
import LoginForm from './components/LoginForm'
import RegisterForm from './components/RegisterForm'
import './App.css'

function App() {
  const [showRegister, setShowRegister] = useState(false)

  return (
    <AuthProvider>
      <div className="app">
        {showRegister ? (
          <RegisterForm onSwitchToLogin={() => setShowRegister(false)} />
        ) : (
          <LoginForm onSwitchToRegister={() => setShowRegister(true)} />
        )}
      </div>
    </AuthProvider>
  )
}

export default App

