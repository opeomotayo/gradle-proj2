package com.nisum.myteam.schedular;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.Mail;
import com.nisum.myteam.model.dao.Employee;
import com.nisum.myteam.model.dao.Project;
import com.nisum.myteam.service.IEmployeeService;
import com.nisum.myteam.service.IMailService;
import com.nisum.myteam.service.IProjectService;
import com.nisum.myteam.utils.MyTeamDateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class ProjectEndDateScheduler {
    private static final Logger logger = LoggerFactory.getLogger(LeaveNotificationScheduler.class);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private IMailService mailService;

    @Autowired
    private IProjectService projectService;


    @Autowired
    private IEmployeeService empService;

    @Autowired
    private Environment environment;


    @Scheduled(cron = "${email.project.notification.cron}")
    //@Scheduled(cron = "0 * * * * ?")
    private void sendMailToDls() throws IOException, MessagingException {

        logger.info("Project End date mail notification::");
        Mail mail = new Mail();
        mail.setFrom(environment.getProperty("email.project.notification.from"));
        mail.setSubject(environment.getProperty("email.project.notification.subject"));

        try {
            List<Project> activeProjects = projectService.getActiveProjects();

            logger.info("The active projects count::" + activeProjects.size());

            if (activeProjects != null && activeProjects.size() > 0) {
                LocalDate currentDate = LocalDate.now();
                for (Project activeProject : activeProjects) {
                    LocalDate projectEndDate = MyTeamDateUtils.convertUtilDateToLocalDate(activeProject.getProjectEndDate());
                    logger.info("Project End Date::" + projectEndDate);
                    long noOfDaysBetween = ChronoUnit.DAYS.between(currentDate, projectEndDate);
                    logger.info("Days Difference between EndDate of project and Current Date::" + noOfDaysBetween);

                    if (noOfDaysBetween == 7 || noOfDaysBetween == 1) {
                        List<String> dlIds = activeProject.getDeliveryLeadIds();
                        for (String dlId : dlIds) {
                            Employee employee = empService.getEmployeeById(dlId);
                            mail.setEmpName(employee.getEmployeeName());
                            mail.setTo(employee.getEmailId());

                            //mail.setEmpName("Vijay");
                           // mail.setTo("vakula@nisum.com");
                            logger.info("Mail Notification is sending to:" + employee.getEmployeeName() + "::" + employee.getEmailId());
                            mailService.sendProjectNotification(mail);
                        }

                    }
                }
            }

        } catch (MyTeamException e) {
            e.printStackTrace();
        }


    }


}
