import { useAuth } from '../context/AuthContext'
import './Dashboard.css'

const Dashboard = () => {
  const { user, logout } = useAuth()

  return (
    <div className="dashboard-container">
      <div className="dashboard-header">
        <h1>Admin Panel</h1>
        <div className="user-info">
          <span>Hoş geldiniz, {user?.username}</span>
          <button onClick={logout} className="logout-button">
            Çıkış Yap
          </button>
        </div>
      </div>
      <div className="dashboard-content">
        {/* Bu sayfada işlemler yapılacak */}
      </div>
    </div>
  )
}

export default Dashboard

