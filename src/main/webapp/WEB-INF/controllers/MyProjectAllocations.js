myApp.controller("myProjectAllocationsController",function($scope, myFactory, $mdDialog, $http, appConfig, $timeout){
	$scope.records = [];
	$scope.empSearchId = "";
	
	$scope.parentData = {
			"employeeId":"",
			"projectName": "",
			"account": "",
			"managerName":"",
			"billableStatus": "",
			"shift":"",
			"active": ""
	};
	$scope.employees = [];
	$scope.projects = [];
	var getCellTemplate = '<div class="ui-grid-cell-contents"><a href="#" ng-click="grid.appScope.getRowData(row,\'View\')">{{COL_FIELD}}</a></div>';
	//var getCellActiveTemplate='<div ng-show="COL_FIELD==true"><p class="col-lg-12">Active</P></div><div ng-show="COL_FIELD==false"><p class="col-lg-12">In Active</p></div>';
	//var getCellTemplate = '<p class="col-lg-12"><i class="fa  fa-2x" aria-hidden="true" style="font-size:1.5em;colormargin-top:3px;cursor:pointer;" ng-click="grid.appScope.getRowData(row,\'Update\')">{{COL_FIELD}}</i></i></p>';
    //	var getCellActiveTemplate='<div >COL_FIELD<p class="col-lg-12">Y</P></div><div ng-show="COL_FIELD==false"><p class="col-lg-12">N</p></div>';
	
	$scope.gridOptions = {
		paginationPageSizes : [ 10, 20, 30, 40, 50, 100],
		paginationPageSize : 10,
	    pageNumber: 1,
		pageSize:10,     
		enableFiltering:true,
		columnDefs : [ 
			{field : 'projectName',displayName: 'Project',cellTemplate:getCellTemplate, enableColumnMenu: true, enableSorting: true,enableFiltering:true},
			{field : 'accountName',displayName: 'Account', enableColumnMenu: false, enableSorting: false,enableFiltering:true},
			{field : 'deliveryLeadIds',displayName: 'Delivery Lead',cellTemplate:'<div ng-repeat= "item in row.entity[col.field]">{{item.employeeName}}<span ng-hide="$last">,</span></div>', enableColumnMenu: false, enableSorting: false,enableFiltering:false},
			{field : 'billableStatus',displayName: 'Billability', enableColumnMenu: false, enableSorting: false,enableFiltering:false},
			{field : 'billingStartDate',displayName: 'Start Date', enableColumnMenu: false, enableSorting: false,cellFilter: 'date:"dd-MMM-yyyy"',enableFiltering:false},
			{field : 'billingEndDate',displayName: 'End Date', enableColumnMenu: false, enableSorting: false,cellFilter: 'date:"dd-MMM-yyyy"',enableFiltering:false},
			{field : 'shift',displayName: 'Shift', enableColumnMenu: false, enableSorting: false,enableFiltering:false},
			{field : 'resourceStatus',displayName: 'Status', enableColumnMenu: false,enableSorting: false,enableFiltering:false}
		]
	};
	$scope.gridOptions.data = $scope.records;
	
	$scope.getRowData = function(row, action){
		$scope.parentData.employeeId = row.entity.employeeId;
		$scope.parentData.employeeName = row.entity.employeeName;
		$scope.parentData.emailId = row.entity.emailId;
		$scope.parentData.role = row.entity.role;
		$scope.parentData.shift = row.entity.shift;
		$scope.parentData.projectId = row.entity.projectId;
		$scope.parentData.projectName = row.entity.projectName;
		$scope.parentData.managerId = row.entity.managerId;
		$scope.parentData.managerName = row.entity.managerName;
		$scope.parentData.experience = row.entity.experience;
		$scope.parentData.designation = row.entity.designation;
		$scope.parentData.deliveryLeadIds = row.entity.deliveryLeadIds;
		
		if(action == "View")
			$scope.viewTeamDetails(action, $scope.parentData);
	
	}
	
	$scope.refreshPage = function(){
		$scope.empSearchId = "";
		$scope.getMyProjectAllocations();
	}
	
	$scope.getMyProjectAllocations = function(){
		$http({
	        method : "GET",
	        url : appConfig.appUri + "resources/getMyProjectAllocations?employeeId="+myFactory.getEmpId()
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

	$scope.viewTeamDetails = function(action, userData){
		$('#home').addClass('md-scroll-mask');
		userData.action = action;
		$mdDialog.show({
		      controller: ProjectDetailsController,
		      templateUrl: 'templates/newProjectTeamDetails.html',
		      parent: angular.element(document.body),
		      clickOutsideToClose:false,
		      locals:{dataToPass: userData,gridOptionsData: $scope.gridOptions.data, managers: $scope.managers},
		    })
		    .then(function(result) {});
	};
	$scope.cancel = function() {
	    $mdDialog.hide();
	};
	function ProjectDetailsController($scope, $mdDialog, dataToPass,gridOptionsData, managers) {
		$scope.templateTitle = dataToPass.action;
		$scope.alertMsg = "";
		$scope.isDisabled = false;
		$scope.result = "";
		$scope.managerDetails = managers;
		$scope.prjctStses=["Active","Completed","On Hold","Proposed"];
		$scope.accounts=myFactory.getAccounts();
		if(dataToPass.action == "Assign"){}else if(dataToPass.action == "Update"){}else if(dataToPass.action == "View"){
		$scope.projectId = dataToPass.projectId;
		$scope.projectName = dataToPass.projectName;
//		$scope.managerId = dataToPass.managerId;
//		$scope.managerName = dataToPass.managerName;
		$scope.deliveryLeads = dataToPass.deliveryLeadIds;
		$scope.projectStatus = dataToPass.status;
	    $scope.gridOptions = {
				paginationPageSizes : [ 10, 20, 30, 40, 50, 100],
				paginationPageSize : 10,
			    pageNumber: 1,
				pageSize:10,
				columnDefs : [ 
					{field : 'employeeId',displayName: 'Emp ID', enableColumnMenu: true, enableSorting: true, width:100},
					{field : 'employeeName',displayName: 'Empl Name ', enableColumnMenu: false, enableSorting: false},
					{field : 'resourceRole',displayName: 'Role', enableColumnMenu: false, enableSorting: false},
//					{field : 'emailId',displayName: 'Email Id ', enableColumnMenu: false, enableSorting: false},
//					{field : 'experience',displayName: 'Exp', enableColumnMenu: true, enableSorting: true,width:80},
					{field : 'designation',displayName: 'Designation ', enableColumnMenu: false, enableSorting: false},
					{field : 'billableStatus',displayName: 'Billability ', enableColumnMenu: false, enableSorting: false},
				]
			};
			$scope.isDisabled = true;
			$http({
		        method : "GET",
		        url : appConfig.appUri + "resources/project/"+$scope.projectId
		    }).then(function mySuccess(response) {
		    	if(response.data.records.length > 10){
		    		$scope.gridOptions.enablePaginationControls = true;
		    	}
		    	else{
		    		$scope.gridOptions.enablePaginationControls = false;
		    	}
		    	$scope.gridOptions.data = response.data.records;
		    }, function myError(response) {
		    	showAlert("Something went wrong while fetching data!!!");
		    	$scope.gridOptions.data = [];
		    });
	}else if(dataToPass.action == "UnAssigned"){}else if(dataToPass.action == "allocated"){}
		$scope.cancel = function() {
		    $mdDialog.hide();
		};
		$scope.cancelDialog = function() {
			$mdDialog.hide();
		};
	}

	function showAlert(message) {
		$mdDialog.show($mdDialog.alert().parent(
				angular.element(document.querySelector('#popupContainer')))
				.clickOutsideToClose(true).textContent(message).ariaLabel(
						'Alert Dialog').ok('Ok'));
	}
	
	});