package com.suleymansecgin.admin_panel.repository;

import com.suleymansecgin.admin_panel.model.UrunPazaryeriEslesme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrunPazaryeriEslesmeRepository extends JpaRepository<UrunPazaryeriEslesme, Long> {
	
	Optional<UrunPazaryeriEslesme> findByUrunIdAndPazaryeriId(Long urunId, Long pazaryeriId);
	
	Optional<UrunPazaryeriEslesme> findByPazaryeriIdAndPazaryeriUrunKodu(Long pazaryeriId, String pazaryeriUrunKodu);
	
	List<UrunPazaryeriEslesme> findByUrunId(Long urunId);
	
	List<UrunPazaryeriEslesme> findByPazaryeriId(Long pazaryeriId);
	
	List<UrunPazaryeriEslesme> findByUrunIdAndAktifTrue(Long urunId);
	
	@Query("SELECT e FROM UrunPazaryeriEslesme e WHERE e.pazaryeri.id = :pazaryeriId AND e.aktif = true")
	List<UrunPazaryeriEslesme> findAktifEslesmelerByPazaryeriId(@Param("pazaryeriId") Long pazaryeriId);
}

