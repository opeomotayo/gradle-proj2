myApp.controller("projectController", function ($scope,uiGridConstants, myFactory, exportUiGridService, $mdDialog, $http, appConfig, $timeout, $window, $mdSelect, $element) {
	$scope.records = [];
	$scope.isEditable = false;
	$scope.aliasModel = {};
	$scope.empSearchId = "";
	$scope.parentData = {
		"projectId": "",
		"projectName": "",
		"account": "",
		"status": "",
		"action": "",
		"managerIds":[],
		"accountId":"",
		"domainId":"",
		"employeeId": "",
		"employeeName": "",
		"emailId":"",
		"role": "",
		"shift": "",
		"projectId":"",
		"projectName":"",
		"managerId":"",
		"managerName":"",
		"experience":"",
		"designation":"",
		"billableStatus":"",
		"mobileNumber":"",
		"startDate":"",
		"endDate":"",
		"newBillingStartDate":"",
	};

	$scope.managers = [];
	

	

	var getCellTemplate = '<p class="col-lg-12"><i class="fa fa-users fa-2x" aria-hidden="true" style="font-size:1.4em;margin-top:3px;cursor:pointer;"  ng-class="{ \'my-css-class\': grid.appScope.rowFormatter( row ) }" data-placement="center" title="View" onmouseenter="$(this).tooltip(\'show\')" ng-click="grid.appScope.getRowData(row,\'View\')" ></i>' +
		'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i ng-show="row.entity.status == \'Active\' || row.entity.status == \'Proposed\'" class="fa fa-pencil-square-o fa-2x" aria-hidden="true" style="font-size:1.5em;margin-top:3px;cursor:pointer;" data-placement="center" title="Edit" onmouseenter="$(this).tooltip(\'show\')" ng-click="grid.appScope.getRowData(row,\'Update\')"></i></p>' ;
	
//	'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i class="fa fa-minus-circle fa-2x" aria-hidden="true" style="font-size:1.5em;margin-top:3px;cursor:pointer;" data-placement="left" title="Delete" onmouseenter="$(this).tooltip(\'show\')" ng-click="grid.appScope.getRowData(row,\'Delete\')"></i></p>'
	
	$scope.gridOptions = {
			
		paginationPageSizes: [10, 20, 30, 40, 50, 100],
		paginationPageSize: 10,
		pageNumber: 1,
		pageSize: 10,
		enableFiltering: true,
		columnDefs: [
			{ field: 'projectName', displayName: 'Project ', enableColumnMenu: false, enableSorting: true, enableFiltering: true },
			{ field: 'domain', displayName: 'Domain ', enableColumnMenu: false, enableSorting: true, enableFiltering: true },
			{ field: 'account', displayName: 'Account ', enableColumnMenu: false, enableSorting: true, enableFiltering: true},
			//{field : 'managerId',displayName: 'Manager ID ', enableColumnMenu: false, enableSorting: false},
			{ field: 'deliveryLeadIds', displayName: 'Delivery Lead', cellTemplate: '<div ng-repeat= "item in row.entity[col.field]">{{item.employeeName}}<span ng-hide="$last">,</span></div>', enableColumnMenu: false, enableSorting: true, enableFiltering: false },
			{ field: 'status', displayName: 'Status ', enableColumnMenu: false, enableSorting: true, enableFiltering: true ,width:100},
			{ name: 'projectEndDate',width:120, displayName: 'End Date', enableColumnMenu: false, enableSorting: true , cellFilter:'date:"dd-MMM-yyyy"',enableFiltering:false},
			{ name: 'Actions', displayName: 'Actions', cellTemplate: getCellTemplate, enableColumnMenu: false, enableSorting: false, enableFiltering: false, width:120 }
		]
	};
	$scope.gridOptions.data = $scope.records;
	
	$scope.rowFormatter = function( row ) {
	    return (row.entity.status == 'Active');
	};
	  
	$scope.getRowData = function (row, action) {
		$scope.parentData.projectId = row.entity.projectId;
		$scope.parentData.projectName = row.entity.projectName;
		$scope.parentData.account = row.entity.account;
		$scope.parentData.managerIds=row.entity.managerIds;
		$scope.parentData.domain = row.entity.domain;
		$scope.parentData.status = row.entity.status;
		$scope.parentData.accountId = row.entity.accountId;
		$scope.parentData.domainId = row.entity.domainId;
		$scope.parentData.deliveryLeadIds = row.entity.deliveryLeadIds;
		$scope.parentData.projectStartDate = row.entity.projectStartDate;
		$scope.parentData.projectEndDate = row.entity.projectEndDate;
		if (action == "Update")
			$scope.addProject(action, $scope.parentData);
		else if (action == "Delete")
			$scope.deleteRole(row);
		else if (action == "View")
			$scope.viewTeamDetails(action, $scope.parentData);
	}
	
	

	
	$scope.refreshPage = function () {
		$scope.empSearchId = "";
		$scope.getProjects();
		$scope.getManagerDetails();
	}


	
	$scope.getProjects = function (type) {
		$scope.status="Active";
		$mdDialog.hide();
		if(type == "onload"){
			showProgressDialog("Fetching data please wait...");
		}
		var role = myFactory.getEmpRole();
		var empId = myFactory.getEmpId();
		//var deliveryLeadEmpId = ((role == "Delivery Lead") ? empId : undefined);
		$http({
			method: "GET",
			url: appConfig.appUri + "projects/employeeId/"+empId
		}).then(function mySuccess(response) {
			$mdDialog.hide();
			$scope.status="Active";
    		$scope.activeProjectsArray=[];
    		$scope.completedProjectsArray=[];
    		$scope.allProjectsArray= response.data.records.filter(function (project) {
    		    return project.projectName !== 'Bench';
    		});
    		$scope.activeProjectsArray = response.data.records.filter(function (project) {
    		    return project.status == 'Active' && project.projectName !='Bench';
    		});
    		$scope.completedProjectsArray = response.data.records.filter(function (project) {
    		    return project.status == 'Completed' && project.projectName !='Bench';
    		});
    		$scope.gridOptions.data = $scope.activeProjectsArray;
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
	$scope.getProjectDetails = function (projectStatus){
		if(projectStatus == 'all'){
			$scope.status = projectStatus;
			$scope.gridOptions.data=$scope.allProjectsArray;
		}else if (projectStatus == 'Completed'){
			 $scope.status=projectStatus;
			$scope.gridOptions.data=$scope.completedProjectsArray;
		}else if (projectStatus == 'Active'){
			 $scope.status=projectStatus;
			$scope.gridOptions.data=$scope.activeProjectsArray;
		}else{
			
		}
		
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
	$scope.getManagerDetails = function () {
		$http({
			method: "GET",
			url: appConfig.appUri + "employees/managers/"
		}).then(function mySuccess(response) {
			$scope.managers = response.data.records;
		}, function myError(response) {
			showAlert("Something went wrong while fetching data!!!");
			$scope.gridOptions.data = [];
		});
	};
	$scope.validateEmpId = function () {
		var searchId = $scope.empSearchId;
		if (searchId != "" && isNaN(searchId)) {
			showAlert('Please enter only digits');
			$scope.empSearchId = "";
			document.getElementById('empSearchId').focus();
		} else if (searchId != "" && (searchId.length < 5 || searchId.length > 5)) {
			showAlert('Employee ID should be 5 digits');
			$scope.empSearchId = "";
			document.getElementById('empSearchId').focus();
		} else if (searchId != "" && !checkEmpIdRange(searchId)) {
			showAlert('Employee ID should be in between ' + appConfig.empStartId + ' - ' + appConfig.empEndId);
			$scope.empSearchId = "";
			document.getElementById('empSearchId').focus();
		}
	};

	function checkEmpIdRange(searchId) {
		return parseInt(searchId) >= appConfig.empStartId && parseInt(searchId) <= appConfig.empEndId;
	}

	$scope.getEmployeeRole = function (type) {
		var searchId = $scope.empSearchId;
		if (searchId == "" && searchId.length == 0) {
			showAlert('Employee ID is mandatory');
			$scope.empSearchId = "";
			document.getElementById('empSearchId').focus();
		} else if (searchId != "" && !checkEmpIdRange(searchId)) {
			showAlert('Employee ID should be in between ' + appConfig.empStartId + ' - ' + appConfig.empEndId);
			$scope.empSearchId = "";
			document.getElementById('empSearchId').focus();
		} else {
			$scope.gridOptions.data = [];
			getEmployeeRoleData(searchId);
		}
	};

	function getEmployeeRoleData(empId) {
		$http({
			method: "GET",
			url: appConfig.appUri + "employees/employeeId/" + empId
		}).then(function mySuccess(response) {
			if (response.data != "" && response.data.length != 0) {
				$scope.gridOptions.data.push(response.data);
			}
		}, function myError(response) {
			showAlert("Something went wrong while fetching data!!!");
			$scope.refreshPage();
		});
	}

	function showAlert(message) {
		$mdDialog.show($mdDialog.alert().parent(
			angular.element(document.querySelector('#popupContainer')))
			.clickOutsideToClose(true).textContent(message).ariaLabel(
				'Alert Dialog').ok('Ok'));
	}

	$scope.addProject = function (action, userData) {
		$('#home').addClass('md-scroll-mask');
		userData.action = action;
		$mdDialog.show({
			controller: AddProjectController,
			templateUrl: 'templates/newProject.html',
			parent: angular.element(document.body),
			clickOutsideToClose: false,
			locals: { dataToPass: userData, gridOptionsData: $scope.gridOptions.data, managers: $scope.managers },
		})
			.then(function (result) {
				if (result == "Assign")  { 
					$scope.refreshPage(); 
					showAlert('Project created successfully'); 
				}
				else if (result == "Update") { 
					$scope.refreshPage(); 
					showAlert('Project updated successfully'); 
				}
				else if (result == "Cancelled") {
				
				}
				else {
					showAlert('Project assigning/updation failed!!!');
				}
			});
	};
	$scope.viewTeamDetails = function (action, userData) {
		$('#home').addClass('md-scroll-mask');
		userData.action = action;
		$mdDialog.show({
			controller: AddProjectController,
			templateUrl: 'templates/projectTeamDetails.html',
			parent: angular.element(document.body),
			clickOutsideToClose: false,
			locals: { dataToPass: userData, gridOptionsData: $scope.gridOptions.data, managers: $scope.managers },
		})
			.then(function (result) {
				if (result == "Assign") {
					showAlert('Manager assigned successfully');
				}
				else if (result == "Update") {
					showAlert('Manager updated successfully');
				}
				else if (result == "Cancelled") {
				
				}
				else {
					showAlert('Manager assigning/updation failed!!!');
				}
			});
	};
	$scope.getUnAssignedEmployees = function (action, userData) {
		$('#home').addClass('md-scroll-mask');
		userData.action = action;
		$mdDialog.show({
			controller: AddProjectController,
			templateUrl: 'templates/projectNotAssignedDetails.html',
			parent: angular.element(document.body),
			clickOutsideToClose: false,
			locals: { dataToPass: userData, gridOptionsData: $scope.gridOptions.data, managers: $scope.managers },
		})
			.then(function (result) {
				if (result == "Assign") {
					showAlert('Manager assigned successfully');
				}
				else if (result == "Update") {
					showAlert('Manager updated successfully');
				}
				else if (result == "Cancelled") {
	
				}
				else {
					showAlert('Manager assigning/updation failed!!!');
				}
			});
	};
	$scope.getAllocatedEmployees = function (action, userData) {
		$('#home').addClass('md-scroll-mask');
		userData.action = action;
		$mdDialog.show({
			controller: AddProjectController,
			templateUrl: 'templates/projectAssignedDetails.html',
			parent: angular.element(document.body),
			clickOutsideToClose: false,
			locals: { dataToPass: userData, gridOptionsData: $scope.gridOptions.data, managers: $scope.managers },
		})
			.then(function (result) {
				if (result == "Assign") {
					showAlert('Manager assigned successfully');
				}
				else if (result == "Update") {
					showAlert('Manager updated successfully');
				}
				else if (result == "Cancelled") {
				
				}
				else {
					showAlert('Manager assigning/updation failed!!!');
				}
			});
	};
	$scope.cancel = function () {
		$mdDialog.hide();
	};

	$scope.deleteRole = function (row) {
		$('#home').addClass('md-scroll-mask');
		var confirm = $mdDialog.confirm()
			.clickOutsideToClose(true)
			.textContent('Are you sure you want to delete this project?')
			.ok('Ok')
			.cancel('Cancel');
		$mdDialog.show(confirm).then(function () {
			deleteUserRole(row.entity.projectId);
			$timeout(function () { updateGridAfterDelete(row) }, 500);
		}, function () {
		
		});
	};

	function deleteUserRole(projectId) {
		var req = {
			method: 'DELETE',
			url: appConfig.appUri + "projects/" + projectId
		}
		$http(req).then(function mySuccess(response) {
			$scope.result = response.data;
		}, function myError(response) {
			$scope.result = "Error";
		});
	}

	function deleteTeam(record){
		var urlRequest  = "";
		var loginEmpId = myFactory.getEmpId();
		urlRequest = appConfig.appUri+ "resources?loginEmpId="+loginEmpId;
	var req = {
		method : 'DELETE',
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
	function updateGridAfterDelete(row) {
		if ($scope.result == "Success" || $scope.result == "") {
			var index = $scope.gridOptions.data.indexOf(row.entity);
			$scope.gridOptions.data.splice(index, 1);
			showAlert('Project deleted successfully');
		} else if ($scope.result == "Error") {
			showAlert('Something went wrong while deleting the role.')
		}
	}

	function AddProjectController($scope, $mdDialog, dataToPass, gridOptionsData, managers, $window, $mdSelect) {
		$scope.templateTitle = dataToPass.action;
		$scope.alertMsg = "";
		$scope.isDisabled = false;
		$scope.result = "";
		$scope.employee;
		$scope.projectModel;
		$scope.isSecondTab = false;
		$scope.prjctStses=["Active","Completed","On Hold","Proposed"];	
		$scope.billableStatuses = ["Billable", "Shadow", "Non-Billable", "Trainee"];
		$scope.allocationStatuses = ["Engaged" , "Proposed"];
//		$scope.empAllocationStatus = 'Engaged';
		$scope.employeeShifts = myFactory.getShifts();
		$scope.deliveryLeadIds = dataToPass.deliveryLeadIds;
		var allAccounts = myFactory.getAccounts();
		function getActiveAccounts(){
			var activeAccounts = [];
			for (var i in allAccounts){
				if(allAccounts[i].status == "Active" || allAccounts[i].status == "Y"){
					activeAccounts.push(allAccounts[i]);
				}
			}
			return activeAccounts;
		}
		$scope.accounts = getActiveAccounts();
		$scope.selectedTab = 0;
		$scope.deliveryLeadsSelectedList = [];
		$scope.employeeInTeam = [];
		$scope.employeeRoles = ["Lead","Employee"];
		$scope.accountId = dataToPass.accountId;
		$scope.domainId = dataToPass.domainId;
		$scope.domains = [];
		// code added

		$scope.searchTerm = "";
		$scope.searchAliasTerm = "";
		$scope.updateSearch = function (e) {
			e.stopPropagation();
		}
		$scope.searchFilter = function (obj) {
			if($scope.searchTerm.length > 3){
	    		var re = new RegExp($scope.searchTerm, 'i');
				return !$scope.searchTerm || re.test(obj.employeeName);
	    	}
	    		return obj;	
		};
		$scope.aliasFilter = function (obj) {
			 if($scope.searchAliasTerm.length > 3){
	    		var re = new RegExp($scope.searchAliasTerm, 'i');
				return !$scope.searchAliasTerm || re.test(obj.employeeName);
	    	}
	    		return obj;	
		};
		
		var today = new Date();
		$scope.addTab = function () {
			$scope.alertMsg ="";
			if(! (new Date(dataToPass.projectEndDate) >  today) ){
				$mdDialog.show($mdDialog.alert({
					skipHide: true,
					title:"Attention",
			        textContent: 'Employee cannot be added to Completed Project',
			        ok: 'ok'
			      }));
			}
			else{
				if($scope.isSecondTab === false){
					$scope.templateTitle = "Add";
					$scope.employeeRole = undefined;
					$scope.empBillableStatus = undefined;
					$scope.newBillingStartDate = undefined;
					$scope.employeeShift = undefined;
					$scope.empAllocationStatus = undefined;
					$scope.employeeModel = {};
					$scope.aliasModel = {};
					$scope.toggleAlias = false;
					$('.md-datepicker-input')[0].value = null;
					$('.md-datepicker-input')[1].value = null;
					$('.md-datepicker-input')[2].value = null;
					$scope.startDate = (dataToPass.projectStartDate == null) ? null : new Date(dataToPass.projectStartDate);
					$scope.endDate =   (dataToPass.projectStartDate == null) ? null : new Date(dataToPass.projectEndDate);
					document.getElementsByClassName("md-tab")[1].style.visibility = "visible" ;
					document.getElementsByClassName("md-tab")[2].style.visibility = "hidden" ;
					$scope.isSecondTab = true;
					$scope.selectedTab = 1; 
					$scope.searchTerm = "";				
				}
			}
			
		};		
		var removeTab = function (action) {
			if(action == "Add"){
				if($scope.isSecondTab === true){	
					$('.md-select-value')[0].childNodes[0].innerHTML = null
					$('.md-select-value')[1].childNodes[0].innerHTML = null
					$('.md-select-value')[2].childNodes[0].innerHTML = null
					$('.md-select-value')[3].childNodes[0].innerHTML = null

					$('.md-datepicker-input')[0].value = null;
					$('.md-datepicker-input')[1].value = null;
					$('.md-datepicker-input')[2].value = null;
					$scope.employeeModel = {};
					$scope.aliasModel = {};
					$scope.toggleAlias = false;
				}		
			}
			$scope.selectedTab = 0;
			$scope.templateTitle = 'View';
			$scope.alertMsg = '';
			document.getElementsByClassName("md-tab")[1].style.visibility = "hidden" ;
			document.getElementsByClassName("md-tab")[2].style.visibility = "hidden" ;
			$scope.isSecondTab = !$scope.isSecondTab;	
		};
		
		$window.addEventListener('click', function (e) {
		if (e.target.type !== 'search') {
			$mdSelect.hide();

		}

	});
		// code added

		$scope.clearSearchTerm = function () {


			// $("#lead .lead-search .md-select-value span .md-text").css({
			// '1px solid #000',
			// '1px',
			// }).append('<span class="glyphicon glyphicon-remove" style="margin-left: 5px;"></span>');
			// $(".leads-data").text($("#lead .lead-search .md-select-value span .md-text").text().append('<span class="glyphicon glyphicon-remove" style="margin-left: 5px;"></span>'));
		}

//		$scope.removeSelectedLead = function (item) {
//			var index = $scope.deliveryLeadsSelectedList.indexOf(item);
//			$scope.deliveryLeadsSelectedList.splice(index, 1);
//		}

		$scope.closeSelectBox = function () {
			$mdSelect.hide();
		}

		getSelectedDeliveryLeadIds = function () {
			var deliveryLeadIds = [];
			if($scope.deliveryLeadsSelectedList.name){
				managerIds.push($scope.deliveryLeadsSelectedList.id)
			}
			else{
				deliveryLeadIds.push($scope.deliveryLeadsSelectedList.employeeId)
			}
			
			return deliveryLeadIds;
		}
		
		
		$scope.getEmployeeDetails = function(){
			$http({
		        method : "GET",
		        url : appConfig.appUri + "employees/active/sortByName"
		    }).then(function mySuccess(response) {
		        $scope.employeeList =response.data;
		    }, function myError(response) {
		    	showAlert("Something went wrong while fetching data!!!");
		    	
		    });
		};
		$scope.getAllEmployees = function(){
			$http({
		        method : "GET",
		        url : appConfig.appUri + "employees/getAllEmployees"
		    }).then(function mySuccess(response) {
		        $scope.allEmployeesList =response.data.records;
		    }, function myError(response) {
		    	showAlert("Something went wrong while fetching data!!!");
		    });
		};
		function setDomain(action) {
			$scope.domains.forEach(function(domain){
				if(domain.domainId == dataToPass.domainId){
					$scope.domain = domain
				}
			})
			if($scope.domain.domainId != undefined){
				getManagersAssociatedToDomain($scope.domain.domainId,action);
			}
			else{
				$scope.managerDetails = managers;
			}
		}
		
		if (dataToPass.action == "Assign") {
			$scope.projectId = "";
			$scope.projectName = "";
			$scope.managerId = "";
			$scope.managerName = "";
			$scope.domains;
			$scope.isDisabled = false;
		} else if (dataToPass.action == "Update") {
			$scope.projectId = dataToPass.projectId;
			$scope.projectName = dataToPass.projectName;
			$scope.projectStartDate = (dataToPass.projectStartDate == null) ? null : new Date(dataToPass.projectStartDate);
			$scope.projectEndDate = (dataToPass.projectEndDate == null) ? null : new Date(dataToPass.projectEndDate);
//			$scope.managerId = dataToPass.managerId;
//			$scope.managerName = dataToPass.managerName;
//			var accounts1 = myFactory.getAccounts();
			for (var i = 0; i < $scope.accounts.length; i++) {
				if ($scope.accounts[i].accountName == dataToPass.account) {
					$scope.account = $scope.accounts[i];
				}
			}
			getDomainsAssoicatedToAccount($scope.account.accountId,dataToPass.action);
			$scope.projectStatus = dataToPass.status;
			$scope.managerModel = {
				'employeeName': dataToPass.managerName,
				'employeeId': dataToPass.managerId
			};
			
			$scope.isDisabled = true;
		} else if (dataToPass.action == "View") {
			$scope.projectId = dataToPass.projectId;
			$scope.projectName = dataToPass.projectName;
			$scope.account = dataToPass.account;
			$scope.projectStartDate = dataToPass.projectStartDate;
			$scope.projectEndDate = dataToPass.projectEndDate;
			$scope.managerIds=dataToPass.managerIds;
			$scope.projectStatus = dataToPass.status;
			$scope.domain = dataToPass.domain;
			$scope.managerModel = {
				 'employeeName': dataToPass.managerName,
				 'employeeId': dataToPass.managerId
			};
			$scope.projectModel = {
				 'projectName': dataToPass.projectName,
				 'projectId': dataToPass.projectId
		    };
			$scope.status = "Active";
			var employeeModel = $scope.employeeModel;
			var aliasModel = $scope.aliasModel;
			var getCellTemplate1 = '<p class="col-lg-12"><i ng-show="grid.appScope.showEditIcon(row.entity)" class="fa fa-pencil-square-o fa-2x" data-placement="center" title="Edit" onmouseenter="$(this).tooltip(\'show\')" aria-hidden="true" style="font-size:1.5em;margin-top:3px;cursor:pointer;" ng-click="grid.appScope.edit(row)"></i>'+
			'<i ng-show="row.entity.editrow" class="fa fa-save fa-2x" aria-hidden="true" style="font-size:1.5em;margin-top:3px;cursor:pointer;" data-placement="center" title="Save" onmouseenter="$(this).tooltip(\'show\')" ng-click="grid.appScope.saveRow(row.entity,\'Update\')"></i>'
			+'&nbsp;&nbsp;&nbsp;<i ng-show="row.entity.editrow" id="cancelEdit" class="fa fa-times fa-2x" aria-hidden="true" style="font-size:1.5em;margin-top:3px;cursor:pointer;" data-placement="center" title="Cancel" onmouseenter="$(this).tooltip(\'show\')" ng-click="grid.appScope.cancelEdit(row.entity)"></i>'
			+'<i ng-show="grid.appScope.showMoveToBenchIcon(row.entity) && !row.entity.editrow" id="moveResource" class="fa fa-arrow-circle-right fa-2x" aria-hidden="true" style="font-size:1.8em;margin-top:3px;cursor:pointer;" data-placement="center" title="Release" onmouseenter="$(this).tooltip(\'show\')" ng-click="grid.appScope.moveToBench(row.entity)"></i>'+
			'<i ng-show="grid.appScope.showMoveToEngageIcon(row.entity)" id="moveToProject" class="fa fa-arrow-circle-left fa-2x" aria-hidden="true" style="font-size:1.8em;margin-top:3px;cursor:pointer;" data-placement="center" title="Engage" onmouseenter="$(this).tooltip(\'show\')" ng-click="grid.appScope.changeAllocationStatusToEngaged(row.entity)"></i>'+
			'&nbsp;&nbsp;&nbsp;<i ng-show="row.entity.status == \'Proposed\' && !row.entity.editrow" id="deleteResource" class="fa fa-trash fa-2x" aria-hidden="true" style="font-size:1.8em;margin-top:3px;cursor:pointer;" data-placement="center" title="Delete" onmouseenter="$(this).tooltip(\'show\')" ng-click="grid.appScope.deleteProposedResource(row.entity)"></i></p>';
			//'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i class="fa fa-minus-circle fa-2x" aria-hidden="true" style="font-size:1.5em;margin-top:3px;cursor:pointer;" ng-click="grid.appScope.getRowData(row,\'Delete\')"></i></p>';
			
			var getCellActiveTemplate = '<div ng-show="COL_FIELD==true"><p class="col-lg-12">Y</P></div><div ng-show="COL_FIELD==false"><p class="col-lg-12">N</p></div>';
		   
			var onBehalfOfCellTemplate = '<div class="ui-grid-cell-contents" ng-if="!row.entity.editrow">{{COL_FIELD}}</div><div ng-if="row.entity.editrow" class="grid-Dropdown">'+
	        '<md-select style="margin-right:10px;" ng-model="MODEL_COL_FIELD" data-md-container-class="selectHeader" id="selectEmp" name="employeeModel" placeholder="Add Resource">'+
            	'<md-select-header class="selectHeaderChild header-spacing" layout="column">'+
            		'<input ng-model="grid.appScope.searchTerm" type="search" id="search" ng-keydown="grid.appScope.updateSearch($event)" ng-model-options="{debounce: {\'default\': 500, \'blur\': 0}}"'+
                'placeholder="Search for employee" class="searchBoxHeader demo-header-searchbox md-text search-spacingleft"/>'+
                '<span class="glyphicon glyphicon-remove close-mdselect" ng-click="grid.appScope.closeSelectBox()"></span>'+
               '</md-select-header>'+
               '<md-optgroup label="Employee" class="optionScroll">'+
               		'<md-option value= "">None</md-option>'+
               		'<md-option value="{{employee.employeeName}}" ng-repeat="employee in grid.appScope.allEmployeesList | filter:grid.appScope.searchFilter">{{employee.employeeName}}</md-option>'+
               	'</md-optgroup>'+
         '</md-select></div>';
			
			$scope.gridOptions = {
				paginationPageSizes: [10, 20, 30, 40, 50, 100],
				paginationPageSize: 10,
				pageNumber: 1,
				pageSize: 10,
				enableFiltering:true,
				enableCellEdit:false,
				enableRowSelection: true,
				rowEditWaitInterval: 0,
				columnDefs: [
					{ name: 'employeeId', displayName: 'Emp ID', enableColumnMenu: true, enableSorting: true, enableFiltering:true,width:"*"  },
					{ name: 'employeeName', displayName: 'Emp Name',width:"*", enableSorting: false, enableColumnMenu: false ,enableFiltering:true,sort: {
				          direction: uiGridConstants.ASC,
				          priority: 0,
				        } },
				    { field: 'onBehalfOf', displayName: 'On Behalf Of', enableColumnMenu: true, enableSorting: true, enableFiltering:true,width:"*", 
				        cellTemplate:onBehalfOfCellTemplate
				    },
					{
						name: 'resourceRole', displayName:'Role', enableColumnMenu: false,width:100, enableSorting: false,enableFiltering:false,
						cellTemplate:'<div class="ui-grid-cell-contents"  ng-if="!row.entity.editrow">{{COL_FIELD}}</div><div ng-if="row.entity.editrow" class="grid-Dropdown"><md-select ng-model="MODEL_COL_FIELD" id="empRole">'
                            +'<md-optgroup label="employee role"><md-option ng-value="empRole" ng-repeat="empRole in col.colDef.editDropdownOptionsArray">{{empRole}}</md-option>'
                    +'</md-optgroup></md-select></div>',
						editDropdownOptionsArray: [
                          'Individual Contributor' ,
                         'Lead' ]
					
					},
					{ field: 'designation',width:'*', displayName: 'Designation', enableColumnMenu: false, enableSorting: false,enableFiltering:false },
    					{
                        	name: 'billableStatus', displayName: 'Billability ', enableColumnMenu: false, enableSorting: false ,enableFiltering:false,width:'*',
                        	cellTemplate: '<div class="ui-grid-cell-contents" ng-if="!row.entity.editrow">{{COL_FIELD}}</div><div ng-if="row.entity.editrow" class="grid-Dropdown"><md-select ng-model="MODEL_COL_FIELD" name="empBillableStatus" append-to-body="true">'
                               + '<md-optgroup label="billable statuses "><md-option ng-value="billableStatus " ng-repeat="billableStatus in col.colDef.editDropdownOptionsArray">{{billableStatus}}</md-option>'
                               +'</md-optgroup></md-select></div>',editDropdownOptionsArray: [
                                    'Billable' ,
                                   'Shadow' ,
                                   'Non-Billable',
                                  'Reserved' ,
                                   'Trainee' ]
                        	},
					{ name: 'billingStartDate',width:"*", displayName: 'Start Date', enableColumnMenu: false, enableSorting: false , cellFilter:'date:"dd-MMM-yyyy"',enableFiltering:false,
                       cellTemplate: '<div class="ui-grid-cell-contents"  ng-if="!row.entity.editrow">{{COL_FIELD | date:"dd-MMM-yyyy"}}</div><div ng-if="row.entity.editrow"><md-datepicker ng-model="MODEL_COL_FIELD" md-placeholder="Select Date" id="newBillingStartDate" name="newBillingStartDate" md-min-date="grid.appScope.minBillingStartDate" md-max-date="grid.appScope.maxBillingStartDate"></md-datepicker></div>'},
					{ field: 'billingEndDate',width:"*", displayName: 'End Date', enableColumnMenu: false, enableSorting: false , cellFilter:'date:"dd-MMM-yyyy"',enableFiltering:false,
                       cellTemplate: '<div class="ui-grid-cell-contents"  ng-if="!row.entity.editrow">{{COL_FIELD | date:"dd-MMM-yyyy"}}</div><div ng-if="row.entity.editrow"><md-datepicker ng-model="MODEL_COL_FIELD" md-placeholder="Select Date" id="endDate" name="endDate" md-min-date="row.entity.billingStartDate" md-max-date="grid.appScope.maxBillingEndDate"></md-datepicker></div>'},
					{name : 'Actions',width:140, displayName: 'Actions',cellTemplate: getCellTemplate1, enableColumnMenu: false, enableSorting: false,enableFiltering: false} 
				]
			};
			if($scope.isSecondTab){
				$scope.gridOptions.columnDefs[5].visible = false;
			}
			
			$http({
				method: "GET",
				url: appConfig.appUri + "resources/project/"+ $scope.projectId + "?status=" + $scope.status
			}).then(function mySuccess(response) {
				$scope.gridOptions.data = response.data.records;
				  for(i=0;i<response.data.records.length;i++){
			    		if(response.data.records[i].resourceRole == 'Lead'){
				    		  $scope.gridOptions.data[i].resourceRole = "Lead";
				    	  }else{
				    		  $scope.gridOptions.data[i].resourceRole = "Individual Contributor"; 
				    	  }	
			    	}
				if(response.data.records.length >= 9){
		    		$scope.gridOptions.enablePaginationControls = true;
		    	}
		    	else{
		    		$scope.gridOptions.enablePaginationControls = false;
		    	}

			}, function myError(response) {
				showAlert("Something went wrong while fetching data!!!");
				$scope.gridOptions.data = [];
			});
			
			$scope.showMoveToBenchIcon = function(row){
				if(row.billingEndDate < new Date() && row.status == 'Engaged'){
					return true;
				}
			}
			$scope.showEditIcon = function(row) {
				if(row.status !="Released" && !row.editrow ) {
					return true;
				}	
			}
			$scope.showMoveToEngageIcon = function(row){			
				 var today = new Date();
				 today.setHours(0, 0, 0, 0);
				if(row.status =="Proposed" && !row.editrow && today >= new Date(row.billingStartDate)) {
					return true;
				}

			}
			$scope.getEmp = function (empObj) {
				console.log(empObj)
			}
			
			$scope.parentData = {
					"employeeId":"",
					"projectId": "",
					"projectName": "",
					"account": "",
					"status": "",
					"action": "",
					"managerIds":[],
					"accountId":"",
					"domainId":"",
					"employeeId": "",
					"employeeName": "",
					"emailId":"",
					"role": "",
					"shift": "",
					"projectId":"",
					"projectName":"",
					"managerId":"",
					"managerName":"",
					"experience":"",
					"designation":"",
					"billableStatus":"",
					"mobileNumber":"",
					"startDate":"",
					"endDate":"",
					"newBillingStartDate":"",
					"empAllocationStatus" : "",
					"empBillableStatuses":[],
					"onBehalfOf":""
				};
			

			
		    $scope.edit = function (rowd) {
		    	$scope.searchTerm = "";
		    	$scope.minBillingStartDate = new Date($scope.projectStartDate);
		    	$scope.maxBillingStartDate = new Date($scope.projectEndDate);
		    	$scope.maxBillingEndDate = new Date($scope.projectEndDate);
		    	var row = rowd.entity;
		        var index = $scope.gridOptions.data.indexOf(row);
		        if($scope.previousRow){
		        	$scope.rowCompare(rowd);
		        }
		        $scope.previousRow = angular.copy(row);
		        $scope.previousRowIndex = index;
		       $scope.gridOptions.enableCellEdit = true;
		        for(var i =0; i<$scope.gridOptions.data.length; i++){
		        	if(i==index){
		        		$scope.gridOptions.data[i].editrow = !$scope.gridOptions.data[i].editrow;
		        	}
		        	else{
		        		$scope.gridOptions.data[i].editrow = false;
		        	}
		        }
		        
		    	row.billingStartDate = new Date(row.billingStartDate);
		    	row.billingEndDate = new Date(row.billingEndDate);
		    	
		        $scope.parentData.employeeId = row.employeeId;
				$scope.parentData.employeeName = row.employeeName;
				$scope.parentData.emailId = row.emailId;
				$scope.parentData.id = row.id;
				$scope.parentData.role = row.resourceRole;
				$scope.parentData.projectId = row.projectId;
				$scope.parentData.projectName = row.projectName;
				$scope.parentData.designation = row.designation;
				$scope.parentData.billableStatus = row.billableStatus;
				$scope.parentData.onBehalfOf = row.onBehalfOf;
				$scope.parentData.employeeModel = {
						'employeeName': row.employeeName,
						'employeeId': row.employeeId,
						'emailId':row.emailId,
						'designation': row.designation
					};
				$scope.parentData.onBehalfOf = row.onBehalfOf;
				$scope.parentData.newBillingStartDate = row.billingStartDate;
				$scope.parentData.endDate = row.billingEndDate;
				$scope.parentData.empAllocationStatus = row.status;
		    };
		    
		    $scope.rowCompare = function(row) {
		    	var allRows = row.grid.rows;
		    	for(var i=0; i<allRows.length; i++) {
		    		var newIndex = $scope.gridOptions.data.indexOf(allRows[i].entity)
		    		if(newIndex == $scope.previousRowIndex && $scope.previousRow.status == "Engaged" && allRows[i].entity.status == "Engaged"){
		    			 allRows[i].entity.onBehalfOf = $scope.previousRow.onBehalfOf;
				    	 allRows[i].entity.resourceRole = $scope.previousRow.resourceRole;
				    	 allRows[i].entity.billableStatus = $scope.previousRow.billableStatus;
				    	 allRows[i].entity.billingStartDate = $scope.previousRow.billingStartDate;
				    	 allRows[i].entity.billingEndDate = $scope.previousRow.billingEndDate;
				    	 $scope.previousRow = angular.copy(row.entity);
		    		}
		    		else if(newIndex == $scope.previousRowIndex && $scope.previousRow.status == "Proposed" && allRows[i].entity.status == "Proposed"){
		    			 allRows[i].entity.onBehalfOf = $scope.previousRow.onBehalfOf; 
		    			 allRows[i].entity.resourceRole = $scope.previousRow.resourceRole;
				    	 allRows[i].entity.billableStatus = $scope.previousRow.billableStatus;
				    	 allRows[i].entity.billingStartDate = $scope.previousRow.billingStartDate;
				    	 allRows[i].entity.billingEndDate = $scope.previousRow.billingEndDate;
				    	 $scope.previousRow = angular.copy(row.entity);
		    		}
		    	}
		    }
		    
		    $scope.moveToBench = function (row) {
		    	row.resourceRole == 'Individual Contributor' ? row.resourceRole = 'Employee' : row.resourceRole;
		    	var record = {"id":row.id,"employeeId":row.employeeId,"projectId":row.projectId,"billableStatus":row.billableStatus,"billingEndDate":row.billingEndDate,"resourceRole":row.resourceRole,"billingStartDate":row.billingStartDate};
		    	var urlRequest  = "";
				var loginEmpId = myFactory.getEmpId();
				urlRequest = appConfig.appUri+ "resources/moveToOpenPool?loginEmpId="+loginEmpId;
				var req = {
					method : 'PUT',
					url : urlRequest,
					headers : {
						"Content-type" : "application/json"
					},
					data : record
				}
			$http(req).then(function mySuccess(response) {
			$scope.result = "Success";
			if((response.data.message == "Resource is moved to Bench Successfully") || (response.data.message == "Resource Released successfully")){
				$timeout(function () {
					getProjectDetails($scope.projectId, $scope.status);
				}, 500);
				$mdDialog.show($mdDialog.alert({
					skipHide: true,
					textContent: response.data.message ,
					ok: 'ok'
				})).then(function () {
					$scope.myForm.$setPristine();
				})	
			}else{
				$scope.alertMsg=response.data.message;
		    	   if($scope.alertMsg && $scope.alertMsg != ""){
		    		   row.billingStartDate = $scope.parentData.newBillingStartDate;
		    		   row.billingEndDate = $scope.parentData.endDate;
		    		   row.resourceRole = $scope.parentData.role;
		    		   row.billableStatus = $scope.parentData.billableStatus;
		    	   }
		    	   $scope.previousRow = angular.copy(row);
		   }
		
		}, function myError(response){
				$scope.result = "Error";
	});
		    	
} 
		    
		    $scope.changeAllocationStatusToEngaged = function (row) {
		    	if(row.billableStatus == "Reserved"){
		    		$scope.alertMsg = 'Please Update Your Billability Status From Reserved and then Move to Engage';
		    	}else {
		    		var onBehalfOfEmp;
		    		var onBehalfOfEmpId;
		    		row.resourceRole == 'Individual Contributor' ? row.resourceRole = 'Employee' : row.resourceRole;
		    		$scope.alertMsg ='';
		    		if(row.onBehalfOf != "" && row.onBehalfOf != null) {
		    			onBehalfOfEmp = $scope.getOnBehalfOfEmpId(row.onBehalfOf);
		    			onBehalfOfEmpId = onBehalfOfEmp[0].employeeId;
		    		}
		    		else {
		    			onBehalfOfEmpId = null;	
		    		}
			    	var loginEmpId = myFactory.getEmpId();
			    	var record = {"id":row.id,"employeeId":row.employeeId,"projectId":row.projectId,"onBehalfOf":onBehalfOfEmpId,"billableStatus":row.billableStatus,"billingEndDate":row.billingEndDate,"resourceRole":row.resourceRole,"billingStartDate":row.billingStartDate,"status":"Engaged"};
			    	var urlRequest = appConfig.appUri+ "resources?loginEmpId="+loginEmpId;
					var req = {
						method : 'PUT',
						url : urlRequest,
						headers : {
							"Content-type" : "application/json"
						},
						data : record
					}
				$http(req).then(function mySuccess(response) {
				$scope.result = "Success";
				if(response.data.message == "Resource updated successfully"){
					$timeout(function () {
						getProjectDetails($scope.projectId, $scope.status);
					}, 500);
					$mdDialog.show($mdDialog.alert({
						skipHide: true,
						textContent: response.data.message ,
						ok: 'ok'
					})).then(function () {
						$scope.myForm.$setPristine();
					})
				}else{
					$scope.alertMsg=response.data.message;
			    	   if($scope.alertMsg && $scope.alertMsg != ""){
			    		   row.billingStartDate = $scope.parentData.newBillingStartDate;
			    		   row.billingEndDate = $scope.parentData.endDate;
			    		   row.resourceRole = $scope.parentData.role;
			    		   row.billableStatus = $scope.parentData.billableStatus;
			    	   }
			    	   $scope.previousRow = angular.copy(row);
				}
		
			}, function myError(response){
					$scope.result = "Error";
				});
		    	}

		    	
		    } 
		     
		    $scope.deleteProposedResource = function (row) {
				$scope.id = row.id;
				$scope.employeeId = row.employeeId;
				$scope.projectId = row.projectId;
				var record = {"id":$scope.id, "employeeId":$scope.employeeId, "projectId":$scope.projectId }
				$mdDialog.show($mdDialog.confirm({
					skipHide: true,
					textContent: 'Are you sure you want to delete this teamMate?',
					ok: 'ok',
					cancel:'cancel'
				  })).then(function(){
					  deleteTeam(record);
					  $timeout(function(){
						  getProjectDetails($scope.projectId);
						$mdDialog.show($mdDialog.alert({
								skipHide: true,
						        textContent: 'Deleted resource successfully!',
						        ok: 'ok'
						      }));
						},500);
			    })
			};
		    
		    
		    $scope.cancelEdit = function (row) {
		    	$scope.searchTerm = "";
		        var index = $scope.gridOptions.data.indexOf(row);
		        $scope.gridOptions.data[index].editrow = false;
		           row.onBehalfOf = $scope.parentData.onBehalfOf;
	    		   row.billingStartDate = $scope.parentData.newBillingStartDate;
	    		   row.billingEndDate = $scope.parentData.endDate;
	    		   row.resourceRole = $scope.parentData.role;
	    		   row.billableStatus = $scope.parentData.billableStatus;
		    };
		    $scope.saveRow = function (row,action) {
		    	$scope.searchTerm = "";
		        var index = $scope.gridOptions.data.indexOf(row);
		        $scope.gridOptions.data[index].editrow = false;
				$scope.projectId = row.projectId;
				$scope.projectName =  row.projectName;
				$scope.account = row.account;
				$scope.managerId = row.managerId
				$scope.managerName = $scope.managerName ;
				$scope.employeeModel = {
						'employeeName': row.employeeName,
						'employeeId': row.employeeId,
						'emailId':row.emailId,
						'designation': row.designation
					};
				$scope.newBillingStartDate = row.billingStartDate;
				$scope.endDate = row.billingEndDate;
				$scope.employeeRole = row.resourceRole;
				$scope.empBillableStatus = row.billableStatus;
				$scope.empAllocationStatus = row.status;
				$scope.onBehalfOfEmpName = row.onBehalfOf;
				$scope.id = row.id;
		       if(action == 'Update'){
		    	   $scope.validateFields(action,row);
		    	   if($scope.alertMsg != ""){
		    		   row.onBehalfOf = $scope.parentData.onBehalfOf;
		    		   row.billingStartDate = $scope.parentData.newBillingStartDate;
		    		   row.billingEndDate = $scope.parentData.endDate;
		    		   row.resourceRole = $scope.parentData.role;
		    		   row.billableStatus = $scope.parentData.billableStatus;
		    	   }
		    	   $scope.previousRow = angular.copy(row);
		       }
		    }
		//	$scope.getRowData = function(row, action){
			//	$scope.isEditable = true;
				
/*				$scope.showUpdateBtn = false;
				$scope.showEditDisplay = false;
				$scope.parentData.empBillableStatuses = [{billableStatus:'Billable' , billableStartDate : 1558031400000, billableEndDate : 1558031400001},{billableStatus:'Shadow' , billableStartDate : 1558031400001, billableEndDate : 1577125800001}];
				$scope.parentData.employeeId = row.entity.employeeId;
				$scope.parentData.employeeName = row.entity.employeeName;
				$scope.parentData.emailId = row.entity.emailId;
				$scope.parentData.id = row.entity.id;
				$scope.parentData.role = row.entity.resourceRole;
				//$scope.parentData.shift = row.entity.shift;
				$scope.parentData.projectId = row.entity.projectId;
				$scope.parentData.projectName = row.entity.projectName;
				//$scope.parentData.experience = row.entity.experience;
				$scope.parentData.designation = row.entity.designation;
				$scope.parentData.billableStatus = row.entity.billableStatus;
				//$scope.parentData.shift = row.entity.shift;
				//$scope.parentData.mobileNumber = row.entity.mobileNumber;
				$scope.parentData.employeeModel = {};
				//$scope.parentData.startDate = new Date(row.entity.startDate);
				$scope.parentData.newBillingStartDate = new Date(row.entity.billingStartDate);
				$scope.parentData.endDate = new Date(dataToPass.projectEndDate);
				console.log($scope.parentData)
				if(action == "UpdateTeam"){
					$scope.isEdit = true;
					//$scope.updateTeamMate(action, $scope.parentData);
				}
				if(action == "Delete"){
					$scope.deleteTeamMate(row);
					
				}*/
//				else if(action == "Delete"){
//					$scope.deleteRole(row,$scope.parentData.id);
//			     }else if(action=="ViewTeamDetail"){
//			    	 
//			    	 $scope.viewTeamDetail(action, $scope.parentData);
//			    		
//			     }else if(action=="ViewBillability"){
//			    	 
//			    	 $scope.ViewBillability(action, $scope.parentData);
//			    		
//			     }	
		//}
			document.addEventListener("DOMSubtreeModified", function(e) {
				if(document.getElementsByClassName("md-tab")[1] && (document.getElementsByClassName("md-tab")[1].style.visibility === "")){
					document.getElementsByClassName("md-tab")[1].style.visibility = "hidden" ;
				}
				if(document.getElementsByClassName("md-tab")[2] && (document.getElementsByClassName("md-tab")[2].style.visibility === "")){
					document.getElementsByClassName("md-tab")[2].style.visibility = "hidden" ;
				}
			}, false);
			
			$scope.updateTab = function () {
			//	if($scope.isSecondTab == false){
				    $scope.isSecondTab = !$scope.isSecondTab;
					$scope.templateTitle = "Update";
					document.getElementsByClassName("md-tab")[1].style.visibility = "hidden" ;
					document.getElementsByClassName("md-tab")[2].style.visibility = "visible" ;
					$scope.selectedTab = 2; 
					$scope.searchTerm = "";
					$scope.alertMsg = "";
//				}	
			};
			
			$scope.getTeamMates = function(){
				getProjectDetails($scope.projectId,$scope.status);
				$scope.alertMsg = '';
			}
/*			$scope.updateTeamMate = function(action, userData){
				$scope.updateTab();
				$scope.id = userData.id;
				$('.md-datepicker-input')[2].value = null;
				$scope.employeeModel = {
						'employeeName': userData.employeeName,
						'employeeId': userData.employeeId,
						'emailId':userData.emailId,
						'designation': userData.designation
					};
				$scope.empBillableStatuses = userData.empBillableStatuses;
					
				$scope.employeeModel.employeeId = userData.employeeId;
				$scope.employeeModel.employeeName = userData.employeeName;
				$scope.employeeModel.emailId = userData.emailId;
				$scope.empBillableStatus =null;
				if(userData.role == "Individual Contributor"){
					$scope.employeeRole = "Employee";
				}else{
					$scope.employeeRole = "Lead";
				}
				console.log($scope.employeeRole)
				//$scope.employeeRole = userData.role;
				//$scope.employeeShift = userData.shift;
				$scope.newBillingStartDate = null;
				//$scope.startDate = new Date(userData.startDate);
				$scope.endDate = new Date(userData.endDate);
				//$scope.addTab();
				//$('#home').addClass('md-scroll-mask');
				userData.action = action;
				$mdDialog.show({
				      controller: AddProjectController,
				      templateUrl: 'templates/UpdateTeamMate.html',
				      parent: angular.element(document.body),
				      clickOutsideToClose:false,
				      locals:{dataToPass: userData, gridOptionsData: $scope.gridOptions.data, employees: $scope.employees},
				    })
				    .then(function(result) {
				    	if(result == "Add") showAlert('New Teammate assigned successfully');
				    	else if(result == "Update") {
				    		$scope.refreshPage();
				    		showAlert('Teammate updated successfully');
				    	
				    	}
				    	else if(result == "Cancelled") {}
				    	else showAlert('Teammate assigning/updation failed!!!');
  		    });
			};*/
			$scope.deleteTeamMate = function (row) {
				$scope.id = row.entity.id;
				$scope.employeeId = row.entity.employeeId;
				$scope.projectId = row.entity.projectId;
				var record = {"id":$scope.id, "employeeId":$scope.employeeId, "projectId":$scope.projectId }
				$mdDialog.show($mdDialog.confirm({
					skipHide: true,
					textContent: 'Are you sure you want to inactivate this teamMate?',
					ok: 'ok',
					cancel:'cancel'
				  })).then(function(){
					  deleteTeam(record);
					  $timeout(function(){
						  getProjectDetails($scope.projectId,$scope.status);
						$mdDialog.show($mdDialog.alert({
								skipHide: true,
						        textContent: 'Resource deleted successfully!!!',
						        ok: 'ok'
						      }));
						},500);
			    })
			};
		}else if (dataToPass.action == "UnAssigned") {
			$scope.gridOptions = {
				paginationPageSizes: [10, 20, 30, 40, 50, 100],
				paginationPageSize: 10,
				pageNumber: 1,
				pageSize: 10,
				columnDefs: [
					{ field: 'employeeId', displayName: 'Emp ID', enableColumnMenu: true, enableSorting: true, width: 100 },
					{ field: 'employeeName', displayName: 'Empl Name ', enableColumnMenu: false, enableSorting: false },
					{ field: 'emailId', displayName: 'Email Id ', enableColumnMenu: false, enableSorting: false },
				]
			};
			$scope.isDisabled = true;
			$http({
				method: "GET",
				url: appConfig.appUri + "resources/unAssignedEmployees"
			}).then(function mySuccess(response) {
				$scope.gridOptions.data = response.data.records;
			}, function myError(response) {
				showAlert("Something went wrong while fetching data!!!");
				$scope.gridOptions.data = [];
			});
		} else if (dataToPass.action == "allocated") {
			var getCellTemplate = '<p class="col-lg-12"><i class="fa fa-pencil-square-o fa-2x" aria-hidden="true" style="font-size:1.5em;margin-top:3px;cursor:pointer;" ng-click="grid.appScope.getRowData(row,\'Update\')"></i>'+
			'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i class="fa fa-minus-circle fa-2x" aria-hidden="true" style="font-size:1.5em;margin-top:3px;cursor:pointer;" ng-click="grid.appScope.getRowData(row,\'Delete\')"></i></p>';
			
			$scope.gridOptions = {
				paginationPageSizes: [10, 20, 30, 40, 50, 100],
				paginationPageSize: 10,
				pageNumber: 1,
				pageSize: 10,
				columnDefs: [
					{ field: 'employeeId', displayName: 'Emp ID', enableColumnMenu: true, enableSorting: true, width: 100 },
					{ field: 'employeeName', displayName: 'Empl Name ', enableColumnMenu: false, enableSorting: false },
					{ field: 'emailId', displayName: 'Email Id ', enableColumnMenu: false, enableSorting: false },
					{ field: 'projectName', displayName: 'Project ', enableColumnMenu: false, enableSorting: false },
					{ field: 'managerName', displayName: 'Manager ', enableColumnMenu: false, enableSorting: false },
					{ field: 'experience', displayName: 'Exp', enableColumnMenu: true, enableSorting: true, width: 50 },
					{ field: 'designation', displayName: 'Designation ', enableColumnMenu: false, enableSorting: false },
					{ field: 'billableStatus', displayName: 'Billability ', enableColumnMenu: false, enableSorting: false },
				
				],
				enableGridMenu: true,
				enableSelectAll: true,
				exporterMenuExcel: false,
				exporterMenuCsv: false,
				exporterCsvFilename: 'Allocated.csv',
				exporterExcelFilename: 'AllocatedResources',
				exporterPdfDefaultStyle: { fontSize: 9 },
				exporterPdfTableStyle: { margin: [30, 30, 30, 30] },
				exporterPdfTableHeaderStyle: { fontSize: 10, bold: true, italics: true, color: 'red' },
				exporterPdfHeader: { text: "Allocated Resources", style: 'headerStyle' },
				exporterPdfFooter: function (currentPage, pageCount) {
					return { text: currentPage.toString() + ' of ' + pageCount.toString(), style: 'footerStyle' };
				},
				exporterPdfCustomFormatter: function (docDefinition) {
					docDefinition.styles.headerStyle = { fontSize: 22, bold: true };
					docDefinition.styles.footerStyle = { fontSize: 10, bold: true };
					return docDefinition;
				},
				exporterPdfOrientation: 'portrait',
				exporterPdfPageSize: 'LETTER',
				exporterPdfMaxGridWidth: 500,
				exporterCsvLinkElement: angular.element(document.querySelectorAll(".custom-csv-link-location")),
				onRegisterApi: function (gridApi) {
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
			$scope.isDisabled = true;
			$http({
				method: "GET",
				url: appConfig.appUri + "resources/projects"
			}).then(function mySuccess(response) {
				$scope.gridOptions.data = response.data.records;
			}, function myError(response) {
				showAlert("Something went wrong while fetching data!!!");
				$scope.gridOptions.data = [];
			});
		}
		$scope.getManagers = function () {
			if ($scope.managerModel !== undefined) {
				return $scope.managerModel.employeeName;
			} else {
				return "Please select a manager";
			}
		};
		$scope.getProjectStatus = function () {
			if ($scope.projectStatus !== undefined) {
				return $scope.projectStatus;
			} else {
				return "Please select project status";
			}
		};
		$scope.changeEmpBillableStatus = function(){
//			$scope.newBillingStartDate = null;
			console.log("hi");
			
			if($scope.templateTitle == "Add"){
				$('.md-datepicker-input')[0].value = null;
			}
  		  else{
  			  $scope.endDate =  new Date(dataToPass.projectEndDate);
				$('.md-datepicker-input')[3].value = new Date(dataToPass.projectEndDate);
			}
		} 
		$scope.changeCheckboxValue = function(checkboxValue){
			if(checkboxValue ==false){
				$scope.aliasModel ={};
			}
		};
		$scope.changeBillableStatusList = function() {
			if($scope.empAllocationStatus == 'Proposed') {
				
				$scope.empBillableStatus = 'Reserved' ;
				$scope.billableStatuses = ["Reserved"];
				$scope.newBillingStartDate = null;
				$('.md-datepicker-input')[0].value = null;
				$scope.minBillingStartDate = new Date();
				$scope.maxBillingStartDate = new Date($scope.projectEndDate);
				$scope.maxBillingEndDate = new Date($scope.projectEndDate)
				}
			else if ($scope.empAllocationStatus == 'Engaged'){
				$scope.empBillableStatus = null ; //$scope.billableStatuses ;
				$scope.billableStatuses = ["Billable", "Shadow", "Non-Billable", "Trainee"];
				$scope.minBillingStartDate = new Date($scope.projectStartDate);
				$scope.maxBillingStartDate =new Date();
				$scope.maxBillingEndDate = new Date($scope.projectEndDate);
			}
		}
/*		$scope.getEditTeammate = function(selectedStatus) {
			if(selectedStatus) {
			$scope.showEditDisplay = true;
			$scope.showUpdateBtn = true;
			$scope.empBillableStatus = selectedStatus.billableStatus;
			$scope.newBillingStartDate = new Date(selectedStatus.billableStartDate);
			$scope.endDate = new Date(selectedStatus.billableEndDate);
			}
			
		}*/
		
		var noDomainError = function(){
			$mdDialog.show($mdDialog.alert({
				title: 'Error',
				skipHide: true,
		        textContent: 'No domain added to this account',
		        ok: 'ok'
		      }));
		};
		
		function getDomainsAssoicatedToAccount(id,action){
			$http({
		        method : "GET",
		        url : appConfig.appUri + "domains/"+id
		    }).then(function mySuccess(response) {
		    	var result = response.data;
		    	if(result != "" || result.length != 0){
		    		$scope.domains  = result;
		    	}
		    	else{
//		    		$scope.domains  = [
//		    			{domainName: $scope.account.accountName}
//		    		]
		    		noDomainError();
		    		$scope.domains = [];
		    		$scope.managerDetails = [];
		    	}
		    	if(action == "Update"){
		    		setDomain(action);
		    	}
		    	
		    }, function myError(response) {

		    });
		}
		
		$scope.onAccountChange = function (){
			if ($scope.account !== undefined) {
				getDomainsAssoicatedToAccount($scope.account.accountId);
			}
			$scope.domain = undefined;
			$scope.domains = [];
			$scope.deliveryLeadsSelectedList = {};
			$scope.managerDetails = [];
			$scope.projectStatus = undefined;
		}
		
		$scope.onDomainChange = function (newVal, oldVal){
			
			if(oldVal != ""){
				if(newVal.domainName != (JSON.parse(oldVal)).domainName){
					if($scope.domain.domainId != undefined){
						getManagersAssociatedToDomain($scope.domain.domainId);
					}
					$scope.deliveryLeadsSelectedList = {};
					$scope.managerDetails = [];
					$scope.projectStatus = undefined;	
			}
			}
			else{
				if($scope.domain.domainId != undefined){
					getManagersAssociatedToDomain($scope.domain.domainId);
				}
				$scope.deliveryLeadsSelectedList = {};
				$scope.managerDetails = [];
				$scope.projectStatus = undefined;
			}
					
		}
		
		function setDeliveryLead(){
			$scope.managerDetails.forEach(function(deliveryLead){
				if(deliveryLead.employeeId == dataToPass.deliveryLeadIds[0].employeeId){
					$scope.deliveryLeadsSelectedList = deliveryLead;
				}
			});
		}
		
		$scope.getAccountText = function () {
			if ($scope.account !== undefined) {
				return $scope.account.accountName;
			} else {
				return "Please select account";
			}
		};
		
		$scope.getDomainText = function () {
			if($scope.domain != undefined){
				return $scope.domain.domainName;
			}
			else{
	    		return "Please select domain";
	    	}
			
		};
		
		$scope.getSelectedDeliveryLead = function () {		
			if (Object.keys($scope.deliveryLeadsSelectedList).length > 1 ) {
					return $scope.deliveryLeadsSelectedList.employeeName;
			} else {
					return "Please select a Delivery lead";
			}
		}
		
		
		function getManagersAssociatedToDomain(id,action){
			$http({
		        method : "GET",
		        url : appConfig.appUri + "employees/deliveryLeads/"+id
		    }).then(function mySuccess(response) {
		    	var result = response.data.records;
		    	if(result != "" || result.length != 0){
		    		$scope.managerDetails  = result;
		    	}
		    	if(action == "Update"){
		    		setDeliveryLead();		    	
		    	}
		    	if (action != "Update"){
		    		if(result.length == 1){
		    			$scope.deliveryLeadsSelectedList = result[0];
		    		}
		    	}
		    	
		    }, function myError(response) {
//		    	showProgressDialog("Something went wrong redirecting to login page!!!");
		    });
		}
		
		
		$scope.validateEmpId = function () {
			var searchId = $scope.empId;
			if (searchId != "" && isNaN(searchId)) {
				$scope.alertMsg = "Please enter only digits";
				document.getElementById('empId').focus();
			} else if (searchId != "" && ((searchId.length > 0 && searchId.length < 5) || searchId.length > 5)) {
				$scope.alertMsg = "Employee ID should be 5 digits";
				document.getElementById('empId').focus();
			} else if (searchId != "" && !checkRoleEmpIdRange(searchId)) {
				$scope.alertMsg = 'Employee ID should be in between ' + appConfig.empStartId + ' - ' + appConfig.empEndId;
				document.getElementById('empId').focus();
			} else if (searchId != "" && checkRoleExistence(searchId)) {
				$scope.alertMsg = 'Employee ID is already assigned a role';
				document.getElementById('empId').focus();
			} else {
				$scope.alertMsg = "";
			}
		};

		function checkRoleEmpIdRange(searchId) {
			return parseInt(searchId) >= appConfig.empStartId && parseInt(searchId) <= appConfig.empEndId;
		}

		function checkRoleExistence(searchId) {
			for (var i in gridOptionsData) {
				if (gridOptionsData[i].employeeId == searchId) {
					return true;
				}
			}
			return false;
		}

		$scope.validateEmailId = function () {
			var emailId = $scope.empEmail;
			if (emailId != "" && !validateEmail(emailId)) {
				$scope.alertMsg = "Please enter a valid nisum email id";
				document.getElementById('empEmail').focus();
			} else {
				$scope.alertMsg = "";
			}
		}

		function validateEmail(emailId) {
			var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
			if (re.test(emailId)) {
				if (emailId.indexOf("@nisum.com", emailId.length - "@nisum.com".length) !== -1) {
					return true;
				}
			}
			return false;
		}
		
		
		$scope.currentBillabilityDateChange = function(){
			$scope.startDate = $scope.newBillingStartDate ;
		}
		isDataUpdated = function () {
			if (dataToPass.action == "Update") {
				$scope.previousData = {
					ProjectID: dataToPass.projectId,
					ProjectName: dataToPass.projectName,
					Account: dataToPass.account,
					Domain: dataToPass.domain,
					ProjectStatus: dataToPass.status,
					StartDate:new Date(dataToPass.projectStartDate),
					EndDate:new Date(dataToPass.projectEndDate)
				}
				$scope.currentData = {
					ProjectID: $scope.projectId,
					ProjectName: $scope.projectName,
					Account: $scope.account.accountName,
					Domain: $scope.domain.domainName,
					ProjectStatus: $scope.projectStatus,
					StartDate:$scope.projectStartDate,
					EndDate:$scope.projectEndDate
				}
				var predata = JSON.stringify($scope.previousData);
				var curdata = JSON.stringify($scope.currentData);
				var exsistingMangersList = dataToPass.deliveryLeadIds;
				var temp = [];
				temp.push($scope.deliveryLeadsSelectedList);
				var currentMangersList = temp;
				var managerCheck = false;
				if (exsistingMangersList.length === currentMangersList.length) {
					var temp1 = [];
					var temp2 = [];

					for (var i in exsistingMangersList) {
						temp1.push(exsistingMangersList[i].employeeId);
					}
					for (var j in currentMangersList) {
						temp2.push(currentMangersList[j].employeeId);
					}
					for (var k = 0; k < exsistingMangersList.length; k++) {
						if (temp2.indexOf(temp1[k]) > -1) {
							managerCheck = true;
						} else {
							managerCheck = false;
						}
					}
				} else {
					managerCheck = false;
				}
				if (predata === curdata && managerCheck) {
					//$mdDialog.hide('Cancelled'); 
					return false;
				}
				return true
			}

		}
		$scope.getOnBehalfOfEmpId = function(onBehalfOfEmpName){
	    	return $scope.allEmployeesList.filter(function (employee) {
    		    return employee.employeeName == onBehalfOfEmpName;
    		});
		}
		
		$scope.validateFields = function (action,row) {
			var project = $scope.projectId;
			var projectName = $scope.projectName;
			var account = $scope.account;
			var managerId=$scope.managerId;
			var managerName=$scope.managerName;
			var employeeModel = $scope.employeeModel;
			var projectModel = $scope.projectModel;
			var newBillingStartDate = $scope.newBillingStartDate;
			var empAllocationStatus = $scope.empAllocationStatus;
			if(action == 'Update'){
				$scope.employeeRole == 'Lead' ? $scope.employeeRole ='Lead' : $scope.employeeRole = 'Employee';	
			}
			//var onBehalfEmpId = action=="Add" ? onBehalfEmpId = $scope.aliasModel.employeeId : onBehalfEmpId = $scope.onBehalfOfEmpName;
	           if(action == "Update" && ($scope.onBehalfOfEmpName != undefined && $scope.onBehalfOfEmpName != "")) {
	        	   $scope.onBehalfEmpId = $scope.getOnBehalfOfEmpId($scope.onBehalfOfEmpName)[0].employeeId;
	           }
	           else if(action == "Update" && ($scope.onBehalfOfEmpName == "" || $scope.onBehalfOfEmpName == undefined)){
	        	   $scope.onBehalfEmpId = null;
	           }
	           else if(action == "Add"){
	        	   $scope.onBehalfEmpId = $scope.aliasModel.employeeId;
	           }
			if(action === "Add" || action =="Update"){
				if(employeeModel == undefined || employeeModel.employeeName == undefined){
					$scope.alertMsg = "Please select a employee";
					angular.element(document.getElementById('selectEmp')).focus();
				}else if($scope.employeeRole == undefined){
					 $scope.alertMsg = "Please select a employee role";
					 angular.element(document.getElementById('empRole')).focus();
				}
				else if(action === "Add" && $scope.toggleAlias == true && $scope.aliasModel.employeeName == undefined || $scope.aliasModel == {}){
					 $scope.alertMsg = "Please select a OnBehalf Of Resource";
					 angular.element(document.getElementById('selectAlias')).focus();
				}
				else if(action === "Add" && employeeModel != undefined && employeeModel.employeeName != undefined && employeeModel.employeeName == $scope.aliasModel.employeeName){
					 $scope.alertMsg = "Selected Employee and OnBehalf Of Resource could not be same";
					 angular.element(document.getElementById('selectAlias')).focus();
				}
				else if(action === "Add" && $scope.empAllocationStatus == undefined){
					 $scope.alertMsg = "Please select a employee Allocation Status";
					 angular.element(document.getElementById('empAllocationStatus')).focus();
				}
				else if($scope.empBillableStatus == undefined){
				    $scope.alertMsg = "Please select a billable status";
				    angular.element(document.getElementById('empBillableStatus')).focus();
				}
				 else if($scope.endDate == undefined) {
	                 $scope.alertMsg = "Please select End Date";
	                 angular.element(document.getElementById('endDate')).focus();
				 }
				else if($scope.newBillingStartDate == undefined){
	            	 $scope.alertMsg = "Please select new Billing StartDate";
	            	 angular.element(document.getElementById('newBillingStartDate')).focus();
	            }
				else if($scope.empAllocationStatus == undefined){
	            	 $scope.alertMsg = "Please select a Allocation Status";
	            	 angular.element(document.getElementById('empAllocationStatus')).focus();
	            }
				else if($scope.endDate < $scope.newBillingStartDate){
					$scope.alertMsg = "Billling Start date should not exceed Billing end date";
				}
/*				else if(employeeModel != undefined && projectModel != undefined && action == "Add" && getExistingRecordProjectStatus(employeeModel.employeeId, projectModel.projectName)){
					$scope.alertMsg = "Employee is already assigned to the selected project";
					return false;
				}*/
				else if(!(($scope.newBillingStartDate >= new Date(dataToPass.projectStartDate)) && ($scope.newBillingStartDate <= $scope.endDate))){
						$scope.alertMsg = $scope.empBillableStatus + " start date should be in between project start date and end date";
				} 
//				else if (!(($scope.newBillingStartDate >=  $scope.parentData.newBillingStartDate) && ($scope.newBillingStartDate <= $scope.endDate)) && action == "Update"){
//						$scope.alertMsg = $scope.empBillableStatus + " start date should be in between previous Billing start date and end date";
//				}
				else if($scope.endDate > new Date(dataToPass.projectEndDate)){
					$scope.alertMsg = "End date should not exceed project end date";
				}
				else {
					$scope.id = $scope.id ? $scope.id : $scope.objectId;
					$scope.alertMsg = "";
					var record = {"id":$scope.id,"employeeId":employeeModel.employeeId,"onBehalfOf":$scope.onBehalfEmpId,"projectId":project,"billableStatus":$scope.empBillableStatus,"billingEndDate":$scope.endDate,"resourceRole":$scope.employeeRole,"billingStartDate":newBillingStartDate,"status":empAllocationStatus};
					if(action == "Add"){
						addRecord(record,action);	
						$scope.myForm.$setPristine();					
					} else if (action == "Update") {
						if ($scope.DataUpdate() != true) {
							$mdDialog.show($mdDialog.alert({
								skipHide: true,
								title: 'Attention',
								textContent: 'There is no data change to Update',
								ok: 'ok'
							}))
						}
						else {
							updateTeamRecord(record, action,row);
						}

					}
				}
			}
		};
		$scope.validateProjectFields = function (action){
			var domain = $scope.domain;
			var account = $scope.account;
			var projectName = $scope.projectName;
			var deliveryLeadsSelectedList = $scope.deliveryLeadsSelectedList;
			var projectStartDate = $scope.projectStartDate;
			var projectEndDate = $scope.projectEndDate;
			var projectStatus =  $scope.projectStatus; 

			if (action == "Assign" || action == "Update")
			if (projectName == "") {
				$scope.alertMsg = "Project Name is mandatory";
				document.getElementById('projectName').focus();
			}else if(projectName !="" && !validateTextFields(projectName)){
				$scope.alertMsg = "Please enter alphabets only";
				document.getElementById('projectName').focus();
			}else if (account == undefined || account == "") {
				$scope.alertMsg = "Account is mandatory";
				document.getElementById('account').focus();
	
			} else if (domain == undefined || domain == "") {
				$scope.alertMsg = "Domain is mandatory";
				document.getElementById('domain').focus();
			}
			 else if(deliveryLeadsSelectedList.employeeName == undefined || deliveryLeadsSelectedList.employeeName == ""){
				$scope.alertMsg = "Please select a Delivery Lead";
			 document.getElementById('deliveryLead').focus();
			 }
			 else if(projectStatus == undefined){
				$scope.alertMsg = "Please select a Project Status";
				 document.getElementById('projectStatus').focus();
				 }
			 else if (projectStartDate == undefined){
				 $scope.alertMsg = "Please select project start date";
				 document.getElementById('projectStartDate').focus();
			 }
			 else if (projectEndDate == undefined){
				 $scope.alertMsg = "Please select project end date";
				 document.getElementById('projectEndDate').focus();
			 }
			 else if (projectStartDate >= projectEndDate){
				 $scope.alertMsg = "Start date should not be greater than end date";
			 }
			else {
				$scope.alertMsg = "";
				var record = { "projectId": $scope.projectId, "projectName": $scope.projectName,"account": $scope.account.accountName, "domain": $scope.domain.domainName, "accountId":$scope.account.accountId, "domainId": $scope.domain.domainId, "managerIds": [], "deliveryLeadIds":getSelectedDeliveryLeadIds(), "status": $scope.projectStatus, "projectStartDate": $scope.projectStartDate, "projectEndDate": $scope.projectEndDate };
				if(action == "Update"){
					if (isDataUpdated() != true) {
						$mdDialog.show($mdDialog.alert({
							skipHide: true,
							title: 'Attention',
							textContent: 'There is no data change to Update',
							ok: 'ok'
						}))
					}
					else {
						addOrUpdateProject(record, $scope.templateTitle);
//						$timeout(function () { updateGrid($scope.templateTitle, record) }, 500);
					}
				}else{
					addOrUpdateProject(record, $scope.templateTitle);
//					$timeout(function () { updateGrid($scope.templateTitle, record) }, 500);
				}	
		}}
		$scope.cancel = function () {
			var showConfirmDialog = false;
			if (dataToPass.action == "Update") {
				if ($scope.domain != undefined) {
					if (isDataUpdated() != true) {
						$mdDialog.hide('Cancelled');
					}
					else {
						$mdDialog.show($mdDialog.confirm({
							skipHide: true,
							textContent: 'Are you sure you want to cancel this?',
							ok: 'Yes',
							cancel: 'No'
						})).then(function () {
							$mdDialog.hide('Cancelled');
						})
					}
				}
				else {
					$mdDialog.show($mdDialog.confirm({
						skipHide: true, 
						textContent: 'Are you sure you want to cancel this?',
						ok: 'Yes',
						cancel: 'No'
					})).then(function () {
						$mdDialog.hide('Cancelled');
					})
				}

			}
			if (dataToPass.action == "Assign") {
				var totalFields = $scope.myForm.$$controls;
				for (key in totalFields) {
					if (totalFields[key].$modelValue !== '' && totalFields[key].$modelValue !== undefined && totalFields[key].$modelValue.length !== 0) {
						showConfirmDialog = true;
					}
				}
				if (showConfirmDialog) {
					$mdDialog.show($mdDialog.confirm({
						skipHide: true,
						textContent: 'Are you sure you want to cancel this?',
						ok: 'Yes',
						cancel: 'No'
					})).then(function () {
						$mdDialog.hide('Cancelled');
					})
				}
				else {
					$mdDialog.hide('Cancelled');
				}
			}
		};
		    $scope.cancelDialog = function() {
			$mdDialog.hide('Cancelled');
		       };
		$scope.DataUpdate = function () {
			var data = $scope.parentData;
			
			if (data.role == "Individual Contributor") {
				var roleselected = "Employee";
			} else {
				roleselected = data.role;
			}
			$scope.previousData = {
	            OnBehalfOf : data.onBehalfOf,
				Role: roleselected,
				//Shift: data.shift,
				Billabilitystatus: data.billableStatus,
				newBilligStartDate: new Date(data.newBillingStartDate),
				//Startdate: data.startDate,
				Enddate: data.endDate
			},
				$scope.currentData = {
					OnBehalfOf : $scope.onBehalfOfEmpName,
					Role: $scope.employeeRole,
//					Shift: $scope.employeeShift,
					Billabilitystatus: $scope.empBillableStatus,
					newBilligStartDate: $scope.newBillingStartDate,
					//Startdate: $scope.startDate,
					Enddate: $scope.endDate
				}
			var predata = JSON.stringify($scope.previousData);
			var curdata = JSON.stringify($scope.currentData);
			if (predata == curdata) {
				return false;
			}
			return true;
		}
		$scope.cancelTab = function () {
			if (dataToPass.action == "View") {
				if ($scope.templateTitle == "Update") {
					if ($scope.DataUpdate() == false) {
						removeTab("cancel");
					}
					else {
						$mdDialog.show($mdDialog.confirm({
							skipHide: true,
							textContent: 'Are you sure you want to cancel this?',
							ok: 'Yes',
							cancel: 'No'
						})).then(function () {
							removeTab("cancel");
							$scope.myForm.$setPristine();
						})
					}

				}
				if ($scope.templateTitle == "Add") {
					if ($scope.myForm.$dirty) {
						$mdDialog.show($mdDialog.confirm({
							skipHide: true,
							textContent: 'Are you sure you want to cancel this?',
							ok: 'Yes',
							cancel: 'No'
						})).then(function () {
							removeTab("cancel");
							$scope.myForm.$setPristine();
						})
					}
					else {
						removeTab("cancel")

					}
				}
			}
		};
		
		$scope.getEmployeeSelected = function(){
			if ($scope.employeeModel != undefined ) {
				if(Object.keys($scope.employeeModel).length > 1){
					$scope.employee=$scope.employeeModel;
					return $scope.employeeModel.employeeName;
				}
				else {
					return "Please select a employee";
				}
				
			} else {
				return "Please select a employee";
			}
		};
		$scope.getSelectedAlias = function(){
				if ($scope.aliasModel != undefined) {
					if(Object.keys($scope.aliasModel).length > 1){
						$scope.alias=$scope.aliasModel;
						return $scope.aliasModel.employeeName;
					}
					else {
						return "Please select a Resource";
					}
					
				} else {
					return "Please select a Resource";
				}

		};
		$scope.getSelectedEmpRole = function(){
            if ($scope.employeeRole != undefined ) {
                return $scope.employeeRole;
            } else {
                return "Please select a employee role";
            }
        }
//		$scope.getSelectedEmpShift = function(){
//            if ($scope.employeeShift != undefined ) {
//                return $scope.employeeShift;
//            } else {
//                return "Please select a employee shift";
//            }
//        }
		
//		$scope.getProjectSelected = function(){
//			if ($scope.projectModel !== undefined) {
//				$scope.project=$scope.projectModel;
//				return $scope.projectModel.projectName;
//			} else {
//				return "Please select a project";
//			}
//		};
		$scope.getSelectedBillableStatus = function(){
            if ($scope.empBillableStatus != undefined ) {
                return $scope.empBillableStatus;
            } else {
                return "Please select a billable status";
            }
        };
		$scope.getSelectedAllocationStatus = function(){
            if ($scope.empAllocationStatus != undefined ) {
                return $scope.empAllocationStatus;
            } else {
                return "Please select a allocation status";
            }
        };
		function updateGrid(action, record) {
			if ($scope.alertMsg == "") {
				if ($scope.result == "Success") {
					if (action == "Assign") {
						gridOptionsData.push(record);
						$mdDialog.hide(action);
					} else if (action == "Update") {
						var existingRecord = getRowEntity($scope.projectId);
						var index = gridOptionsData.indexOf(existingRecord);
						gridOptionsData[index] = record;
						$mdDialog.hide(action);

					}
					else if(action == "Add"){
						$scope.gridOptions.data.push(record);
						for(i=0;i<$scope.gridOptions.data.length;i++){
				    		if($scope.gridOptions.data[i].resourceRole == 'Lead'){
					    		  $scope.gridOptions.data[i].resourceRole = "Lead";
					    	  }else{
					    		  $scope.gridOptions.data[i].resourceRole = "Individual Contributor"; 
					    	  }	
				    	}
					}
				} else {
					$mdDialog.hide("Error");
				}

			}
		}

		function addRecord(record, action){
			var urlRequest  = "";
			var loginEmpId = myFactory.getEmpId();
				urlRequest = appConfig.appUri+ "resources?loginEmpId="+loginEmpId;
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
			var titleOfModel = "Attention";
			if(response.data.message == "Resource has been created"){
			  //$scope.objectId = response.data.records;
			   titleOfModel = "";
				$timeout(function () {
					getProjectDetails($scope.projectId, $scope.status);
					removeTab('Add');
				}, 500);
			
				$mdDialog.show($mdDialog.alert({
					skipHide: true,
					title:titleOfModel,
			        textContent: response.data.message,
			        ok: 'ok'
				  }));
				}
			else{
				$scope.alertMsg=response.data.message;
			}
			}, function myError(response){
				$scope.result = "Error";
			});
		}
		function getProjectDetails(projectId,status){
			$http({
				method: "GET",
				url: appConfig.appUri + "resources/project/"+ $scope.projectId + "?status=" + $scope.status
			}).then(function mySuccess(response) {
				$scope.gridOptions.data = response.data.records;
				if(response.data.records.length > 10){
		    		$scope.gridOptions.enablePaginationControls = true;
		    	}
		    	else{
		    		$scope.gridOptions.enablePaginationControls = false;
		    	}
				for(i=0;i<$scope.gridOptions.data.length;i++){
		    		if($scope.gridOptions.data[i].resourceRole == 'Lead'){
			    		  $scope.gridOptions.data[i].resourceRole = "Lead";
			    	  }else{
			    		  $scope.gridOptions.data[i].resourceRole = "Individual Contributor"; 
			    	  }	
		    	}
			}, function myError(response) {
				showAlert("Something went wrong while fetching data!!!");
				$scope.gridOptions.data = [];
			});
		}
		function updateTeamRecord(record, action,row){
			var urlRequest  = "";
			var loginEmpId = myFactory.getEmpId();
			urlRequest = appConfig.appUri+ "resources?loginEmpId="+loginEmpId;
		var req = {
			method : 'PUT',
			url : urlRequest,
			headers : {
				"Content-type" : "application/json"
			},
			data : record
		}
		$http(req).then(function mySuccess(response) {
		$scope.result = "Success";
		if(response.data.message == "Resource updated successfully"){
			$timeout(function () {
				getProjectDetails($scope.projectId, $scope.status);
			}, 500);
			$mdDialog.show($mdDialog.alert({
				skipHide: true,
				textContent: response.data.message ,
				ok: 'ok'
			})).then(function () {
				$scope.myForm.$setPristine();
			})
		}else{
			$scope.alertMsg=response.data.message;
	    	   if($scope.alertMsg && $scope.alertMsg != ""){
	    		   row.onBehalfOf = $scope.parentData.onBehalfOf;
	    		   row.billingStartDate = $scope.parentData.newBillingStartDate;
	    		   row.billingEndDate = $scope.parentData.endDate;
	    		   row.resourceRole = $scope.parentData.role;
	    		   row.billableStatus = $scope.parentData.billableStatus;
	    	   }
	    	   $scope.previousRow = angular.copy(row);
		}
			
	}, function myError(response){
		   $scope.result = "Error";
		   row.onBehalfOf = $scope.parentData.onBehalfOf;
 		   row.billingStartDate = $scope.parentData.newBillingStartDate;
		   row.billingEndDate = $scope.parentData.endDate;
		   row.resourceRole = $scope.parentData.role;
		   row.billableStatus = $scope.parentData.billableStatus;
});
			
}

		function addOrUpdateProject(record, action) {
			var urlRequest = "";
			var requestMethod = "";
			var loginEmpId = myFactory.getEmpId();
			if (action == "Assign") {
				urlRequest = appConfig.appUri + "projects?loginEmpId="+loginEmpId;
				requestMethod = 'POST';
			} else if (action == "Update") {
				urlRequest = appConfig.appUri + "projects/"+record.projectId+"?loginEmpId="+loginEmpId;
				requestMethod = 'PUT';
			}
			var req = {
				method: requestMethod,
				url: urlRequest,
				headers: {
					"Content-type": "application/json"
				},
				data: record
			}
			$http(req).then(function mySuccess(response) {
				if(response.data.statusCode != 700 && response.data.statusCode != 701){
					$mdDialog.show($mdDialog.alert({
						skipHide: true,
						title:'Attention',
				        textContent: response.data.message,
				        ok: 'ok'
				      }));
				}
				else{
					$scope.result = "Success";
					$timeout(function () { updateGrid($scope.templateTitle, record) }, 500);
				}
				
			}, function myError(response) {
				$scope.result = "Error";
			});
		}

		function getRowEntity(empId) {
			for (var i = 0; i < gridOptionsData.length; i++) {
				var record = gridOptionsData[i];
				if (record.projectId == empId) {
					return record;
				}
			}
		}
		
		function getExistingRecordProjectStatus(empId, projectName){
			for(var i=0;i<$scope.gridOptions.data.length;i++){
				var record = $scope.gridOptions.data[i];
				if(record.employeeId == empId){
					if(record.active == true && record.projectName == projectName)
						return true;
				}
			}
			return false;
		}
		function validateTextFields(name){
			var pattern = /^[a-zA-Z#@.,;:'()\/&\-"!]+( [a-zA-Z#@.,;:'()\/&\-"!]+)*$/;
			if(pattern.test(name)){
				return true;
			}
			return false;
		}

	}
});
