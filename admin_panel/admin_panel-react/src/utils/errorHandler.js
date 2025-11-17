/**
 * API hatalarından string mesaj çıkarır
 * @param {*} error - API hatası
 * @param {string} defaultMessage - Varsayılan hata mesajı
 * @returns {string} - Hata mesajı string'i
 */
export const getErrorMessage = (error, defaultMessage = 'Bir hata oluştu') => {
  if (!error) return defaultMessage
  
  // Eğer zaten string ise direkt döndür
  if (typeof error === 'string') return error
  
  // response.data kontrolü
  const responseData = error.response?.data
  
  if (!responseData) {
    // Eğer response.data yoksa, error'un kendisini kontrol et
    if (typeof error === 'object' && error !== null) {
      if (error.message && typeof error.message === 'string') {
        return error.message
      }
    }
    return defaultMessage
  }
  
  // responseData bir string ise
  if (typeof responseData === 'string') return responseData
  
  // responseData.message kontrolü
  if (responseData.message) {
    if (typeof responseData.message === 'string') {
      return responseData.message
    }
    // message bir obje ise (backend'den gelen ErrorMessage formatı)
    if (typeof responseData.message === 'object' && responseData.message !== null) {
      // Backend'den gelen ErrorMessage formatı: { messageType: "...", ofStatic: "..." }
      if (responseData.message.ofStatic && typeof responseData.message.ofStatic === 'string') {
        return responseData.message.ofStatic
      }
      if (responseData.message.message && typeof responseData.message.message === 'string') {
        return responseData.message.message
      }
      // messageType varsa ona göre mesaj oluştur
      if (responseData.message.messageType) {
        const messageType = responseData.message.messageType
        if (messageType === 'ACCESS_DENIED') {
          return 'Bu işlem için yetkiniz bulunmamaktadır'
        }
        if (messageType === 'NOT_FOUND' || messageType === 'USER_NOT_FOUND') {
          return 'Kayıt bulunamadı'
        }
        if (messageType === 'VALIDATION_ERROR') {
          return 'Geçersiz veri'
        }
        return `Hata: ${messageType}`
      }
    }
  }
  
  // Validation errors kontrolü (MethodArgumentNotValidException için)
  if (responseData.errors && typeof responseData.errors === 'object') {
    const errorMessages = Object.values(responseData.errors).filter(msg => typeof msg === 'string')
    if (errorMessages.length > 0) {
      return errorMessages.join(', ')
    }
  }
  
  // Eğer responseData direkt bir obje ise (message property'si yok)
  if (typeof responseData === 'object' && responseData !== null) {
    // messageType varsa ona göre mesaj oluştur
    if (responseData.messageType) {
      const messageType = responseData.messageType
      if (messageType === 'ACCESS_DENIED') {
        return 'Bu işlem için yetkiniz bulunmamaktadır'
      }
      if (messageType === 'NOT_FOUND' || messageType === 'USER_NOT_FOUND') {
        return 'Kayıt bulunamadı'
      }
      if (messageType === 'VALIDATION_ERROR') {
        return 'Geçersiz veri'
      }
      return `Hata: ${messageType}`
    }
    
    // message property'si varsa
    if (responseData.message && typeof responseData.message === 'string') {
      return responseData.message
    }
    
    // Hiçbir anlamlı property yoksa, obje olarak gösterme, varsayılan mesajı döndür
    return defaultMessage
  }
  
  return defaultMessage
}

