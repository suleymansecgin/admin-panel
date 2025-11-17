package com.suleymansecgin.admin_panel.repository;

import com.suleymansecgin.admin_panel.model.Urun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrunRepository extends JpaRepository<Urun, Long> {
	
	Optional<Urun> findByUrunKodu(String urunKodu);
	
	List<Urun> findByAktifTrue();
	
	boolean existsByUrunKodu(String urunKodu);
	
	@Query("SELECT u FROM Urun u LEFT JOIN FETCH u.pazaryeriEslesmeleri WHERE u.id = :id")
	Optional<Urun> findByIdWithEslesmeler(@Param("id") Long id);
	
	@Query("SELECT u FROM Urun u WHERE u.aktif = true AND u.stokMiktari > 0")
	List<Urun> findStokluUrunler();
}

