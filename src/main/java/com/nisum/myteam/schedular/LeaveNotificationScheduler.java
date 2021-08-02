package com.nisum.myteam.schedular;


import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.AttendenceData;
import com.nisum.myteam.model.FunctionalGroup;
import com.nisum.myteam.model.Mail;
import com.nisum.myteam.model.dao.Employee;
import com.nisum.myteam.model.dao.EmployeeSubStatus;
import com.nisum.myteam.service.IFunctionalGroupService;
import com.nisum.myteam.service.IMailService;
import com.nisum.myteam.service.impl.AttendanceService;
import com.nisum.myteam.service.impl.EmployeeService;
import com.nisum.myteam.service.impl.SubStatusService;
import com.nisum.myteam.statuscodes.EmployeeStatus;
import com.nisum.myteam.utils.MyTeamDateUtils;
import com.nisum.myteam.utils.MyTeamUtils;
import com.nisum.myteam.utils.constants.EmpSubStatus;
import com.nisum.myteam.utils.constants.Shifts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class LeaveNotificationScheduler {
    //private static final Logger logger = LoggerFactory.getLogger(LeaveNotificationScheduler.class);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    @Autowired
    private IMailService mailService;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private Environment environment;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private IFunctionalGroupService functionalGroupService;

    @Autowired
    private SubStatusService subStatusService;


    @Scheduled(cron = "${email.leave.notification.shift1.cron}")
    //@Scheduled(cron = "0 * * * * ?")
    public void scheduleLeaveMailForShift1Empls() throws IOException, MessagingException, MyTeamException {
        //Shift 1(9:00 AM - 6:00 PM)
        log.info(Shifts.SHIFT1.getShiftType() + " :: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()));
        sendMailToAbsentees(Shifts.SHIFT1.getShiftType());

    }

    // @Scheduled(cron = "${email.leave.notification.shift2.cron}")
    public void scheduleLeaveMailForShift2Empls() throws IOException, MessagingException, MyTeamException {
        //Shift-2(2:00 PM - 11:00 PM)--General Shift
        log.info(Shifts.SHIFT2.getShiftType() + " :: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()));
        sendMailToAbsentees(Shifts.SHIFT2.getShiftType());

    }

     @Scheduled(cron = "${email.leave.notification.shift3.cron}")
    public void scheduleLeaveMailForShift3Empls() throws IOException, MessagingException, MyTeamException {
        //Shift 3(10:00 PM - 6:00 AM)
        log.info(Shifts.SHIFT3.getShiftType() + " :: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()));
        sendMailToAbsentees(Shifts.SHIFT3.getShiftType());
    }


     @Scheduled(cron = "${email.leave.notification.shift4.cron}")
    public void scheduleLeaveMailForShift4Empls() throws IOException, MessagingException, MyTeamException {
        //Shift 4(7:30 AM - 3:30 PM)
        log.info(Shifts.SHIFT4.getShiftType() + " :: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()));
        sendMailToAbsentees(Shifts.SHIFT4.getShiftType());

    }

    @Scheduled(cron = "${email.leave.notification.shift5.cron}")
    public void scheduleLeaveMailForShift5Empls() throws IOException, MessagingException, MyTeamException {
        //Shift 5(11:30 AM - 7:30 PM)
        log.info(Shifts.SHIFT5.getShiftType() + " :: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()));
        sendMailToAbsentees(Shifts.SHIFT5.getShiftType());
    }


    private void sendMailToAbsentees(String shift) throws IOException, MessagingException, MyTeamException {

        Map<String, Object> model = new HashMap<String, Object>();
        String currentDate = dateTimeFormatter.format(LocalDateTime.now());
        String functionalGroupName;
        final String mailSubject = String.valueOf("Date::").concat(MyTeamDateUtils.FormatTodaysDate() + "::").concat(environment.getProperty("email.leave.notification.subject"));
        List<AttendenceData> attendenceList = new ArrayList<>();
        List<AttendenceData> absentiesList = new ArrayList<>();
        Employee employee = new Employee();
        String empSubStatus = "";

        if (!MyTeamDateUtils.isTodayHoliday(environment.getProperty("email.holidays.list.2020", "01-01-2020,15-01-2020,25-03-2020,10-04-2020,25-05-2020,02-06-2020,31-07-2020,02-10-2020,26-10-2020,25-12-2020"))) {

            try {
                attendenceList = attendanceService.getAttendanciesReport(currentDate, shift);
                absentiesList = attendenceList.stream()
                        .filter(attendance -> attendance.getPresent().equalsIgnoreCase(MyTeamUtils.ABSENT)).collect(Collectors.toList());
            } catch (MyTeamException ex) {
                ex.printStackTrace();
                log.error("An exception occured while fetching absenties list::" + ex.getMessage());
            }

            for (AttendenceData absentee : absentiesList) {

                //model.put("employeeName", );//mail.setModel(model);
                Mail mail = new Mail();
                mail.setFrom(environment.getProperty("email.leave.notification.from"));
                mail.setSubject(mailSubject);

                try {
                    employee = employeeService.getEmployeeById(absentee.getEmployeeId());
                    log.info("employee Details::" + employee);
                    EmployeeSubStatus subStatusObj = subStatusService.getCurrentSubStatus(employee.getEmployeeId());
                    log.info("Emplyee SubStatusObj::" + subStatusObj);
                    if (subStatusObj != null) {
                        if (subStatusObj.getSubStatus() != null) {
                            empSubStatus = (String) subStatusObj.getSubStatus();
                        } else {
                            empSubStatus = "";
                        }
                    } else {
                        empSubStatus = "";
                    }
                } catch (Exception statusException) {
                    statusException.printStackTrace();
                    log.error("Exception happend while fetching the Employee sub status::" + statusException.getMessage());
                }
                log.info("The final empSubStatus::" + empSubStatus);
                if (employee.getEmpStatus().equalsIgnoreCase(EmployeeStatus.ACTIVE.getStatus()) &&
                        (!empSubStatus.equalsIgnoreCase(EmpSubStatus.LONG_LEAVE.getLeaveType()) &&
                                !empSubStatus.equalsIgnoreCase(EmpSubStatus.MATERNITY_LEAVE.getLeaveType()) &&
                                !empSubStatus.equalsIgnoreCase(EmpSubStatus.ONSITE_TRAVEL.getLeaveType()) &&
                                !empSubStatus.equalsIgnoreCase(EmpSubStatus.AT_CLIENT_LOCATION.getLeaveType()))) {


                    try {
                        log.info("Mail Notification is sending to:" + absentee.getEmployeeName() + "::" + absentee.getEmailId());
                        mail.setEmpName(absentee.getEmployeeName());
                        mail.setTo(absentee.getEmailId());//mail.setEmpName("Prayas");//mail.setTo("pjain@nisum.com");

                        FunctionalGroup functionalGroup = functionalGroupService.getFunctionalGroup(employee.getFunctionalGroup());
                        if (functionalGroup != null) {
                            mail.setCc(new String[]{functionalGroup.getGroupHeadEmailId()});//mail.setCc(new String[]{"prayasjain21@gmail.com"});
                        }
                        mailService.sendLeaveNotification(mail);

                    } catch (Exception sentEx) {
                        sentEx.printStackTrace();
                        log.error("An error Occured while sending mail to Absentie::" + sentEx.getMessage());
                    }


                }

            }
        }


    }

}
