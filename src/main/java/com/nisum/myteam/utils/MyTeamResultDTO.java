package com.nisum.myteam.utils;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MyTeamResultDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static String ERROR_CODE = "MTERR100";
	
	public static String SUCCESS_CODE = "MTS100";

	private String resultCode;
	
	private String resultData[];
	
	private int dataArrayCounter;

	public String[] getResultData() {
		return resultData;
	}

	public void setResultData(String resultData) {
		if (this.resultData == null) {
			this.resultData = new String[10];
		}
		for (int i = 0; i < 10; i++) {
			setDataArrayCounter(i);
			if (this.resultData[i] == null || this.resultData[i].isEmpty()) {
				this.resultData[i] = resultData;
				break;
			}
		}
	}

	public int getDataArrayCounter() {
		return dataArrayCounter;
	}

	private void setDataArrayCounter(int dataArrayCounter) {
		this.dataArrayCounter = dataArrayCounter;
	}
	
	

}
