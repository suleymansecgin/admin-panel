package com.suleymansecgin.admin_panel.handler;

import com.suleymansecgin.admin_panel.exception.BaseException;
import com.suleymansecgin.admin_panel.exception.ErrorMessage;
import com.suleymansecgin.admin_panel.exception.MessageType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(BaseException.class)
	public ResponseEntity<com.suleymansecgin.admin_panel.handler.Exception<ErrorMessage>> handleBaseException(BaseException ex, HttpServletRequest request) {
		ErrorMessage errorMessage = new ErrorMessage();
		errorMessage.setMessageType(MessageType.GENERAL_EXCEPTION);
		errorMessage.setOfStatic(ex.getMessage());
		
		com.suleymansecgin.admin_panel.handler.Exception<ErrorMessage> exception = new com.suleymansecgin.admin_panel.handler.Exception<>();
		exception.setPath(request.getRequestURI());
		exception.setCreateTime(LocalDateTime.now());
		exception.setHostName(request.getRemoteHost());
		exception.setMessage(errorMessage);
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception);
	}
	
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<com.suleymansecgin.admin_panel.handler.Exception<ErrorMessage>> handleBadCredentialsException(BadCredentialsException ex, HttpServletRequest request) {
		ErrorMessage errorMessage = new ErrorMessage();
		errorMessage.setMessageType(MessageType.INVALID_CREDENTIALS);
		
		com.suleymansecgin.admin_panel.handler.Exception<ErrorMessage> exception = new com.suleymansecgin.admin_panel.handler.Exception<>();
		exception.setPath(request.getRequestURI());
		exception.setCreateTime(LocalDateTime.now());
		exception.setHostName(request.getRemoteHost());
		exception.setMessage(errorMessage);
		
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exception);
	}
	
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<com.suleymansecgin.admin_panel.handler.Exception<ErrorMessage>> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
		ErrorMessage errorMessage = new ErrorMessage();
		errorMessage.setMessageType(MessageType.ACCESS_DENIED);
		
		com.suleymansecgin.admin_panel.handler.Exception<ErrorMessage> exception = new com.suleymansecgin.admin_panel.handler.Exception<>();
		exception.setPath(request.getRequestURI());
		exception.setCreateTime(LocalDateTime.now());
		exception.setHostName(request.getRemoteHost());
		exception.setMessage(errorMessage);
		
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exception);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
		Map<String, Object> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		
		Map<String, Object> response = new HashMap<>();
		response.put("path", request.getRequestURI());
		response.put("createTime", LocalDateTime.now());
		response.put("errors", errors);
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}
	
	@ExceptionHandler(java.lang.Exception.class)
	public ResponseEntity<com.suleymansecgin.admin_panel.handler.Exception<ErrorMessage>> handleGenericException(java.lang.Exception ex, HttpServletRequest request) {
		ErrorMessage errorMessage = new ErrorMessage();
		errorMessage.setMessageType(MessageType.GENERAL_EXCEPTION);
		errorMessage.setOfStatic(ex.getMessage());
		
		com.suleymansecgin.admin_panel.handler.Exception<ErrorMessage> exception = new com.suleymansecgin.admin_panel.handler.Exception<>();
		exception.setPath(request.getRequestURI());
		exception.setCreateTime(LocalDateTime.now());
		exception.setHostName(request.getRemoteHost());
		exception.setMessage(errorMessage);
		
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception);
	}
}

