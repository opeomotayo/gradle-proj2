package com.nisum.myteam.schedular;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.Mail;
import com.nisum.myteam.model.dao.EffectiveLoginData;
import com.nisum.myteam.model.dao.Employee;
import com.nisum.myteam.model.dao.SchedulersLogsDetails;
import com.nisum.myteam.model.vo.LoginDetailsVO;
import com.nisum.myteam.service.IEffectiveLoginTimeService;
import com.nisum.myteam.service.IEmployeeService;
import com.nisum.myteam.service.IMailService;
import com.nisum.myteam.service.ISchedulersLogsDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class EmployeeAVGHoursScheduler {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeAVGHoursScheduler.class);

    @Autowired
    private IEmployeeService employeeService;

    @Autowired
    private IEffectiveLoginTimeService effectiveLoginTimeService;

    @Autowired
    private ISchedulersLogsDetailsService schedulersLogsDetailsService;

    @Autowired
    private IMailService mailService;

    @Autowired
    private Environment environment;

    @Value("${myTeam.exemptHours.fromemail}")
    private String fromMail;

    @Value("${myTeam.exemptHours.toemail}")
    private String toEmail;


    @Scheduled(cron = "${email.exemptHours.notification.cron}")
    private void sendEmployessAvgHoursMailToLeads() throws IOException, MessagingException {
        logger.info("sendEmployessAvgHoursMailToLeadsnotification::");
        SchedulersLogsDetails schedulersLogsDetails = schedulersLogsDetailsService.getCurrentSchedulerLogDetails("EmployeeAVGHoursScheduler", new Date());
        if (schedulersLogsDetails == null || !"Active".equalsIgnoreCase(schedulersLogsDetails.getSchedulerStatus())) {

        }
        sendhoursExemptEmpListToLeads();
    }

    private void sendhoursExemptEmpListToLeads() throws MessagingException {
        logger.info("sending avg hours list to managers");
        SchedulersLogsDetails schedulersLogsDetails;
        Mail mail = new Mail();
        LocalDate lastWeekDay = LocalDate.now().minusWeeks(1);
        try {
            Date monday = Date.from(lastWeekDay.with(DayOfWeek.MONDAY).atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date sunday = Date.from(lastWeekDay.with(DayOfWeek.SUNDAY).atStartOfDay(ZoneId.systemDefault()).toInstant());
            List<Employee> activeEmpList = employeeService.getActiveEmployees();
            List<LoginDetailsVO> hoursExemptEmployeeList = new ArrayList<>();
            Map<String, String> employeeMangerMap = employeeService.getEmployeeManagers(activeEmpList);
            activeEmpList.removeIf(e -> {
                try {
                    Map<String, Object> obj = effectiveLoginTimeService.getEmployeesEffLoginData(new Long(e.getEmployeeId()), monday, sunday);
                    if (obj != null && (String) obj.get("averageTime") != null) {
                        String hoursMins[] = ((String) obj.get("averageTime")).split(":");
                        if (hoursMins != null) {
                            boolean isHoursExemptEmployee = new Long(hoursMins[0]) < 8;
                            if (isHoursExemptEmployee) {
                                LoginDetailsVO loginDetailsVO = new LoginDetailsVO();
                                loginDetailsVO.setEmployeeId(e.getEmployeeId());
                                loginDetailsVO.setEmployeeName(e.getEmployeeName());
                                loginDetailsVO.setFunctionalGroup(e.getFunctionalGroup());
                                loginDetailsVO.setDeliverManager(employeeMangerMap.get(e.getEmployeeId()));
                                loginDetailsVO.setAvgHours((String) obj.get("averageTime"));
                                loginDetailsVO.setEffectiveLoginData((List<EffectiveLoginData>) obj.get("data"));
                                List<EffectiveLoginData> effectiveLoginDataList = (List<EffectiveLoginData>) obj.get("data");
                                StringBuilder orphanLoginSB = new StringBuilder();
                                effectiveLoginDataList.stream().forEach(eloginData -> {
                                    if (eloginData.getOrphanLogin() != null && !eloginData.getOrphanLogin().isEmpty())
                                        orphanLoginSB.append(" " + parseDate(eloginData.getDate(), "dd-MMM-yyyy")
                                                + " - " + eloginData.getOrphanLogin().stream()
                                                .collect(Collectors.joining(",", "[", "]")));
                                });
                                loginDetailsVO.setOrphanLogin(orphanLoginSB.toString());
                                hoursExemptEmployeeList.add(loginDetailsVO);
                            }
                            return isHoursExemptEmployee;
                        }
                    }

                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
                return false;
            });
            mail.setFrom(fromMail);
            mail.setTo(toEmail);
            mail.setSubject(" Weekly Login Report ");
            mail.setCc(new String[]{"hrops@nisum.com"});
            mail.setBcc(new String[] {"msuleman@nisum.com", "pjain@nisum.com"});
            Collections.sort(hoursExemptEmployeeList, Comparator.comparing(l -> l.getDeliverManager()));
            mailService.sendExemptHoursEmployeDetailsToLeads(mail, hoursExemptEmployeeList);
            schedulersLogsDetails = new SchedulersLogsDetails();
            schedulersLogsDetails.setCreatedDate(new Date());
            schedulersLogsDetails.setSchedulerName("EmployeeAVGHoursScheduler");
            schedulersLogsDetails.setSchedulerStatus("Active");
            schedulersLogsDetails.setDate(parseDate(new Date(), "yyyy-MM-dd"));
            schedulersLogsDetailsService.saveSchedulersLog(schedulersLogsDetails);
        } catch (MyTeamException e) {
            logger.error("Error " + e.getMessage());
        } catch (IOException ie) {
            logger.error("ie Error " + ie.getMessage());
        }
    }

    public static String parseDate(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }
}
