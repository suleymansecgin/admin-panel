package com.suleymansecgin.admin_panel.service;

import com.suleymansecgin.admin_panel.dto.KarZararRaporuResponse;
import com.suleymansecgin.admin_panel.model.Siparis;
import com.suleymansecgin.admin_panel.model.SiparisKalemi;
import com.suleymansecgin.admin_panel.model.Urun;
import com.suleymansecgin.admin_panel.repository.SiparisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RaporService {
	
	@Autowired
	private SiparisRepository siparisRepository;
	
	@Transactional(readOnly = true)
	public KarZararRaporuResponse getKarZararRaporu(LocalDateTime baslangicTarihi, LocalDateTime bitisTarihi) {
		// Tarih aralığındaki tüm siparişleri getir (sadece teslim edilen ve iptal olmayan)
		List<Siparis> siparisler = siparisRepository.findAll().stream()
			.filter(s -> s.getSiparisTarihi().isAfter(baslangicTarihi.minusDays(1)) 
					&& s.getSiparisTarihi().isBefore(bitisTarihi.plusDays(1))
					&& !s.getDurum().equals("İptal"))
			.collect(Collectors.toList());
		
		KarZararRaporuResponse rapor = new KarZararRaporuResponse();
		rapor.setBaslangicTarihi(baslangicTarihi);
		rapor.setBitisTarihi(bitisTarihi);
		
		// Genel özet hesaplama
		BigDecimal toplamSatisTutari = BigDecimal.ZERO;
		BigDecimal toplamMaliyet = BigDecimal.ZERO;
		int toplamSiparisSayisi = siparisler.size();
		int toplamSatilanUrunAdedi = 0;
		
		// Pazaryeri bazında toplamlar
		Map<Long, PazaryeriToplam> pazaryeriToplamlari = new HashMap<>();
		
		// Ürün bazında toplamlar
		Map<Long, UrunToplam> urunToplamlari = new HashMap<>();
		
		// Tarih bazında toplamlar
		Map<LocalDate, TarihToplam> tarihToplamlari = new HashMap<>();
		
		for (Siparis siparis : siparisler) {
			// Sipariş kalemlerini işle
			if (siparis.getSiparisKalemleri() != null) {
				for (SiparisKalemi kalem : siparis.getSiparisKalemleri()) {
					Urun urun = kalem.getUrun();
					BigDecimal satisTutari = kalem.getToplamFiyat();
					
					// Maliyet hesaplama (maliyet fiyatı varsa kullan, yoksa 0)
					BigDecimal birimMaliyet = urun.getMaliyetFiyati() != null 
							? urun.getMaliyetFiyati() 
							: BigDecimal.ZERO;
					BigDecimal kalemMaliyet = birimMaliyet.multiply(BigDecimal.valueOf(kalem.getMiktar()));
					
					toplamSatisTutari = toplamSatisTutari.add(satisTutari);
					toplamMaliyet = toplamMaliyet.add(kalemMaliyet);
					toplamSatilanUrunAdedi += kalem.getMiktar();
					
					// Pazaryeri bazında toplam
					Long pazaryeriId = siparis.getPazaryeri().getId();
					PazaryeriToplam pazaryeriToplam = pazaryeriToplamlari.computeIfAbsent(
							pazaryeriId, 
							k -> new PazaryeriToplam(pazaryeriId, siparis.getPazaryeri().getPazaryeriAdi())
					);
					pazaryeriToplam.satisTutari = pazaryeriToplam.satisTutari.add(satisTutari);
					pazaryeriToplam.maliyet = pazaryeriToplam.maliyet.add(kalemMaliyet);
					pazaryeriToplam.siparisSayisi = siparisler.stream()
							.filter(s -> s.getPazaryeri().getId().equals(pazaryeriId))
							.collect(Collectors.toSet()).size();
					
					// Ürün bazında toplam
					Long urunId = urun.getId();
					UrunToplam urunToplam = urunToplamlari.computeIfAbsent(
							urunId,
							k -> new UrunToplam(urunId, urun.getUrunKodu(), urun.getUrunAdi())
					);
					urunToplam.satisTutari = urunToplam.satisTutari.add(satisTutari);
					urunToplam.maliyet = urunToplam.maliyet.add(kalemMaliyet);
					urunToplam.satilanMiktar += kalem.getMiktar();
					
					// Tarih bazında toplam
					LocalDate tarih = siparis.getSiparisTarihi().toLocalDate();
					TarihToplam tarihToplam = tarihToplamlari.computeIfAbsent(
							tarih,
							k -> new TarihToplam(tarih)
					);
					tarihToplam.satisTutari = tarihToplam.satisTutari.add(satisTutari);
					tarihToplam.maliyet = tarihToplam.maliyet.add(kalemMaliyet);
					tarihToplam.siparisSayisi = siparisler.stream()
							.filter(s -> s.getSiparisTarihi().toLocalDate().equals(tarih))
							.collect(Collectors.toSet()).size();
				}
			}
		}
		
		// Pazaryeri bazında toplamları düzelt (siparis sayısı)
		for (Siparis siparis : siparisler) {
			Long pazaryeriId = siparis.getPazaryeri().getId();
			PazaryeriToplam pazaryeriToplam = pazaryeriToplamlari.get(pazaryeriId);
			if (pazaryeriToplam != null) {
				pazaryeriToplam.siparisSayisi = (int) siparisler.stream()
						.filter(s -> s.getPazaryeri().getId().equals(pazaryeriId))
						.count();
			}
		}
		
		// Tarih bazında toplamları düzelt (siparis sayısı)
		for (Siparis siparis : siparisler) {
			LocalDate tarih = siparis.getSiparisTarihi().toLocalDate();
			TarihToplam tarihToplam = tarihToplamlari.get(tarih);
			if (tarihToplam != null) {
				tarihToplam.siparisSayisi = (int) siparisler.stream()
						.filter(s -> s.getSiparisTarihi().toLocalDate().equals(tarih))
						.count();
			}
		}
		
		// Genel özet
		rapor.setToplamSatisTutari(toplamSatisTutari);
		rapor.setToplamMaliyet(toplamMaliyet);
		rapor.setToplamKar(toplamSatisTutari.subtract(toplamMaliyet));
		rapor.setToplamSiparisSayisi(toplamSiparisSayisi);
		rapor.setToplamSatilanUrunAdedi(toplamSatilanUrunAdedi);
		
		// Kar marjı yüzdesi hesaplama
		if (toplamSatisTutari.compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal karMarji = toplamSatisTutari.subtract(toplamMaliyet)
					.divide(toplamSatisTutari, 4, RoundingMode.HALF_UP)
					.multiply(BigDecimal.valueOf(100));
			rapor.setKarMarjiYuzdesi(karMarji);
		} else {
			rapor.setKarMarjiYuzdesi(BigDecimal.ZERO);
		}
		
		// Pazaryeri bazında rapor
		List<KarZararRaporuResponse.PazaryeriKarZarar> pazaryeriList = pazaryeriToplamlari.values().stream()
				.map(pt -> {
					KarZararRaporuResponse.PazaryeriKarZarar p = new KarZararRaporuResponse.PazaryeriKarZarar();
					p.setPazaryeriId(pt.pazaryeriId);
					p.setPazaryeriAdi(pt.pazaryeriAdi);
					p.setSatisTutari(pt.satisTutari);
					p.setMaliyet(pt.maliyet);
					p.setKar(pt.satisTutari.subtract(pt.maliyet));
					if (pt.satisTutari.compareTo(BigDecimal.ZERO) > 0) {
						p.setKarMarjiYuzdesi(pt.satisTutari.subtract(pt.maliyet)
								.divide(pt.satisTutari, 4, RoundingMode.HALF_UP)
								.multiply(BigDecimal.valueOf(100)));
					} else {
						p.setKarMarjiYuzdesi(BigDecimal.ZERO);
					}
					p.setSiparisSayisi(pt.siparisSayisi);
					return p;
				})
				.sorted((a, b) -> b.getSatisTutari().compareTo(a.getSatisTutari()))
				.collect(Collectors.toList());
		rapor.setPazaryeriBazinda(pazaryeriList);
		
		// Ürün bazında rapor
		List<KarZararRaporuResponse.UrunKarZarar> urunList = urunToplamlari.values().stream()
				.map(ut -> {
					KarZararRaporuResponse.UrunKarZarar u = new KarZararRaporuResponse.UrunKarZarar();
					u.setUrunId(ut.urunId);
					u.setUrunKodu(ut.urunKodu);
					u.setUrunAdi(ut.urunAdi);
					u.setSatisTutari(ut.satisTutari);
					u.setMaliyet(ut.maliyet);
					u.setKar(ut.satisTutari.subtract(ut.maliyet));
					if (ut.satisTutari.compareTo(BigDecimal.ZERO) > 0) {
						u.setKarMarjiYuzdesi(ut.satisTutari.subtract(ut.maliyet)
								.divide(ut.satisTutari, 4, RoundingMode.HALF_UP)
								.multiply(BigDecimal.valueOf(100)));
					} else {
						u.setKarMarjiYuzdesi(BigDecimal.ZERO);
					}
					u.setSatilanMiktar(ut.satilanMiktar);
					return u;
				})
				.sorted((a, b) -> b.getSatisTutari().compareTo(a.getSatisTutari()))
				.collect(Collectors.toList());
		rapor.setUrunBazinda(urunList);
		
		// Tarih bazında rapor
		List<KarZararRaporuResponse.TarihBazindaKarZarar> tarihList = tarihToplamlari.values().stream()
				.map(tt -> {
					KarZararRaporuResponse.TarihBazindaKarZarar t = new KarZararRaporuResponse.TarihBazindaKarZarar();
					t.setTarih(tt.tarih.atStartOfDay());
					t.setSatisTutari(tt.satisTutari);
					t.setMaliyet(tt.maliyet);
					t.setKar(tt.satisTutari.subtract(tt.maliyet));
					t.setSiparisSayisi(tt.siparisSayisi);
					return t;
				})
				.sorted((a, b) -> a.getTarih().compareTo(b.getTarih()))
				.collect(Collectors.toList());
		rapor.setTarihBazinda(tarihList);
		
		return rapor;
	}
	
	// Yardımcı sınıflar
	private static class PazaryeriToplam {
		Long pazaryeriId;
		String pazaryeriAdi;
		BigDecimal satisTutari = BigDecimal.ZERO;
		BigDecimal maliyet = BigDecimal.ZERO;
		int siparisSayisi = 0;
		
		PazaryeriToplam(Long pazaryeriId, String pazaryeriAdi) {
			this.pazaryeriId = pazaryeriId;
			this.pazaryeriAdi = pazaryeriAdi;
		}
	}
	
	private static class UrunToplam {
		Long urunId;
		String urunKodu;
		String urunAdi;
		BigDecimal satisTutari = BigDecimal.ZERO;
		BigDecimal maliyet = BigDecimal.ZERO;
		int satilanMiktar = 0;
		
		UrunToplam(Long urunId, String urunKodu, String urunAdi) {
			this.urunId = urunId;
			this.urunKodu = urunKodu;
			this.urunAdi = urunAdi;
		}
	}
	
	private static class TarihToplam {
		LocalDate tarih;
		BigDecimal satisTutari = BigDecimal.ZERO;
		BigDecimal maliyet = BigDecimal.ZERO;
		int siparisSayisi = 0;
		
		TarihToplam(LocalDate tarih) {
			this.tarih = tarih;
		}
	}
}

