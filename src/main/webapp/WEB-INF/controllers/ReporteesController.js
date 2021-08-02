myApp.controller("employeesController", function($scope, $http, myFactory, $mdDialog, appConfig, exportUiGridService) {
	$scope.records = [];
	$scope.empId = myFactory.getEmpId();
	$scope.empName = myFactory.getEmpName();
	$scope.empEmailId = myFactory.getEmpEmailId();
	$scope.role = myFactory.getEmpRole();
	$scope.avgLoginHrs = "";
	$scope.isVisible = false;
	$scope.searchId="";
	$scope.hidethis = false;

	// Date picker related code
	var today = new Date();
	var priorDt = new Date(today.getTime() - (1 * 24 * 60 * 60 * 1000));;
	
	$scope.maxDate = priorDt;
	$scope.fromDate = priorDt;
	$scope.toDate = priorDt;

	$scope.gridOptions = {
		paginationPageSizes : [ 10, 20, 30, 40, 50, 100],
		paginationPageSize : 10,
	    pageNumber: 1,
		pageSize:10,
		enableFiltering:true,
		columnDefs : [ 
				{field : 'employeeId',displayName: 'Employee ID', enableColumnMenu: true, enableSorting: true, enableFiltering: true, width:130},
				{field : 'name',displayName: 'Employee Name', enableColumnMenu: false, enableSorting: false, enableFiltering: true,},
				{field : 'date',displayName: 'Date', enableColumnMenu: true, enableSorting: true,enableFiltering: false, cellFilter:'date:"dd-MMM-yyyy"', width: 130},
				{field : 'loginTime',displayName: 'Login Time', enableColumnMenu: false,enableSorting: false,enableFiltering: false, width: 140}, 
				{field : 'logoutTime',displayName: 'Logout Time', enableColumnMenu: false, enableSorting: false,enableFiltering: false, width:140}, 
				{field : 'orphanLogin.join(" , ")',displayName: 'Tail Gaited At' ,enableColumnMenu: false, enableSorting: true,enableFiltering: true, cellClass: 'grid-align'}, 
				//{field : 'orphanLogin',displayName: 'Tail Gaited At',cellTemplate: '<div class="ui-grid-cell-contents" ng-repeat= "item in row.entity[col.field]">{{item}}</div>' ,enableColumnMenu: false, enableSorting: true,enableFiltering: true, cellClass: 'grid-align'}, 
				{field : 'durationAtWorkPlace',displayName: 'Duration at Work Place', enableColumnMenu: false, enableSorting: true,enableFiltering: false}
			],
			enableGridMenu: true,
		    enableSelectAll: true,
		    exporterMenuExcel:false,
		    exporterMenuCsv:false,
		    exporterCsvFilename: 'EmployeeLoginDetails.csv',
		    exporterExcelFilename:'Employee Login  Details',
		    exporterPdfDefaultStyle: {fontSize: 9},
		    exporterPdfTableStyle: {margin: [15, 15, 15, 15]},
		    exporterPdfTableHeaderStyle: {fontSize: 10, bold: true, italics: true, color: 'red'},
		    exporterPdfHeader: { text: "Employee Login Details", style: 'headerStyle' },
		    exporterPdfFooter: function ( currentPage, pageCount ) {
		      return { text: currentPage.toString() + ' of ' + pageCount.toString(), style: 'footerStyle' };
		    },
		    exporterPdfCustomFormatter: function ( docDefinition ) {
		      docDefinition.styles.headerStyle = { fontSize: 22, bold: true };
		      docDefinition.styles.footerStyle = { fontSize: 10, bold: true };
		      return docDefinition;
		    },
		    exporterPdfOrientation: 'portrait',
		    exporterPdfPageSize: 'LETTER',
		    exporterPdfMaxGridWidth: 400,
		    exporterCsvLinkElement: angular.element(document.querySelectorAll(".custom-csv-link-location")),
		    onRegisterApi: function(gridApi){
		      $scope.gridApi = gridApi;
		    },
		            gridMenuCustomItems: [{
		                    title: 'Export all data as EXCEL',
		                    action: function ($event) {
		                        exportUiGridService.exportToExcel('sheet 1', $scope.gridApi, 'all', 'all');
		                    },
		                    order: 110
		                },
		                {
		                    title: 'Export visible data as EXCEL',
		                    action: function ($event) {
		                        exportUiGridService.exportToExcel('sheet 1', $scope.gridApi, 'visible', 'visible');
		                    },
		                    order: 111
		                }
		            ]
	};
	$scope.gridOptions.data = [];
	
	$scope.setPageDefaults = function(){
		getData(0, getFormattedDate($scope.fromDate), getFormattedDate($scope.toDate), 'onload');
	}
	
	$scope.refreshPage = function(){
		$scope.searchId="";
		$scope.fromDate = priorDt;
		$scope.toDate = priorDt;
		$scope.gridOptions.data = [];
		$scope.isVisible = false;
		$scope.setPageDefaults();
		$scope.hidethis = true;
	};
	
	$scope.employeeDetails = [];
	
	$scope.getEmployeeDetailsForAutocomplete = function(){
		$http({
	        method : "GET",
	        url : appConfig.appUri + "employee/autocomplete"
	    }).then(function mySuccess(response) {
	    	if(response.data != "" && response.data.length !=0){
	    		$scope.employeeDetails = response.data;
	    	}else{
	    		$scope.employeeDetails = [];
	    	}
	    }, function myError(response) {
	    	$scope.employeeDetails = [];
	    });
	};
	
	$scope.autoComplete = function(searchId){
		var output = [];
		$scope.filterEmployees = [];
		if(searchId.length > 0){
			$scope.hidethis = false;
		}else{
			$scope.hidethis = true;
		}
		angular.forEach($scope.employeeDetails, function(searchString){
			if(searchString.toLowerCase().indexOf(searchId.toLowerCase()) >=0){
				output.push(searchString);
			}
		});
		$scope.filterEmployees = output;
	}
	
	$scope.fillTextbox = function(searchString){
		$scope.searchId = searchString;
		$scope.hidethis = true;
	}
	
	var validation = {
	    isEmailAddress:function(str) {
	        var pattern =/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/;
	        return pattern.test(str); // returns a boolean
	    },
	    isNotEmpty:function (str) {
	        var pattern =/\S+/;
	        return pattern.test(str);  // returns a boolean
	    },
	    isNumber:function(str) {
	        var pattern = /^\d+$/;
	        return pattern.test(str);  // returns a boolean
	    }
	};
		
	$scope.getEmployeeData = function(type){
		$scope.hidethis = true;
		var searchId = $scope.searchId;
		var fromDate = getFormattedDate($scope.fromDate);
		var toDate = getFormattedDate($scope.toDate);   
		if (type == "click") {
			if (!validation.isNotEmpty(searchId)) {
				getData(0, fromDate, toDate, 'onclick');
			} else if (validation.isNotEmpty(searchId)
					&& validation.isNumber(searchId)
					&& checkEmpIdRange(searchId)) {
				getData(searchId, fromDate, toDate, 'onclick');
			} else if (validation.isNotEmpty(searchId)
					&& validation.isNumber(searchId)
					&& !checkEmpIdRange(searchId)) {
				showAlert('Please enter a valid Employee ID');
				setFieldsEmpty();
			} else if (validation.isNotEmpty(searchId)
					&& !validation.isEmailAddress(searchId)
					&& (searchId.indexOf('@') > -1 || searchId
							.indexOf('.') > -1)) {
				showAlert('Please enter a valid Email ID');
				setFieldsEmpty();
			}else if (validation.isNotEmpty(searchId)
					&& validation.isEmailAddress(searchId)
					&& searchId.indexOf("@nisum.com", searchId.length - "@nisum.com".length) == -1) {
				showAlert('Please enter a valid Nisum Email-Id');
				setFieldsEmpty();
			} else if (validation.isNotEmpty(searchId)
					&& validation.isEmailAddress(searchId)
					&& !validation.isNumber(searchId)) {
				getEmployeeIdFromRoles(searchId, 'emailId', fromDate, toDate, 'onclick');
			} else if (validation.isNotEmpty(searchId)
					&& !validation.isEmailAddress(searchId)
					&& !validation.isNumber(searchId)) {
				getEmployeeIdFromRoles(searchId, 'employeeName', fromDate, toDate, 'onclick');
			}
		}
	}
	
	function checkEmpIdRange(searchId){
		if(searchId.length < 5 || searchId.length > 5)
			return false;
		else
			return parseInt(searchId) >= appConfig.empStartId && parseInt(searchId) <= appConfig.empEndId;
	}
	
	function getEmployeeIdFromRoles(searchId, searchAttribute, fromDate, toDate, type){
		$http({
	        method : "GET",
	        url : appConfig.appUri + "employee/searchCriteria?searchId=" + searchId + "&searchAttribute=" + searchAttribute
	    }).then(function mySuccess(response) {
	    	if(response.data != "" && response.data.length !=0){
	    		getData(response.data.employeeId, fromDate, toDate, type);
	    	}else{
	    		showAlert('Please enter a valid Employee ID/Name/Email ID');
				setFieldsEmpty();
	    	}
	    }, function myError(response) {
	    	showAlert("Something went wrong while fetching data!!!");
	    	$scope.gridOptions.data = [];
	    });
	}
	
	function getData(empId, fromDate, toDate, type){
		$http({
	        method : "GET",
	        url : appConfig.appUri + "effectiveLogin?employeeId=" + empId + "&fromDate="+ fromDate + "&toDate=" + toDate
	    }).then(function mySuccess(response) {
	    	var recs = response.data.data;
	    	if(recs.length == 0 && type == "onclick"){
	    		showAlert('No data available');
	    		setFieldsEmpty();
	    	}else{
	    		if(empId == 0){
	    			$scope.isVisible = false;
	    			$scope.gridOptions.data = recs;
	    		}else{
	    			if(recs.length >0 ){
	    				$scope.isVisible = true;
		    			$scope.avgLoginHrs = response.data.averageTime +" Hrs";
	    			}
	    			$scope.gridOptions.data = recs;
	    		}
	    		
	    	}
	    	if(recs.length > 10){
	    		$scope.gridOptions.enablePaginationControls = true;
	    	}
	    	else{
	    		$scope.gridOptions.enablePaginationControls = false;
	    	}
	    }, function myError(response) {
	    	showAlert("Something went wrong while fetching data!!!");
	    	$scope.gridOptions.data = [];
	    });
	}

	$scope.validateDates = function(dateValue, from) {
		if(from == "FromDate"){
			var toDat = $scope.toDate;
			var difference = daysBetween(dateValue, toDat);
			if(difference < 0 ){
				showAlert('From Date should not be greater than To Date');
				$scope.fromDate = priorDt;
				$scope.toDate = priorDt;
			}else{
				$scope.fromDate = dateValue;
				$scope.toDate = toDat;
			}
		}else if(from == "ToDate"){
			var fromDat = $scope.fromDate;
			var differene = daysBetween(fromDat, dateValue);
			if(differene < 0 ){
				showAlert('To Date should not be less than From Date');
				$scope.fromDate = priorDt;
				$scope.toDate = priorDt;
			}else{
				$scope.fromDate = fromDat;
				$scope.toDate = dateValue;
			}
		}
	};
	
	function showAlert(message) {
		$mdDialog.show($mdDialog.alert().parent(
				angular.element(document.querySelector('#popupContainer')))
				.clickOutsideToClose(true).textContent(message).ariaLabel(
						'Alert Dialog').ok('Ok'));
	}
	
	function getFormattedDate(date){
		var day = date.getDate();
		var month = date.getMonth() + 1;
		var year = date.getFullYear();
		return year + '-' + (month < 10 ? "0" + month : month) + '-'
				+ (day < 10 ? "0" + day : day);
	}
	
	function setFieldsEmpty(){
		$scope.searchId="";
		$scope.fromDate = priorDt;
		$scope.toDate = priorDt;
		$scope.gridOptions.data = [];
		$scope.isVisible = false;
		$scope.avgLoginHrs = "";
	}
	
	function treatAsUTC(date) {
	    var result = new Date(date);
	    result.setMinutes(result.getMinutes() - result.getTimezoneOffset());
	    return result;
	}

	function daysBetween(fromDate, toDate) {
	    var millisecondsPerDay = 24 * 60 * 60 * 1000;
	    return Math.round((treatAsUTC(toDate) - treatAsUTC(fromDate)) / millisecondsPerDay);
	}
	
});
