import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import productService from '../services/productService'

const ProductManagement = () => {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const [sidebarOpen, setSidebarOpen] = useState(true)
  const [products, setProducts] = useState([])
  const [loading, setLoading] = useState(true)
  const [showModal, setShowModal] = useState(false)
  const [editingProduct, setEditingProduct] = useState(null)
  const [formData, setFormData] = useState({
    productName: '',
    costPrice: '',
    sellPrice: '',
    stockQuantity: '',
  })
  const [error, setError] = useState('')

  const menuItems = [
    { id: 1, name: 'Dashboard', icon: '📊', path: '/dashboard' },
    { id: 2, name: 'Ürün Yönetimi', icon: '📦', path: '/products', active: true },
    { id: 3, name: 'Ayarlar', icon: '⚙️', path: '/settings' },
    { id: 4, name: 'Raporlar', icon: '📈', path: '/reports' },
    { id: 5, name: 'İstatistikler', icon: '📉', path: '/statistics' },
  ]

  useEffect(() => {
    loadProducts()
  }, [])

  const loadProducts = async () => {
    try {
      setLoading(true)
      const data = await productService.getAllProducts()
      setProducts(data)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  const handleMenuClick = (path) => {
    if (path) {
      navigate(path)
    }
  }

  const handleOpenModal = (product = null) => {
    if (product) {
      setEditingProduct(product)
      setFormData({
        productName: product.productName,
        costPrice: product.costPrice.toString(),
        sellPrice: product.sellPrice.toString(),
        stockQuantity: product.stockQuantity.toString(),
      })
    } else {
      setEditingProduct(null)
      setFormData({
        productName: '',
        costPrice: '',
        sellPrice: '',
        stockQuantity: '',
      })
    }
    setError('')
    setShowModal(true)
  }

  const handleCloseModal = () => {
    setShowModal(false)
    setEditingProduct(null)
    setFormData({
      productName: '',
      costPrice: '',
      sellPrice: '',
      stockQuantity: '',
    })
    setError('')
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')

    try {
      // Validasyon kontrolü
      if (!formData.productName || formData.productName.trim() === '') {
        setError('Ürün adı boş olamaz')
        return
      }

      const costPrice = parseFloat(formData.costPrice)
      const sellPrice = parseFloat(formData.sellPrice)
      const stockQuantity = parseInt(formData.stockQuantity)

      if (isNaN(costPrice) || costPrice <= 0) {
        setError('Geçerli bir maliyet fiyatı giriniz')
        return
      }

      if (isNaN(sellPrice) || sellPrice <= 0) {
        setError('Geçerli bir satış fiyatı giriniz')
        return
      }

      if (isNaN(stockQuantity) || stockQuantity < 0) {
        setError('Geçerli bir stok miktarı giriniz')
        return
      }

      const productData = {
        productName: formData.productName.trim(),
        costPrice: costPrice,
        sellPrice: sellPrice,
        stockQuantity: stockQuantity,
      }

      if (editingProduct) {
        await productService.updateProduct(editingProduct.id, productData)
      } else {
        await productService.createProduct(productData)
      }

      handleCloseModal()
      loadProducts()
    } catch (err) {
      console.error('Ürün ekleme/güncelleme hatası:', err)
      const errorMessage = err?.message || 'Bir hata oluştu. Lütfen tekrar deneyin.'
      setError(errorMessage)
    }
  }

  const handleDelete = async (id) => {
    if (!window.confirm('Bu ürünü silmek istediğinize emin misiniz?')) {
      return
    }

    try {
      await productService.deleteProduct(id)
      loadProducts()
    } catch (err) {
      setError(err.message)
    }
  }

  const formatDate = (dateString) => {
    if (!dateString) return '-'
    const date = new Date(dateString)
    return date.toLocaleString('tr-TR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
    })
  }

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
              onClick={() => handleMenuClick(item.path)}
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
              <h2 className="h3 mb-0 fw-bold text-dark">Ürün Yönetimi</h2>
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
          {/* Yeni Ürün Ekle Butonu */}
          <div className="d-flex justify-content-between align-items-center mb-4">
            <button
              onClick={() => handleOpenModal()}
              className="btn btn-primary"
              type="button"
            >
              ➕ Yeni Ürün Ekle
            </button>
          </div>

          {/* Error Message */}
          {error && (
            <div className="alert alert-danger alert-dismissible fade show" role="alert">
              {error}
              <button
                type="button"
                className="btn-close"
                onClick={() => setError('')}
              ></button>
            </div>
          )}

          {/* Products Table */}
          {loading ? (
            <div className="text-center py-5">
              <div className="spinner-border text-primary" role="status">
                <span className="visually-hidden">Yükleniyor...</span>
              </div>
            </div>
          ) : (
            <div className="bg-white rounded shadow-sm border">
              <div className="table-responsive">
                <table className="table table-hover mb-0">
                  <thead className="table-light">
                    <tr>
                      <th>ID</th>
                      <th>Ürün Adı</th>
                      <th>Maliyet Fiyatı</th>
                      <th>Satış Fiyatı</th>
                      <th>Stok Miktarı</th>
                      <th>Oluşturulma Tarihi</th>
                      <th>İşlemler</th>
                    </tr>
                  </thead>
                  <tbody>
                    {products.length === 0 ? (
                      <tr>
                        <td colSpan="7" className="text-center py-5 text-muted">
                          Henüz ürün eklenmemiş. Yeni ürün eklemek için yukarıdaki butonu kullanın.
                        </td>
                      </tr>
                    ) : (
                      products.map((product) => (
                        <tr key={product.id}>
                          <td>{product.id}</td>
                          <td>{product.productName}</td>
                          <td>{product.costPrice.toFixed(2)} ₺</td>
                          <td>{product.sellPrice.toFixed(2)} ₺</td>
                          <td>{product.stockQuantity}</td>
                          <td>{formatDate(product.createTime)}</td>
                          <td>
                            <div className="d-flex gap-2">
                              <button
                                onClick={() => handleOpenModal(product)}
                                className="btn btn-sm btn-outline-primary"
                                type="button"
                              >
                                ✏️ Düzenle
                              </button>
                              <button
                                onClick={() => handleDelete(product.id)}
                                className="btn btn-sm btn-outline-danger"
                                type="button"
                              >
                                🗑️ Sil
                              </button>
                            </div>
                          </td>
                        </tr>
                      ))
                    )}
                  </tbody>
                </table>
              </div>
            </div>
          )}
        </main>
      </div>

      {/* Modal */}
      {showModal && (
        <div
          className="modal show d-block"
          style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}
          tabIndex="-1"
        >
          <div className="modal-dialog modal-dialog-centered">
            <div className="modal-content">
              <div className="modal-header">
                <h5 className="modal-title">
                  {editingProduct ? 'Ürün Düzenle' : 'Yeni Ürün Ekle'}
                </h5>
                <button
                  type="button"
                  className="btn-close"
                  onClick={handleCloseModal}
                ></button>
              </div>
              <form onSubmit={handleSubmit}>
                <div className="modal-body">
                  {error && (
                    <div className="alert alert-danger" role="alert">
                      {error}
                    </div>
                  )}
                  <div className="mb-3">
                    <label htmlFor="productName" className="form-label">
                      Ürün Adı <span className="text-danger">*</span>
                    </label>
                    <input
                      type="text"
                      className="form-control"
                      id="productName"
                      value={formData.productName}
                      onChange={(e) =>
                        setFormData({ ...formData, productName: e.target.value })
                      }
                      required
                    />
                  </div>
                  <div className="mb-3">
                    <label htmlFor="costPrice" className="form-label">
                      Maliyet Fiyatı <span className="text-danger">*</span>
                    </label>
                    <input
                      type="number"
                      step="0.01"
                      min="0"
                      className="form-control"
                      id="costPrice"
                      value={formData.costPrice}
                      onChange={(e) =>
                        setFormData({ ...formData, costPrice: e.target.value })
                      }
                      required
                    />
                  </div>
                  <div className="mb-3">
                    <label htmlFor="sellPrice" className="form-label">
                      Satış Fiyatı <span className="text-danger">*</span>
                    </label>
                    <input
                      type="number"
                      step="0.01"
                      min="0"
                      className="form-control"
                      id="sellPrice"
                      value={formData.sellPrice}
                      onChange={(e) =>
                        setFormData({ ...formData, sellPrice: e.target.value })
                      }
                      required
                    />
                  </div>
                  <div className="mb-3">
                    <label htmlFor="stockQuantity" className="form-label">
                      Stok Miktarı <span className="text-danger">*</span>
                    </label>
                    <input
                      type="number"
                      min="0"
                      className="form-control"
                      id="stockQuantity"
                      value={formData.stockQuantity}
                      onChange={(e) =>
                        setFormData({ ...formData, stockQuantity: e.target.value })
                      }
                      required
                    />
                  </div>
                </div>
                <div className="modal-footer">
                  <button
                    type="button"
                    className="btn btn-secondary"
                    onClick={handleCloseModal}
                  >
                    İptal
                  </button>
                  <button type="submit" className="btn btn-primary">
                    {editingProduct ? 'Güncelle' : 'Ekle'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default ProductManagement

