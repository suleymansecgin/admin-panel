package com.suleymansecgin.admin_panel.service;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static dev.samstevens.totp.util.Utils.getDataUriForImage;

@Service
public class MfaService {
	
	@Value("${app.name:Admin Panel}")
	private String appName;
	
	private final SecretGenerator secretGenerator = new DefaultSecretGenerator();
	private final CodeGenerator codeGenerator = new DefaultCodeGenerator(HashingAlgorithm.SHA1);
	private final TimeProvider timeProvider = new SystemTimeProvider();
	private final CodeVerifier codeVerifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
	
	public String generateSecret() {
		return secretGenerator.generate();
	}
	
	public String generateQrCodeImageUri(String secret, String email) {
		QrData data = new QrData.Builder()
				.label(email)
				.secret(secret)
				.issuer(appName)
				.algorithm(HashingAlgorithm.SHA1)
				.digits(6)
				.period(30)
				.build();
		
		QrGenerator generator = new ZxingPngQrGenerator();
		byte[] imageData = new byte[0];
		try {
			imageData = generator.generate(data);
		} catch (QrGenerationException e) {
			throw new RuntimeException("QR kod oluşturma hatası", e);
		}
		
		return getDataUriForImage(imageData, generator.getImageMimeType());
	}
	
	public boolean verifyCode(String code, String secret) {
		return codeVerifier.isValidCode(secret, code);
	}
}

