import { BrowserRouter, Routes, Route, Navigate, useLocation } from 'react-router-dom'
import { AuthProvider, useAuth } from './context/AuthContext'
import HomePage from './components/HomePage'
import LoginForm from './components/LoginForm'
import RegisterForm from './components/RegisterForm'
import Dashboard from './components/Dashboard'
import ProductManagement from './components/ProductManagement'
import './App.css'

const PrivateRoute = ({ children }) => {
  const { isAuthenticated, loading } = useAuth()
  const location = useLocation()
  const isAdminPanel = location.pathname.startsWith('/admin_panel')
  const loginPath = isAdminPanel ? '/admin_panel/login' : '/login'

  if (loading) {
    return <div className="loading">Yükleniyor...</div>
  }

  return isAuthenticated ? children : <Navigate to={loginPath} replace />
}

const PublicRoute = ({ children }) => {
  const { isAuthenticated, loading } = useAuth()
  const location = useLocation()
  const isAdminPanel = location.pathname.startsWith('/admin_panel')
  const dashboardPath = isAdminPanel ? '/admin_panel/dashboard' : '/dashboard'

  if (loading) {
    return <div className="loading">Yükleniyor...</div>
  }

  return !isAuthenticated ? children : <Navigate to={dashboardPath} replace />
}

function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route path="/admin_panel" element={<Navigate to="/admin_panel/login" replace />} />
      <Route
        path="/admin_panel/login"
        element={
          <PublicRoute>
            <LoginForm />
          </PublicRoute>
        }
      />
      <Route
        path="/admin_panel/register"
        element={
          <PublicRoute>
            <RegisterForm />
          </PublicRoute>
        }
      />
      <Route
        path="/admin_panel/dashboard"
        element={
          <PrivateRoute>
            <Dashboard />
          </PrivateRoute>
        }
      />
      <Route
        path="/admin_panel/products"
        element={
          <PrivateRoute>
            <ProductManagement />
          </PrivateRoute>
        }
      />
      <Route
        path="/login"
        element={
          <PublicRoute>
            <LoginForm />
          </PublicRoute>
        }
      />
      <Route
        path="/register"
        element={
          <PublicRoute>
            <RegisterForm />
          </PublicRoute>
        }
      />
      <Route
        path="/dashboard"
        element={
          <PrivateRoute>
            <Dashboard />
          </PrivateRoute>
        }
      />
      <Route
        path="/products"
        element={
          <PrivateRoute>
            <ProductManagement />
          </PrivateRoute>
        }
      />
    </Routes>
  )
}

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <div className="app">
          <AppRoutes />
        </div>
      </BrowserRouter>
    </AuthProvider>
  )
}

export default App

