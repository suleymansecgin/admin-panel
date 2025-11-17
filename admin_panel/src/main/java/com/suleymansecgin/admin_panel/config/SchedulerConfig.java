package com.suleymansecgin.admin_panel.config;

import com.suleymansecgin.admin_panel.service.SenkronizasyonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedulerConfig {
	
	@Autowired
	private SenkronizasyonService senkronizasyonService;
	
	/**
	 * Her 5 dakikada bir tüm pazaryerlerinden yeni siparişleri çeker
	 */
	@Scheduled(fixedRate = 300000) // 5 dakika = 300000 ms
	public void syncOrders() {
		senkronizasyonService.syncOrdersFromAllPazaryerleri();
	}
}

