package com.suleymansecgin.admin_panel.service;

import com.suleymansecgin.admin_panel.dto.*;
import com.suleymansecgin.admin_panel.exception.BaseException;
import com.suleymansecgin.admin_panel.exception.ErrorMessage;
import com.suleymansecgin.admin_panel.exception.MessageType;
import com.suleymansecgin.admin_panel.model.Urun;
import com.suleymansecgin.admin_panel.model.UrunPazaryeriEslesme;
import com.suleymansecgin.admin_panel.model.PazaryeriAyarlari;
import com.suleymansecgin.admin_panel.repository.UrunRepository;
import com.suleymansecgin.admin_panel.repository.UrunPazaryeriEslesmeRepository;
import com.suleymansecgin.admin_panel.repository.PazaryeriAyarlariRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UrunService {
	
	@Autowired
	private UrunRepository urunRepository;
	
	@Autowired
	private UrunPazaryeriEslesmeRepository eslesmeRepository;
	
	@Autowired
	private PazaryeriAyarlariRepository pazaryeriAyarlariRepository;
	
	@Autowired(required = false)
	private SenkronizasyonService senkronizasyonService;
	
	@Transactional(readOnly = true)
	public List<UrunResponse> getAllUrunler() {
		return urunRepository.findAll().stream()
			.map(this::convertToResponse)
			.collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public UrunResponse getUrunById(Long id) {
		Urun urun = urunRepository.findByIdWithEslesmeler(id)
			.orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, "Ürün bulunamadı")));
		return convertToResponse(urun);
	}
	
	@Transactional
	public UrunResponse createUrun(UrunRequest request) {
		if (urunRepository.existsByUrunKodu(request.getUrunKodu())) {
			throw new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, "Bu ürün kodu zaten mevcut"));
		}
		
		Urun urun = new Urun();
		urun.setUrunKodu(request.getUrunKodu());
		urun.setUrunAdi(request.getUrunAdi());
		urun.setAciklama(request.getAciklama());
		urun.setStokMiktari(request.getStokMiktari());
		urun.setTemelFiyat(request.getTemelFiyat());
		urun.setMaliyetFiyati(request.getMaliyetFiyati());
		urun.setKategori(request.getKategori());
		urun.setResimUrl(request.getResimUrl());
		urun.setAktif(request.getAktif());
		
		urun = urunRepository.save(urun);
		
		// Yeni ürün eklendiğinde stok senkronizasyonu yapılabilir (opsiyonel)
		// İlk eşleştirme yapıldığında senkronize edilebilir
		
		return convertToResponse(urun);
	}
	
	@Transactional
	public UrunResponse updateUrun(Long id, UrunRequest request) {
		Urun urun = urunRepository.findById(id)
			.orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, "Ürün bulunamadı")));
		
		if (!urun.getUrunKodu().equals(request.getUrunKodu())) {
			if (urunRepository.existsByUrunKodu(request.getUrunKodu())) {
				throw new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, "Bu ürün kodu zaten kullanılıyor"));
			}
		}
		
		urun.setUrunKodu(request.getUrunKodu());
		urun.setUrunAdi(request.getUrunAdi());
		urun.setAciklama(request.getAciklama());
		urun.setStokMiktari(request.getStokMiktari());
		urun.setTemelFiyat(request.getTemelFiyat());
		urun.setMaliyetFiyati(request.getMaliyetFiyati());
		urun.setKategori(request.getKategori());
		urun.setResimUrl(request.getResimUrl());
		urun.setAktif(request.getAktif());
		
		Integer eskiStok = urun.getStokMiktari();
		urun = urunRepository.save(urun);
		
		// Stok değiştiyse pazaryerlerine senkronize et
		if (!eskiStok.equals(urun.getStokMiktari()) && senkronizasyonService != null) {
			try {
				senkronizasyonService.syncStockToAllPazaryerleri(urun.getId());
			} catch (Exception e) {
				// Hata loglanacak ama ürün kaydı başarılı
			}
		}
		
		return convertToResponse(urun);
	}
	
	@Transactional
	public void deleteUrun(Long id) {
		if (!urunRepository.existsById(id)) {
			throw new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, "Ürün bulunamadı"));
		}
		urunRepository.deleteById(id);
	}
	
	@Transactional
	public UrunPazaryeriEslesmeResponse createEslesme(UrunPazaryeriEslesmeRequest request) {
		Urun urun = urunRepository.findById(request.getUrunId())
			.orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, "Ürün bulunamadı")));
		
		PazaryeriAyarlari pazaryeri = pazaryeriAyarlariRepository.findById(request.getPazaryeriId())
			.orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, "Pazaryeri bulunamadı")));
		
		if (eslesmeRepository.findByUrunIdAndPazaryeriId(request.getUrunId(), request.getPazaryeriId()).isPresent()) {
			throw new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, "Bu eşleştirme zaten mevcut"));
		}
		
		UrunPazaryeriEslesme eslesme = new UrunPazaryeriEslesme();
		eslesme.setUrun(urun);
		eslesme.setPazaryeri(pazaryeri);
		eslesme.setPazaryeriUrunKodu(request.getPazaryeriUrunKodu());
		eslesme.setFiyat(request.getFiyat());
		eslesme.setAktif(request.getAktif());
		
		eslesme = eslesmeRepository.save(eslesme);
		return convertEslesmeToResponse(eslesme);
	}
	
	@Transactional
	public UrunPazaryeriEslesmeResponse updateEslesme(Long id, UrunPazaryeriEslesmeRequest request) {
		UrunPazaryeriEslesme eslesme = eslesmeRepository.findById(id)
			.orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, "Eşleştirme bulunamadı")));
		
		eslesme.setPazaryeriUrunKodu(request.getPazaryeriUrunKodu());
		eslesme.setFiyat(request.getFiyat());
		eslesme.setAktif(request.getAktif());
		
		eslesme = eslesmeRepository.save(eslesme);
		return convertEslesmeToResponse(eslesme);
	}
	
	@Transactional
	public void deleteEslesme(Long id) {
		if (!eslesmeRepository.existsById(id)) {
			throw new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, "Eşleştirme bulunamadı"));
		}
		eslesmeRepository.deleteById(id);
	}
	
	private UrunResponse convertToResponse(Urun urun) {
		UrunResponse response = new UrunResponse();
		response.setId(urun.getId());
		response.setUrunKodu(urun.getUrunKodu());
		response.setUrunAdi(urun.getUrunAdi());
		response.setAciklama(urun.getAciklama());
		response.setStokMiktari(urun.getStokMiktari());
		response.setTemelFiyat(urun.getTemelFiyat());
		response.setMaliyetFiyati(urun.getMaliyetFiyati());
		response.setKategori(urun.getKategori());
		response.setResimUrl(urun.getResimUrl());
		response.setAktif(urun.getAktif());
		response.setCreatedAt(urun.getCreatedAt());
		response.setUpdatedAt(urun.getUpdatedAt());
		
		if (urun.getPazaryeriEslesmeleri() != null) {
			response.setPazaryeriEslesmeleri(urun.getPazaryeriEslesmeleri().stream()
				.map(this::convertEslesmeToResponse)
				.collect(Collectors.toList()));
		}
		
		return response;
	}
	
	private UrunPazaryeriEslesmeResponse convertEslesmeToResponse(UrunPazaryeriEslesme eslesme) {
		UrunPazaryeriEslesmeResponse response = new UrunPazaryeriEslesmeResponse();
		response.setId(eslesme.getId());
		response.setUrunId(eslesme.getUrun().getId());
		response.setUrunKodu(eslesme.getUrun().getUrunKodu());
		response.setUrunAdi(eslesme.getUrun().getUrunAdi());
		response.setPazaryeriId(eslesme.getPazaryeri().getId());
		response.setPazaryeriAdi(eslesme.getPazaryeri().getPazaryeriAdi());
		response.setPazaryeriUrunKodu(eslesme.getPazaryeriUrunKodu());
		response.setFiyat(eslesme.getFiyat());
		response.setAktif(eslesme.getAktif());
		response.setCreatedAt(eslesme.getCreatedAt());
		response.setUpdatedAt(eslesme.getUpdatedAt());
		return response;
	}
}

