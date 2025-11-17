package com.suleymansecgin.admin_panel.repository;

import com.suleymansecgin.admin_panel.model.RefreshToken;
import com.suleymansecgin.admin_panel.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	
	Optional<RefreshToken> findByToken(String token);
	
	@Modifying
	@Query("DELETE FROM RefreshToken rt WHERE rt.user = :user")
	void deleteByUser(@Param("user") User user);
	
	@Modifying
	@Query("DELETE FROM RefreshToken rt WHERE rt.expiryDate < :now")
	void deleteExpiredTokens(@Param("now") LocalDateTime now);
	
	@Modifying
	@Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.user = :user")
	void revokeAllUserTokens(@Param("user") User user);
}

