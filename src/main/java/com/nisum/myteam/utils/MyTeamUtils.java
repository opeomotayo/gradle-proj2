package com.nisum.myteam.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class MyTeamUtils {




	private MyTeamUtils() {

	}

	//public final static String driverUrl = "jdbc:ucanaccess://";
	//public final static String msdriveUrl ="jdbc:sqlserver://";
	public final static String STATUS_RELEASED="Released";
	public final static String STATUS_ENGAGED ="Engaged";
	public static final String STATUS_PROPOSED ="Proposed" ;

	public final static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public final static DateFormat tdf = new SimpleDateFormat("HH:mm");
	public final static DateFormat dfmt = new SimpleDateFormat("yyyy-MM-dd");
	//public final static DateFormat dfmtDdMmmYyyy = new SimpleDateFormat("dd-MM-yyyy");
	public final static String UNDER_SCORE = "_";
	public final static String DATE_OF_LOGIN = "dateOfLogin";
	public final static String EMPLOYEE = "Employee";
	public final static String EMPLOYEE_ID = "employeeId";
	public final static String EMPLOYEE_NAME = "employeeName";
	public final static String FIRST_LOGIN = "firstLogin";
	public final static String LAST_LOGOUT = "lastLogout";
	public final static String TOTAL_LOGIN_TIME = "totalLoginTime";
	public final static String EMPLOYEE_COLLECTION = "EmployeesLoginData";
	public final static String ID = "_id";
	public final static String HYPHEN = "-";
	public final static String ZERO = "0";
	public final static String COLON = ":";
	public final static String EMP_NAME_QUERY = "SELECT * FROM EMPLOYEES Where EMPLOYEECODE=?";

	//public final static String WORKING_EMPLOYEES = "SELECT * FROM EMPLOYEES WHERE EMPLOYEECODE NOT IN(SELECT UserId FROM DeviceLogs_12_2017 WHERE LogDate BETWEEN '2017-12-27 06:00:00' AND '2017-12-27 11:00:00') AND STATUS='Working'";
	//public final static String QUERY = "SELECT * FROM DeviceLogs_";
	//public final static String USERID_QUERY = "SELECT USERID FROM DeviceLogs_";
	//public final static String WHERE_COND = " WHERE LogDate between '";
	public final static String AND_COND = " AND '";
	public final static String SINGLE_QUOTE = "'";
	//public final static String ABESENT_QUERY = "SELECT * FROM EMPLOYEES WHERE EMPLOYEECODE NOT IN(";
	//public final static String ABESENT_QUERY1 = ") AND STATUS='Working' AND EMPLOYEECODE NOT LIKE 'del%' ";
	public final static String ABESENT = "Absent";
	public final static String EMAIL_ID = "emailId";

	public final static String ACCOUNT_ID = "accountId";
	public static final int ONE = 1;
	public final static String ACC = "Acc";
	public static final String ZERO_ = "00";
	// Manage account details
	public static final String ID_ = "id";
	public static final String ACCOUNT_NAME = "accountName";
	public static final String STATUS = "status";
	public static final String CLIENT_ADDRESS = "clientAddress";
	public static final String INDUSTRY_TYPE = "industryType";
	public static final String DELIVERY_MANAGERS = "deliveryManagers";
	public static final String ACTIVE = "Active";
	public static final String IN_ACTIVE = "InActive";
	public static final String IN_ACTIVE_SPACE = "In Active";
	public static final String IN_HYPEN_ACTIVE_SPACE = "In-Active";
	public final static String TEAMDETAILS_COLLECTION_NAME = "TeamDetails";
	public final static String BILLINGDETAILS_COLLECTION_NAME = "BillingDetails";
	public final static String ENDDATE_COLUMN = "endDate";
	public final static String BILLING_ENDDATE_COLUMN = "billingEndDate";
	public final static String CREATED_DATE_COLUMN = "createDate";
	public final static String PROJECT_NAME = "projectName";
	public final static String SET = "$set";
	public final static int MINUS_ONE = -1;

	public final static String FREE_POLL="Bench";
	public final static String START_DATE="startDate";
	
	//Domain Constants
	public final static String DOM="DOM";
	public final static String DOMAIN_NAME="domainName";
	public final static String DOMAIN_ID="domainId";
	//public final static String NAME="deliveryManagers";
	
	public final static String STRING_Y="Y";
	public final static String STRING_N="N";
	       
	
	//Biometric Attendance 
	public final static String ABESENT_STATUS_QUERY = "select emp.EmployeeCode,emp.FirstName,"
			+ "MIN(tr.aDateTime) AS FirstLogin,MAX(tr.aDateTime) AS LastLogin\n" + 
			"from Transactions as tr,EmployeeMaster as emp\n" + 
			"where tr.EmployeemasterID=emp.EmployeeMasterID  and \n" + 
			"convert(varchar,tr.aDateTime, 111) >= ";
	
	public final static String ABESENT_STATUS_QUERY1 =" and convert(varchar,tr.aDateTime, 111) <= ";
	public final static String ABESENT_STATUS_QUERY2 =" and  emp.EmployeeCode = ";
	public final static String ABESENT_STATUS_QUERY3 =" group by convert(varchar,tr.aDateTime, 111), emp.EmployeeCode, emp.FirstName";
	
	public final static String EMPLOYEE_EFFORTS_QUERY = "Select EmployeeCode, FirstName, SUM(MinutesInOffice)/60 TotalHoursInWeek ,"
			+ "SUM(MinutesInOffice)%60 TotalMinutesInWeek FROM(select emp.EmployeeCode,emp.FirstName, "
			+ "DATEDIFF(MINUTE, MIN(tr.aDateTime),MAX(tr.aDateTime)) MinutesInOffice "
	        +" from [Transactions] as tr,EmployeeMaster as emp "+
			" where tr.EmployeemasterID=emp.EmployeeMasterID and convert(varchar,tr.aDateTime, 111) >=";  
	public final static String EMPLOYEE_EFFORTS_QUERY1 = " and convert(varchar,tr.aDateTime, 111) <=";
	public final static String EMPLOYEE_EFFORTS_QUERY2 = " group by convert(varchar,tr.aDateTime, 111), emp.EmployeeCode, emp.FirstName)"
			+"A  Group by EmployeeCode, FirstName HAVING SUM(MinutesInOffice) >=3000";

	public final static String EMPLOYEE_YESTERDAY_LOGIN_DETAILS_QUERY = "SELECT emp.FirstName, emp.EmployeeCode, pd.TransactionDateTime, re.IOEntryStatus, re.ReaderName\n" +
			"FROM ProcessData AS pd , EmployeeMaster AS emp, Readers AS re \n" +
			"WHERE emp.EmployeeMasterID = pd.EmployeeID AND re.ReaderID = pd.ReaderID AND  emp.EmployeeCode = %s \n" +
			"AND pd.TransactionDateTime < CONVERT(date,GETDATE()) AND pd.TransactionDateTime >= CONVERT(date,GETDATE()-1) " +
			"ORDER BY pd.TransactionDateTime";
	public final static String EMPLOYEE_LOGIN_DETAILS_QUERY_OF_DATE = "SELECT emp.FirstName, emp.EmployeeCode, pd.TransactionDateTime, re.IOEntryStatus, re.ReaderName\n" +
			"FROM ProcessData AS pd , EmployeeMaster AS emp, Readers AS re \n" +
			"WHERE emp.EmployeeMasterID = pd.EmployeeID AND re.ReaderID = pd.ReaderID AND  emp.EmployeeCode = %s\n" +
			"AND pd.TransactionDateTime >= '%s' and pd.TransactionDateTime < DATEADD(day,+1,'%s') ORDER BY pd.TransactionDateTime";

	
	public final static String UNION=" Union ";   

	//public final static String PRESENT_QUERY =  "SELECT DISTINCT Emp.EmployeeCode, Emp.FirstName FROM Transactions AS Tr, EmployeeMaster AS Emp WHERE Tr.EmployeemasterID = Emp.EmployeeMasterID AND  CONVERT(VARCHAR,Tr.aDateTime, 111) = '<REPORTDATE>' AND Emp.EmployeeCode IN (<EMPIDS>)";
	//public final static String ABSENT_QUERY = "SELECT [EmployeeCode], [FirstName] FROM [EmployeeMaster] WHERE [EmployeeCode] IN(<ABSENTLIST>)";
	public final static String PRESENT_QUERY =  "SELECT DISTINCT Emp.EmployeeCode, Emp.FirstName, Emp.EmailId FROM Transactions AS Tr, EmployeeMaster AS Emp WHERE Tr.EmployeemasterID = Emp.EmployeeMasterID AND  CONVERT(VARCHAR,Tr.aDateTime, 111) = '<REPORTDATE>' AND Emp.EmployeeCode IN (<EMPIDS>)";
	public final static String ABSENT_QUERY = "SELECT [EmployeeCode], [FirstName],[EmailId] FROM [EmployeeMaster] WHERE [EmployeeCode] IN(<ABSENTLIST>)";


	public final static String PRESENT = "P";
	public final static String ABSENT = "A";
	
	public final static  String ALL = "All";
	public final static  String SHIFT = "Shift";
	
	public final static String LOGIN_REPORT_BY_TIME="select emp.EmployeeCode,emp.FirstName,MIN(tr.aDateTime) AS FirstLogin,MAX(tr.aDateTime) AS LastLogin \n" + 
			"  from [smartiSCC].[dbo].[Transactions] as tr,[smartiSCC].[dbo].[EmployeeMaster] as emp \n" + 
			"  where tr.EmployeemasterID=emp.EmployeeMasterID  and \n" + 
			"  tr.aDateTime >= ";   
	public final static String LOGIN_REPORT_BY_TIME2 =" and tr.aDateTime<= ";
	public final static String LOGIN_REPORT_BY_TIME3=" and  emp.EmployeeCode = ";
	public final static String LOGIN_REPORT_BY_TIME4=" group by  emp.EmployeeCode, emp.FirstName ";
	
	// Role Mapping Info
	public static final String ACCOUNT = "Delivery Manager";
	public static final String DOMAIN = "Delivery Lead";
	public static final String LEAD="Lead";
	public static final String IS_ACTIVE = "isActive";
	
	public final static  String BOTH="Both";
	public final static  String BENCH_ACCOUNT="Nisum India";
	public final static  String BENCH_PROJECT_ID="Nisum0000";
	public final static  String BENCH_BILLABILITY_STATUS="Non-Billable";
	public final static  String BILLABLE_TEXT = "Billable";
	
	public final static  int INT_ZERO = 0;
	public final static  int INT_TWO = 2;
	public final static  int INT_TWENTY = 20;
	
	public final static  String DM= "DM";
	public final static  String DL= "DL";
	public final static  String L= "L";

	public final static  String EMPTY_STRING = "";
	
	public final static  String MALE = "Male";	
	public final static  String FEMALE ="Female";
	public final static  String M ="M";
	public final static  String F="F";
	
	public final static  String YES = "Yes";
	public final static  String NO = "No";
	
	public final static  long EMPID_START = 16001;
	public final static  long EMPID_END = 99999;
	
	public final static  String MASTERDATA_FG = "FunctionalGrp";
	public final static String MASTERDATA_DESIGNATION = "designations";
	public final static String MASTERDATAD_EMLOYMENT_TYPE = "EmpType";
	public final static String MASTERDATA_ROLES = "roles";
	
	public final static String FULL_TIME ="Full Time";
	
	public final static String CAMA = ",";
	
	public final static String CREATE ="CREATE";
	public final static String UPDATE ="UPDATE";



	public static final String SHIFT1="Shift 1(9:00 AM - 6:00 PM)";
	public static final String SHIFT2="Shift-2(2:00 PM - 11:00 PM)";
	public static final String SHIFT3="Shift 3(10:00 PM - 6:00 AM)";
	public static final String SHIFT4="Shift 4(7:30 AM - 3:30 PM)";
	public static final String SHIFT5="Shift 5(11:30 AM - 7:30 PM)";

	public final static String PROJECT_START_DATE="ProjectStartDate";
	public final static String PROJECT_END_DATE="ProjectEndDate";
	public final static String BILLING_START_DATE="billingStartDate";
	public final static String BILLING_END_DATE="billingEndDate";

	public final static String TEXT_DESIGNATIONS="designations";
	public final static String TEXT_ENABLE="enable";
	public final static String TEXT_DISABLE="disable";
	public final static String EMPLOYEE_LOGIN_DETAILS_QUERY = "SELECT top 30 emp.FirstName, emp.EmployeeCode, pd.TransactionDateTime, re.IOEntryStatus, re.ReaderName\n" +
			"FROM ProcessData AS pd , EmployeeMaster AS emp, Readers AS re \n" +
			"WHERE emp.EmployeeMasterID = pd.EmployeeID AND re.ReaderID = pd.ReaderID AND  emp.EmployeeCode = %s \n" +
			//"AND pd.TransactionDateTime <= CONVERT(date,GETDATE()) AND pd.TransactionDateTime >= CONVERT(date,GETDATE()-1) " +
			"ORDER BY pd.TransactionDateTime desc";
	public final static String EMPLOYEE_LOGIN_DETAILS_QUERY_BY_DATES = "SELECT emp.FirstName, emp.EmployeeCode, pd.TransactionDateTime, re.IOEntryStatus, re.ReaderName\n" +
			"FROM ProcessData AS pd , EmployeeMaster AS emp, Readers AS re \n" +
			"WHERE emp.EmployeeMasterID = pd.EmployeeID AND re.ReaderID = pd.ReaderID AND  emp.EmployeeCode = %s \n" +
			"AND pd.TransactionDateTime BETWEEN CONVERT(datetime,'%2$s') AND CONVERT(datetime,'%3$s') " +
			"ORDER BY pd.TransactionDateTime";
	public final static String _OLD="_old";
}
