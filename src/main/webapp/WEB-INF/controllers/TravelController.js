myApp.controller("travelController",function($scope, myFactory, $mdDialog, $http, appConfig, $timeout){
	$scope.records = [];
	$scope.empSearchId = "";
	
	$scope.parentData = {
			"id": "",
			"employeeId": "",
			"employeeName": "",
			"visaName":"",
	        "visaCountry":"",
	        "visaNo":"",
	 "visaExpiryDate":"",
	 "status":"",
	 "comments":"",
		
			"action":""
	};
	$scope.employees = [];
	$scope.projects = [];
	var getCellTemplate = '<p class="col-lg-12"><i class="fa fa-pencil-square-o fa-2x" aria-hidden="true" style="font-size:1.5em;margin-top:3px;cursor:pointer;" ng-click="grid.appScope.getRowData(row,\'Update\')"></i>'+
	'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i class="fa fa-minus-circle fa-2x" aria-hidden="true" style="font-size:1.5em;margin-top:3px;cursor:pointer;" ng-click="grid.appScope.getRowData(row,\'Delete\')"></i></p>';
	var getCellTemplate1='<div ng-show="COL_FIELD!=\'Employee\' && COL_FIELD!=\'HR\' "><p class="col-lg-12">{{COL_FIELD}}  <i class="fa fa-sitemap fa-2x"  aria-hidden="true" style="font-size:1.5em;color:blue;margin-top:3px;cursor:pointer;" ng-click="grid.appScope.getRowData(row,\'ViewTeamDetail\')"></i></p></div><div ng-show="COL_FIELD==\'Employee\' || COL_FIELD==\'HR\'"><p class="col-lg-12">{{COL_FIELD}}</p></div>'
	var getCellActiveTemplate='<div ng-show="COL_FIELD==true"><p class="col-lg-12">Y</P></div><div ng-show="COL_FIELD==false"><p class="col-lg-12">N</p></div>';
	var getCellTemplateBillable='<div><p class="col-lg-12">{{COL_FIELD}}  <i class="fa fa-usd fa-lg"  aria-hidden="true" style="font-size:1.5em;color:blue;margin-top:3px;cursor:pointer;" ng-click="grid.appScope.getRowData(row,\'ViewBillability\')"></i></p></div>'
	//var getCellTemplateBillable='<div><p class="col-lg-12">{{COL_FIELD}}</p></div>'
				
		$scope.gridOptions = {
		paginationPageSizes : [ 10, 20, 30, 40, 50, 100],
		paginationPageSize : 10,
	    pageNumber: 1,
		pageSize:10,
	
		//enableCellEdit: false,
		//enableCellEditOnFocus: true,
		enableFiltering: true,
	    columnDefs : [ 
			{field : 'employeeId',displayName: 'Emp ID', enableColumnMenu: true, enableSorting: true, width:100,enableFiltering: true},
			{field : 'employeeName',displayName: 'Emp Name', enableColumnMenu: false, enableSorting: true, enableCellEdit: false,enableFiltering: true,width:200},
			{field : 'visaName',displayName: 'Visa', enableColumnMenu: false, enableSorting: false, enableFiltering: true,width:100}, 
			//{field : 'visaCountry',displayName: 'Country', enableColumnMenu: false, enableSorting: true,enableFiltering: false}, 
			//{field : 'visaNo',displayName: 'Visa No', enableColumnMenu: false,enableFiltering: false, enableSorting: true},
			//{field : 'visaIntiatedDate',displayName: 'Intiated Date', enableColumnMenu: false, enableSorting: false,enableFiltering: false, cellFilter: 'date:"dd-MMM-yyyy"'},
			//{field : 'approvedDate',displayName: 'Approved Date', enableColumnMenu: false, enableSorting: false,enableFiltering: false, cellFilter: 'date:"dd-MMM-yyyy"'},
			//{field : 'visaExpiryDate',displayName: 'Expired Date', enableColumnMenu: false, enableSorting: false,enableFiltering: false, cellFilter: 'date:"dd-MMM-yyyy"'},
			{field : 'fromLocation',displayName: 'From Location', enableColumnMenu: false, enableSorting: true,enableFiltering: false,width:120},
			{field : 'toLocation',displayName: 'To Location', enableColumnMenu: false, enableSorting: true,enableFiltering: false,width:100},
			{field : 'buddy',displayName: 'Buddy', enableColumnMenu: false, enableSorting: true,enableFiltering: false,width:100},
			{field : 'account',displayName: 'Account', enableColumnMenu: false, enableSorting: true,enableFiltering: true,width:100},
			{field : 'project',displayName: 'project', enableColumnMenu: false, enableSorting: true,enableFiltering: true,width:100},
			{field : 'status',displayName: 'Status', enableColumnMenu: false, enableSorting: true,enableFiltering: true,width:100},
			{field : 'travelDate',displayName: 'Travel Date', enableColumnMenu: false, enableSorting: false,enableFiltering: false, cellFilter: 'date:"dd-MMM-yyyy"',width:100},
			{field : 'returnDate',displayName: 'Return Date', enableColumnMenu: false, enableSorting: false,enableFiltering: false, cellFilter: 'date:"dd-MMM-yyyy"',width:100},
			{field : 'comments',displayName: 'Comments',  enableColumnMenu: false, enableSorting: false,enableFiltering: false,width:100},
			{name : 'Actions', displayName: 'Actions',cellTemplate: getCellTemplate, enableColumnMenu: false, enableSorting: false, width:100,enableFiltering: false} 
			
			]
	};
	
/*	$scope.gridOptions.onRegisterApi = function(gridApi) {
		//alert('test');
	    $scope.gridApi = gridApi;

	  gridApi.edit.on.afterCellEdit($scope, function(rowEntity, colDef, newValue, oldValue) {
        $scope.$apply();

	    });

	  };*/
	
	$scope.gridOptions.data = $scope.records;
	
	$scope.getRowData = function(row, action){
		$scope.parentData.id = row.entity.id;
		$scope.parentData.employeeId = row.entity.employeeId;
		$scope.parentData.employeeName = row.entity.employeeName;
		$scope.parentData.visaName = row.entity.visaName;
		$scope.parentData.visaCountry = row.entity.visaCountry;
		$scope.parentData.visaNo = row.entity.visaNo;
		$scope.parentData.visaExpiryDate = row.entity.visaExpiryDate;
		$scope.parentData.status = row.entity.status;
		$scope.parentData.comments = row.entity.comments;
		
		if(action == "Update"){
			$scope.assignRole(action, $scope.parentData);
		}
		else if(action == "Delete"){
			$scope.deleteRole(row,$scope.parentData.id);
			$scope.refreshPage();
	     }else if(action=="ViewTeamDetail"){
	    	 
	    	 $scope.viewTeamDetail(action, $scope.parentData);
	    		
	     }else if(action=="ViewBillability"){
	    	 
	    	 $scope.ViewBillability(action, $scope.parentData);
	    		
	     }
	}
	
	$scope.refreshPage = function(){
		$scope.empSearchId = "";
		$scope.getEmpVisas();
		$scope.getEmployeesToTeam();
	}
	
	$scope.getEmpVisas = function(){
		$http({
	        method : "GET",
	        url : appConfig.appUri + "/visa/getAllTravelRequests"
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
	};
	$scope.getEmployeesToTeam = function(){
		$http({
	        method : "GET",
	        url : appConfig.appUri + "/projectTeam/getEmployeesToTeam"
	    }).then(function mySuccess(response) {
	        $scope.employees=response.data;
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
	
	$scope.assignRole = function(action, userData){
		$('#home').addClass('md-scroll-mask');
		userData.action = action;
		$mdDialog.show({
		      controller: AddProjectTeamController,
		      templateUrl: 'templates/addUpdateTravel.html',
		      parent: angular.element(document.body),
		      clickOutsideToClose:false,
		      locals:{dataToPass: userData, gridOptionsData: $scope.gridOptions.data, employees: $scope.employees},
		    })
		    .then(function(result) {
		    	if(result == "Add"){
		    		showAlert('Travel request created successfully');
		    	}
		    	else if(result == "Update"){
		    		showAlert('Travel request updated successfully');
		    	}
		    	else if(result == "Cancelled") {
		    		
		    	}
		    	else {
		    		 showAlert('Teammate assigning/updation failed!!!');
		    		$scope.refreshPage();
		    	}
		    	
		    });		
	};
	
	$scope.updateEmployee = function(action, userData){
		$('#home').addClass('md-scroll-mask');
		userData.action = action;
		$mdDialog.show({
		      controller: AddProjectTeamController,
		      templateUrl: 'templates/addUpdateVisa.html',
		      parent: angular.element(document.body),
		      clickOutsideToClose:false,
		      locals:{dataToPass: userData, gridOptionsData: $scope.gridOptions.data, employees: $scope.employees},
		    })
		    .then(function(result) {
		    	if(result == "Add") {
		    		showAlert('New Teammate assigned successfully');
		    	}
		    	else if(result == "Update") {
		    		$scope.refreshPage();
		    		showAlert('Teammate updated successfully');
		    	
		    	}
		    	else if(result == "Cancelled") {
		    		
		    	}
		    	else {
		    		showAlert('Teammate assigning/updation failed!!!');
		    	}
		    });
	};
	$scope.cancel = function() {
		$mdDialog.hide();
		};
	$scope.viewTeamDetail = function(action, userData){
		$('#home').addClass('md-scroll-mask');
		userData.action = action;
		$mdDialog.show({
		      controller: AddProjectTeamController,
		      templateUrl: 'templates/reporteeTeam.html',
		      parent: angular.element(document.body),
		      clickOutsideToClose:false,
		      locals:{dataToPass: userData, gridOptionsData: $scope.gridOptions.data, employees: $scope.employees},
		    })
		    .then(function(result) {
		    	if(result == "Add") {
		    		showAlert('New Teammate assigned successfully');
		    	}
		    	else if(result == "Update") {
		    		$scope.refreshPage();
		    		showAlert('Teammate updated successfully');
		    	
		    	}
		    	else if(result == "Cancelled") {
		    	
		    	}
		    	else {
		    		showAlert('Teammate assigning/updation failed!!!');
		    	}
		    });
	};
	$scope.ViewBillability = function(action, userData){
		$('#home').addClass('md-scroll-mask');
		userData.action = action;
		$mdDialog.show({
		      controller: AddProjectTeamController,
		      templateUrl: 'templates/ViewBillability.html',
		      parent: angular.element(document.body),
		      clickOutsideToClose:false,
		      locals:{dataToPass: userData, gridOptionsData: $scope.gridOptions.data, employees: $scope.employees},
		    })
		    .then(function(result) {
		    	if(result == "Add"){
		    		showAlert('New Teammate assigned successfully');
		    	}
		    	else if(result == "Update") {
		    		$scope.refreshPage();
		    		showAlert('Teammate updated successfully');
		    	
		    	}
		    	else if(result == "Cancelled") {
		    		
		    	}
		    	else{
		    		showAlert('Teammate assigning/updation failed!!!');
		    	}
		    });
	};
	
	$scope.cancel = function() {
		$mdDialog.hide();
	};
	$scope.deleteRole = function(row,id){
	     $('#home').addClass('md-scroll-mask');
	    var confirm = $mdDialog.confirm()
	    	  .clickOutsideToClose(true)
	          .textContent('Are you sure you want to remove the Travel Request?')
	          .ok('Ok')
	          .cancel('Cancel');
	    $mdDialog.show(confirm).then(function() {
	        deleteUserRole(row.entity.employeeId,row.entity.id);
			$timeout(function(){updateGridAfterDelete(row.entity.employeeId)},500);
			$scope.refreshPage();
	    }, function() {
	 
	    });
	};
	
	function deleteUserRole(empId,id){
		var record = {"id":id,"employeeId":empId};
		var req = {
				method : 'DELETE',
				url : appConfig.appUri+ "visa/deleteTravelRequest",
				headers : {
					"Content-type" : "application/json"
				},
				data : record
			}
			$http(req).then(function mySuccess(response) {
				$scope.result =response.data;
			}, function myError(response){
				$scope.result = "Error";
			});
	}
	
	function updateGridAfterDelete(empId){
		var gridOptionsData = $scope.gridOptions.data; 
		if($scope.result == "Success" || $scope.result == ""){
			var existingRecord = getRowEntity(empId);
			existingRecord.active = false;
			var index = gridOptionsData.indexOf(existingRecord);
			gridOptionsData[index] = existingRecord;
			showAlert('Travel Request removed  successfully');
		}else if($scope.result == "Error"){
			showAlert('Something went wrong while deleting the role.')
		}
	}
	
	function getRowEntity(empId){
		var gridOptionsData = $scope.gridOptions.data; 
		for(var i=0;i<gridOptionsData.length;i++){
			var record = gridOptionsData[i];
			if(record.employeeId == empId){
				return record;
			}
		}
	}
	
	function AddProjectTeamController($scope, $mdDialog, dataToPass, gridOptionsData,employees) {
		$scope.templateTitle = dataToPass.action;
		$scope.alertMsg = "";
		$scope.isDisabled = false;
		$scope.result = "";
		$scope.employeeList = employees;
		$scope.projectList = [];
		$scope.accounts=myFactory.getAccounts();
		$scope.employeeModel;
		$scope.projectModel;
		$scope.objectId = "";
		$scope.visaStatuses=["Proposed","Initiated","In process","Approved","Rejected"];
		$scope.travelStatuses=["Proposed","Initiated","In process","Done","Cancelled"];
		
		$scope.getVisaStatus = function(){
			if ($scope.visaStatus !== undefined) {
				return $scope.visaStatus;
			} else {
				return "Please select Visa status";
			}
		};
		$scope.getProjects = function(){
			$http({
		        method : "GET",
		        url : appConfig.appUri + "/visa/getAllVisas"
		    }).then(function mySuccess(response) {
		        $scope.projectList = response.data;
		    }, function myError(response) {
		    	$scope.projectList = [];
		    });
		};
		$scope.getProjectsP = function(){
			$http({
		        method : "GET",
		        url : appConfig.appUri +"project/getProjects"
		    }).then(function mySuccess(response) {
		        $scope.projectListP = response.data;
		    }, function myError(response) {
		    	$scope.projectListP = [];
		    });
		};
		$scope.getAccountText = function(){
			if ($scope.account !== undefined) {
				return $scope.account;
			} else {
				return "Please select account";
			}
		};
		if(dataToPass.action == "Add"){
			$scope.empId = "";
			$scope.empName = "";
			$scope.empRole;
			$scope.empShift;
			$scope.empEmail = "";
			$scope.isDisabled = false;
		}else if(dataToPass.action == "Update"){
			$scope.id = dataToPass.id;
			$scope.employeeId = dataToPass.employeeId;
			$scope.employeeName = dataToPass.employeeName;
			$scope.projectModel = {
					 'visaName': dataToPass.visaName,
					 'visaCountry': dataToPass.visaCountry
					  };
			$scope.visaCountry = dataToPass.visaCountry;
			$scope.visaNo = dataToPass.visaNo;
			$scope.expiryDate = new Date(dataToPass.visaExpiryDate);
			$scope.visaStatus = dataToPass.status;
			$scope.comments = dataToPass.comments;
			$scope.employeeModel = {
					 'employeeName': dataToPass.employeeName,
					 'employeeId': dataToPass.employeeId
					  };
			
		}else if(dataToPass.action == "ViewTeamDetail"){}else if(dataToPass.action == "ViewBillability"){}
		$scope.designations = myFactory.getDesignations(); 
		$scope.billableStatuses = ["Billable","Shadow","Non-Billable","Reserved"];
		$scope.shifts =myFactory.getShifts();
		$scope.getSelectedDesignation = function(){
			if ($scope.empDesignation !== undefined) {
				return $scope.empDesignation;
			} else {
				return "Please select a designation";
			}
		};
		$scope.getSelectedBillableStatus = function(){
			if ($scope.empBillableStatus !== undefined) {
				return $scope.empBillableStatus;
			} else {
				return "Please select a billable status";
			}
		};
		$scope.getEmployeeSelected = function(){
			if ($scope.employeeModel !== undefined) {
				$scope.employee=$scope.employeeModel;
				return $scope.employeeModel.employeeName;
			} else {
				return "Please select a employee";
			}
		};
		$scope.fetchEmpVisa = function(){
			//alert('fetchEmpVisa '+$scope.projectModel.visaName);
			$http({
		        method : "GET",
		        url : appConfig.appUri + "/projectTeam/getEmployeesHavingVisa?visa="+$scope.projectModel.visaName
		    }).then(function mySuccess(response) {
		        $scope.employeeList=response.data;
		    }, function myError(response) {
		    	showAlert("Something went wrong while fetching data!!!");
		    	$scope.gridOptions.data = [];
		    });
		};
		$scope.getProjectSelected = function(){
			if ($scope.projectModel !== undefined) {
				$scope.project=$scope.projectModel;
				return $scope.projectModel.visaName;
			} else {
				return "Please select a Visa";
			}
		};
		$scope.getProjectSelectedP = function(){
			if ($scope.projectModelP !== undefined) {
				$scope.projectP=$scope.projectModelP;
				return $scope.projectModelP.projectName;
			} else {
				return "Please select a project";
			}
		};
		$scope.getSelectedShift = function(){
			if ($scope.shift !== undefined) {
				return $scope.shift;
			} else {
				return "Please select a shift";
			}
		};
		
		
		$scope.validateFields = function(action){
			//alert('validateFields');
			//alert('validateFields'+$scope.returnDate+'fff'+$scope.travelDate);
			var employeeModel = $scope.employeeModel;
			var projectModel = $scope.projectModel;
			var statusModel = $scope.visaStatus;
			if(action == "Add"){
			
				if(employeeModel == undefined){
					$scope.alertMsg = "Please select a employee";
				document.getElementById('selectEmp').focus();
				}else if(projectModel == undefined){
					$scope.alertMsg = "Please select a visa";
					document.getElementById('selectProject').focus();
				}/*else if(employeeModel != undefined && projectModel != undefined 
						&& getExistingRecordProjectStatus(employeeModel.employeeId, projectModel.projectName)){
					$scope.alertMsg = "Employee is already assigned to the selected project";
				}*/ else {
					
					$scope.alertMsg = "";
					var record = {"employeeId":employeeModel.employeeId, "employeeName":employeeModel.employeeName, "visaName": projectModel.visaName,"status":$scope.visaStatus,"visaNo":$scope.visaNo,"visaExpiryDate":$scope.expiryDate,"comments":$scope.comments,"fromLocation":$scope.fromLocation,"toLocation":$scope.toLocation,"account":$scope.account,"project":$scope.projectP,"travelDate":$scope.travelDate,"returnDate":$scope.returnDate};
					addOrUpdateRole(record, $scope.templateTitle);
					$timeout(function(){updateGrid($scope.templateTitle, record)},500);
				}
			}else{
				$scope.alertMsg = "";
				var record = {"id":$scope.id,"employeeId":employeeModel.employeeId, "employeeName":employeeModel.employeeName, "visaName": projectModel.visaName,"status":$scope.visaStatus,"visaNo":$scope.visaNo,"visaExpiryDate":$scope.expiryDate,"comments":$scope.comments,"fromLocation":$scope.fromLocation,"toLocation":$scope.toLocation,"account":$scope.account,"project":$scope.projectP,"travelDate":$scope.travelDate,"returnDate":$scope.returnDate};
				addOrUpdateRole(record, $scope.templateTitle);
				$timeout(function(){updateGrid($scope.templateTitle, record)},500);
			}
			
			
		};
		
		$scope.cancel = function() {
			var showConfirmDialog = false;
			var totalFields = $scope.myForm.$$controls;
			for (key in totalFields) {
				if(totalFields[key].$modelValue !== '' && totalFields[key].$modelValue !== undefined && totalFields[key].$modelValue.length !== 0) {
					showConfirmDialog = true;
				}
			}
			if(showConfirmDialog){
				$mdDialog.show($mdDialog.confirm({
					skipHide: true,
					textContent: 'Are you sure you want to cancel this?',
					ok: 'ok',
					cancel:'cancel'
				  })).then(function(){
					  $mdDialog.hide('Cancelled');
			})
				}
				else{
					$mdDialog.hide('Cancelled'); 
				}
		};
		
		function updateGrid(action, record){
			if($scope.alertMsg == ""){
				if($scope.result == "Success"){
					if(action == "Add"){
						record.id = $scope.objectId;
						gridOptionsData.push(record);
					}else if(action == "Update"){
						var existingRecord = getRowEntity($scope.empId);
						var index = gridOptionsData.indexOf(existingRecord);
						gridOptionsData[index] = record;
					}
					$mdDialog.hide(action);
				}else{
					$mdDialog.hide("Error");
				}
				
			}
		}
		
		function addOrUpdateRole(record, action){
			var urlRequest  = "";
			if(action == "Add"){
				urlRequest = appConfig.appUri+ "visa/addTravelRequest";
			}else if(action == "Update"){
				urlRequest = appConfig.appUri+ "visa/updateTravelRequest";
			}
			var req = {
				method : 'POST',
				url : urlRequest,
				headers : {
					"Content-type" : "application/json"
				},
				data : record
			}
			$http(req).then(function mySuccess(response) {
				$scope.result = "Success";
				$scope.objectId = response.data.id;
			}, function myError(response){
				$scope.result = "Error";
			});
		}
		
		function getRowEntity(empId){
			for(var i=0;i<gridOptionsData.length;i++){
				var record = gridOptionsData[i];
				if(record.employeeId == empId){
					return record;
				}
			}
		}
		
		function getExistingRecordProjectStatus(empId, projectName){
			for(var i=0;i<gridOptionsData.length;i++){
				var record = gridOptionsData[i];
				if(record.employeeId == empId){
					if(record.active == true && record.projectName == projectName)
						return true;
				}
			}
			return false;
		}
	}
});