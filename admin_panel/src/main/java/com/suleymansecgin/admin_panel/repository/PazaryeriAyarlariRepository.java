package com.suleymansecgin.admin_panel.repository;

import com.suleymansecgin.admin_panel.model.PazaryeriAyarlari;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PazaryeriAyarlariRepository extends JpaRepository<PazaryeriAyarlari, Long> {
	
	Optional<PazaryeriAyarlari> findByPazaryeriAdi(String pazaryeriAdi);
	
	List<PazaryeriAyarlari> findByAktifTrue();
	
	boolean existsByPazaryeriAdi(String pazaryeriAdi);
	
	@Query("SELECT p FROM PazaryeriAyarlari p WHERE p.aktif = true ORDER BY p.pazaryeriAdi")
	List<PazaryeriAyarlari> findAllAktifPazaryerleri();
}

