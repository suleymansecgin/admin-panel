package com.suleymansecgin.admin_panel.repository;

import com.suleymansecgin.admin_panel.model.SenkronizasyonLoglari;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SenkronizasyonLoglariRepository extends JpaRepository<SenkronizasyonLoglari, Long> {
	
	List<SenkronizasyonLoglari> findByPazaryeriId(Long pazaryeriId);
	
	List<SenkronizasyonLoglari> findByIslemTipi(String islemTipi);
	
	List<SenkronizasyonLoglari> findByDurum(String durum);
	
	@Query("SELECT l FROM SenkronizasyonLoglari l WHERE l.pazaryeri.id = :pazaryeriId AND l.tarih >= :baslangicTarihi ORDER BY l.tarih DESC")
	List<SenkronizasyonLoglari> findByPazaryeriIdAndTarih(@Param("pazaryeriId") Long pazaryeriId, @Param("baslangicTarihi") LocalDateTime baslangicTarihi);
	
	@Query("SELECT l FROM SenkronizasyonLoglari l WHERE l.durum = 'Hata' AND l.tarih >= :baslangicTarihi ORDER BY l.tarih DESC")
	List<SenkronizasyonLoglari> findSonHatalar(@Param("baslangicTarihi") LocalDateTime baslangicTarihi);
	
	@Query("SELECT l FROM SenkronizasyonLoglari l ORDER BY l.tarih DESC")
	List<SenkronizasyonLoglari> findAllOrderByTarihDesc();
}

