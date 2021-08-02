package com.nisum.myteam.schedular;

import java.sql.SQLException;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.service.impl.EmployeeDataService;
import com.nisum.myteam.utils.MyTeamLogger;

@DisallowConcurrentExecution
public class MyTeamCronSchedularJob implements Job {

	@Autowired
	private EmployeeDataService employeeDataService;     

	@Override
	public void execute(JobExecutionContext jobExecutionContext) {
		/*try {
			if (employeeDataService.fetchEmployeesDataOnDayBasis()) {
				MyTimeLogger.getInstance().info("Shedular Executed Successfully Records Saved in DB");
			}
		} catch (MyTimeException | SQLException e) {
			MyTimeLogger.getInstance().error("Shedular failed to Executed ::: " , e);
		}*/
	}
}
