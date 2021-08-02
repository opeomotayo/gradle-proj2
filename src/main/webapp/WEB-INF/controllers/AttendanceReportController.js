myApp.controller("attendanceReportController", function($scope, $http, myFactory,exportUiGridService, $mdDialog, appConfig,$timeout) {
	$scope.records = [];
	$scope.empId = myFactory.getEmpId();
	$scope.empName = myFactory.getEmpName();
	$scope.empEmailId = myFactory.getEmpEmailId();
	$scope.shifts = myFactory.getShifts();
	if($scope.shifts.indexOf("All") == "-1"){
		$scope.shifts.unshift("All");
	}
	
	// Date picker related code
	var today = new Date();
	$scope.maxDate = today;
	$scope.reportDate = today;
	
	$scope.gridOptions = {
		paginationPageSizes : [ 10, 20, 30, 40, 50, 100],
		paginationPageSize : 10,
	    pageNumber: 1,
		pageSize:10,
		enableFiltering:true,
		columnDefs : [ 
				{field : 'employeeId',displayName: 'Employee Id', enableColumnMenu: false, enableSorting: true,enableFiltering: true},
				{field : 'employeeName',displayName: 'Employee Name', enableColumnMenu: false, enableSorting: false, enableFiltering: true},
				{field : 'present',displayName: 'Status', enableColumnMenu: false, enableSorting: false, enableFiltering: false}
			],
			enableGridMenu: true,
		    enableSelectAll: true,
		    exporterMenuExcel:false,
		    exporterMenuCsv:false,
		    exporterCsvFilename: 'AbsentDetails.csv',
		    exporterExcelFilename:'Attendance Report',
		    exporterPdfDefaultStyle: {fontSize: 9},
		    exporterPdfTableStyle: {margin: [30, 30, 30, 30]},
		    exporterPdfTableHeaderStyle: {fontSize: 10, bold: true, italics: true, color: 'red'},
		    exporterPdfHeader: { text: "Attendance Details", style: 'headerStyle' },
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
	
	$scope.getEmployeePresent = function(type){
		$scope.attendenceType="both";
		$mdDialog.hide();
		if(type == "onload"){
			showProgressDialog("Fetching data please wait...");
		}
		else if(type == "onclick" && $scope.reportDate < today){
			showProgressDialog("Fetching data please wait...");
		}else{
			
		}
		var reportDate = getFormattedDate($scope.reportDate);
		var shiftV = $scope.shiftValue;
		if(shiftV == undefined || shiftV==""){
			shiftV="All";
		}
		$scope.shiftValue=shiftV;
		$scope.selectedshiftValue=shiftV;
		$http({
	        method : "GET",
	        url : appConfig.appUri + "attendance/attendanciesReport/" + reportDate+"?shift="+shiftV
	    }).then(function mySuccess(response) {
	    	$mdDialog.hide();
	    	if(response.data.length == 0){
	    		$timeout(function(){showAlert('No data available');},600);
	    		$scope.refreshPage();	    		
	    		$scope.attendenceType="both";
	    			    
	    	}else{
	    		$scope.attendenceType="both";
	    		$scope.totalAttendanceArray=response.data;
	    		$scope.gridOptions.data = response.data;
	    		$scope.presentArray=[];
	    		$scope.absentArray=[];
	    		angular.forEach(response.data, function(attendance) {	    			
	    			if(attendance.present =='P'){
	    				$scope.presentArray.push(attendance);
	    			}else if(attendance.present =='A'){
	    				$scope.absentArray.push(attendance);
	    			}
	    			});	    		    		
		        $scope.totalPresent = $scope.presentArray.length;
		        $scope.totalAbsent = $scope.absentArray.length;
		        
	    	}
	    	if(response.data.length > 10){
	    		$scope.gridOptions.enablePaginationControls = true;
	    	}
	    	else{
	    		$scope.gridOptions.enablePaginationControls = false;
	    	}
	    }, function myError(response) {
	    	showAlert("Something went wrong while fetching data!!!");
	    	$scope.refreshPage();
	    });
	};
	
	$scope.setSearchDate = function(dateValue){
		$scope.reportDate = dateValue;
	};
	
	$scope.getSelectedShift = function(){
		if ($scope.shiftValue !== undefined) {
			return $scope.shiftValue;
		} else {
			return "Please select a shift";
		}
	};
	
	$scope.refreshPage = function(){
		$scope.gridOptions.data = [];
		$scope.reportDate = today;
		$scope.totalPresent = "";
        $scope.totalAbsent = "";
        $scope.totalEmployees = "";
        
		
	};
	
	function getFormattedDate(date){
		var day = date.getDate();
		var month = date.getMonth() + 1;
		var year = date.getFullYear();
		return year + '-' + (month < 10 ? "0" + month : month) + '-'
				+ (day < 10 ? "0" + day : day);
	}

	function showAlert(message) {
		$mdDialog.show($mdDialog.alert().parent(
				angular.element(document.querySelector('#popupContainer')))
				.clickOutsideToClose(true).textContent(message).ariaLabel(
						'Alert Dialog').ok('Ok'));
	}
	
	function showProgressDialog(msg){
		$('#home').addClass('md-scroll-mask');
		$mdDialog.show({
	      templateUrl: 'templates/progressDialog.html',
	      controller: ProgressController,
	      parent: angular.element(document.body),
	      clickOutsideToClose:false,
	      locals: {dataToPass:msg}
	    });
	}
	
	function ProgressController($scope, dataToPass) {
		$scope.progressText = dataToPass;
	}
	
	$scope.getAttendanceReport = function(value){
		if(value == 'both'){
			$scope.attendenceType=value;
			$scope.gridOptions.data=$scope.totalAttendanceArray;
		}else if (value == 'present'){
			 $scope.present=value;
			$scope.gridOptions.data=$scope.presentArray;
		}else if (value == 'absent'){
			 $scope.absent=value;
			$scope.gridOptions.data=$scope.absentArray;
		}
	
		
	}
	
});
