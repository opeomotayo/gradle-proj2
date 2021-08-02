package com.nisum.myteam.model;

import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;

import com.nisum.myteam.model.dao.EmpLoginData;
import com.nisum.myteam.utils.MyTeamLogger;
import com.nisum.myteam.utils.MyTeamUtils;

public class DateCompare implements Comparator<EmpLoginData> {

	public int compare(EmpLoginData o1, EmpLoginData o2) {

		Date first = null;
		Date second = null;
		try {
			first = MyTeamUtils.df.parse(o1.getFirstLogin());
			second = MyTeamUtils.df.parse(o2.getFirstLogin());
			int result = (first.after(second) ? 1 : 0);
			return (first.before(second) ? -1 : result);
		} catch (ParseException e) {
			MyTeamLogger.getInstance().info(e.getMessage());
		}
		return -1;
	}
}