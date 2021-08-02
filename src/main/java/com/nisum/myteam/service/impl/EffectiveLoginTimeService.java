package com.nisum.myteam.service.impl;

import com.nisum.myteam.configuration.DbConnection;
import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.EmployeeLoginData;
import com.nisum.myteam.model.dao.EffectiveLoginData;
import com.nisum.myteam.model.dao.Employee;
import com.nisum.myteam.repository.EffectiveLoginDataRepo;
import com.nisum.myteam.service.IEffectiveLoginTimeService;
import com.nisum.myteam.service.IEmployeeService;
import com.nisum.myteam.utils.CommomUtil;
import com.nisum.myteam.utils.MyTeamDateUtils;
import com.nisum.myteam.utils.MyTeamLogger;
import com.nisum.myteam.utils.MyTeamUtils;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EffectiveLoginTimeService implements IEffectiveLoginTimeService {

    @Autowired
    DbConnection dbConnection;

    @Autowired
    private EffectiveLoginDataRepo effectiveLoginDataRepo;

    @Autowired
    private IEmployeeService employeeService;

    @Override
    public EffectiveLoginData calculateEffectiveLoginTimeForEmpAndSave(Employee employee, Date date) throws MyTeamException {

        String query = null;
        if(date == null){
            query = String.format(MyTeamUtils.EMPLOYEE_YESTERDAY_LOGIN_DETAILS_QUERY,employee.getEmployeeId());
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateStr = sdf.format(date);
            query = String.format(MyTeamUtils.EMPLOYEE_LOGIN_DETAILS_QUERY_OF_DATE,employee.getEmployeeId(), dateStr, dateStr);
        }

        EffectiveLoginData effectiveLoginData = null;
        try (Connection connection = dbConnection.getDBConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query.toString())) {
            String entryType = null;
            long time = 0;
            long totalTime=0;
            StringBuilder times = new StringBuilder();
            while (resultSet.next()) {
                long differenceTime;
                EmployeeLoginData employeeLoginData = new EmployeeLoginData();
                employeeLoginData.setEmployeeId(resultSet.getString("EmployeeCode"));
                employeeLoginData.setName(resultSet.getString("FirstName"));
                employeeLoginData.setDate(resultSet.getDate("TransactionDateTime"));
                employeeLoginData.setTime(resultSet.getTime("TransactionDateTime"));
                employeeLoginData.setEntryType(resultSet.getString("IOEntryStatus"));
                employeeLoginData.setEntryDoor(resultSet.getString("ReaderName"));
                if(Objects.nonNull(entryType) && entryType.equals(employeeLoginData.getEntryType())){
                    String type = entryType.equals("IN") ? entryType+ "-" +new Time(time) :
                            employeeLoginData.getEntryType() + "-" + new Time(employeeLoginData.getTime().getTime());
                    effectiveLoginData.getOrphanLogin().add(type);
                }
                if(Strings.isNullOrEmpty(entryType)){
                    effectiveLoginData = new EffectiveLoginData();
                    entryType = employeeLoginData.getEntryType();
                    effectiveLoginData.setName(employee.getEmployeeName());
                    effectiveLoginData.setEmployeeId(employeeLoginData.getEmployeeId());
                    effectiveLoginData.setDate(employeeLoginData.getDate());
                    effectiveLoginData.setLoginTime(employeeLoginData.getTime().toString());
                    effectiveLoginData.setLogoutTime(employeeLoginData.getTime().toString());
                    effectiveLoginData.setDurationAtWorkPlace(getTimeInString(totalTime));
                    if(entryType.equals("OUT"))
                        effectiveLoginData.getOrphanLogin().add("OUT-"+employeeLoginData.getTime().toString());
                }
                if(entryType.equals("IN") && employeeLoginData.getEntryType().equals("OUT")){
                    differenceTime = employeeLoginData.getTime().getTime() - time;
                    totalTime += differenceTime;
                    effectiveLoginData.setLogoutTime(employeeLoginData.getTime().toString());
                    effectiveLoginData.setDurationAtWorkPlace(getTimeInString(totalTime));
                }
                time = employeeLoginData.getTime().getTime();
                entryType = employeeLoginData.getEntryType();
            }
            if(Objects.nonNull(effectiveLoginData)){
                log.info("Storing effective login time for employee:{}",effectiveLoginData.getEmployeeId());
                effectiveLoginDataRepo.save(effectiveLoginData);
            }


        }
        catch (Exception e) {
            e.printStackTrace();
            MyTeamLogger.getInstance().error(e.getMessage());
            throw new MyTeamException(e.getMessage());
        }
        log.info(" end");
        return effectiveLoginData;

    }

    @Override
    public Map<String,Object> getEmployeesEffLoginData(long employeeId, Date fromDate,Date toDate)
            throws ParseException {
        Map<String,Object> response = new HashMap<>();
        if(fromDate.compareTo(toDate) <= 0) {
            List<EffectiveLoginData> loginDataList =
                    effectiveLoginDataRepo.findByDateBetweenOrderByDate(MyTeamDateUtils.getDayLessThanDate(fromDate),
                            toDate);
            if (Objects.nonNull(loginDataList) && employeeId != 0) {
                long totalTime = 0;
                loginDataList.removeIf(effectiveLoginData ->
                        !effectiveLoginData.getEmployeeId().equals(String.valueOf(employeeId)));
                for (EffectiveLoginData effectiveLoginData : loginDataList) {
                    if (effectiveLoginData.getDurationAtWorkPlace() != null) {
                        Date loginTime = MyTeamUtils.tdf.parse(effectiveLoginData.getDurationAtWorkPlace());
                        totalTime += loginTime.getTime();
                    }
                }
                if (totalTime != 0 && !loginDataList.isEmpty())
                    response.put("averageTime", MyTeamUtils.tdf.format(totalTime / loginDataList.size()));
            }
            response.put("data", loginDataList);
        } else {
            response.put("data", "Invalid Dates");
        }
        log.info("getEmployeesEffLoginData end");
        return response;
    }

    @Override
    public void deleteLogins(Date date) throws ParseException {
        List<EffectiveLoginData> list =(List<EffectiveLoginData>) this.getEmployeesEffLoginData(0,date,date).get("data");
        list.forEach(effectiveLoginData -> effectiveLoginDataRepo.delete(effectiveLoginData));
    }

    private String getTimeInString(long timeLong){
        long totalSeconds = timeLong/1000;
        int hours = (int) (totalSeconds / 3600);
        int minutes = (int) ((totalSeconds % 3600) / 60);
        int seconds = (int) ((totalSeconds % 3600) % 60);
        return CommomUtil.appendZero(hours) + ":" + CommomUtil.appendZero(minutes) + ":"
                + CommomUtil.appendZero(seconds);
    }
//    @Override
//    public void deleteEmployeeLoginData(String employeeId, Date fromDate, Date toDate) throws MyTeamException {
//        log.info("Start");
//        List<EffectiveLoginData> loginDataList = effectiveLoginDataRepo.findByDateBetweenOrderByDate(fromDate, toDate);
//        System.out.println(loginDataList.size());
//
//        List<Employee> allEmployees = employeeService.getActiveEmployees();
//        List<Object> deleteLoginData = new ArrayList<>();
//        allEmployees.stream().forEach(e -> {
//
//            List<EffectiveLoginData> data = loginDataList.stream().filter(l -> (
//                    e.getEmployeeId().equalsIgnoreCase(l.getEmployeeId())
//            )).collect(Collectors.toList());
//            //System.out.println(" Data size " +data.size());
//            //System.out.println(data);
//            if (data.size() > 1) {
//                //System.out.println(" Data TESTTTTT " +data);
//                deleteLoginData.add(Optional.ofNullable(data).get().get(0).getId());
//                //effectiveLoginDataRepo.delete(Optional.ofNullable(data).get().get(0));
//            }
//            //System.out.println(effectiveLoginData + " effective login data : " + effectiveLoginData.getEmployeeId() + " Login ID " + effectiveLoginData.getId());
//        });
//        deleteLoginData.stream().forEach(System.out::println);
//       // System.out.println("size" + deleteLoginData.size());
//       // deleteLoginData.stream().forEach(e -> System.out.println(e));
//        //System.out.println("Data between Days" + loginDataList.size());
//        //List<EffectiveLoginData> loginData=  loginDataList.stream().collect(EffectiveLoginData:fromDate).filter(s->"40270".equalsIgnoreCase(s.getEmployeeId())).findFirst();
//        log.info("end");
//
//    }
//
//    @Override
//    public EffectiveLoginData calculateEffectiveLoginTimeForEmpAndSave(Employee employee, Date fromDate, Date toDate) throws MyTeamException {
//        log.info(" start");
//        String query = String.format(MyTeamUtils.EMPLOYEE_LOGIN_DETAILS_QUERY_BY_DATES, employee.getEmployeeId(), parseDate(fromDate), parseDate(toDate));
//        EffectiveLoginData effectiveLoginData = null;
//        HashMap<String,EffectiveLoginData> loginDataHashMap = new HashMap<>();
//        try (Connection connection = dbConnection.getDBConnection();
//             Statement statement = connection.createStatement();
//             ResultSet resultSet = statement.executeQuery(query.toString())) {
//            String entryType = null;
//            long time = 0;
//            long totalTime=0;
//            StringBuilder times = new StringBuilder();
//            while (resultSet.next()) {
//                long differenceTime;
//                EmployeeLoginData employeeLoginData = new EmployeeLoginData();
//                employeeLoginData.setEmployeeId(resultSet.getString("EmployeeCode"));
//                employeeLoginData.setName(resultSet.getString("FirstName"));
//                employeeLoginData.setDate(resultSet.getDate("TransactionDateTime"));
//                employeeLoginData.setTime(resultSet.getTime("TransactionDateTime"));
//                employeeLoginData.setEntryType(resultSet.getString("IOEntryStatus"));
//                employeeLoginData.setEntryDoor(resultSet.getString("ReaderName"));
//                if(Objects.nonNull(entryType) && entryType.equals(employeeLoginData.getEntryType())){
//                    String type = entryType.equals("IN") ? entryType+ "-" +new Time(time) :
//                            employeeLoginData.getEntryType() + "-" + new Time(employeeLoginData.getTime().getTime());
//                    effectiveLoginData.getOrphanLogin().add(type);
//                }
//                if(Strings.isNullOrEmpty(entryType)){
//                    effectiveLoginData = new EffectiveLoginData();
//                    entryType = employeeLoginData.getEntryType();
//                    effectiveLoginData.setName(employee.getEmployeeName());
//                    effectiveLoginData.setEmployeeId(employeeLoginData.getEmployeeId());
//                    effectiveLoginData.setDate(employeeLoginData.getDate());
//                    effectiveLoginData.setLoginTime(employeeLoginData.getTime().toString());
//                    effectiveLoginData.setLogoutTime(employeeLoginData.getTime().toString());
//                    effectiveLoginData.setDurationAtWorkPlace(getTimeInString(totalTime));
//                    if(entryType.equals("OUT"))
//                        effectiveLoginData.getOrphanLogin().add("OUT-"+employeeLoginData.getTime().toString());
//                }
//                if(entryType.equals("IN") && employeeLoginData.getEntryType().equals("OUT")){
//                    differenceTime = employeeLoginData.getTime().getTime() - time;
//                    totalTime += differenceTime;
//                    effectiveLoginData.setLogoutTime(employeeLoginData.getTime().toString());
//                    effectiveLoginData.setDurationAtWorkPlace(getTimeInString(totalTime));
//                }
//                time = employeeLoginData.getTime().getTime();
//                entryType = employeeLoginData.getEntryType();
//                loginDataHashMap.put(parseDate(employeeLoginData.getDate()),effectiveLoginData);
//            }
//            if(Objects.nonNull(effectiveLoginData)){
//                log.info("Storing effective login time for employee:{}",effectiveLoginData.getEmployeeId());
//                if ("40270".equalsIgnoreCase(employee.getEmployeeId()))
//                    effectiveLoginDataRepo.save(effectiveLoginData);
//            }
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//            MyTeamLogger.getInstance().error(e.getMessage());
//            throw new MyTeamException(e.getMessage());
//        }
//        log.info(" end");
//        return effectiveLoginData;
//    }

    public static Date parseDate(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String parseDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }
}
