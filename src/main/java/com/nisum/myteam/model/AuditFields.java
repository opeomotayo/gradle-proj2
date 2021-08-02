package com.nisum.myteam.model;

import java.util.Date;

import com.nisum.myteam.utils.MyTeamUtils;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuditFields {
    String createdBy;
    String modifiedBy;
    Date createdOn;
    Date lastModifiedOn;

	public void setAuditFields(String loginEmpId, String action) {
		Date currentDate = new Date();
		if (MyTeamUtils.CREATE.equals(action)) {
			this.createdBy = loginEmpId;
			this.createdOn = currentDate;
		}
		this.modifiedBy = loginEmpId;
		this.lastModifiedOn = currentDate;
	}
}
