myApp.controller("assignRoleController",function($scope, myFactory, $mdDialog, $http, appConfig, $timeout,$window,$mdSelect){
	$scope.records = [];
	$scope.empSearchId = "";
	$scope.status = "Active";
	$scope.parentData = {
			"employeeId": "",
			"employeeName": "",
			"emailId":"",
			"designation":"",
			"action":"",
			"passportExpiryDate":""
	};
	
	var getCellTemplate = '<p ng-show="row.entity.empStatus == \'Active\'" class="col-lg-12"><i class="fa fa-pencil-square-o fa-2x" aria-hidden="true" data-placement="center" title="Edit" onmouseenter="$(this).tooltip(\'show\')" style="font-size:1.5em;margin-top:3px;cursor:pointer;" ng-click="grid.appScope.getRowData(row,\'Update\')"></i>'+
	'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i ng-disabled="row.entity.updateProfile" data-placement="center" title="Save" onmouseenter="$(this).tooltip(\'show\')" class="fa fa-download fa-2x" aria-hidden="true" style="font-size:1.5em;margin-top:3px;cursor:pointer;" ng-click="grid.appScope.download(row)"></i></p>'+
	'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;';
	//var getDownloadTemplate = '<a target="_self" class="add-btn md-raised md-primary" href ng-click="grid.appScope.download(row)"> Download</a>';
	var getDownloadTemplate = '<p ng-disabled="row.entity.updateProfile" class="col-lg-12"><i class="fa fa-download fa-2x" aria-hidden="true" style="font-size:1.5em;margin-top:3px;cursor:pointer;" ng-click="grid.appScope.download(row)"></i>'+
	'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;';
//	<i class="fa fa-minus-circle fa-2x" aria-hidden="true" style="font-size:1.5em;margin-top:3px;cursor:pointer;" ng-click="grid.appScope.getRowData(row,\'Delete\')"></i></p>
	$scope.gridOptions = {
		paginationPageSizes : [ 10, 20, 30, 40, 50, 100],
		paginationPageSize : 10,
	    pageNumber: 1,
		pageSize:10,
		enableFiltering: true,
		enableHorizontalScrollbar:1,
		columnDefs : [ 
			{field : 'employeeId',displayName: 'EmpId', enableColumnMenu: true, enableSorting: true,enableFiltering: true, width:'*',cellClass: 'grid-align'},
			{field : 'employeeName',displayName: 'Emp Name', enableColumnMenu: true, enableSorting: true,enableFiltering: true,cellClass: 'grid-align',width:'*'},
			{field : 'mobileNumber',displayName: 'Mobile', enableColumnMenu: false, enableSorting: false,enableFiltering: false,cellClass: 'grid-align', width:'*'},
			{field : 'empStatus',displayName: 'Status', enableColumnMenu: false, enableSorting: false,enableFiltering: true,width:'*'},
			{field : 'empSubStatus.subStatus',displayName: 'Sub Status', enableColumnMenu: false, enableSorting: true,enableFiltering:true,width:'*',cellClass:function(grid,row,col){
				
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
			{field : 'emailId',displayName: 'Email', enableColumnMenu: false, enableSorting: false,enableFiltering: true,cellClass: 'grid-align', width:'*'},
			{field : 'baseTechnology',displayName: 'Skill', enableColumnMenu: false, enableSorting: false,enableFiltering: false,cellClass: 'grid-align', width:'*'},
			{field : 'designation',displayName: 'Designation', enableColumnMenu: false, enableSorting: false,enableFiltering: true,cellClass: 'grid-align',width:'*'}, 
			{field : 'functionalGroup',displayName: 'Func. Org', enableColumnMenu: false, enableSorting: false,enableFiltering:true,width:'*', cellClass: 'grid-align'},
			{field : 'dateOfJoining',displayName: 'DOJ', enableColumnMenu: true, enableSorting: true,enableFiltering:true,cellFilter:'date:"dd-MMM-yyyy"',width:'*', cellClass: 'grid-align'},
			{field : 'endDate',displayName: 'DOL', enableColumnMenu: true, enableSorting: true,enableFiltering:true,cellFilter:'date:"dd-MMM-yyyy"',width:'*', cellClass: 'grid-align'},
			{name : 'Actions', displayName: 'Actions',cellTemplate: getCellTemplate, enableColumnMenu: false, enableSorting: false,enableFiltering: false} ,
			//{name : 'Dowload Profile', displayName: 'Dowload',cellTemplate: getDownloadTemplate, enableColumnMenu: false, enableSorting: false,enableFiltering: false, width:100,cellClass: 'grid-align'}
		]
	};
	
	
	$scope.gridOptions.data = $scope.records;
	$scope.gridOptionsOrgView = {
			paginationPageSizes : [ 10, 20, 30, 40, 50, 100],
			paginationPageSize : 10,
		    pageNumber: 1,
			pageSize:10,
			enableFiltering: true,
			columnDefs : [ 
				{field : 'employeeId',displayName: 'Employee ID', enableColumnMenu: true, enableSorting: true,enableFiltering: true, width:120},
				{field : 'employeeName',displayName: 'Employee Name', enableColumnMenu: true, enableSorting: true,enableFiltering: true},
				{field : 'emailId',displayName: 'Email', enableColumnMenu: true, enableSorting: true,enableFiltering: true},
				{field : 'baseTechnology',displayName: 'Skill', enableColumnMenu: false, enableSorting: false,enableFiltering: false},
				{field : 'designation',displayName: 'Designation', enableColumnMenu: false, enableSorting: true,enableFiltering: false}, 
				{field : 'functionalGroup',displayName: 'Functional Org', enableColumnMenu: false, enableSorting: true,enableFiltering: false, width:120} 	
			]
		};
	
	$scope.getRowData = function(row, action){

		$scope.parentData.id = row.entity.id;
		$scope.parentData.employeeId = row.entity.employeeId;
		$scope.parentData.employeeName = row.entity.employeeName;
		$scope.parentData.gender = row.entity.gender;
		$scope.parentData.mobileNumber = row.entity.mobileNumber;
		$scope.parentData.emailId = row.entity.emailId;
		$scope.parentData.role = row.entity.role;
		$scope.parentData.empCountry = row.entity.empCountry;
		$scope.parentData.empLocation = row.entity.empLocation;
		$scope.parentData.designation = row.entity.designation;
		$scope.parentData.functionalGroup = row.entity.functionalGroup;
		$scope.parentData.empStatus = row.entity.empStatus;
		$scope.parentData.empSubStatus = (row.entity.empSubStatus == null) ? null : row.entity.empSubStatus.subStatus;
		$scope.parentData.employmentType = row.entity.employmentType;
		$scope.parentData.domain = row.entity.domain;
		$scope.parentData.dateOfJoining = row.entity.dateOfJoining;
		$scope.parentData.dateOfBirth = row.entity.dateOfBirth;
		$scope.parentData.hasPassort = row.entity.hasPassort;
		$scope.parentData.hasB1 = row.entity.hasB1;
		$scope.parentData.passportExpiryDate = row.entity.passportExpiryDate;
		$scope.parentData.b1ExpiryDate = row.entity.b1ExpiryDate;
		$scope.parentData.endDate = row.entity.endDate;
		$scope.parentData.subStatusStartDate = (row.entity.empSubStatus == null) ? null : row.entity.empSubStatus.fromDate;
		$scope.parentData.subStatusEndDate = (row.entity.empSubStatus == null) ? null : row.entity.empSubStatus.toDate;
		if(action == "Update")
			$scope.assignRole(action, $scope.parentData);
		else if(action == "Delete")
			$scope.deleteRole(row);
	}

	$scope.download = function(row){
        		var employeeId = row.entity.employeeId;
        		var  urlRequest = appConfig.appUri+ "/employees/downloadFile/"+employeeId;
                    		    $http({
                    	        method : "GET",
                    	        url : urlRequest,
                    	        headers:{'Content-type': 'application/msword'},
                                responseType : 'arraybuffer',
                    	             }).then(function mySuccess(response) {
                    	                          var value=response.data;
                    	                          var fileName=employeeId+".doc"
                    	                          var file = new Blob([value], {type: 'application/msword'});
                                                  var url = window.URL || window.webkitURL;
                                                  var downloadLink = angular.element('<a></a>');
                                                      downloadLink.attr('href',url.createObjectURL(file));
                                                      downloadLink.attr('target','_self');
                                                      downloadLink.attr('download', fileName);
                                                      downloadLink[0].click();

                    	                }, function myError(response) {
                    	    	        showAlert("No Resume found, Please Upload Resume in My Profile Section");
                    	                     });
        	}
	
	$scope.refreshPage = function(){
		$scope.empSearchId = "";
		$scope.getUserRoles();
	}
	
	$scope.refreshPageOrg = function(){
		$scope.getOrgEmps();
	}
	
	var gridOptionsReport = {
			paginationPageSizes : [ 10, 20, 30, 40, 50, 100],
			paginationPageSize : 10,
		    pageNumber: 1,
			pageSize:10,
			enableFiltering: true,
			columnDefs : [ 
				{field : 'employeeId',displayName: 'Employee ID', enableColumnMenu: true, enableSorting: true,enableFiltering: true, width:120},
				{field : 'employeeName',displayName: 'Name', enableColumnMenu: false, enableSorting: false,enableFiltering: true},
				{field : 'mobileNumber',displayName: 'Mobile', enableColumnMenu: false, enableSorting: false,enableFiltering: false},
				{field : 'emailId',displayName: 'Email', enableColumnMenu: false, enableSorting: false,enableFiltering: true},
				{field : 'baseTechnology',displayName: 'Skill', enableColumnMenu: false, enableSorting: false,enableFiltering: true},
				{field : 'designation',displayName: 'Designation', enableColumnMenu: false, enableSorting: true,enableFiltering: true},
				{name : 'Actions', displayName: 'Actions',cellTemplate: getCellTemplate, enableColumnMenu: false, enableSorting: false,enableFiltering: false, width:100} ,
				{name : 'Dowload Profile', displayName: 'Dowload',downloadTemplate: getDownloadTemplate, enableColumnMenu: false, enableSorting: false,enableFiltering: false, width:100}
			]
		};
	$scope.getUserRoles = function(){
		$http({
	        method : "GET",
	        url : appConfig.appUri + "employees/?status="+$scope.status
	    }).then(function mySuccess(response) {
	    	if(response.data.records.length > 10){
	    		$scope.gridOptions.enablePaginationControls = true;
	    	}
	    	else{
	    		$scope.gridOptions.enablePaginationControls = false;
	    	}
	    	if($scope.status == "In Active"){
	    		$scope.gridOptions.columnDefs[6].visible = false;
	    		$scope.gridOptions.columnDefs[10].visible = true;
	    		$scope.gridOptions.columnDefs[8].visible = false;
	    	}
	    	else{
	    		$scope.gridOptions.columnDefs[6].visible = true;
	    		$scope.gridOptions.columnDefs[10].visible = false;
	    		$scope.gridOptions.columnDefs[8].visible = true;
	    	}
	        $scope.gridOptions.data = response.data.records;
	    }, function myError(response) {
	    	showAlert("Something went wrong while fetching data!!!");
	    	$scope.gridOptions.data = [];
	    });
	};
	
	$scope.getOrgEmps = function(){
		$http({
	        method : "GET",
	        url : appConfig.appUri + "employees/active"
	    }).then(function mySuccess(response) {
	        $scope.gridOptionsOrgView.data = response.data.records;
	        if(response.data.records.length > 10){
	    		$scope.gridOptions.enablePaginationControls = true;
	    	}
	    	else{
	    		$scope.gridOptions.enablePaginationControls = false;
	    	}
	    }, function myError(response) {
	    	showAlert("Something went wrong while fetching data!!!");
	    	$scope.gridOptionsOrgView.data = [];
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
		      controller: AddRoleController,
		      templateUrl: 'templates/newRoleTemplate.html',
		      parent: angular.element(document.body),
		      clickOutsideToClose:false,
		      locals:{dataToPass: userData, gridOptionsData: $scope.gridOptions.data},
		    })
		    .then(function(result) {
		    	if(result == "Add"){
		    		showAlert('Employee assigned successfully');
		    	}
		    	else if(result == "Update") { 
		    		$scope.getUserRoles(); 
		    		showAlert('Employee updated successfully');
		    	}	
		    	else if(result == "Cancelled") {
		    		$scope.getUserRoles(); 
		    	}
		    	else {
		    		showAlert('Role assigning/updation failed!!!');
		    	}
		    });
	};
	
	$scope.cancel = function() {
		$mdDialog.hide();
		};
	$scope.deleteRole = function(row){
		$('#home').addClass('md-scroll-mask');
	    var confirm = $mdDialog.confirm()
	    	  .clickOutsideToClose(true)
	          .textContent('Are you sure want to delete the role?')
	          .ok('Ok')
	          .cancel('Cancel');
	    $mdDialog.show(confirm).then(function() {
			deleteUserRole(row.entity.employeeId);
			$timeout(function(){updateGridAfterDelete(row)},500);
	    }, function() {
	    	
	    });
	};
	function deleteUserRole(empId){
		var req = {
				method : 'DELETE',
				url : appConfig.appUri+ "employees/"+empId
			}
			$http(req).then(function mySuccess(response) {
				$scope.result = response.data;
			}, function myError(response){
				$scope.result = "Error";
			});
	}
	
	function updateGridAfterDelete(row){
		if($scope.result == "Success" || $scope.result == ""){
			var index = $scope.gridOptions.data.indexOf(row.entity);
			$scope.gridOptions.data.splice(index, 1);
			showAlert('Role deleted successfully');
		}else if($scope.result == "Error"){
			showAlert('Something went wrong while deleting the role.')
		}
    	
	}
	$window.addEventListener('click', function (e) {
		if (e.target.type !== 'search') {
			$mdSelect.hide();
		}
	});
	
	function AddRoleController($scope, $mdDialog, dataToPass, gridOptionsData) {
		$scope.templateTitle = dataToPass.action;
		$scope.alertMsg = "";
		$scope.isDisabled = false;
		$scope.result = "";
		$scope.disableSubStatus = false;
		if(dataToPass.action == "Add"){
			$scope.empId = "";
			$scope.empName = "";
			$scope.gender;
			$scope.empRole;
			$scope.empEmail = "";
			 $scope.functionalGroup;
			 $scope.empStatus;
			 $scope.empMobileNo = "";
			 $scope.empSubStatus;
			 $scope.employmentType;
			 $scope.designation;
			 $scope.empCountry ;
			 $scope.empLocation;
			 $scope.dateOfJoining;
			 $scope.domain = "";
			 $scope.isDisabled = false;
			 $scope.hasPassort;
			 $scope.hasB1;
			 $scope.subStatusEndDate;
			 $scope.subStatusStartDate;
		}else if(dataToPass.action == "Update") {
			if(dataToPass.empSubStatus == null){
				$scope.disableSubStatus = false;
			}
			else {
				$scope.disableSubStatus = true;
			}
			$scope.id = dataToPass.id;
			$scope.empId = dataToPass.employeeId;
			$scope.empName = dataToPass.employeeName;
			$scope.gender = dataToPass.gender;
			$scope.empRole = dataToPass.role;
			$scope.empMobileNo = dataToPass.mobileNumber;
			$scope.empEmail = dataToPass.emailId;
			$scope.empCountry = dataToPass.empCountry;
			$scope.empLocation = dataToPass.empLocation;
			$scope.functionalGroup = dataToPass.functionalGroup;
			$scope.empStatus = dataToPass.empStatus;
			$scope.empSubStatus = dataToPass.empSubStatus;
			$scope.subStatusEndDate = (dataToPass.subStatusEndDate == null) ? null : new Date(dataToPass.subStatusEndDate);
			$scope.subStatusStartDate = (dataToPass.subStatusStartDate == null) ? null : new Date(dataToPass.subStatusStartDate);
			$scope.employmentType = dataToPass.employmentType;
			$scope.domain = dataToPass.domain;
			$scope.designation = dataToPass.designation;
			$scope.dateOfJoining = (dataToPass.dateOfJoining == null) ? null : new Date(dataToPass.dateOfJoining);
			$scope.dateOfBirth =  (dataToPass.dateOfBirth == null) ? null : new Date(dataToPass.dateOfBirth);
			$scope.isDisabled = true;
			$scope.hasPassort = dataToPass.hasPassort;
			$scope.hasB1 = dataToPass.hasB1;
			$scope.passportExpiryDate =  (dataToPass.passportExpiryDate == null) ? null : new Date(dataToPass.passportExpiryDate);
			$scope.b1ExpiryDate =  (dataToPass.b1ExpiryDate == null) ? null : new Date(dataToPass.b1ExpiryDate);
			$scope.exitDate = (dataToPass.endDate == null) ? null :new Date(dataToPass.endDate);
		}
		$scope.domains = myFactory.getDomains();
		$scope.roles = myFactory.getRoles();
		$scope.countrys=myFactory.getCountrys();
		$scope.locations=myFactory.getLocations();
		$scope.designations=myFactory.getDesignations();
		$scope.functionalGroups=myFactory.getFunctionalgroups();
		$scope.employmentTypes=myFactory.getEmployementTypes();
		$scope.empStatuses=myFactory.getEmployeeStatus();
		$scope.empSubStatuses=myFactory.getEmployeeSubStatus();
		$scope.getSelectedRole = function(){
			if ($scope.empRole !== undefined) {
				return $scope.empRole;
			} else {
				return "Please select a role";
			}
		};
		$scope.getSelectedCountry = function(){
                			if ($scope.empCountry !== undefined) {
                				$scope.empLocation="";
                				$scope.locations="";
                			getAllLocationsCountry($scope.empCountry );
                				return $scope.empCountry;
                			} else {
                				return "Please select a Country";
                			}
                		};
                		function getAllLocationsCountry(country){
                            		$http({
                            	        method : "GET",
                            	        url : appConfig.appUri +"/organization/locations/"+country
                            	    }).then(function mySuccess(response) {
                            	   	$scope.locations=response.data.records;
                            	    }, function myError(response) {

                            	    });
                            	};
		$scope.getSelectedLocation = function(){
			if ($scope.empLocation !== undefined) {
				return $scope.empLocation;
			} else {
				return "Please select a location";
			}
		};
		$scope.getDesignationText = function(){
			if ($scope.designation !== undefined) {
				return $scope.designation;
			} else {
				return "Please select a designation";
			}
		};
		$scope.getSelectedFunctionalGroup = function(){
			if ($scope.functionalGroup !== undefined) {
				return $scope.functionalGroup;
			} else {
				return "Please select a Functional Group";
			}
		};
		$scope.getSelectedEmploymentType = function(){
			if ($scope.employmentType !== undefined) {
				return $scope.employmentType;
			} else {
				return "Please select a Employment Type";
			}
		};
		$scope.getSelectedDomain = function(){
			if ($scope.domain !== undefined) {
				return $scope.domain;
			} else {
				return "Please select a Domain";
			}
		};
		$scope.getSelectedEmpStatus = function(){
			if ($scope.empStatus !== undefined) {
				return $scope.empStatus;
			} else {
				return "Please select a employee status";
			}
		};
		$scope.getSelectedGender = function() {
			if ($scope.gender !== undefined) {
				return $scope.gender;
			} else {
				return "Please select a Gender";
			}
		};
			
		
		function checkRoleEmpIdRange(searchId){
			return parseInt(searchId) >= appConfig.empStartId && parseInt(searchId) <= appConfig.empEndId;
		}
		
		function checkRoleExistence(searchId){
			for(var i in gridOptionsData){
				if(gridOptionsData[i].employeeId == searchId){
					return true;
				}
			}
			return false;
		}
		
		$scope.validateEmpId = function(){
			var empId = $scope.empId;
			if(empId == ""){
			  $scope.alertMsg = "";
			  document.getElementById('empId').focus();
			  $scope.alertMsg = "";
			}else{
				$scope.alertMsg = "";	
			}
		}

		$scope.validateMessage = function(type) {

			var emailId = $scope.empEmail;
			var mobileNumber = $scope.empMobileNo;
			if (type === 'email') {
				if (emailId != "" && !validateEmail(emailId)) {
					// $scope.alertMsg = "Please enter a valid
					// nisum email id";
					$scope.alertMsg = "";
					document.getElementById('empEmail').focus();
					// $scope.errorMessage = true;
				} else {
					// $scope.alertMsg = "";
					$scope.errorMessage = false;
				}
			} 
			else {
				$scope.errorMessage = false;
			}
		}
		
		$scope.validateEmpName = function(){
			var empName = $scope.empName;
			if(empName == ""){
			   $scope.alertMsg = "";
			   document.getElementById('empName').focus();
			   $scope.alertMsg = "";
			}else{
				 $scope.alertMsg = "";
			}
		}
		$scope.validateMobileNumber = function(){
			var empMobileNo = $scope.empMobileNo;
			if (checkMobileNoValue(empMobileNo)) {
				$scope.alertMsg = "";
				document.getElementById('empMobileNo').focus();
			} else {
				$scope.alertMsg = "";
			}
		}
		
		function checkMobileNoValue(empMobileNo) {
			var re = /^[6-9][0-9]{9}$/;
			if (re.test(empMobileNo) && empMobileNo != "") {
				return false;
			} else {
				return true;
			}
		}
        
		$scope.validateEmailId = function(){
			var emailId = $scope.empEmail;
			if(emailId != "" && !validateEmail(emailId)){
				$scope.alertMsg = "Please enter a valid nisum email id";
				document.getElementById('empEmail').focus();
			}else{
				//$scope.alertMsg = "Email ID is mandatory";
				$scope.alertMsg = "";
			}
		}
		
		function validateEmail(emailId){
			 var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
			 if(re.test(emailId)){
		        if(emailId.indexOf("@nisum.com", emailId.length - "@nisum.com".length) !== -1){
		        	return true;
		        }
			 }
			 return false;
		 }
		function validateTextFields(name){
			var pattern = /^[a-zA-Z\s]*$/;
			if(pattern.test(name)){
				return true;
			}
			return false;
		}
		$scope.changeEmpSubStatus = function(){
			$scope.subStatusStartDate = null;
			$scope.subStatusEndDate = null;
           $('.md-datepicker-input')[3].value = null;
           $('.md-datepicker-input')[4].value = null;
		}
		
		$scope.endSubStatus = function() {
			
			var record = {	
				id:$scope.id,
				employeeID: $scope.empId,
				subStatus : $scope.empSubStatus,
				fromDate: $scope.subStatusStartDate,
				toDate: $scope.subStatusEndDate
			};
			var empId = myFactory.getEmpId();
			var req = {
				method : 'PUT',
				url : appConfig.appUri+ "subStatus?loginEmpId="+empId,
				headers : {
					"Content-type" : "application/json"
				},
				data : record
			}
			$http(req).then(function mySuccess(response) {
				$scope.result = "Success";
				if(response.data.message == "Sub status end updated successfully"){
				    	 $mdDialog.show($mdDialog.alert({
				    	 skipHide: true,
				    	 textContent: response.data.message ,
				    	 ok: 'ok'
				    	 })).then(function () {
				    	 $scope.myForm.$setPristine();
				    	 })
						 
		    	}else{
		    		$scope.alertMsg=response.data.message;
		    	}
			}, function myError(response){
				$scope.result = "Error";
			});
			
			$scope.empSubStatus=null;
			$scope.subStatusStartDate=null;
			$scope.subStatusEndDate=null;
			
		};
		
		$scope.validateFields = function(){
			var today = new Date();
			if($scope.templateTitle == "Add"){
				$scope.empStatus ="Active";
			}
			var searchId = $scope.empId;
			var empName = $scope.empName;
			var gender = $scope.gender;
			var empRole = $scope.empRole;
			var empEmail = $scope.empEmail;
			var empMobileNo = $scope.empMobileNo;
			 var designation = $scope.designation;
			 var empCountry = $scope.empCountry;
			 var empLocation = $scope.empLocation;
			 var employmentType = $scope.employmentType;
			 var functionalGroup = $scope.functionalGroup;
			 var hasPassort = $scope.hasPassort;
			 var hasB1 = $scope.hasB1;
			 var empStatus = $scope.empStatus;
			 var empSubStatus = $scope.empSubStatus;
			 var dateOfJoining = $scope.dateOfJoining;
			 var dateOfBirth = $scope.dateOfBirth;
			 var b1ExpiryDate = $scope.b1ExpiryDate;
			 var passportExpiryDate = $scope.passportExpiryDate;
			 var subStatusStartDate = $scope.subStatusStartDate;
			 var subStatusEndDate = $scope.subStatusEndDate;
			var empSubStatusObj = {
				employeeID: $scope.empId,
				subStatus : $scope.empSubStatus,
				fromDate: $scope.subStatusStartDate,
				toDate: $scope.subStatusEndDate
			};
			var prevEmpSubStatusObj = {
				employeeID: dataToPass.employeeId,
				subStatus : dataToPass.empSubStatus,
				fromDate: (dataToPass.subStatusStartDate == null) ? null : new Date(dataToPass.subStatusStartDate),
				toDate: (dataToPass.subStatusEndDate == null) ? null : new Date(dataToPass.subStatusEndDate)
			};
			if(searchId == ""){
				$scope.alertMsg = "Employee Id is mandatory";
				document.getElementById('empId').focus();
			}else if(searchId != "" && !checkRoleEmpIdRange(searchId)){
				$scope.alertMsg = 'Employee Id should be in between '+appConfig.empStartId+' - '+appConfig.empEndId;
				document.getElementById('empId').focus();
			}else if(searchId != "" && checkRoleExistence(searchId) && $scope.templateTitle == "Add"){
				$scope.alertMsg = 'Employee Id is already assigned a role';
				document.getElementById('empId').focus();
			}else if(empName == ""){
				$scope.alertMsg = "Employee Name is mandatory";
				document.getElementById('empName').focus();
			}else if(empName !="" && !validateTextFields(empName)){
					$scope.alertMsg = "Please enter alphabets only";
					document.getElementById('empName').focus();
			}else if(gender == "") {
				$scope.alertMsg = "Gender is mandatory";
				document.getElementById('gender').focus();
			}else if(empMobileNo == "") {
				$scope.alertMsg = "Mobile Number is mandatory";
				document.getElementById('empMobileNo').focus();
			}else if(checkMobileNoValue(empMobileNo)){
                $scope.alertMsg = "Please enter a valid ten digit mobile number"; 
                document.getElementById('empMobileNo').focus();
            }else if(empEmail == "") {
				$scope.alertMsg = "Email ID is mandatory";
				document.getElementById('empEmail').focus();
			}else if(!validateEmail(empEmail)){
                $scope.alertMsg = "Please enter a valid nisum email id"; 
                document.getElementById('empEmail').focus();
            }
			else if(dateOfJoining == undefined){
				$scope.alertMsg = "Please select a Date Of Joining";
				document.getElementById('dateOfJoining').focus();
			}
			else if(empStatus == 'Active' && $scope.templateTitle != "Add" && subStatusStartDate == undefined && empSubStatus != null && empSubStatus != ''){
				$scope.alertMsg = "Please select a SubStatus start Date";
				document.getElementById('subStatusStartDate').focus();
			}
			else if(empStatus == 'Active' && $scope.templateTitle != "Add" && subStatusEndDate == undefined && empSubStatus != null && empSubStatus != ''){
				$scope.alertMsg = "Please select a SubStatus end Date";
				document.getElementById('subStatusEndDate').focus();
			}
			else if(dateOfBirth != undefined  && !(today.getFullYear() - dateOfBirth.getFullYear() > 20 )){
				$scope.alertMsg = "Please check your date of birth";
				document.getElementById('dateOfBirth').focus();
			}else if(empStatus == undefined){
				$scope.alertMsg = "Please select a employee status";
				document.getElementById('empStatus').focus();
			}else if(functionalGroup == undefined){
				$scope.alertMsg = "Please select a Functional Org";
				document.getElementById('functionalGroup').focus();
			}else if(designation == undefined){
				$scope.alertMsg = "Please select a designation";
				document.getElementById('designation').focus();
			}else if(empCountry == undefined){
               $scope.alertMsg = "Please select a Country";
               document.getElementById('empCountry').focus();
             }else if(empLocation == undefined || empLocation==""){
             	$scope.alertMsg = "Please select a location";
             	document.getElementById('empLocation').focus();
            }else if(employmentType == undefined){
				$scope.alertMsg = "Please select a Employment Type";
				document.getElementById('employmentType').focus();
			}else if(empRole == undefined){
				$scope.alertMsg = "Please select a role";
				document.getElementById('empRole').focus();
			}else if(hasPassort == undefined){
				$scope.alertMsg = "Please select a Passport";
				document.getElementById('hasPassort').focus();
			}
			else if(hasPassort == "Yes" && (passportExpiryDate == undefined  || passportExpiryDate == "")){
				$scope.alertMsg = "Please select the Passport Expiry Date";
				document.getElementById('passportExpiryDate').focus();	
			}
			else if(hasPassort == "Yes" && (passportExpiryDate < today)){
				$scope.alertMsg = "Please select the Future Date for passport Expiry";
				document.getElementById('passportExpiryDate').focus();	
			}
			else if(hasPassort == "Yes" && hasPassort !== undefined && hasB1 == undefined){
				$scope.alertMsg = "Please select a Visa";
				document.getElementById('hasB1').focus();	
			}
			else if(hasPassort == "Yes" && hasPassort !== undefined && hasB1 == "Yes" && (b1ExpiryDate == undefined  || b1ExpiryDate == "")){
				$scope.alertMsg = "Please select the B1 Expiry Date ";
				document.getElementById('b1ExpiryDate').focus();	
			}
			else if(hasPassort == "Yes" && hasPassort !== undefined && hasB1 == "Yes" && (b1ExpiryDate < today)){
				$scope.alertMsg = "Please select the Future Date for B1 Expiry";
				document.getElementById('b1ExpiryDate').focus();	
			}
			else if($scope.templateTitle != "Add" && $scope.exitDate == undefined && $scope.empStatus != "Active"){
				$scope.alertMsg = "Please select the exit date";
				document.getElementById('hasB1').focus();	
			}
			else if($scope.templateTitle != "Add" && $scope.exitDate >= today){
				$scope.alertMsg = "Please select the exit date less  than or equal to current date";
				document.getElementById('hasB1').focus();	
			}
			else if (subStatusStartDate >= subStatusEndDate && empSubStatus != null && empSubStatus != ''){
				 $scope.alertMsg = "Start date should not be greater than end date";
			 }else{
				$scope.alertMsg = "";
				var record = {"employeeId":$scope.empId, "employeeName": $scope.empName, "gender": $scope.gender,"mobileNumber": $scope.empMobileNo, "emailId": $scope.empEmail, 
						"role": $scope.empRole,"country":$scope.empCountry, "empLocation": $scope.empLocation,"designation": $scope.designation,"functionalGroup": $scope.functionalGroup,
						"empStatus": $scope.empStatus,"employmentType": $scope.employmentType,"dateOfJoining":$scope.dateOfJoining,
						"dateOfBirth":$scope.dateOfBirth,"hasPassort":$scope.hasPassort,"hasB1":$scope.hasB1,"passportExpiryDate":$scope.passportExpiryDate,
						"b1ExpiryDate":$scope.b1ExpiryDate, "endDate":$scope.exitDate,"empSubStatus":empSubStatusObj
						};
				if($scope.templateTitle == "Add"){
					addOrUpdateRole(record, $scope.templateTitle);
					$timeout(function(){updateGrid($scope.templateTitle, record)},500);
				}
				else{
					var recordFromDb = {"employeeId":dataToPass.employeeId, "employeeName": dataToPass.employeeName, "gender": dataToPass.gender,"mobileNumber": dataToPass.mobileNumber,"emailId": dataToPass.emailId, 
							"role": dataToPass.role,"country":dataToPass.empCountry, "empLocation": dataToPass.empLocation,"designation": dataToPass.designation,"functionalGroup": dataToPass.functionalGroup,
							"empStatus": dataToPass.empStatus,"empSubStatus":dataToPass.empSubStatus, "employmentType": dataToPass.employmentType,"dateOfJoining":new Date(dataToPass.dateOfJoining),
							"dateOfBirth":new Date(dataToPass.dateOfBirth),"hasPassort":dataToPass.hasPassort,"hasB1":dataToPass.hasB1,"passportExpiryDate":new Date(dataToPass.passportExpiryDate),
							"b1ExpiryDate":new Date(dataToPass.b1ExpiryDate), "endDate":new Date(dataToPass.endDate) ,"empSubStatus": prevEmpSubStatusObj
							};
					objectConstructionBasedOnParameters(recordFromDb);
					objectConstructionBasedOnParameters(record);
					if(myFactory.updateFormDataCheck(record,recordFromDb)){
						addOrUpdateRole(record, $scope.templateTitle);
						$timeout(function(){updateGrid($scope.templateTitle, record)},500);
					}
					else{
						$mdDialog.show($mdDialog.alert({
							skipHide: true,
							title:'Attention',
							textContent: 'There is no data change to Update',
					        ok: 'ok'
					    }))
					}
				}
				
			}
			$scope.errorMessage = true;
		};
		
		objectConstructionBasedOnParameters = function(obj){
			if(obj.hasB1 != "Yes"){
				delete obj['b1ExpiryDate'];	
			}
			
			if(obj.hasPassort != "Yes"){
				delete obj['passportExpiryDate'];
				delete obj['hasB1'];	
				delete obj['b1ExpiryDate'];	
			}
			if(obj.empStatus == "Active"){
				delete obj['endDate'];
			}
			return obj;
		}
		
		
		var confirmationMsg = function(){
			
			$mdDialog.show($mdDialog.confirm({
				skipHide: true,
				textContent: 'Are you sure you want to cancel this?',
				ok: 'Yes',
				cancel: 'No'
			})).then(function () {
				$mdDialog.hide('Cancelled');
			})
		}
		
		$scope.cancel = function() {
			var empSubStatusObj = {
					employeeID: $scope.empId,
					subStatus : $scope.empSubStatus,
					fromDate: $scope.subStatusStartDate,
					toDate: $scope.subStatusEndDate
				};
			var prevEmpSubStatusObj = {
					employeeID: dataToPass.employeeId,
					subStatus : dataToPass.empSubStatus,
					fromDate: (dataToPass.subStatusStartDate == null) ? null : new Date(dataToPass.subStatusStartDate),
					toDate: (dataToPass.subStatusEndDate == null) ? null : new Date(dataToPass.subStatusEndDate)
				};
			var record = {
					"employeeId":$scope.empId, "employeeName": $scope.empName, "gender": $scope.gender,"mobileNumber": $scope.empMobileNo,"emailId": $scope.empEmail, 
					"role": $scope.empRole,"country":$scope.empCountry, "empLocation": $scope.empLocation,"designation": $scope.designation,"functionalGroup": $scope.functionalGroup,
					"empStatus": $scope.empStatus, "empSubStatus":$scope.empSubStatus, "employmentType": $scope.employmentType,"dateOfJoining":$scope.dateOfJoining,
					"dateOfBirth":$scope.dateOfBirth,"hasPassort":$scope.hasPassort,"hasB1":$scope.hasB1,"passportExpiryDate":$scope.passportExpiryDate,
					"b1ExpiryDate":$scope.b1ExpiryDate, "endDate":$scope.exitDate ,"empSubStatus":empSubStatusObj
			};
			var recordFromDb = {
					"employeeId":dataToPass.employeeId, "employeeName": dataToPass.employeeName, "gender": dataToPass.gender,"mobileNumber": dataToPass.mobileNumber,"emailId": dataToPass.emailId, 
					"role": dataToPass.role,"country":dataToPass.empCountry, "empLocation": dataToPass.empLocation,"designation": dataToPass.designation,"functionalGroup": dataToPass.functionalGroup,
					"empStatus": dataToPass.empStatus, "empSubStatus":dataToPass.empSubStatus, "employmentType": dataToPass.employmentType,"dateOfJoining":new Date(dataToPass.dateOfJoining),
					"dateOfBirth":new Date(dataToPass.dateOfBirth),"hasPassort":dataToPass.hasPassort,"hasB1":dataToPass.hasB1,"passportExpiryDate":new Date(dataToPass.passportExpiryDate),
					"b1ExpiryDate":new Date(dataToPass.b1ExpiryDate), "endDate":new Date(dataToPass.endDate) , "empSubStatus":prevEmpSubStatusObj
			};
			objectConstructionBasedOnParameters(recordFromDb);
			objectConstructionBasedOnParameters(record);
			
			if(dataToPass.action=="Add"){
				var employeeForm = myFactory.addFormDataCheck($scope.myForm);
				if(Object.keys(employeeForm).length == 0){
					$mdDialog.hide('Cancelled');
				}
				else{
					confirmationMsg();
				}
			}
			else if(dataToPass.action=="Update"){
				if(myFactory.updateFormDataCheck(record,recordFromDb) != true){
					$mdDialog.hide('Cancelled'); 
				}
				else{
					confirmationMsg();
				}
			}
		
		};
		function updateGrid(action, record){
			if($scope.alertMsg == ""){
				if($scope.result == "Success"){
					if(action == "Add"){
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
			var requestMethod = "";
			var empId = myFactory.getEmpId();
			var urlRequest  = "";
			if(action == "Add"){
				urlRequest = appConfig.appUri+ "employees/"+empId;
				requestMethod ='POST';
			}else if(action == "Update"){
				urlRequest = appConfig.appUri+ "employees/"+empId;
				requestMethod = 'PUT';
			}
			var req = {
				method : requestMethod,
				url : urlRequest,
				headers : {
					"Content-type" : "application/json"
				},
				data : record
			}
			$http(req).then(function mySuccess(response) {
				$scope.result = "Success";
				if(response.data.message == "An Employee is already existed by the Id"){
		    		$scope.alertMsg=response.data.message;
		    	}else{
		    		$scope.alertMsg="";
		    	}
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
	}
});