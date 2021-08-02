
myApp.controller("dashboardController", function($scope, $http, myFactory,exportUiGridService, $mdDialog, appConfig) {
	$scope.records = [];
	$scope.parentData = {
			"projectId": "",
			"projectName": "",
			"account": "",
			"managerId":"",
			"managerName": "",
			"status": "",
			"action":""
	};
	
	
	
	$scope.managers = [];
	var getCellActiveTemplate='<div ng-show="COL_FIELD==true"><p class="col-lg-12">Y</P></div><div ng-show="COL_FIELD==false"><p class="col-lg-12">N</p></div>';
	
	var getCellTemplate = '<p class="col-lg-12"><i class="fa fa-book fa-2x" aria-hidden="true" style="font-size:1.5em;margin-top:3px;cursor:pointer;" ng-click="grid.appScope.getRowData(row,\'View\')"></i>'+
	'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i class="fa fa-pencil-square-o fa-2x" aria-hidden="true" style="font-size:1.5em;margin-top:3px;cursor:pointer;" ng-click="grid.appScope.getRowData(row,\'Update\')"></i>'+
	'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i class="fa fa-minus-circle fa-2x" aria-hidden="true" style="font-size:1.5em;margin-top:3px;cursor:pointer;" ng-click="grid.appScope.getRowData(row,\'Delete\')"></i></p>';
	var getEmpDetTemplate = '<p><i  style="margin-top:3px;color:blue;cursor:pointer" ng-click="grid.appScope.getRowData(row,\'View\')">{{COL_FIELD}}</i></p>';
	
	$scope.gridOptions = {
		paginationPageSizes : [ 10, 20, 30, 50, 100 ,150, 200 ,400],
		paginationPageSize : 10,
	    pageNumber: 1,
		pageSize:10,
		enableFiltering: true,
		enableHorizontalScrollbar:1,
		columnDefs : [ 
			{field : 'employeeId',displayName: 'Emp ID', enableColumnMenu: false, enableSorting: false,enableFiltering:true, width:100,cellTemplate: getEmpDetTemplate},
			{field : 'employeeName',displayName: 'Employee Name ', enableColumnMenu: false, enableSorting: true,enableFiltering:true,width:200},
			{field : 'accountName',displayName: 'Client', enableColumnMenu: false, enableSorting: true,enableFiltering:true,width:100},
			{field : 'domain',displayName: 'Domain', enableColumnMenu: false, enableSorting: true,enableFiltering:true,width:100},
			{field : 'projectName',displayName: 'Project', enableColumnMenu: false, enableSorting: false,enableFiltering:true,width:150},
			{field : 'onBehalfOf',displayName: 'On Behalf Of', enableColumnMenu: false, enableSorting: true,enableFiltering: true,width:180},
			{field : 'billableStatus',displayName: 'Billable Status', enableColumnMenu: false, enableSorting: true,enableFiltering: true,width:120},
			{field : 'billingStartDate',displayName: 'Billing Start Date', enableColumnMenu: false, enableSorting: true,cellFilter: 'date:"dd-MMM-yyyy"',enableFiltering:false,width:150},
			{field : 'billingEndDate',displayName: 'Billing End Date', enableColumnMenu: false, enableSorting: true,cellFilter: 'date:"dd-MMM-yyyy"',enableFiltering:false,width:150},
			{field : 'empSubStatus',displayName: 'Sub Status', enableColumnMenu: false, enableSorting: true,enableFiltering:true,width:140,cellClass:function(grid,row,col){

				if(grid.getCellValue(row,col)==='Maternity Leave') {
					 return 'blue';
				 }
				if(grid.getCellValue(row,col)==='Onsite Travel') {
			        return 'green';
		         }
				 else if(grid.getCellValue(row,col)==='Long Leave') {
					 return 'orange';
				 }
				 else if(grid.getCellValue(row,col)==='Resigned') {
					return 'red';
				 }
	             else if(grid.getCellValue(row,col)==='At Client Location') {
			         return 'violet';
			     }
               }},
            {field : 'designation',displayName: 'Designation', enableColumnMenu: false, enableSorting: true,enableFiltering:true,width:140},
			{field : 'functionalGroup',displayName: 'Functional Org', enableColumnMenu: false, enableSorting: true,enableFiltering:true,width:100},
			{field : 'emailId',displayName: 'Email Id', enableColumnMenu: false, enableSorting: true,enableFiltering:false,width:100},
			
			
			
			
			
			/*{field : 'projectAssigned',displayName: 'Allocated ', enableColumnMenu: false, enableSorting: true,enableFiltering:true,cellTemplate: getCellActiveTemplate,filterHeaderTemplate: '<div class="ui-grid-filter-container" ng-repeat="colFilter in col.filters"><div my-custom-dropdown></div></div>', 
		        filter: { 
		            term: 1,
		            options: [ {id: 1, value: 'male'}, {id: 2, value: 'female'}]     // custom attribute that goes with custom directive above 
		          }, "Billable","Shadow","Bench","NA"
		          cellFilter: 'mapGender'},*/
		          // { field: 'billableStatus',displayName: 'Allocation Status',
		          //     filterHeaderTemplate: '<div class="ui-grid-filter-container" ng-repeat="colFilter in col.filters"><div my-custom-dropdown></div></div>', 
		          //     filter: { 
		          //       term: '',
		          //       options: [ {id: 'Billable', value: 'Billable'}, {id: 'Shadow', value: 'Shadow'},{id: 'NA', value: 'NA'},{id: 'Bench', value: 'Bench'},{id: 'Reserved', value: 'Reserved'},{id: 'UA', value: 'Unassigned'},{id: '', value: 'All'}]     // custom attribute that goes with custom directive above 
		          //     }, 
		          //     cellFilter: 'mapGender' ,width:150}
		   // {name : 'Actions', displayName: 'Actions',cellTemplate: getCellTemplate, enableColumnMenu: false, enableSorting: false, enableFiltering:false,width:130} 
		],
        enableGridMenu: true,
	    enableSelectAll: true,
	    exporterMenuExcel:false,
	    exporterMenuCsv:false,
	    exporterCsvFilename: 'EmployeeDetails.csv',
	    exporterExcelFilename:'Employee Details',
	    exporterPdfDefaultStyle: {fontSize: 9},
	    exporterPdfTableStyle: {margin: [15, 15, 15, 15]},
	    exporterPdfTableHeaderStyle: {fontSize: 10, bold: true, italics: true, color: 'red'},
	    exporterPdfHeader: { text: "Employee Details", style: 'headerStyle' },
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
	$scope.gridOptions.data = $scope.records;
	
	$scope.getRowData = function(row, action){
		 if(action == "View")
			$scope.viewEmpDetails(action, row.entity);
	}
	$scope.viewEmpDetails = function(action, userData){
		$('#home').addClass('md-scroll-mask');
		userData.action = action;
		$mdDialog.show({
		      controller: ViewEmpController,
		      templateUrl: 'templates/employeeDetails.html',
		      parent: angular.element(document.body),
		      clickOutsideToClose:false,
		      locals:{dataToPass: userData},
		    })
		    .then(function(result) {/*
		    	if(result == "Assign") showAlert('Manager assigned successfully');
		    	else if(result == "Update") showAlert('Manager updated successfully');
		    	else if(result == "Cancelled") {}
		    	else showAlert('Manager assigning/updation failed!!!');
		    */});
	};
	$scope.getEmployeesDashBoardData = function(type){
		
		if(type == "onload"){
			showProgressDialog("Fetching data please wait...");
		} 
		$http({
	        method : "GET",
	        url : appConfig.appUri + "resources/getEmployeesDashBoard"
	    }).then(function mySuccess(response) {
	    	//alert("response"+response);
	     //	alert("response"+response.data);
	    	$mdDialog.hide();  	
	    	if(response.data.records.length > 10){
	    		$scope.gridOptions.enablePaginationControls = true;
	    	}
	    	else{
	    		$scope.gridOptions.enablePaginationControls = false;
	    	}

	     	$scope.gridOptions.data = response.data.records;
	      }, function myError(response) {
	    	showAlert("Something went wrong while fetching data!!!");
	    });
	}
	
	$scope.refreshPage = function(){
			$scope.getEmployeesDashBoardData();
	};
	
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
	
	function ViewEmpController($scope, $mdDialog,dataToPass) {
		  $scope.profile = dataToPass;
		
		  $scope.showOrHidePA="Show";
		  $scope.showOrHidePV="Show";
		  $scope.showOrHideBD="Show";
		    $scope.toggleProjectAllocations = function() {
		        $scope.showProjectAllocations = !$scope.showProjectAllocations;
		        if($scope.showOrHidePA=="Show"){
		        $scope.showOrHidePA="Hide";
		        }else {
		        	 $scope.showOrHidePA="Show";
		        }
		    };
		    $scope.toggleEmpLocationDetails = function() {
		        $scope.showEmplocations = !$scope.showEmplocations;
		        if($scope.showOrHidePA=="Show"){
		        $scope.showOrHidePA="Hide";
		        }else {
		        	 $scope.showOrHidePA="Show";
		        }
		    };
		    
		    $scope.toggleVisaDisplay = function() {
		        $scope.showVisaDisplay= !$scope.showVisaDisplay;
		        if($scope.showOrHidePV=="Show"){
			        $scope.showOrHidePV="Hide";
			        }else {
			        	 $scope.showOrHidePV="Show";
			        }
		    };
		//var getCellActiveTemplate='<div ng-show="COL_FIELD==true"><p class="col-lg-12">Y</P></div><div ng-show="COL_FIELD==false"><p class="col-lg-12">N</p></div>';
		
		$scope.gridOptionsProjectAllocatons = {
			paginationPageSizes : [ 10, 20, 30, 40, 50, 100],
			paginationPageSize : 10,
		    pageNumber: 1,
			pageSize:10,
			columnDefs : [ 
				{field : 'projectName',displayName: 'Project', enableColumnMenu: true, enableSorting: true,minWidth : 100,width: 150},
				{field : 'accountName',displayName: 'Account', enableColumnMenu: false, enableSorting: false,minWidth : 100,width: 150},
				{field : 'deliveryLeadIds[0].employeeName',displayName: 'Delivery Lead',minWidth : 100,width: 150},
				{field : 'billableStatus',displayName: 'Billability', enableColumnMenu: false, enableSorting: false,minWidth : 100,width: 150},
				{field : 'projectStartDate',displayName: 'Start Date', enableColumnMenu: false, enableSorting: false, cellFilter: 'date:"dd-MMM-yyyy"',minWidth : 100,width: 150},
				{field : 'projectEndDate',displayName: 'End Date', enableColumnMenu: false, enableSorting: false, cellFilter: 'date:"dd-MMM-yyyy"' ,minWidth : 100,width: 150},
				{field : 'projectStatus',displayName: 'Status', enableColumnMenu: false,enableSorting: false,minWidth : 100,width: 150}
			]
		};
		

		$scope.gridOptionsEmpLocationDetails = {
				paginationPageSizes : [ 10, 20, 30, 40, 50, 100],
				paginationPageSize : 10,
			    pageNumber: 1,
				pageSize:10,
				columnDefs : [ 
					{field : 'employeeName',displayName: 'Employee Name', enableColumnMenu: true, enableSorting: true,minWidth : 100,width: 150},
					{field : 'employeeId',displayName: 'EmployeeId', enableColumnMenu: false, enableSorting: false,minWidth : 100,width: 150},
					{field : 'empLocation',displayName: 'Location', enableColumnMenu: false, enableSorting: false,minWidth : 100,width: 150},
					{field : 'startDate',displayName: 'Start Date', enableColumnMenu: false, enableSorting: false,cellFilter: 'date:"dd-MMM-yyyy"', minWidth : 100,width: 150 },
					{field : 'endDate',displayName: 'End Date', enableColumnMenu: false, enableSorting: false, cellFilter: 'date:"dd-MMM-yyyy"',minWidth : 100,width: 150 },
					{field : 'updatedDate',displayName: 'Updated Date', enableColumnMenu: false, enableSorting: false, cellFilter: 'date:"dd-MMM-yyyy"' ,minWidth : 100,width: 150},
					{field : 'createDate',displayName: 'Create Date', enableColumnMenu: false, enableSorting: false, cellFilter: 'date:"dd-MMM-yyyy"' ,minWidth : 100,width: 150},
					{field : 'active',displayName: 'Active', enableColumnMenu: false,cellTemplate:getCellActiveTemplate,enableSorting: false,minWidth : 100,width: 150}
				]
			};
		$scope.gridOptionsVisaDetails = {
				paginationPageSizes : [ 10, 20, 30, 40, 50, 100],
				paginationPageSize : 10,
			    pageNumber: 1,
				pageSize:10,
				columnDefs : [ 
					{field : 'visa',displayName: 'Visa', enableColumnMenu: true, enableSorting: true,minWidth : 100,width: 150},
					{field : 'country',displayName: 'Country', enableColumnMenu: false, enableSorting: false,minWidth : 100,width: 150},
					{field : 'visaNo',displayName: 'Visa No', enableColumnMenu: false, enableSorting: false,minWidth : 100,width: 150},
					{field : 'visaStatus',displayName: 'Status', enableColumnMenu: false, enableSorting: false,minWidth : 100,width: 150},
					{field: 'visaExpiryDate',
				        displayName: 'Expiry Date',
				        cellFilter: 'date:"dd-MMM-yyyy"',minWidth : 100,width: 150
				      },
				    {field : 'comments',displayName: 'Comments',  enableColumnMenu: false, enableSorting: false,minWidth : 100,width: 150}
				]
			};

		// var getCellActiveTemplateBilling='<div ng-show="COL_FIELD==true"><p class="col-lg-12">Y</P></div><div ng-show="COL_FIELD==false"><p class="col-lg-12">N</p></div>';
			
			$scope.gridOptionsEmpBillability= {
				paginationPageSizes : [ 10, 20, 30, 40, 50, 100],
				paginationPageSize : 10,
			    pageNumber: 1,
				pageSize:10,
				enableCellEditOnFocus: true,
		        columnDefs : [ 
					{field : 'projectName',displayName: 'Project', enableColumnMenu: true, enableSorting: true,minWidth : 100,width: 150},
					{field : 'accountName',displayName: 'Account', enableColumnMenu: false, enableSorting: false,minWidth : 100,width: 150},
					{field: 'billingStartDate',displayName: 'Billing Start Date',
				        cellFilter: 'date:"dd-MMM-yyyy"',minWidth : 100,width: 150
				      },
					{
				        field: 'billingEndDate',
				        displayName: 'Billing End Date',
				        cellFilter: 'date:"dd-MMM-yyyy"',minWidth : 100,width: 150
				         },
					{field : 'billableStatus',displayName: 'Billability Status',  enableColumnMenu: false, enableSorting: false,minWidth : 100,width: 150},
					{field : 'resourceRole',displayName: 'Role',  enableColumnMenu: false, enableSorting: false,minWidth : 100,width: 150},
					{field : 'resourceStatus',displayName: 'Allocation Status',  enableColumnMenu: false, enableSorting: false,minWidth : 100,width: 150}
				]
			};
			$http({
		        method : "GET",
		        url : appConfig.appUri + "resources/billing?employeeId="+$scope.profile.employeeId
		    }).then(function mySuccess(response) {
		     	$scope.gridOptionsEmpBillability.data = response.data;
		    	if(response.data.length > 10){
		    		$scope.gridOptions.enablePaginationControls = true;
		    	}
		    	else{
		    		$scope.gridOptions.enablePaginationControls = false;
		    	}
		      }, function myError(response) {
		    	showAlert("Something went wrong while fetching data!!!");
		    });
			$http({
		        method : "GET",
		        url : appConfig.appUri + "resources/getMyProjectAllocations?employeeId="+$scope.profile.employeeId
		    }).then(function mySuccess(response) {
		     	$scope.gridOptionsProjectAllocatons.data = response.data.records;
		    	if(response.data.records.length > 10){
		    		$scope.gridOptions.enablePaginationControls = true;
		    	}
		    	else{
		    		$scope.gridOptions.enablePaginationControls = false;
		    	}
		      }, function myError(response) {
		    	showAlert("Something went wrong while fetching data!!!");
		    });
			$http({
		        method : "GET",
		        url : appConfig.appUri + "employees/locations/"+$scope.profile.employeeId
		    }).then(function mySuccess(response) {
		    	//alert("response"+response);
		     //	alert("response"+response.data);
		     	$scope.gridOptionsEmpLocationDetails.data = response.data.records; 
		      }, function myError(response) {
		    	showAlert("Something went wrong while fetching data!!!");
		    });
		//$scope.gridOptionsProjectAllocatons.data = $scope.dataToPass;
		//$scope.gridOptionsEmpBillability.data = $scope.dataToPass;
	//	$scope.gridOptionsVisaDetails.data = $scope.dataToPass;
		
		$scope.cancel = function() {
		    $mdDialog.hide();
		};
		 $scope.toggleBillability = function() {
				 $scope.showBillable = !$scope.showBillable;

				  if($scope.showOrHideBD=="Show"){
			        $scope.showOrHideBD="Hide";
			        }else {
			        	 $scope.showOrHideBD="Show";
			        }
				};
		 }

	function showAlert(message) {
		$mdDialog.show($mdDialog.alert().parent(
				angular.element(document.querySelector('#popupContainer')))
				.clickOutsideToClose(true).textContent(message).ariaLabel(
						'Alert Dialog').ok('Ok'));
	}
	
}).filter('mapGender', function() {
	  var genderHash = {
			    'Billable': 'Billable',
			    'Shadow': 'Shadow',
			    'NA': 'NA',
			    'Bench': 'Bench',
			    'UA': 'Unassigned',
			    "Reserved":"Reserved",
			    '': 'All'
			  };

			  return function(input) {
			    if (!input){
			      return '';
			    } else {
			      return genderHash[input];
			    }
			  };
			}).directive('myCustomDropdown', function() {
				  return {
					    template: '<select class="form-control" ng-model="colFilter.term" ng-options="option.id as option.value for option in colFilter.options"></select>'
					  };
					});
				
