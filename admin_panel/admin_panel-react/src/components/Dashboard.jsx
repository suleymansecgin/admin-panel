import { useState } from 'react'
import { useAuth } from '../context/AuthContext'

const Dashboard = () => {
  const { user, logout } = useAuth()
  const [sidebarOpen, setSidebarOpen] = useState(true)

  const menuItems = [
    { id: 1, name: 'Dashboard', icon: '📊', active: true },
    { id: 2, name: 'Kullanıcılar', icon: '👥' },
    { id: 3, name: 'Ayarlar', icon: '⚙️' },
    { id: 4, name: 'Raporlar', icon: '📈' },
    { id: 5, name: 'İstatistikler', icon: '📉' },
  ]

  const getColorClasses = (color) => {
    const colors = {
      blue: {
        bg: 'bg-primary bg-opacity-10',
        text: 'text-primary',
        bgLight: 'bg-primary bg-opacity-25',
        border: 'border-primary',
      },
      green: {
        bg: 'bg-success bg-opacity-10',
        text: 'text-success',
        bgLight: 'bg-success bg-opacity-25',
        border: 'border-success',
      },
      purple: {
        bg: 'bg-info bg-opacity-10',
        text: 'text-info',
        bgLight: 'bg-info bg-opacity-25',
        border: 'border-info',
      },
      yellow: {
        bg: 'bg-warning bg-opacity-10',
        text: 'text-warning',
        bgLight: 'bg-warning bg-opacity-25',
        border: 'border-warning',
      },
    }
    return colors[color] || colors.blue
  }

  const stats = [
    { title: 'Toplam Kullanıcı', value: '1,234', change: '+12%', icon: '👥', color: 'blue' },
    { title: 'Aktif Oturumlar', value: '456', change: '+8%', icon: '🟢', color: 'green' },
    { title: 'Toplam İşlem', value: '8,901', change: '+23%', icon: '💼', color: 'purple' },
    { title: 'Gelir', value: '₺45,678', change: '+15%', icon: '💰', color: 'yellow' },
  ]

  return (
    <div className="d-flex vh-100 bg-light" style={{ width: '100vw', height: '100vh', overflow: 'hidden' }}>
      {/* Sidebar */}
      <aside
        className={`${
          sidebarOpen ? 'sidebar-expanded' : 'sidebar-collapsed'
        } bg-white shadow-lg d-flex flex-column`}
        style={{
          width: sidebarOpen ? '256px' : '80px',
          transition: 'width 0.3s ease-in-out',
          height: '100vh',
          position: 'fixed',
          left: 0,
          top: 0,
          zIndex: 1000,
        }}
      >
        {/* Logo */}
        <div className="d-flex align-items-center justify-content-between p-4 border-bottom">
          {sidebarOpen && (
            <h1 className="h5 mb-0 fw-bold text-dark">Admin Panel</h1>
          )}
          <button
            onClick={() => setSidebarOpen(!sidebarOpen)}
            className="btn btn-sm btn-outline-secondary"
            type="button"
          >
            {sidebarOpen ? '◀' : '▶'}
          </button>
        </div>

        {/* Menu Items */}
        <nav className="flex-grow-1 p-3">
          {menuItems.map((item) => (
            <button
              key={item.id}
              className={`w-100 d-flex align-items-center ${
                sidebarOpen ? 'justify-content-start px-3' : 'justify-content-center'
              } py-2 mb-2 rounded border-0 ${
                item.active
                  ? 'bg-primary bg-opacity-10 text-primary fw-semibold'
                  : 'text-dark bg-transparent'
              }`}
              style={{ transition: 'all 0.2s' }}
              onMouseEnter={(e) => {
                if (!item.active) e.target.classList.add('bg-light')
              }}
              onMouseLeave={(e) => {
                if (!item.active) e.target.classList.remove('bg-light')
              }}
            >
              <span className="fs-5">{item.icon}</span>
              {sidebarOpen && <span className="ms-3">{item.name}</span>}
            </button>
          ))}
        </nav>

        {/* User Info */}
        <div className="p-3 border-top">
          <div className={`d-flex align-items-center ${sidebarOpen ? 'justify-content-start' : 'justify-content-center'}`}>
            <div className="rounded-circle bg-primary d-flex align-items-center justify-content-center text-white fw-semibold" style={{ width: '40px', height: '40px' }}>
              {user?.username?.charAt(0).toUpperCase() || 'U'}
            </div>
            {sidebarOpen && (
              <div className="ms-3 flex-grow-1" style={{ minWidth: 0 }}>
                <p className="small mb-0 fw-medium text-dark text-truncate">
                  {user?.username || 'Kullanıcı'}
                </p>
                <p className="small mb-0 text-muted text-truncate">{user?.email || ''}</p>
              </div>
            )}
          </div>
        </div>
      </aside>

      {/* Main Content */}
      <div 
        className="flex-grow-1 d-flex flex-column overflow-hidden"
        style={{
          marginLeft: sidebarOpen ? '256px' : '80px',
          transition: 'margin-left 0.3s ease-in-out',
          width: sidebarOpen ? 'calc(100vw - 256px)' : 'calc(100vw - 80px)',
          height: '100vh',
        }}
      >
        {/* Header */}
        <header className="bg-white shadow-sm border-bottom">
          <div className="d-flex align-items-center justify-content-between px-4 py-3">
            <div>
              <h2 className="h3 mb-0 fw-bold text-dark">Dashboard</h2>
              <p className="small text-muted mb-0 mt-1">
                Hoş geldiniz, {user?.username || 'Kullanıcı'}
              </p>
            </div>
            <div className="d-flex align-items-center gap-3">
              <button className="btn btn-sm btn-outline-secondary" type="button">
                🔔
              </button>
              <button
                onClick={logout}
                className="btn btn-danger btn-sm"
                type="button"
              >
                Çıkış Yap
              </button>
            </div>
          </div>
        </header>

        {/* Content Area */}
        <main className="flex-grow-1 overflow-auto p-4">
          {/* Stats Grid */}
          <div className="row g-3 mb-4">
            {stats.map((stat, index) => {
              const colorClasses = getColorClasses(stat.color)
              return (
                <div key={index} className="col-12 col-md-6 col-lg-3">
                  <div
                    className="bg-white rounded shadow-sm p-4 border h-100"
                    style={{ transition: 'box-shadow 0.3s' }}
                    onMouseEnter={(e) => e.currentTarget.classList.add('shadow')}
                    onMouseLeave={(e) => e.currentTarget.classList.remove('shadow')}
                  >
                    <div className="d-flex align-items-center justify-content-between mb-3">
                      <div className={`rounded ${colorClasses.bg} d-flex align-items-center justify-content-center`} style={{ width: '48px', height: '48px' }}>
                        <span className="fs-4">{stat.icon}</span>
                      </div>
                      <span className={`badge ${colorClasses.bgLight} ${colorClasses.text}`}>
                        {stat.change}
                      </span>
                    </div>
                    <h6 className="text-muted small mb-1 fw-medium">{stat.title}</h6>
                    <p className="h4 mb-0 fw-bold text-dark">{stat.value}</p>
                  </div>
                </div>
              )
            })}
          </div>

          {/* Content Cards */}
          <div className="row g-3 mb-4">
            {/* Recent Activity */}
            <div className="col-12 col-lg-6">
              <div className="bg-white rounded shadow-sm p-4 border h-100">
                <h5 className="fw-semibold text-dark mb-3">Son Aktiviteler</h5>
                <div>
                  {[1, 2, 3, 4].map((item) => (
                    <div key={item} className="d-flex align-items-center gap-3 pb-3 mb-3 border-bottom">
                      <div className="rounded-circle bg-primary bg-opacity-10 d-flex align-items-center justify-content-center" style={{ width: '40px', height: '40px' }}>
                        <span className="text-primary">👤</span>
                      </div>
                      <div className="flex-grow-1">
                        <p className="small mb-0 fw-medium text-dark">Yeni kullanıcı kaydı</p>
                        <p className="small mb-0 text-muted">2 saat önce</p>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div>

            {/* Quick Actions */}
            <div className="col-12 col-lg-6">
              <div className="bg-white rounded shadow-sm p-4 border h-100">
                <h5 className="fw-semibold text-dark mb-3">Hızlı İşlemler</h5>
                <div className="row g-3">
                  {[
                    { name: 'Yeni Kullanıcı', icon: '➕', color: 'primary' },
                    { name: 'Rapor Oluştur', icon: '📄', color: 'success' },
                    { name: 'Ayarlar', icon: '⚙️', color: 'info' },
                    { name: 'Yedekleme', icon: '💾', color: 'warning' },
                  ].map((action, index) => {
                    const getHoverClasses = (color) => {
                      const colorMap = {
                        primary: { border: 'border-primary', bg: 'bg-primary bg-opacity-10' },
                        success: { border: 'border-success', bg: 'bg-success bg-opacity-10' },
                        info: { border: 'border-info', bg: 'bg-info bg-opacity-10' },
                        warning: { border: 'border-warning', bg: 'bg-warning bg-opacity-10' },
                      }
                      return colorMap[color] || colorMap.primary
                    }
                    const hoverClasses = getHoverClasses(action.color)
                    return (
                      <div key={index} className="col-6">
                        <button
                          className={`w-100 p-3 rounded border-2 border-secondary bg-transparent text-center quick-action-btn`}
                          data-hover-border={hoverClasses.border}
                          data-hover-bg={hoverClasses.bg}
                          style={{ transition: 'all 0.3s' }}
                          onMouseEnter={(e) => {
                            e.currentTarget.classList.add(hoverClasses.border, ...hoverClasses.bg.split(' '))
                          }}
                          onMouseLeave={(e) => {
                            e.currentTarget.classList.remove(hoverClasses.border, ...hoverClasses.bg.split(' '))
                          }}
                          type="button"
                        >
                          <div className="fs-4 mb-2">{action.icon}</div>
                          <div className="small fw-medium text-dark">{action.name}</div>
                        </button>
                      </div>
                    )
                  })}
                </div>
              </div>
            </div>
          </div>

          {/* Chart/Table Section */}
          <div className="bg-white rounded shadow-sm p-4 border">
            <h5 className="fw-semibold text-dark mb-3">Genel Bakış</h5>
            <div className="text-muted text-center py-5">
              <p className="h6 mb-2">📊 Grafik ve tablolar buraya eklenecek</p>
              <p className="small mb-0">Bu alan gelecekte detaylı istatistikler için kullanılacak</p>
            </div>
          </div>
        </main>
      </div>
    </div>
  )
}

export default Dashboard

