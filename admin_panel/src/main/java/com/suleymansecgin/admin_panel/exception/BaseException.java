package com.suleymansecgin.admin_panel.exception;

public class BaseException extends RuntimeException{

	public BaseException(ErrorMessage errorMessage) {
		super(errorMessage.prepareErrorMessage());
	}
}
