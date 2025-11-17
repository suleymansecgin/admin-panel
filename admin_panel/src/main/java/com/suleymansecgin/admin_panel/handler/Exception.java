package com.suleymansecgin.admin_panel.handler;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Exception<E> {

	private String path;
	
	private LocalDateTime createTime;
	
	private String hostName;
	
	private E message;
}
