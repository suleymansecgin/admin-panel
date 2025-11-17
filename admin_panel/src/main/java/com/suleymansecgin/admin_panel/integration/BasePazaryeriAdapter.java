package com.suleymansecgin.admin_panel.integration;

import com.suleymansecgin.admin_panel.model.PazaryeriAyarlari;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;

/**
 * Pazaryeri adaptörleri için base class
 * Ortak HTTP işlemleri burada yapılır
 */
public abstract class BasePazaryeriAdapter implements PazaryeriAdapter {
	
	protected WebClient webClient;
	
	public BasePazaryeriAdapter() {
		this.webClient = WebClient.builder()
			.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
			.build();
	}
	
	/**
	 * HTTP GET isteği yapar
	 */
	protected String getRequest(String url, PazaryeriAyarlari ayarlar) {
		try {
			return webClient.get()
				.uri(url)
				.headers(headers -> addAuthHeaders(headers, ayarlar))
				.retrieve()
				.bodyToMono(String.class)
				.timeout(Duration.ofSeconds(30))
				.block();
		} catch (WebClientResponseException e) {
			throw new RuntimeException("API isteği başarısız: " + e.getMessage(), e);
		}
	}
	
	/**
	 * HTTP POST isteği yapar
	 */
	protected String postRequest(String url, Object body, PazaryeriAyarlari ayarlar) {
		try {
			return webClient.post()
				.uri(url)
				.headers(headers -> addAuthHeaders(headers, ayarlar))
				.bodyValue(body)
				.retrieve()
				.bodyToMono(String.class)
				.timeout(Duration.ofSeconds(30))
				.block();
		} catch (WebClientResponseException e) {
			throw new RuntimeException("API isteği başarısız: " + e.getMessage(), e);
		}
	}
	
	/**
	 * HTTP PUT isteği yapar
	 */
	protected String putRequest(String url, Object body, PazaryeriAyarlari ayarlar) {
		try {
			return webClient.put()
				.uri(url)
				.headers(headers -> addAuthHeaders(headers, ayarlar))
				.bodyValue(body)
				.retrieve()
				.bodyToMono(String.class)
				.timeout(Duration.ofSeconds(30))
				.block();
		} catch (WebClientResponseException e) {
			throw new RuntimeException("API isteği başarısız: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Pazaryeri özel authentication header'larını ekler
	 * Her pazaryeri için override edilmelidir
	 */
	protected abstract void addAuthHeaders(org.springframework.http.HttpHeaders headers, PazaryeriAyarlari ayarlar);
}

