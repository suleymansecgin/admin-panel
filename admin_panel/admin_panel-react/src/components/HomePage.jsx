import { Link } from 'react-router-dom'
import './HomePage.css'

const HomePage = () => {
  return (
    <div className="home-page">
      <div className="home-content">
        <Link to="/admin_panel" className="admin-panel-link">
          e-ticaret yönetim paneli
        </Link>
      </div>
    </div>
  )
}

export default HomePage

