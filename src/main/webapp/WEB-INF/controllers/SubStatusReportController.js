myApp.controller("subStatusController", function($scope, $http, myFactory, $mdDialog, appConfig, exportUiGridService) {
	$scope.records = [];
	$scope.empSubStatuses = myFactory.getEmployeeSubStatus();
	if($scope.empSubStatuses.indexOf("All") == "-1"){
		$scope.empSubStatuses.unshift("All");
	}

	// Date picker related code
	var today = new Date();
	var priorDt = new Date(today.getTime() - ( 30 * 24 * 60 * 60 * 1000));
	$scope.maxDate = today;
	$scope.fromDate = priorDt;
	$scope.toDate = today;

	$scope.gridOptions = {
			paginationPageSizes : [ 10, 20, 30, 40, 50, 100],
			paginationPageSize : 10,
		    pageNumber: 1,
			pageSize:10,
			enableFiltering:true,
			columnDefs : [ 
				{field : 'empId',displayName: 'Employee ID', enableColumnMenu: true, enableSorting: true, enableFiltering: true,width:'*'},
				{field : 'empName',displayName: 'Employee Name', enableColumnMenu: false, enableSorting: false, enableFiltering: true,width:'*'},
				{field : 'empStatus',displayName: 'Sub Status', enableColumnMenu: false, enableSorting: false,enableFiltering: true,width:'*'}, 
				{field : 'fromDate',displayName: 'From Date', enableColumnMenu: false,enableFiltering: false, cellFilter: 'date:"dd-MMM-yyyy"',width:'*'},
				{field : 'toDate',displayName: 'To Date', enableColumnMenu: false,enableSorting: false,enableFiltering: false , cellFilter: 'date:"dd-MMM-yyyy"',width:'*'}, 
				{field : 'functionalGroup',displayName: 'Functional Group', enableColumnMenu: false, enableSorting: false, enableFiltering: true,width:'*'} 
			],
		enableGridMenu: true,
	    enableSelectAll: true,
	    exporterMenuExcel:false,
	    exporterMenuCsv:false,
	    exporterCsvFilename: 'SubStatusReport.csv',
	    exporterExcelFilename:'SubStatusReport',
	    exporterPdfDefaultStyle: {fontSize: 9},
	    exporterPdfTableStyle: {margin: [15, 15, 15, 15]},
	    exporterPdfTableHeaderStyle: {fontSize: 10, bold: true, italics: true, color: 'red'},
	    exporterPdfHeader: { text: "Employee SubStatus Report", style: 'headerStyle' },
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
	$scope.gridOptions.data = [];

	$scope.getSubStatusDetails = function(){
		var fromDate = getFormattedDate($scope.fromDate);
		var toDate = getFormattedDate($scope.toDate);
		var empSubStatusV = $scope.empSubStatusValue;
		if(empSubStatusV == undefined || empSubStatusV==""){
			empSubStatusV="All";
		}
		$scope.empSubStatusValue=empSubStatusV;
		$http({
	        method : "GET",
	        url : appConfig.appUri + 'employeesBasedOnSubStatusForGivenDates?fromDate='+fromDate+'&toDate='+toDate+'&subStatus='+$scope.empSubStatusValue
	    }).then(function mySuccess(response) {
	        $scope.gridOptions.data = response.data.records;
	        if(response.data.records.length > 10){
	    		$scope.gridOptions.enablePaginationControls = true;
	    	}
	    	else{
	    		$scope.gridOptions.enablePaginationControls = false;
	    	}
	    }, function myError(response) {
	    	showAlert("Something went wrong while fetching data!!!");
	    	$scope.gridOptions.data = [];
	    });
	};
	$scope.getSelectedSubStatus = function(){
		if ($scope.empSubStatusValue !== undefined) {
			return $scope.empSubStatusValue;
		} else {
			return "Please select a SubStatus";
		}
	};
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
				$scope.toDate = toDat;
			}
		}else if(from == "ToDate"){
			var fromDat = $scope.fromDate;
			var differene = daysBetween(fromDat, dateValue);
			if(differene < 0 ){
				showAlert('To Date should not be less than From Date');
				$scope.fromDate = priorDt;
				$scope.toDate = today;
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
