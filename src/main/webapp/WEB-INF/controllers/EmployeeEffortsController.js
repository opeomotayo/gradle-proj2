myApp.controller("assignEmployeeEffortrsController",function($scope,exportUiGridService, myFactory, $mdDialog, $http, appConfig, $timeout, $element, $window){
	$scope.records = [];
	$scope.parentData = {
			"employeeId": "",
			"employeeName": "",
			"totalHoursSpent":"",
			"projectName":"",
			"account":"",
			"functionalOrg":""
	};
	var today = new Date();
	var priorDt = new Date(today.getTime() - (6 * 24 * 60 * 60 * 1000));
	$scope.maxDate = today;
	$scope.maxFromDate = priorDt;
	$scope.toDate = today;
	$scope.fromDate = priorDt;
	
	
		
//	+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i ng-show="row.entity.status == \'InActive\'">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</i><i class="fa fa-minus-circle fa-2x" aria-hidden="true" style="font-size:1.5em;margin-top:3px;cursor:pointer;" data-placement="left" title="Delete" onmouseenter="$(this).tooltip(\'show\')" ng-click="grid.appScope.getRowData(row,\'Delete\')"></i></p>';
	
	$scope.gridOptions = {
			paginationPageSizes : [ 10, 20, 30, 40, 50, 100],
			paginationPageSize : 10,
		    pageNumber: 1,
			pageSize:10,
			enableFiltering:true,
			columnDefs : [
				{field : 'employeeId',displayName: 'Employee Id', enableColumnMenu: false, enableSorting: false,enableFiltering: true},
				{field : 'employeeName',displayName: 'Employee Name', enableColumnMenu: false, enableSorting: true,enableFiltering: true}, 
				{name : 'totalHoursSpentInWeek', displayName: 'Total Hours(HH:MM)', enableColumnMenu: false, enableSorting: false, enableFiltering:false,width :230},
				{field : 'projectName',displayName: 'Project Name', enableColumnMenu: false, enableSorting: true,enableFiltering: true}, 
				{name : 'accountName', displayName: 'Account', enableColumnMenu: false, enableSorting: false, enableFiltering:false},
				{name : 'functionalOrg', displayName: 'Functional Org', enableColumnMenu: false, enableSorting: false, enableFiltering:true} 
			],
		enableGridMenu: true,
	    enableSelectAll: true,
	    exporterMenuExcel:false,
	    exporterMenuCsv:false,
	    exporterCsvFilename: 'EmployeeEfforts.csv',
	    exporterExcelFilename:'EmployeeEfforts',
	    exporterPdfDefaultStyle: {fontSize: 9},
	    exporterPdfTableStyle: {margin: [15, 15, 15, 15]},
	    exporterPdfTableHeaderStyle: {fontSize: 10, bold: true, italics: true, color: 'red'},
	    exporterPdfHeader: { text: "Employee Efforts", style: 'headerStyle' },
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
	    exporterPdfMaxGridWidth: 500,
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
	$scope.gridOptions.data = $scope.records;
	
	$scope.getEmployeeEffortsData = function(){
		var fromDate = getFormattedDate($scope.fromDate);
		var toDate = getFormattedDate($scope.toDate);
		$http({
	        method : "GET",
	        url : appConfig.appUri + "employeeEfforts/getWeeklyReport?fromDate=" + fromDate + "&toDate=" +toDate
	    }).then(function mySuccess(response) {
	        $scope.gridOptions.data = response.data;
	        if(response.data.length > 10){
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
				$scope.toDate = today;
			}else{
				$scope.fromDate = dateValue;
				$scope.toDate = new Date($scope.fromDate.getTime() + (6 * 24 * 60 * 60 * 1000));
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

