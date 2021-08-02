myApp.controller("reservedReportsController",function($scope,exportUiGridService, myFactory, $mdDialog, $http, appConfig, $timeout, $element, $window){
	$scope.records = [];
	var today = new Date();
	$scope.gridOptions = {
			paginationPageSizes : [ 10, 20, 30, 40, 50, 100],
			paginationPageSize : 10,
		    pageNumber: 1,
			pageSize:10,
			enableFiltering:true,
			columnDefs : [
				{field : 'employeeId',displayName: 'Employee Id', enableColumnMenu: false, enableSorting: false,enableFiltering: true},
				{field : 'employeeName',displayName: 'Employee Name', enableColumnMenu: false, enableSorting: true,enableFiltering: true,cellClass:function(grid,row,col){
					if(daysBetween(row.entity.billingStartDate,today) > '14') {
						return 'red';
					 }
				}}, 
				{name : 'accountName', displayName: 'Client', enableColumnMenu: false, enableSorting: false, enableFiltering:true},
				{field : 'projectName',displayName: 'Project Name', enableColumnMenu: false, enableSorting: true,enableFiltering: true}, 
				{name : 'billingStartDate', displayName: 'Reserved Start Date', enableColumnMenu: false,cellFilter: 'date:"dd-MMM-yyyy"', enableSorting: false, enableFiltering:false},
				{name : 'billingEndDate', displayName: 'Reserved End Date', enableColumnMenu: false,cellFilter: 'date:"dd-MMM-yyyy"', enableSorting: false, enableFiltering:false} 
			],
		enableGridMenu: true,
	    enableSelectAll: true,
	    exporterMenuExcel:false,
	    exporterMenuCsv:false,
	    exporterCsvFilename: 'ReservedReport.csv',
	    exporterExcelFilename:'ReservedReport',
	    exporterPdfDefaultStyle: {fontSize: 9},
	    exporterPdfTableStyle: {margin: [15, 15, 15, 15]},
	    exporterPdfTableHeaderStyle: {fontSize: 10, bold: true, italics: true, color: 'red'},
	    exporterPdfHeader: { text: "Reserved Report", style: 'headerStyle' },
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
	$scope.getReservedEnployeesData = function(){
		$http({
	        method : "GET",
	        url : appConfig.appUri + 'resources/reports?resourceStatus=Reserved'
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
	}
	function showAlert(message) {
		$mdDialog.show($mdDialog.alert().parent(
				angular.element(document.querySelector('#popupContainer')))
				.clickOutsideToClose(true).textContent(message).ariaLabel(
						'Alert Dialog').ok('Ok'));
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