package com.nisum.myteam.exception.handler;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor

public class ResponseDetails {
	private Date timestamp;
	private int statusCode;
	private String message;
	private String description;
	private Object records;
	private String uriPath;
	private String details;
	private Object requestObject;

	public ResponseDetails(Date timestamp, int statusCode, String message, String description, Object records,
			String path, String details, Object requestObject) {
		super();
		this.timestamp = timestamp;
		this.statusCode = statusCode;
		this.message = message;
		this.description = description;
		this.records = records;
		this.uriPath = path;
		this.details = details;
		this.requestObject = requestObject;
	}

}