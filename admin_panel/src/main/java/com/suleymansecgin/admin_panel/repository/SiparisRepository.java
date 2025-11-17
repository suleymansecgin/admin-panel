package com.suleymansecgin.admin_panel.repository;

import com.suleymansecgin.admin_panel.model.Siparis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SiparisRepository extends JpaRepository<Siparis, Long> {
	
	Optional<Siparis> findByPazaryeriIdAndPazaryeriSiparisId(Long pazaryeriId, String pazaryeriSiparisId);
	
	List<Siparis> findByPazaryeriId(Long pazaryeriId);
	
	List<Siparis> findByDurum(String durum);
	
	@Query("SELECT s FROM Siparis s LEFT JOIN FETCH s.siparisKalemleri WHERE s.id = :id")
	Optional<Siparis> findByIdWithKalemler(@Param("id") Long id);
	
	@Query("SELECT s FROM Siparis s WHERE s.pazaryeri.id = :pazaryeriId AND s.siparisTarihi >= :baslangicTarihi")
	List<Siparis> findByPazaryeriIdAndTarih(@Param("pazaryeriId") Long pazaryeriId, @Param("baslangicTarihi") LocalDateTime baslangicTarihi);
	
	@Query("SELECT s FROM Siparis s WHERE s.durum = :durum ORDER BY s.siparisTarihi DESC")
	List<Siparis> findByDurumOrderByTarih(@Param("durum") String durum);
}

