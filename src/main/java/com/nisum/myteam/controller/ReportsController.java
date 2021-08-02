
package com.nisum.myteam.controller;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.ColumnChartData;
import com.nisum.myteam.model.GroupByCount;
import com.nisum.myteam.model.ReportSeriesRecord;
import com.nisum.myteam.model.Reports;
import com.nisum.myteam.model.dao.Account;
import com.nisum.myteam.model.dao.Employee;
import com.nisum.myteam.model.dao.Project;
import com.nisum.myteam.model.dao.Resource;
import com.nisum.myteam.model.vo.ReportVo;
import com.nisum.myteam.service.IAccountService;
import com.nisum.myteam.service.IEmployeeService;
import com.nisum.myteam.service.IReportService;
import com.nisum.myteam.service.IResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

//import com.nisum.myteam.model.dao.Resource;

@RestController
@RequestMapping("/reports")
public class ReportsController {


    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IEmployeeService employeeService;

    @Autowired
    private IResourceService resourceService;

    @Autowired
    private IAccountService accountService;

    @Autowired
    private IReportService reportService;

   //Ok Response
    @RequestMapping(value = "/getEmployeesByFunctionalGroup1",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GroupByCount>> getEmployeesByFunctionalGroup()
            throws MyTeamException {
        ProjectionOperation projectToMatchModel = project()
                .andExpression("functionalGroup").as("name").andExpression("y")
                .as("y");

        Aggregation agg = newAggregation(
                // match(Criteria.where("employeeId").gt(10)),
                group("functionalGroup").count().as("y"),
                project("y").and("functionalGroup").previousOperation(),
                projectToMatchModel,

                sort(Sort.Direction.DESC, "y")

        );

        // Convert the aggregation result into a List
        AggregationResults<GroupByCount> groupResults = mongoTemplate
                .aggregate(agg, Employee.class, GroupByCount.class);
        List<GroupByCount> result = groupResults.getMappedResults();

        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    //Ok Response
    @RequestMapping(value = "/getEmployeesByFunctionalGroupForReport",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ColumnChartData> getEmployeesByFunctionalGroupForReport()
            throws MyTeamException {
        ProjectionOperation projectToMatchModel = project()
                .andExpression("functionalGroup").as("name").andExpression("y")
                .as("y");
        MatchOperation matchStage = Aggregation
                .match(new Criteria("empStatus").is("Active"));
        Aggregation agg = newAggregation(
                // match(Criteria.where("employeeId").gt(10)),
                matchStage, group("functionalGroup").count().as("y"),
                project("y").and("functionalGroup").previousOperation(),
                projectToMatchModel,

                sort(Sort.Direction.DESC, "y")

        );

        // Convert the aggregation result into a List
        AggregationResults<GroupByCount> groupResults = mongoTemplate
                .aggregate(agg, Employee.class, GroupByCount.class);
        List<GroupByCount> result = groupResults.getMappedResults();
        ColumnChartData reportData = new ColumnChartData();
        reportData.setCategories("data");
        reportData.setSeriesDataList(result);
        return new ResponseEntity<>(reportData, HttpStatus.OK);
    }




    //ok response
    @RequestMapping(value = "/getBillabilityDetailsByMonth",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ColumnChartData> getBillabilityDetailsByMonth()
            throws MyTeamException {
        Date reportDate = new Date();

        Map m = new HashMap();
        ReportSeriesRecord reportSeriesRecordBillable = new ReportSeriesRecord();
        reportSeriesRecordBillable.setName("Billable");
        reportSeriesRecordBillable.setData(new long[12]);
        m.put("Billable", reportSeriesRecordBillable);

        ReportSeriesRecord reportSeriesRecordShadow = new ReportSeriesRecord();
        reportSeriesRecordShadow.setName("Shadow");
        reportSeriesRecordShadow.setData(new long[12]);
        m.put("Shadow", reportSeriesRecordShadow);

        ReportSeriesRecord reportSeriesRecordReserved = new ReportSeriesRecord();
        reportSeriesRecordReserved.setName("Reserved");
        reportSeriesRecordReserved.setData(new long[12]);
        m.put("Reserved", reportSeriesRecordReserved);

        ReportSeriesRecord reportSeriesRecordNBillable = new ReportSeriesRecord();
        reportSeriesRecordNBillable.setName("Non-Billable");
        reportSeriesRecordNBillable.setData(new long[12]);
        m.put("Non-Billable", reportSeriesRecordNBillable);
        List<String> catagories = new ArrayList();

        for (int i = 0; i < 12; i++) {
            reportDate = new Date();
            reportDate.setDate(1);
            reportDate.setMonth(i);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(reportDate);
            int lastDate = calendar.getActualMaximum(Calendar.DATE);
            reportDate.setDate(lastDate);

            String pattern = "MM-dd-yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String date = simpleDateFormat.format(reportDate);
            catagories.add(date);

            Criteria criteriaV1 = Criteria.where("billingStartDate")
                    .lt(reportDate);
            Criteria criteriaV21 = Criteria.where("billingEndDate").is(null);
            Criteria criteriaV22 = Criteria.where("billingEndDate")
                    .gt(reportDate);
            Criteria criteriaV221 = criteriaV1.orOperator(criteriaV21,
                    criteriaV22);
            /*
             * MatchOperation matchStage = Aggregation.match(new Criteria()
             * .andOperator(criteriaV1).andOperator(criteriaV221));
             */
            MatchOperation matchStage = Aggregation.match(criteriaV221);

            Aggregation agg1 = newAggregation(matchStage,
                    group("billableStatus").count().as("count"),
                    project("count").and("billableStatus").previousOperation());

            // Convert the aggregation result into a List
            AggregationResults<ColumnChartData> groupResults1 = mongoTemplate
                    .aggregate(agg1, Resource.class,
                            ColumnChartData.class);
            List<ColumnChartData> result1 = groupResults1.getMappedResults();

            List<String> statusList = new ArrayList();
            statusList.add("Billable");
            statusList.add("Shadow");
            statusList.add("Reserved");
            statusList.add("Non-Billable");
            for (String status : statusList) {

                for (ColumnChartData columnChartData : result1) {
                    if (columnChartData.getBillableStatus() != null
                            && columnChartData.getBillableStatus()
                            .equalsIgnoreCase(status)) {
                        ReportSeriesRecord record = (ReportSeriesRecord) m
                                .get(status);
                        record.getData()[i] = columnChartData.getCount();
                    }

                }
            }
        }
        ColumnChartData reportData = new ColumnChartData();
        System.out.println("catagories" + catagories);
        System.out.println("m.values()" + m.values());
        reportData.setCategoriesList(catagories);
        reportData.setSeriesDataList(new ArrayList<String>(m.values()));
        return new ResponseEntity<>(reportData, HttpStatus.OK);
    }

    //ok response
    @RequestMapping(value = "/getEmployeesByFunctionalGroup",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Map<String, List<GroupByCount>>>> getEmployeesByFunctionalGroup1()
            throws MyTeamException {
        ProjectionOperation projectToMatchModel = project()
                .andExpression("functionalGroup").as("name").andExpression("y")
                .as("y");
        MatchOperation matchStage = Aggregation
                .match(new Criteria("empStatus").is("Active"));
        Aggregation agg = newAggregation(
                // match(Criteria.where("employeeId").gt(10)),
                matchStage, group("functionalGroup").count().as("y"),
                project("y").and("functionalGroup").previousOperation(),
                projectToMatchModel,

                sort(Sort.Direction.DESC, "y")

        );

        // Convert the aggregation result into a List
        AggregationResults<GroupByCount> groupResults = mongoTemplate
                .aggregate(agg, Employee.class, GroupByCount.class);
        List<GroupByCount> result = groupResults.getMappedResults();
        Map<String, List<GroupByCount>> map = new HashMap<String, List<GroupByCount>>();
        map.put("data", result);
        List<Map<String, List<GroupByCount>>> list = new ArrayList<Map<String, List<GroupByCount>>>();
        list.add(map);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @RequestMapping(value = "/fetchEmployeeDetailsByFGAndBillability",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Reports>> getEmployeesByFGAndBillability(@RequestParam("fGroup") String fGroup,
                                                                        @RequestParam("billableStatus") String billableStatus,
                                                                        @RequestParam("onDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date onDate) throws MyTeamException {
        List<Reports> empList=null;
        empList = reportService.getEmployeeDetailsByFGAndBillability(fGroup,billableStatus,onDate);
        return new ResponseEntity<>(empList, HttpStatus.OK);
    }

    @RequestMapping(value = "/fetchEmployeeDetailsByFGAccountAndBillability",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Reports>> getEmployeesByFGAccountAndBillability(@RequestParam("fGroup") String fGroup,
                                                                               @RequestParam("billableStatus") String billableStatus,
                                                                               @RequestParam("acccount") String account,
                                                                               @RequestParam("onDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date onDate) throws MyTeamException {
        List<Reports> empList=null;
        empList = reportService.getEmployeeDetailsByFGAccountAndBillability(fGroup,billableStatus,account,onDate);
        return new ResponseEntity<>(empList, HttpStatus.OK);
    }


    @RequestMapping(value = "/getBarChartReport",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ReportVo billabilityReportByFunctionalGroup(@RequestParam("byType") String byType,
                                                       @RequestParam("onDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date onDate) throws MyTeamException {
        return reportService.getBarChartReport(byType,onDate);
    }



    //Not Ok Response
    @RequestMapping(value = "/getBillabilityDetailsByAccount",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ColumnChartData> getBillabilityDetailsByAccount()
            throws MyTeamException {


        ProjectionOperation projectToMatchModel = project()
                .andExpression("account").as("categories")
                .andExpression("billableStatus").as("seriesName")
                .andExpression("count").as("count");

        MatchOperation matchStage = Aggregation
                .match(new Criteria("active").is(true));
        Aggregation aggregate = Aggregation.newAggregation(matchStage,
                Aggregation.group("account", "billableStatus").count()
                        .as("count"),
                projectToMatchModel);


        // Convert the aggregation result into a List
        AggregationResults<ColumnChartData> groupResults = mongoTemplate.aggregate(aggregate, Resource.class,
                        ColumnChartData.class);
        List<ColumnChartData> result = groupResults.getMappedResults();
        List<String> statusList = new ArrayList();
        statusList.add("Billable");
        statusList.add("Shadow");
        statusList.add("Reserved");
        statusList.add("Non-Billable");
        List<String> catagories = new ArrayList();
        List<ReportSeriesRecord> seriesDetails = new ArrayList<ReportSeriesRecord>();

        List<Account> accounts = accountService.getAllAccounts();


        ColumnChartData reportData = new ColumnChartData();
        for (String status : statusList) {
            catagories = new ArrayList();
            long seriesData[] = new long[accounts.size()];
            int i = 0;
            for (Account acct : accounts) {
                boolean seriesDataExists = false;
                catagories.add(acct.getAccountName());
                for (ColumnChartData columnChartData : result) {
                    if (columnChartData.getCategories() != null
                            && columnChartData.getSeriesName() != null
                            & columnChartData.getCategories()
                            .equalsIgnoreCase(
                                    acct.getAccountName())
                            && columnChartData.getSeriesName()
                            .equalsIgnoreCase(status)) {
                        seriesDataExists = true;
                        seriesData[i] = columnChartData.getCount();
                    }
                }
                if (!seriesDataExists) {
                    // seriesData[i] = 0;
                }
                i++;
            }
            ReportSeriesRecord reportSeriesRecord = new ReportSeriesRecord();
            reportSeriesRecord.setName(status);
            reportSeriesRecord.setData(seriesData);
            seriesDetails.add(reportSeriesRecord);

        }
        reportData.setCategoriesList(catagories);
        reportData.setSeriesDataList(seriesDetails);
        return new ResponseEntity<>(reportData, HttpStatus.OK);
    }

    @RequestMapping(value = "/fetchEmployeeDetailsByAccountBillability",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Reports>> fetchEmployeeDetailsByAccountBillability(@RequestParam("account") String account,
                                                                                  @RequestParam("billabilityStatus") String billabilityStatus,
                                                                                  @RequestParam("onDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date onDate) throws MyTeamException {
        List<Reports> resourcesList = new ArrayList<>();
        if (account != null && !account.isEmpty()) {
            resourcesList = reportService.getEmployeeDetailsByAccountBillability(account,billabilityStatus,onDate);
        }
        return new ResponseEntity<>(resourcesList, HttpStatus.OK);
    }
    
    
    @RequestMapping(value = "/fetchEmployeeDetailsByDateBillability",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Reports>> fetchEmployeeDetailsByDateBillability(
            @RequestParam("billabilityStatus") String billabilityStatus,
            @RequestParam("reportDate") String reportDateString)
            throws MyTeamException {
        List<Resource> empList = new ArrayList<>();
        List<Reports> reports=new ArrayList<>();

        if (reportDateString != null && !reportDateString.isEmpty()) {
            String pattern = "MM-dd-yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            Date reportDateValue = new Date();
            try {
                reportDateValue = simpleDateFormat.parse(reportDateString);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Criteria status = Criteria.where("billableStatus")
                    .is(billabilityStatus);
            Criteria criteriaV1 = Criteria.where("billingStartDate")
                    .lt(reportDateValue);
            Criteria criteriaV21 = Criteria.where("billingEndDate").is(null);
            Criteria criteriaV22 = Criteria.where("billingEndDate")
                    .gt(reportDateValue);
            Criteria criteriaV221 = status.andOperator(
                    criteriaV1.orOperator(criteriaV21, criteriaV22));
            Query query = new Query();
            query.addCriteria(criteriaV221);
            empList = mongoTemplate.find(query, Resource.class);
            empList.stream().forEach(e->{
            	Employee employee=employeeService.getEmployeeById(e.getEmployeeId());
            	Project project=reportService.getProjectById(e.getProjectId());
            	Reports report=new Reports();
            	report.setEmployeeName(employee.getEmployeeName());
            	report.setEmployeeId(employee.getEmployeeId());
            	report.setEmailId(employee.getEmailId());
            	report.setProjectName(project.getProjectName());
            	report.setFunctionalGroup(employee.getFunctionalGroup());
            	report.setBillableStatus(e.getBillableStatus());
            	report.setBillingStartDate(e.getBillingStartDate());
            	report.setBillingEndDate(e.getBillingEndDate());
        		report.setOnBehalfOf(e.getOnBehalfOf()!=null?employeeService.getEmployeeById(e.getOnBehalfOf()).getEmployeeName():"");

            	reports.add(report);
            	
            	
            });  
        }
        return new ResponseEntity<>(reports, HttpStatus.OK);
    }

    @RequestMapping(value = "/getPieChartReport",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ReportVo allBillabilityReport(@RequestParam("byType") String byType,
                                                       @RequestParam("onDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date onDate) throws MyTeamException {
        return reportService.getPieChartReport(byType,onDate);
    }

    @RequestMapping(value = "/fetchEmployeesByBillabilityType",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Reports>> getEmployeesByBillabilityType(
            @RequestParam("billableStatus") String billableStatus,
            @RequestParam("onDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date onDate) throws MyTeamException {
        List<Reports> empList = null;
        empList = reportService.getEmployeeDetailsByBillabilityType(billableStatus, onDate);
        return new ResponseEntity<>(empList, HttpStatus.OK);
    }
}

