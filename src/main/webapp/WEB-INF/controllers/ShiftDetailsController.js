myApp.controller("shiftDetailsController",function($scope, myFactory,exportUiGridService, $mdDialog, $http, appConfig, $timeout){
	$scope.records = [];
	
	$scope.gridOptions = {
		paginationPageSizes : [ 10, 20, 30, 40, 50, 100],
		paginationPageSize : 10,
	    pageNumber: 1,
		pageSize:10,
		enableFiltering:true,
		columnDefs : [ 
			{field : 'employeeId',displayName: 'Employee ID', enableColumnMenu: true, enableSorting: true,enableFiltering:true, width:120},
			{field : 'employeeName',displayName: 'Employee Name', enableColumnMenu: false, enableSorting: false,enableFiltering:true},
			{field : 'emailId',displayName: 'Email', enableColumnMenu: false, enableSorting: false,enableFiltering:true},
			{field : 'projectName',displayName: 'Project', enableColumnMenu: false, enableSorting: false,enableFiltering:true},
			{field : 'mobileNumber',displayName: 'Mobile No', enableColumnMenu: false, enableSorting: false,enableFiltering:false}
		],
	enableGridMenu: true,
    enableSelectAll: true,
    exporterMenuExcel:false,
    exporterMenuCsv:false,
    exporterCsvFilename: 'ShiftDetails.csv',
    exporterExcelFilename:'ShiftDetails',
    exporterPdfDefaultStyle: {fontSize: 9},
    exporterPdfTableStyle: {margin: [30, 30, 30, 30]},
    exporterPdfTableHeaderStyle: {fontSize: 10, bold: true, italics: true, color: 'red'},
    exporterPdfHeader: { text: "Shift Details", style: 'headerStyle' },
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
	
	$scope.refreshPage = function(){
		$scope.shiftValue = undefined;
		$scope.getShiftDetails();
	}
	$scope.getSelectedShift = function(){
		if ($scope.shiftValue !== undefined) {
			return $scope.shiftValue;
		} else {
			return "Please select a shift";
		}
	};
	$scope.getShiftDetails = function(){
		
		$scope.shifts = myFactory.getShifts(); //["Shift 1(09:00 AM - 06:00 PM)","Shift 2(03:30 PM - 12:30 PM)", "Shift 3(09:00 PM - 06:00 AM)"];
		if($scope.shifts.indexOf("All") != "-1"){
			$scope.shifts.shift();
		}
		var shiftV = $scope.shiftValue;
		if(shiftV == undefined || shiftV==""){
			shiftV="Shift 1(9:00 AM - 6:00 PM)";
		}
		$scope.shiftValue=shiftV;
		$scope.selectedshiftValue=shiftV;
		$http({
	        method : "GET",
	        url : appConfig.appUri + "resources/shifts/"+shiftV
	    }).then(function mySuccess(response) {
	        $scope.gridOptions.data = response.data.records;
	        $scope.shiftCount=response.data.records.length;
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
	
	
	function showAlert(message) {
		$mdDialog.show($mdDialog.alert().parent(
				angular.element(document.querySelector('#popupContainer')))
				.clickOutsideToClose(true).textContent(message).ariaLabel(
						'Alert Dialog').ok('Ok'));
	}
	
});