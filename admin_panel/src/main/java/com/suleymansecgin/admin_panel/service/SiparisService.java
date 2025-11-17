package com.suleymansecgin.admin_panel.service;

import com.suleymansecgin.admin_panel.dto.*;
import com.suleymansecgin.admin_panel.exception.BaseException;
import com.suleymansecgin.admin_panel.exception.ErrorMessage;
import com.suleymansecgin.admin_panel.exception.MessageType;
import com.suleymansecgin.admin_panel.model.Siparis;
import com.suleymansecgin.admin_panel.model.SiparisKalemi;
import com.suleymansecgin.admin_panel.model.Urun;
import com.suleymansecgin.admin_panel.model.PazaryeriAyarlari;
import com.suleymansecgin.admin_panel.repository.SiparisRepository;
import com.suleymansecgin.admin_panel.repository.UrunRepository;
import com.suleymansecgin.admin_panel.repository.PazaryeriAyarlariRepository;
import com.suleymansecgin.admin_panel.integration.PazaryeriAdapter;
import com.suleymansecgin.admin_panel.integration.PazaryeriAdapterFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SiparisService {
	
	@Autowired
	private SiparisRepository siparisRepository;
	
	@Autowired
	private UrunRepository urunRepository;
	
	@Autowired
	private PazaryeriAyarlariRepository pazaryeriAyarlariRepository;
	
	@Autowired
	private PazaryeriAdapterFactory adapterFactory;
	
	@Transactional(readOnly = true)
	public List<SiparisResponse> getAllSiparisler() {
		return siparisRepository.findAll().stream()
			.map(this::convertToResponse)
			.collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public SiparisResponse getSiparisById(Long id) {
		Siparis siparis = siparisRepository.findByIdWithKalemler(id)
			.orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, "Sipariş bulunamadı")));
		return convertToResponse(siparis);
	}
	
	@Transactional(readOnly = true)
	public List<SiparisResponse> getSiparislerByDurum(String durum) {
		return siparisRepository.findByDurumOrderByTarih(durum).stream()
			.map(this::convertToResponse)
			.collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public List<SiparisResponse> getSiparislerByPazaryeri(Long pazaryeriId) {
		return siparisRepository.findByPazaryeriId(pazaryeriId).stream()
			.map(this::convertToResponse)
			.collect(Collectors.toList());
	}
	
	@Transactional
	public SiparisResponse createSiparis(SiparisRequest request) {
		PazaryeriAyarlari pazaryeri = pazaryeriAyarlariRepository.findById(request.getPazaryeriId())
			.orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, "Pazaryeri bulunamadı")));
		
		// Aynı pazaryeri sipariş ID'si kontrolü
		if (siparisRepository.findByPazaryeriIdAndPazaryeriSiparisId(request.getPazaryeriId(), request.getPazaryeriSiparisId()).isPresent()) {
			throw new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, "Bu sipariş zaten mevcut"));
		}
		
		Siparis siparis = new Siparis();
		siparis.setPazaryeri(pazaryeri);
		siparis.setPazaryeriSiparisId(request.getPazaryeriSiparisId());
		siparis.setDurum(request.getDurum());
		siparis.setToplamTutar(request.getToplamTutar());
		siparis.setSiparisTarihi(request.getSiparisTarihi());
		siparis.setMusteriAdi(request.getMusteriAdi());
		siparis.setMusteriAdresi(request.getMusteriAdresi());
		siparis.setMusteriTelefon(request.getMusteriTelefon());
		siparis.setMusteriEmail(request.getMusteriEmail());
		siparis.setKargoTakipNo(request.getKargoTakipNo());
		siparis.setKargoFirmasi(request.getKargoFirmasi());
		siparis.setNotlar(request.getNotlar());
		
		// Sipariş kalemlerini oluştur
		for (SiparisKalemiRequest kalemRequest : request.getSiparisKalemleri()) {
			Urun urun = urunRepository.findById(kalemRequest.getUrunId())
				.orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, "Ürün bulunamadı: " + kalemRequest.getUrunId())));
			
			SiparisKalemi kalem = new SiparisKalemi();
			kalem.setSiparis(siparis);
			kalem.setUrun(urun);
			kalem.setMiktar(kalemRequest.getMiktar());
			kalem.setBirimFiyat(kalemRequest.getBirimFiyat());
			kalem.setToplamFiyat(kalemRequest.getBirimFiyat().multiply(BigDecimal.valueOf(kalemRequest.getMiktar())));
			kalem.setPazaryeriUrunKodu(kalemRequest.getPazaryeriUrunKodu());
			
			siparis.getSiparisKalemleri().add(kalem);
			
			// Stok güncelle
			urun.setStokMiktari(urun.getStokMiktari() - kalemRequest.getMiktar());
			urunRepository.save(urun);
		}
		
		siparis = siparisRepository.save(siparis);
		return convertToResponse(siparis);
	}
	
	@Transactional
	public SiparisResponse updateSiparisDurum(Long id, SiparisDurumGuncellemeRequest request) {
		Siparis siparis = siparisRepository.findByIdWithKalemler(id)
			.orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, "Sipariş bulunamadı")));
		
		String eskiDurum = siparis.getDurum();
		siparis.setDurum(request.getDurum());
		
		if (request.getKargoTakipNo() != null) {
			siparis.setKargoTakipNo(request.getKargoTakipNo());
		}
		if (request.getKargoFirmasi() != null) {
			siparis.setKargoFirmasi(request.getKargoFirmasi());
		}
		
		siparis = siparisRepository.save(siparis);
		
		// Durum değiştiyse pazaryerine bildir
		if (!eskiDurum.equals(request.getDurum())) {
			try {
				PazaryeriAdapter adapter = adapterFactory.getAdapter(siparis.getPazaryeri().getPazaryeriAdi());
				adapter.updateOrderStatus(
					siparis.getPazaryeri(),
					siparis.getPazaryeriSiparisId(),
					request.getDurum(),
					request.getKargoTakipNo(),
					request.getKargoFirmasi()
				);
			} catch (Exception e) {
				// Hata loglanacak ama sipariş güncellemesi başarılı
			}
		}
		
		return convertToResponse(siparis);
	}
	
	private SiparisResponse convertToResponse(Siparis siparis) {
		SiparisResponse response = new SiparisResponse();
		response.setId(siparis.getId());
		response.setPazaryeriId(siparis.getPazaryeri().getId());
		response.setPazaryeriAdi(siparis.getPazaryeri().getPazaryeriAdi());
		response.setPazaryeriSiparisId(siparis.getPazaryeriSiparisId());
		response.setDurum(siparis.getDurum());
		response.setToplamTutar(siparis.getToplamTutar());
		response.setSiparisTarihi(siparis.getSiparisTarihi());
		response.setMusteriAdi(siparis.getMusteriAdi());
		response.setMusteriAdresi(siparis.getMusteriAdresi());
		response.setMusteriTelefon(siparis.getMusteriTelefon());
		response.setMusteriEmail(siparis.getMusteriEmail());
		response.setKargoTakipNo(siparis.getKargoTakipNo());
		response.setKargoFirmasi(siparis.getKargoFirmasi());
		response.setNotlar(siparis.getNotlar());
		response.setCreatedAt(siparis.getCreatedAt());
		response.setUpdatedAt(siparis.getUpdatedAt());
		
		if (siparis.getSiparisKalemleri() != null) {
			response.setSiparisKalemleri(siparis.getSiparisKalemleri().stream()
				.map(this::convertKalemToResponse)
				.collect(Collectors.toList()));
		}
		
		return response;
	}
	
	private SiparisKalemiResponse convertKalemToResponse(SiparisKalemi kalem) {
		SiparisKalemiResponse response = new SiparisKalemiResponse();
		response.setId(kalem.getId());
		response.setUrunId(kalem.getUrun().getId());
		response.setUrunKodu(kalem.getUrun().getUrunKodu());
		response.setUrunAdi(kalem.getUrun().getUrunAdi());
		response.setMiktar(kalem.getMiktar());
		response.setBirimFiyat(kalem.getBirimFiyat());
		response.setToplamFiyat(kalem.getToplamFiyat());
		response.setPazaryeriUrunKodu(kalem.getPazaryeriUrunKodu());
		return response;
	}
}

