package com.nisum.myteam.schedular;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.Mail;
import com.nisum.myteam.model.dao.Employee;
import com.nisum.myteam.service.IEmployeeService;
import com.nisum.myteam.service.IMailService;
import com.nisum.myteam.utils.MyTeamDateUtils;

@Component
public class WorkAnniversaryScheduler {

    private static final Logger logger = LoggerFactory.getLogger(LeaveNotificationScheduler.class);
    
    @Autowired
    private IMailService mailService;

    @Autowired
    private IEmployeeService empService;

    @Autowired
    private Environment environment;
    
    @Scheduled(cron = "${email.workAnniversary.notification.cron}")
    private void sendWorkAnniversaryMailToEmployees () throws MessagingException, IOException {
    	
        logger.info("Work Anniversary notification::");
        Mail mail = new Mail();
        mail.setFrom(environment.getProperty("email.workAnniversary.notification.from"));
        mail.setSubject(environment.getProperty("email.workAnniversary.notification.subject"));
        
        try {
			List<Employee> activeEmpList = empService.getActiveEmployees();
			
			logger.info("The active Employees count::" + activeEmpList.size());
			
            if (activeEmpList != null && activeEmpList.size() > 0) {
                LocalDate currentDate = LocalDate.now();
                for (Employee activeEmployee : activeEmpList) {
                    LocalDate empDateOfJoining = MyTeamDateUtils.convertUtilDateToLocalDate(activeEmployee.getDateOfJoining());
                    logger.info("Date Of Joining::" + empDateOfJoining);
                    if((empDateOfJoining.getDayOfMonth() == currentDate.getDayOfMonth()) &&
                    		(empDateOfJoining.getMonthValue() == currentDate.getMonthValue())) {
                    	logger.info("Mail Notification is sending to:" + activeEmployee.getEmployeeName() + "::" + activeEmployee.getEmailId());
                    	mail.setTo(activeEmployee.getEmailId());
                    	mail.setCc(new String[]{"hrops@nisum.com"});
//                    	mail.setTo("pjain@nisum.com");
//                    	mail.setCc(new String[]{"vksingh@nisum.com"});
                    	mailService.sendWorkAnniversaryNotification(mail);
                    }
                }
            }
		} catch (MyTeamException e) {
			e.printStackTrace();
		}
    }
}
