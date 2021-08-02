myApp.controller("assignAccountsController",function($scope, myFactory, $mdDialog, $http, appConfig, $timeout, $element, $window){
	$scope.records = [];
	$scope.parentData = {
			"accountId": "",
			"accountName": "",
			"industryType":"",
			"status":"",
			"deliveryManagers":[],
			"action":"",
			"clientAddress":""
	};
	var getCellTemplate = '<p class="col-lg-12"><i class="fa fa-pencil-square-o fa-2x" aria-hidden="true" style="font-size:1.5em;margin-top:3px;cursor:pointer;" data-placement="center" title="Edit" onmouseenter="$(this).tooltip(\'show\')" ng-click="grid.appScope.getRowData(row,\'Update\')"></i></p>';
		
//	+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i ng-show="row.entity.status == \'InActive\'">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</i><i class="fa fa-minus-circle fa-2x" aria-hidden="true" style="font-size:1.5em;margin-top:3px;cursor:pointer;" data-placement="left" title="Delete" onmouseenter="$(this).tooltip(\'show\')" ng-click="grid.appScope.getRowData(row,\'Delete\')"></i></p>';
	
	$scope.gridOptions = {
			paginationPageSizes : [10, 20, 30, 40, 50, 100],
			paginationPageSize : 10,
		    pageNumber: 1,
			pageSize:10,
			enableFiltering: true,
			rowHeight:22,
			columnDefs : [
				{field : 'accountName',displayName: 'Account Name', enableColumnMenu: false, enableSorting: false,enableFiltering: true,cellClass: 'grid-align'},
				{field : 'industryType',displayName: 'Industry Type', enableColumnMenu: false, enableSorting: true,enableFiltering: true,cellClass: 'grid-align'}, 
				{field : 'deliveryManagers',displayName: 'Delivery Managers',cellTemplate: '<div ng-repeat= "item in row.entity[col.field]">{{item.employeeName}}<span ng-hide="$last">,</span></div>' ,enableColumnMenu: false, enableSorting: true,enableFiltering: false,cellClass: 'grid-align'}, 
				{field : 'status',displayName: 'Status', enableColumnMenu: false, enableSorting: true,enableFiltering: true,cellClass: 'grid-align'}, 
				{name : 'Actions', displayName: 'Actions',cellTemplate: getCellTemplate, enableColumnMenu: false, enableSorting: false, enableFiltering:false,width:100} 
			]
		};
	$scope.gridOptions.data = $scope.records;
	$scope.getRowData = function(row, action){
		$scope.parentData.id = row.entity.id;
		$scope.parentData.accountId = row.entity.accountId;
		$scope.parentData.accountName = row.entity.accountName;
		$scope.parentData.industryType = row.entity.industryType;
		$scope.parentData.deliveryManagers = row.entity.deliveryManagers;
		$scope.parentData.status = row.entity.status;
		$scope.parentData.clientAddress = row.entity.clientAddress;
		if(action == "Update")
			$scope.addAccount(action, $scope.parentData);
		else if(action == "Delete")
			$scope.deleteAccount(row,action);
	}
	$scope.refreshPage = function(){
		$scope.getAccountDetails();
	}
	$scope.getAccountDetails = function(){
		$http({
	        method : "GET",
	        url : appConfig.appUri + "accounts"
	    }).then(function mySuccess(response) {
	    	if(response.data.records.length > 10){
	    		$scope.gridOptions.enablePaginationControls = true;
	    	}
	    	else{
	    		$scope.gridOptions.enablePaginationControls = false;
	    	}
	    	  $scope.gridOptions.data=response.data.records;
	    	  myFactory.setAccounts(response.data.records);
	    	  for(i=0;i<response.data.records.length;i++){
		    		if(response.data.records[i].status == 'Y'){
			    		  $scope.gridOptions.data[i].status = "Active";
			    	  }else if(response.data.records[i].status == 'N'){
			    		  $scope.gridOptions.data[i].status = "InActive"; 
			    	  }else if(response.data.records[i].status == 'B'){
			    		  $scope.gridOptions.data[i].status = "Bench"; 
			    	  } 
		    	}
	    }, function myError(response) {
	    	showAlert("Something went wrong while fetching data!!!");
	    	$scope.gridOptions.data = [];
	    });
	};
	$scope.addAccount = function(action, userData){
		$('#home').addClass('md-scroll-mask');
		userData.action = action;
		$mdDialog.show ({
            clickOutsideToClose: false,
            scope: $scope,        
            preserveScope: true,           
            templateUrl: 'templates/newAccount.html',
            controller: addController,
            locals:{dataToPass: userData,gridOptionsData: $scope.gridOptions.data},
         }).then(function(result) {
		    	if(result == "Add") {
		    		showAlert('Account assigned successfully');
		    	}
		    	else if(result == "Update") {
		    		showAlert('Account updated successfully');
		    	}
		    	else if(result == "Cancelled") {
		    	
		    	}
		    	else {
		    		showAlert('Account assigning/updation failed!!!');
		    	}
		    });
	}
	function showAlert(message) {
		$mdDialog.show($mdDialog.alert().parent(
				angular.element(document.querySelector('#popupContainer')))
				.clickOutsideToClose(true).textContent(message).ariaLabel(
						'Alert Dialog').ok('Ok'));
	}
	$scope.deleteAccount = function(row,action){
		$('#home').addClass('md-scroll-mask');
	    var confirm = $mdDialog.confirm()
	    	  .clickOutsideToClose(true)
	          .textContent('Are you sure you want to inactive this account?')
	          .ok('Ok')
	          .cancel('Cancel');
	    $mdDialog.show(confirm).then(function() {
	    	deleteAccountRole(row.entity.accountId);
	    	$timeout(function(){updateGridAfterDelete()},500);
	    }, function() {

	    });
	};
	function deleteAccountRole(accountId){
		var req = {
				method : 'DELETE',
				url : appConfig.appUri+ "accounts/"+accountId
			}
			$http(req).then(function mySuccess(response) {
				$scope.result = "Success";
			}, function myError(response){
				$scope.result = "Error";
			});
	}
	function updateGridAfterDelete(){
		if($scope.result == "Success"){
			$scope.refreshPage();
			showAlert('Account is inactivated successfully');
		}else if($scope.result == "Error"){
			showAlert('Something went wrong while deleting the account.')
		}
	}
	
	function addController($scope, $mdDialog, dataToPass, gridOptionsData, $mdSelect) {
		$scope.selectedEmployeeNames = [];
		$scope.accountName;
		$scope.industryType;
		$scope.clientAddress;
		$scope.searchTerm = '';
		$scope.templateTitle=dataToPass.action;
		$scope.accountValidationMessage='';
		$scope.addButtonvisible=false;
		$scope.managerDetails = [];
		$scope.managersSelectedList = [];
        $scope.employeeInTeam = []; 
		$scope.industryTypesList = ['Retail','Finance', 'HealthCare','Government','Insurance'];
		$window.addEventListener('click',function(e){
			if(e.target.type !== 'search'){
				 $mdSelect.hide();
			}
		})
		if(dataToPass.action == "Add"){
			$scope.accountId = "";
			$scope.accountName = "";
			$scope.industryType = "";
			$scope.clientAddress = "";
			$scope.deliveryManagers = [];
			$scope.alertMsg = "";
 			
		}else if(dataToPass.action == "Update"){
			$scope.accountId = dataToPass.accountId;
			$scope.accountName = dataToPass.accountName;
			$scope.industryType = dataToPass.industryType;
			$scope.clientAddress = dataToPass.clientAddress;
			//$scope.managersSelectedList = dataToPass.deliveryManagers;
			for (key in dataToPass.deliveryManagers) {
				$scope.selectedEmployeeNames.push(dataToPass.deliveryManagers[key].employeeName);
			}
		}
		$scope.ifEmployeeNameExists = function(empName) {
			return $scope.selectedEmployeeNames.includes(empName);
		}
		$scope.getDeliveryManagers = function(){
			$http({
		        method : "GET",
		        url : appConfig.appUri + "employees/active/sortByName"
		    }).then(function mySuccess(response) {
				$scope.managerDetails=response.data;
		    }, function myError(response) {
		    	showAlert("Something went wrong while fetching data!!!");
		    	$scope.gridOptions.data = [];
		    });
		};

		$scope.closeSelectBox = function () {
			$mdSelect.hide();
		}
		
		var getDeliveryManagersFromService = function(){
			var managerIds = [];
			dataToPass.deliveryManagers.forEach(function(manager){
				managerIds.push(manager.employeeId);
			})
			return managerIds;
		}
		
		
		var confirmationMsg = function(){
			
			$mdDialog.show($mdDialog.confirm({
				skipHide: true,
				textContent: 'Are you sure you want to cancel this?',
				ok: 'ok',
				cancel: 'cancel'
			})).then(function () {
				$mdDialog.hide('Cancelled');
			})
		}
		
		$scope.cancel = function () {
			
			if (dataToPass.action == "Add") {
				var accountForm = myFactory.addFormDataCheck($scope.myForm);
				if(Object.keys(accountForm).length == 0){
					$mdDialog.hide('Cancelled');
				}
				else{
					confirmationMsg();
				}
			}
			else{
	             	var dataRequired = {"accountName":dataToPass.accountName,"industryType":dataToPass.industryType,"clientAddress":dataToPass.clientAddress,"deliveryManagers":getDeliveryManagersFromService()};
	             	var isFormUpated = myFactory.updateFormDataCheck($scope.myForm,dataRequired);
					if(isFormUpated == true){
						confirmationMsg();
					}
					else{
						$mdDialog.hide('Cancelled');
					}
			}
		}

	    $scope.updateSearch = function (e) {
				e.stopPropagation();
				$scope.getSelectedLead();
	    }
	    $scope.searchFilter = function (obj) {
	    	if($scope.searchTerm.length > 3){
	    		var re = new RegExp($scope.searchTerm, 'i');
				return !$scope.searchTerm || re.test(obj.employeeName);
	    	}
	    		return obj;	 	   
	    };
        $scope.removeSelectedLead = function(item){
            var index = $scope.managersSelectedList.indexOf(item);
            $scope.employeeInTeam.splice(index,1);
            $scope.managersSelectedList.splice(index,1);
        }
        $scope.getSelectedLead = function(){
        	if ($scope.managersSelectedList != undefined ) {
				if($scope.managersSelectedList.length <= 0){
					return "Please select Delivery Manager";
				}
				return "";
			} else {
				return "Please select Delivery Manager";
			}		            
        }
        $scope.persistSelections = function(){
        	$scope.managersSelectedList.forEach(function(manager){
			 $scope.selectedEmployeeNames.push(manager.employeeName);
			 removeDuplicates($scope.selectedEmployeeNames);							
           if(!$scope.employeeInTeam.includes(manager.employeeId)){
				$scope.employeeInTeam.push(manager.employeeId) ;									
			}
		})
        }
		$scope.getIndustryTypeSelected = function(){
			if ($scope.industryType != "") {
				$scope.industryType = $scope.industryType;
				return $scope.industryType;
			} else {
				return "Please select a Industry Type";
			}
		};
	    $scope.clearSearchTerm = function() {
			$scope.searchTerm = '';			
	    };
	    $element.find('input').on('keydown', function(ev) {
	          ev.stopPropagation();
	    });
	  
	    $scope.accountManagers = function(){
			var employeeIdsArray = [];
			for(i=0;i< $scope.managersSelectedList.length;i++){
				 employeeIdsArray[i] =$scope.managersSelectedList[i].employeeId;
			}
			return employeeIdsArray;
	  }
	    
      $scope.validateFields = function(action){
    	  var managersSelectedList = $scope.managersSelectedList;
    	  var accountName = $scope.accountName;
    	  var industryType = $scope.industryType;
    	  var clientAddress = $scope.clientAddress;
    	  
  		  if(action == "Add"){
			 if(accountName == undefined || accountName == ""){
				$scope.alertMsg = "Please enter the account Name";
				document.getElementById('accountName').focus();
			}else if(accountName !="" && !validateTextFields(accountName)){
				$scope.alertMsg = "Please enter alphabets only";
				document.getElementById('accountName').focus();
			}else if(industryType == undefined || industryType == ""){
				$scope.alertMsg = "Please enter the industry type";
				document.getElementById('industryType').focus();
			}else if(clientAddress == undefined || clientAddress == ""){
				$scope.alertMsg = "Please enter the client address";
				document.getElementById('clientAddress').focus();
			}else if(managersSelectedList == undefined || managersSelectedList == ""){
				$scope.alertMsg = "Please select a delivery Manager";
				document.getElementById('selectManager').focus();
				}
			else{
				$scope.alertMsg = "";
				var record = {"accountName":$scope.accountName,"industryType":$scope.industryType,"clientAddress":$scope.clientAddress,"deliveryManagers":$scope.accountManagers()};
		
				addOrUpdateAccount(record,$scope.templateTitle,"N");
				$timeout(function(){updateGrid(action, record)},300);
			}
          }else if(action == "Update"){
  			if(accountName == undefined || accountName == ""){
  				$scope.alertMsg = "Please enter the account Name";
  				document.getElementById('accountName').focus();
  			}else if(accountName !="" && !validateTextFields(accountName)){
				$scope.alertMsg = "Please enter alphabets only";
				document.getElementById('accountName').focus();
  			}else if(industryType == undefined || industryType == ""){
  				$scope.alertMsg = "Please enter the industry type";
  				document.getElementById('industryType').focus();
  			}else if(clientAddress == undefined  || clientAddress == ""){
  				$scope.alertMsg = "Please enter the client address";
  				document.getElementById('clientAddress').focus();
  			}else if(managersSelectedList == undefined || managersSelectedList == ""){
  				$scope.alertMsg = "Please select a delivery Manager";
  	  			document.getElementById('selectManager').focus();
  			}
  			else{
        	  $scope.alertMsg = "";
        	  var record =  {"accountId":$scope.accountId, "accountName":$scope.accountName,"industryType":$scope.industryType,"clientAddress":$scope.clientAddress,"deliveryManagers":$scope.accountManagers()};
              var dataRequired = {"accountName":dataToPass.accountName,"industryType":dataToPass.industryType,"clientAddress":dataToPass.clientAddress,"deliveryManagers":getDeliveryManagersFromService()};
              var isFormUpated = myFactory.updateFormDataCheck($scope.myForm,dataRequired);
				if(isFormUpated == true){
					addOrUpdateAccount(record, action, "U");
					$timeout(function () { updateGrid(action, record) }, 300);
				}
				else{
					$mdDialog.show($mdDialog.alert({
						skipHide: true,
						title: 'Attention',
						textContent: 'There is no data change to Update',
						ok: 'ok'
					}))
				}
				record.id = $scope.parentData.id;
			}
          }
		};
		addOrUpdateAccount = function(record,action,flag){
			var urlRequest  = "";
			var requestMethod = "";
			if(action == "Add"){
				urlRequest = appConfig.appUri+  "accounts";
				requestMethod ='POST';
			}else if (action == "Update"){
				urlRequest = appConfig.appUri+ "accounts/" + record.accountId;
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
				if(response.data.message =='An Account is already existed'){
		    		$scope.alertMsg=response.data.message;
		    	}else{
		    		$scope.alertMsg="";
		    	}	
				}, function myError(response){
				$scope.result = "Error";
			});
		}
		function updateGrid(action, record){
			if($scope.alertMsg == ""){
				if($scope.result == "Success"){
					if(action == "Add" || action == "Delete"){
						$scope.refreshPage();
				    }
					else if(action == "Update"){
						$scope.refreshPage();
					}
					$mdDialog.hide(action);
				}else{
					$mdDialog.hide("Error");
				}
				
			}
		}
		function removeDuplicates(arr){
			return arr.filter(function(item, pos) {
				return arr.indexOf(item) == pos;
			})
		}
		function validateTextFields(name){
			var pattern = /^[a-zA-Z\s]*$/;
			if(pattern.test(name)){
				return true;
			}
			return false;
		}
	}
});

