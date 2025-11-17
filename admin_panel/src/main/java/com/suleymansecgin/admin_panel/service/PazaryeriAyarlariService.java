package com.suleymansecgin.admin_panel.service;

import com.suleymansecgin.admin_panel.dto.PazaryeriAyarlariRequest;
import com.suleymansecgin.admin_panel.dto.PazaryeriAyarlariResponse;
import com.suleymansecgin.admin_panel.exception.BaseException;
import com.suleymansecgin.admin_panel.exception.ErrorMessage;
import com.suleymansecgin.admin_panel.exception.MessageType;
import com.suleymansecgin.admin_panel.integration.PazaryeriAdapter;
import com.suleymansecgin.admin_panel.integration.PazaryeriAdapterFactory;
import com.suleymansecgin.admin_panel.model.PazaryeriAyarlari;
import com.suleymansecgin.admin_panel.repository.PazaryeriAyarlariRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PazaryeriAyarlariService {
	
	@Autowired
	private PazaryeriAyarlariRepository pazaryeriAyarlariRepository;
	
	@Autowired
	private PazaryeriAdapterFactory adapterFactory;
	
	@Transactional(readOnly = true)
	public List<PazaryeriAyarlariResponse> getAllPazaryerleri() {
		return pazaryeriAyarlariRepository.findAll().stream()
			.map(this::convertToResponse)
			.collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public List<PazaryeriAyarlariResponse> getAktifPazaryerleri() {
		return pazaryeriAyarlariRepository.findAllAktifPazaryerleri().stream()
			.map(this::convertToResponse)
			.collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public PazaryeriAyarlariResponse getPazaryeriById(Long id) {
		PazaryeriAyarlari pazaryeri = pazaryeriAyarlariRepository.findById(id)
			.orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, "Pazaryeri bulunamadı")));
		return convertToResponse(pazaryeri);
	}
	
	@Transactional
	public PazaryeriAyarlariResponse createPazaryeri(PazaryeriAyarlariRequest request) {
		if (pazaryeriAyarlariRepository.existsByPazaryeriAdi(request.getPazaryeriAdi())) {
			throw new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, "Bu pazaryeri zaten mevcut"));
		}
		
		PazaryeriAyarlari pazaryeri = new PazaryeriAyarlari();
		pazaryeri.setPazaryeriAdi(request.getPazaryeriAdi());
		pazaryeri.setApiKey(request.getApiKey());
		pazaryeri.setSecretKey(request.getSecretKey());
		pazaryeri.setYetkiToken(request.getYetkiToken());
		pazaryeri.setAktif(request.getAktif());
		pazaryeri.setEkstraAyarlar(request.getEkstraAyarlar());
		
		// API kimlik bilgilerini doğrula (sadece aktif ve API anahtarları doluysa)
		if (request.getAktif() && hasValidApiCredentials(request)) {
			try {
				PazaryeriAdapter adapter = adapterFactory.getAdapter(request.getPazaryeriAdi());
				if (!adapter.validateCredentials(pazaryeri)) {
					throw new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, "API kimlik bilgileri geçersiz. Lütfen API anahtarlarınızı kontrol edin veya 'Aktif' seçeneğini kapatarak test modunda kaydedin."));
				}
			} catch (IllegalArgumentException e) {
				throw new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, "Pazaryeri adaptörü bulunamadı: " + request.getPazaryeriAdi()));
			} catch (RuntimeException e) {
				// API bağlantı hatası - test modunda kaydetmeye izin ver
				if (e.getMessage() != null && e.getMessage().contains("API isteği başarısız")) {
					throw new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, "API bağlantısı başarısız. Lütfen API anahtarlarınızı kontrol edin veya 'Aktif' seçeneğini kapatarak test modunda kaydedin."));
				}
				throw e;
			}
		}
		
		pazaryeri = pazaryeriAyarlariRepository.save(pazaryeri);
		return convertToResponse(pazaryeri);
	}
	
	@Transactional
	public PazaryeriAyarlariResponse updatePazaryeri(Long id, PazaryeriAyarlariRequest request) {
		PazaryeriAyarlari pazaryeri = pazaryeriAyarlariRepository.findById(id)
			.orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, "Pazaryeri bulunamadı")));
		
		// Pazaryeri adı değişiyorsa kontrol et
		if (!pazaryeri.getPazaryeriAdi().equals(request.getPazaryeriAdi())) {
			if (pazaryeriAyarlariRepository.existsByPazaryeriAdi(request.getPazaryeriAdi())) {
				throw new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, "Bu pazaryeri adı zaten kullanılıyor"));
			}
		}
		
		pazaryeri.setPazaryeriAdi(request.getPazaryeriAdi());
		pazaryeri.setApiKey(request.getApiKey());
		pazaryeri.setSecretKey(request.getSecretKey());
		pazaryeri.setYetkiToken(request.getYetkiToken());
		pazaryeri.setAktif(request.getAktif());
		pazaryeri.setEkstraAyarlar(request.getEkstraAyarlar());
		
		// API kimlik bilgilerini doğrula (sadece aktif ve API anahtarları doluysa)
		if (request.getAktif() && hasValidApiCredentials(request)) {
			try {
				PazaryeriAdapter adapter = adapterFactory.getAdapter(request.getPazaryeriAdi());
				if (!adapter.validateCredentials(pazaryeri)) {
					throw new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, "API kimlik bilgileri geçersiz. Lütfen API anahtarlarınızı kontrol edin veya 'Aktif' seçeneğini kapatarak test modunda kaydedin."));
				}
			} catch (IllegalArgumentException e) {
				throw new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, "Pazaryeri adaptörü bulunamadı: " + request.getPazaryeriAdi()));
			} catch (RuntimeException e) {
				// API bağlantı hatası - test modunda kaydetmeye izin ver
				if (e.getMessage() != null && e.getMessage().contains("API isteği başarısız")) {
					throw new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, "API bağlantısı başarısız. Lütfen API anahtarlarınızı kontrol edin veya 'Aktif' seçeneğini kapatarak test modunda kaydedin."));
				}
				throw e;
			}
		}
		
		pazaryeri = pazaryeriAyarlariRepository.save(pazaryeri);
		return convertToResponse(pazaryeri);
	}
	
	@Transactional
	public void deletePazaryeri(Long id) {
		if (!pazaryeriAyarlariRepository.existsById(id)) {
			throw new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, "Pazaryeri bulunamadı"));
		}
		pazaryeriAyarlariRepository.deleteById(id);
	}
	
	/**
	 * API kimlik bilgilerinin geçerli olup olmadığını kontrol eder
	 * Boş veya test anahtarları için false döner
	 */
	private boolean hasValidApiCredentials(PazaryeriAyarlariRequest request) {
		// API Key ve Secret Key boş değilse ve test anahtarları değilse true döner
		boolean hasApiKey = request.getApiKey() != null && !request.getApiKey().trim().isEmpty();
		boolean hasSecretKey = request.getSecretKey() != null && !request.getSecretKey().trim().isEmpty();
		
		// Test anahtarlarını kontrol et (örnek: TRNDYL-1234-ABCD-XYZ-5678 gibi)
		boolean isTestKey = hasApiKey && (
			request.getApiKey().contains("TEST") || 
			request.getApiKey().contains("1234") ||
			request.getApiKey().startsWith("TRNDYL-") && request.getApiKey().contains("1234")
		);
		
		return hasApiKey && hasSecretKey && !isTestKey;
	}
	
	private PazaryeriAyarlariResponse convertToResponse(PazaryeriAyarlari pazaryeri) {
		PazaryeriAyarlariResponse response = new PazaryeriAyarlariResponse();
		response.setId(pazaryeri.getId());
		response.setPazaryeriAdi(pazaryeri.getPazaryeriAdi());
		response.setSonSenkronizasyonTarihi(pazaryeri.getSonSenkronizasyonTarihi());
		response.setAktif(pazaryeri.getAktif());
		response.setCreatedAt(pazaryeri.getCreatedAt());
		response.setUpdatedAt(pazaryeri.getUpdatedAt());
		return response;
	}
}

