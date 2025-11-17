package com.suleymansecgin.admin_panel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KarZararRaporuResponse {
	
	private LocalDateTime baslangicTarihi;
	private LocalDateTime bitisTarihi;
	
	// Genel Özet
	private BigDecimal toplamSatisTutari;
	private BigDecimal toplamMaliyet;
	private BigDecimal toplamKar;
	private BigDecimal karMarjiYuzdesi;
	private Integer toplamSiparisSayisi;
	private Integer toplamSatilanUrunAdedi;
	
	// Pazaryeri Bazında Özet
	private List<PazaryeriKarZarar> pazaryeriBazinda;
	
	// Ürün Bazında Özet
	private List<UrunKarZarar> urunBazinda;
	
	// Tarih Bazında Özet (günlük)
	private List<TarihBazindaKarZarar> tarihBazinda;
	
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PazaryeriKarZarar {
		private Long pazaryeriId;
		private String pazaryeriAdi;
		private BigDecimal satisTutari;
		private BigDecimal maliyet;
		private BigDecimal kar;
		private BigDecimal karMarjiYuzdesi;
		private Integer siparisSayisi;
	}
	
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UrunKarZarar {
		private Long urunId;
		private String urunKodu;
		private String urunAdi;
		private BigDecimal satisTutari;
		private BigDecimal maliyet;
		private BigDecimal kar;
		private BigDecimal karMarjiYuzdesi;
		private Integer satilanMiktar;
	}
	
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class TarihBazindaKarZarar {
		private LocalDateTime tarih;
		private BigDecimal satisTutari;
		private BigDecimal maliyet;
		private BigDecimal kar;
		private Integer siparisSayisi;
	}
}

