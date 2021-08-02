package com.nisum.myteam.service.impl;

import com.nisum.myteam.exception.handler.MyTeamException;
import com.nisum.myteam.model.FunctionalGroup;
import com.nisum.myteam.model.Reports;
import com.nisum.myteam.model.dao.Employee;
import com.nisum.myteam.model.dao.Project;
import com.nisum.myteam.model.dao.Resource;
import com.nisum.myteam.model.vo.ReportVo;
import com.nisum.myteam.service.IReportService;
import com.nisum.myteam.utils.MyTeamUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService implements IReportService {
	
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private FunctionalGroupService functionalGroupService;

    public ReportVo getBarChartReport(String byType,Date onDate) throws MyTeamException {
        ReportVo reportVo = new ReportVo();

        if(byType.equals("AllFunctionalOrgs")) {
            functionalGroupService.getAllFunctionalGroups().stream().
                    filter(f -> !Arrays.asList("IT","Recruiter","Admin","HR","Accounts","Delivery Org","Global Mobility").contains(f.getName())).
                    forEach(f -> reportVo.getCategoriesList().add(f.getName()));
        } else {
            accountService.getAllAccounts().forEach(a -> reportVo.getCategoriesList().add(a.getAccountName()));
        }

        Map<String,Object> billableData = new HashMap();
        Map<String,Object> nonBillableData = new HashMap();
        Map<String,Object> traineeData = new HashMap();
        List<Object> billableCount = new ArrayList<>();
        List<Object> nonBillableCount = new ArrayList<>();
        List<Object> traineeCount = new ArrayList<>();
        billableData.put("name","Billable");
        nonBillableData.put("name","Non-Billable");
        traineeData.put("name", "Trainee");
        for(String category:reportVo.getCategoriesList()){
            Map<String,Object> billableObj = new HashMap();
            Map<String,Object> nonbillableObj = new HashMap();
            //     Map<String,Object> traineeObj = new HashMap();
            Integer billableC=0;
            Integer nonBillableC=0;
            Integer traineeC=0;
            //         float traineePer;
            float billper;
            float nonBillPer;
            List<Employee> employeeList = new ArrayList<>();
            if(byType.equals("AllFunctionalOrgs")) {
                employeeList   = getEmployeesByFunctionalGroup(category,onDate);

            } else if(byType.equals("Account")){
                employeeList = getEmployeeByAccounts(category,onDate);
            }
            else {
            	employeeList = getEmployeesByAccAndFG(category,byType,onDate);
            }
            for(Employee employee:employeeList){
                Resource resource = null;
                if(Objects.nonNull(onDate)){
                    resource = resourceService.getAllocationOfDate(employee.getEmployeeId(),onDate);
                }
                if(resource!=null && resource.getBillableStatus().equals("Billable")){
                    billableC++;
                }else if(resource!=null && resource.getBillableStatus().equals("Trainee")) {
                    traineeC++;
                } else if(resource!=null){
                    nonBillableC++;
                }
            }
            billper = ((billableC / (float)(employeeList.size() - traineeC))*100);
            nonBillPer = nonBillableC /(float) (employeeList.size()-traineeC)*100;
            billableObj.put("percent", billper);
            billableObj.put("y", billableC);
            nonbillableObj.put("percent", nonBillPer);
            nonbillableObj.put("y", nonBillableC);
            billableCount.add(billableObj);
            nonBillableCount.add(nonbillableObj);
            traineeCount.add(traineeC);
        }
        billableData.put("data",billableCount);
        nonBillableData.put("data",nonBillableCount);
        traineeData.put("data", traineeCount);
        reportVo.getSeriesDataList().add(billableData);
        reportVo.getSeriesDataList().add(nonBillableData);
        reportVo.getSeriesDataList().add(traineeData);
    return reportVo;
    }

    private List<Employee> getEmployeesByAccAndFG(String account, String functionalGroup, Date onDate) {
    	List<Employee> empList;
    	empList = getEmployeeByAccounts(account,onDate).stream().filter(e -> e.getFunctionalGroup().equals(functionalGroup)).collect(Collectors.toList());
		return empList;
	}

	private List<Employee> getEmployeesByFunctionalGroup(String functionalGroup,Date onDate){
        List<Employee> employeeList= employeeService.getEmployeesByFunctionalGrp(functionalGroup);
        employeeList.removeIf(e -> !filterEmployeesByDate(onDate, e));
        return employeeList;
    }

    private List<Employee> getEmployeeByAccounts(String accountName,Date onDate){
        List<Employee> employeeList = new ArrayList<>();
        List<Project> projects = projectService.getProjectsByAccountId(accountService.getAccountByName(accountName).getAccountId());
        projects.stream().forEach(p -> {
        resourceService.getResourceByProjectId(p.getProjectId()).stream().filter(resource -> resource.getBillingStartDate().compareTo(onDate) <=0 &&
                resource.getBillingEndDate().compareTo(onDate)>=0 ).
                forEach(r ->{
                	Employee employee= employeeService.findByEmployeeId(r.getEmployeeId());
                	if(employee!=null)
                			employeeList.add(employeeService.getEmployeeById(r.getEmployeeId()));
                });
        });
        employeeList.removeIf(e -> !filterEmployeesByDate(onDate, e));
        return employeeList;
    }

	@Override
	public List<Reports> getEmployeeDetailsByFGAndBillability(String fGroup, String billableStatus,Date onDate)
			throws MyTeamException {
		List<Employee> employeesByFG=employeeService.getEmployeesByFunctionalGrp(fGroup);
        employeesByFG.removeIf(e -> !filterEmployeesByDate(onDate, e));
		return resultantEmployeeWithBillability(employeesByFG,billableStatus,onDate);
	}

	@Override
	public List<Reports> getEmployeeDetailsByAccountBillability(String accountName, String billabilityStatus,Date onDate)
			throws MyTeamException {
		
		return resultantEmployeeWithBillability(getEmployeeByAccounts(accountName,onDate),billabilityStatus,onDate);
	}

    @Override
    public List<Reports> getEmployeeDetailsByFGAccountAndBillability(String fGroup, String billableStatus, String account,Date onDate) throws MyTeamException {
        List<Employee> empList = getEmployeesByAccAndFG(account,fGroup,onDate);
        return resultantEmployeeWithBillability(empList,billableStatus,onDate);
    }

    @Override
    public ReportVo getPieChartReport(String byType, Date onDate) throws MyTeamException {
        List<Employee> employeeList = employeeService.getAllEmployeeListByFunGrps(functionalGroupService.getAllBillableFunctionalGroups().stream().collect(Collectors.mapping(FunctionalGroup::getName, Collectors.toList())), onDate);
        return preparePieChartData(byType, onDate, employeeList);
    }

    @Override
    public List<Reports> getEmployeeDetailsByBillabilityType(String billabilityType, Date onDate) throws MyTeamException {
        List<Employee> employeeList = employeeService.getAllEmployeeListByFunGrps(functionalGroupService.getAllBillableFunctionalGroups().stream().collect(Collectors.mapping(FunctionalGroup::getName, Collectors.toList())), onDate);
        return resultantEmployeeWithBillability(employeeList, billabilityType, onDate);
    }

    private List<Reports> resultantEmployeeWithBillability(List<Employee> employees,
			String billableStatus,Date ondate) throws MyTeamException {
		
		List<Reports> billableEmployees=new ArrayList<Reports>();
		List<Reports> nonBillableEmployees=new ArrayList<Reports>();
		List<Reports> trainees=new ArrayList<Reports>();
		   for(Employee employee:employees){
               Resource resource = resourceService.getAllocationOfDate(employee.getEmployeeId(),ondate);
               if(resource!=null && resource.getBillableStatus().equals("Billable")){
               	   billableEmployees.add(mappingReports(employee,resource));
               }else if(resource!=null && resource.getBillableStatus().equals("Trainee")) {
            	   trainees.add(mappingReports(employee,resource));
               } else if(resource!=null){
            		   nonBillableEmployees.add(mappingReports(employee,resource));
               }
           }
		if(billableStatus.equals("Billable")) {
			return billableEmployees;
		}
		else if(billableStatus.equals("Trainee")) {
			return trainees;
		}
		else 
			return nonBillableEmployees;

	}

	private Reports  mappingReports(Employee employee,Resource resourceObj){
		Reports Reports=new Reports();
		Reports.setEmployeeName(employee.getEmployeeName());
		Reports.setEmailId(employee.getEmailId());
		Reports.setFunctionalGroup(employee.getFunctionalGroup());
		Reports.setEmployeeId(employee.getEmployeeId());
		if(resourceObj!=null) {
		Project project=projectService.getProjectByProjectId(resourceObj.getProjectId());
		Reports.setProjectName(project.getProjectName());
		Reports.setOnBehalfOf(resourceObj.getOnBehalfOf()!=null?employeeService.getEmployeeById(resourceObj.getOnBehalfOf()).getEmployeeName():"");
		Reports.setBillingStartDate(resourceObj.getBillingStartDate());
		Reports.setBillableStatus(resourceObj.getBillableStatus());
		Reports.setBillingEndDate(resourceObj.getBillingEndDate());
		}
	
		return Reports;
	 }

	@Override
	public Project getProjectById(String projectId) {
		return projectService.getProjectByProjectId(projectId);
	}

    public ReportVo preparePieChartData(String byType, Date onDate, List<Employee> employeeList) throws MyTeamException {
        ReportVo reportVo = new ReportVo();
        Integer billableCount = 0;
        Integer nonBillableCount = 0;
        Integer traineeCount = 0;
        Integer allEmployeesSize = employeeList.size();
        float traineePer;
        float billper;
        Map<String, Object> billableObj = new HashMap();
        Map<String, Object> nonBillableObj = new HashMap();
        Map<String, Object> traineeObj = new HashMap();
        float nonBillPer;
        Map<String,List<Resource>> resourceServiceAllocationOfDateMap = resourceService.getAllocationOfDateMap(employeeList.stream().collect(Collectors.mapping(Employee::getEmployeeId, Collectors.toList())), onDate);
        Map<String, Integer> utilityResourceDataMap = new HashMap<>();
        utilityResourceDataMap.put("Billable", billableCount);
        utilityResourceDataMap.put("Non-Billable", nonBillableCount);
        utilityResourceDataMap.put("Trainee", traineeCount);
        resourceServiceAllocationOfDateMap.forEach((s, resources) -> {
            Resource resource = resources.stream().findFirst().get();
            if (resource != null && resource.getBillableStatus().equals("Billable")) {
                utilityResourceDataMap.put("Billable", utilityResourceDataMap.get("Billable")+1);
            } else if (resource != null && resource.getBillableStatus().equals("Trainee")) {
                utilityResourceDataMap.put("Trainee", utilityResourceDataMap.get("Trainee")+1);
            } else if (resource != null) {
                utilityResourceDataMap.put("Non-Billable", utilityResourceDataMap.get("Non-Billable")+1);
            }
        });
        billper = ((utilityResourceDataMap.get("Billable") / (float) (allEmployeesSize - utilityResourceDataMap.get("Trainee"))) * 100);
        nonBillPer = utilityResourceDataMap.get("Non-Billable") / (float) (allEmployeesSize - utilityResourceDataMap.get("Trainee")) * 100;
        traineePer = utilityResourceDataMap.get("Trainee") / (float) (allEmployeesSize - (utilityResourceDataMap.get("Billable") + utilityResourceDataMap.get("Non-Billable"))) * 100;
        billableObj.put("percent", billper);
        billableObj.put("y", utilityResourceDataMap.get("Billable"));
        billableObj.put("name", "Billable");
        nonBillableObj.put("percent", nonBillPer);
        nonBillableObj.put("y", utilityResourceDataMap.get("Non-Billable"));
        nonBillableObj.put("name", "Non-Billable");
        traineeObj.put("y", utilityResourceDataMap.get("Trainee"));
        traineeObj.put("name", "Trainee");
        traineeObj.put("percent", traineePer);
        reportVo.getSeriesDataList().add(billableObj);
        reportVo.getSeriesDataList().add(nonBillableObj);
        reportVo.getSeriesDataList().add(traineeObj);
        return reportVo;
    }
   private boolean filterEmployeesByDate(Date onDate, Employee e) {
        //System.out.println(e.getEndDate() + " : Empty Value : "+ Optional.ofNullable(e.getEndDate()).isPresent()+ " Emp "+e);
        return onDate.after(e.getDateOfJoining()) &&  Optional.ofNullable(e.getEndDate()).isPresent() ? onDate.before(e.getEndDate()):true;
    }
}
