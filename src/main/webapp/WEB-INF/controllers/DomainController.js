myApp.controller("domainController",
	function($scope, myFactory, exportUiGridService, $mdDialog,$http, appConfig, $timeout, $window, $mdSelect) {
		$scope.records = [];
		$scope.empSearchId = "";
		$scope.parentData = {};
		$scope.isDisabled = false;

		$window.addEventListener('click', function(e) {
			if (e.target.type !== 'search') {
				$mdSelect.hide();
			}

		});

		var getCellTemplate = '<p class="col-lg-12"><i class="fa fa-pencil-square-o fa-2x" aria-hidden="true" id="UpdateBtnVisible" style="font-size:1.5em;margin-top:3px;cursor:pointer;" data-placement="center" title="Edit" onmouseenter="$(this).tooltip(\'show\')" ng-click="grid.appScope.getRowData(row,\'Update\')"></i></p>'
//			+
//		'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i ng-show="row.entity.status == \'InActive\'">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</i><i class="fa fa-minus-circle fa-2x" aria-hidden="true" style="font-size:1.5em;margin-top:3px;cursor:pointer;" data-placement="left" title="Delete" onmouseenter="$(this).tooltip(\'show\')" ng-click="grid.appScope.getRowData(row,\'Delete\')"></i></p>';

		$scope.gridOptions = {

			paginationPageSizes : [ 10, 20, 30, 40, 50, 100 ],
			paginationPageSize : 10,
			pageNumber : 1,
			pageSize : 10,
			enableFiltering : true,
			columnDefs : [
					{
						field : 'domainName',
						displayName : 'Domain',
						enableColumnMenu : false,
						enableSorting : true,
						enableFiltering : true
					},
					{
						field : 'accountName',
						displayName : 'Account',
						enableColumnMenu : false,
						enableSorting : true,
						enableFiltering : true
					},
					{
						field : 'deliveryManagers',
						displayName : 'Delivery Leads',
						cellTemplate : '<div ng-repeat= "deliveryManager in row.entity[col.field]">{{deliveryManager.employeeName}}<span ng-hide="$last">,</span></div>',
						enableColumnMenu : false,
						enableSorting : true,
						enableFiltering : false
					}, {
						field : 'status',
						displayName : 'Status ',
						enableColumnMenu : false,
						enableSorting : true,
						enableFiltering : true
					}, {
						name : 'Actions',
						displayName : 'Actions',
						cellTemplate : getCellTemplate,
						enableColumnMenu : false,
						enableSorting : false,
						enableFiltering : false,
						width : 100
					}
			]
		};

		$scope.getRowData = function(row, action) {
			$scope.parentData.id = row.entity.id;
			$scope.parentData.domainId = row.entity.domainId;
			$scope.parentData.domainName = row.entity.domainName;
			$scope.parentData.AccountName = row.entity.accountName;
			$scope.parentData.AccountId = row.entity.accountId;
			$scope.parentData.deliveryManagers = row.entity.deliveryManagers;

			if (action == "Update")
				$scope.addDomain(action, $scope.parentData);
			else if (action == "Delete")
				$scope.deleteDomain(row);
		}
		$scope.records = [];
		$scope.alertMsg = "";
		$scope.gridOptions.data = $scope.records;

		$scope.getEmployeesToTeam = function() {
			$http(
					{
						method : "GET",
						url : appConfig.appUri
								+ "employees/active/sortByName"
					})
					.then(
							function mySuccess(response) {
								$scope.employees = response.data;

							},
							function myError(response) {
								showAlert("Something went wrong while fetching data!!!");
								$scope.gridOptions.data = [];
							});
		};

		function showAlert(message) {
			$mdDialog.show($mdDialog.alert().parent(
					angular.element(document
							.querySelector('#popupContainer')))
					.clickOutsideToClose(true).textContent(message)
					.ariaLabel('Alert Dialog').ok('Ok'));
		}

		$scope.getDomains = function() {
			$http({
				method : "GET",
				url : appConfig.appUri + "domains"
			})
					.then(
							function mySuccess(response) {
								if(response.data.records.length > 10){
						    		$scope.gridOptions.enablePaginationControls = true;
						    	}
						    	else{
						    		$scope.gridOptions.enablePaginationControls = false;
						    	}

								$scope.gridOptions.data = response.data.records;
							},
							function myError(response) {
								showAlert("Something went wrong while fetching data!!!");
							});
		};

		function refreshPage() {
			$scope.getDomains();
		}

		$scope.addDomain = function(action, userData) {
			$('#home').addClass('md-scroll-mask');
			userData.action = action;
			$mdDialog
					.show(
							{
								controller : AddDomainController,
								templateUrl : 'templates/newDomain.html',
								parent : angular
										.element(document.body),
								clickOutsideToClose : false,
								locals : {
									dataToPass : userData,
									gridOptionsData : $scope.gridOptions.data,
									employees : $scope.employees
								},
							})
					.then(
							function(result) {
								if (result == "Add") {
									showAlert('Domain created successfully');
								} else if (result == "Update") {
									showAlert('Domain updated successfully');
								} else if (result == "Cancelled") {
									
								}
								 else {
									showAlert('Domain assigning/updation failed!!!');
								}
							});
		};

		$scope.deleteDomain = function(row) {
			$('#home').addClass('md-scroll-mask');
			var confirm = $mdDialog.confirm().clickOutsideToClose(
					true).textContent(
					'Are you sure want to Inactive this Domain?').ok(
					'Ok').cancel('Cancel');
			$mdDialog.show(confirm).then(function() {
				deleteDomainRow(row.entity.domainId);
				showAlert("Domain Inactivated successfully");
			}, function() {

			});
		};

		function deleteDomainRow(domainId) {
			var req = {
				method : 'DELETE',
				url : appConfig.appUri + "domains?domainId="
						+ domainId
			}
			$http(req).then(function mySuccess(response) {
				refreshPage();
			}, function myError(response) {
				$scope.result = "Error";
			});
		}
		function updateGrid(action, result) {
			if ($scope.alertMsg == "") {
				if (result == "Success") {
					if (action == "Add") {
						refreshPage();
					} else if (action == "Update") {
						refreshPage();
					}
					$mdDialog.hide(action);
				} else {
					$mdDialog.hide("Error");
				}
			}
		}
		function validateTextFields(name){
			var pattern = /^[a-zA-Z\s]*$/;
			if(pattern.test(name)){
				return true;
			}
			return false;
		}

		function AddDomainController($scope, $mdDialog, dataToPass,gridOptionsData, employees) {
		
			$scope.searchTerm = "";
			$scope.updateSearch = function(e) {
				e.stopPropagation();
			}
			$scope.searchFilter = function(obj) {
				if($scope.searchTerm.length > 3){
		    		var re = new RegExp($scope.searchTerm, 'i');
					return !$scope.searchTerm || re.test(obj.employeeName);
		    	}
		    		return obj;	 
			};
			$scope.clearSearchTerm = function() {
				$scope.searchTerm = '';
			};
			$('input').on('keydown', function(ev) {

				ev.stopPropagation();

			});

			$scope.employeeList = employees;
			$scope.employeeNamesArray = [];
			$scope.employeeModel;
			$scope.selectedEmployeeNames = [];
			$scope.templateTitle = dataToPass.action;
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
			$scope.getSelectedText = function() {
				if ($scope.employeeModel != undefined ) {
					if($scope.employeeModel.length <= 0){
						return "Please select a Delivery Lead";
					}
					return "";
				} else {
					return "Please select a Delivery Lead";
				}
			}
			$scope.persistSelections = function(objModel) {
                $scope.selectedEmployeeNames = [];
                objModel.forEach(function(obj) {
                    $scope.selectedEmployeeNames.push(obj.employeeName);
                });
            }
			$scope.getAccountText = function() {

				if ($scope.AccountInfo !== undefined) {
					return $scope.AccountInfo.accountName;
				}

				else {
					return "Please select account";
				}
			}; 
			isDataUpdated = function () {
				if (dataToPass.action == "Update") {
					$scope.previousData = {
						domainId: dataToPass.domainId,
						accountinfo: dataToPass.AccountName,
						domainname: dataToPass.domainName
					}
					$scope.currentdata = {
						domainId: $scope.domainId,
						accountinfo: $scope.AccountInfo.accountName,
						domainname: $scope.domainName
					}

					var predata = JSON.stringify($scope.previousData);
					var curdata = JSON.stringify($scope.currentdata);
					var exsistingMangersList = dataToPass.deliveryManagers;
					var currentMangersList = $scope.employeeModel;
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
					return true;

				}
			}
            $scope.cancel = function () {
				var showConfirmDialog = false;
				if (dataToPass.action == "Update") {
					if (isDataUpdated() != true) {
						$mdDialog.hide('Cancelled');
					}
					else {
						$mdDialog.show($mdDialog.confirm({
							skipHide: true,
							textContent: 'Are you sure you want to cancel this?',
							ok: 'ok',
							cancel: 'cancel'
						})).then(function () {
							$mdDialog.hide('Cancelled');
						})
					}
				}
				if (dataToPass.action == "Add") {
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
							ok: 'ok',
							cancel: 'cancel'
						})).then(function () {
							$mdDialog.hide('Cancelled');
						})
					}
					else {
						$mdDialog.hide('Cancelled');
					}
				}
			};

			$scope.deliveryHeads = function() {
				var length = $scope.employeeModel.length;
				for (i = 0; i < length; i++) {
					$scope.employeeNamesArray[i] = $scope.employeeModel[i].employeeId;
				}
				return $scope.employeeNamesArray;

			}

			$scope.removeSelectedLead = function(item) {
				var index = $scope.employeeModel.indexOf(item);
				$scope.employeeNamesArray.splice(index, 1);
				$scope.employeeModel.splice(index, 1);

			}

			function updatingGridBasedOnStatus(status, response,result) {
				if (response.message == "Domain has been created") {
					updateGrid("Add", result);
				} else if (response.message == "Domain has been updated") {
					updateGrid("Update", result);
				} else if (response.message == "Domain is already existed") {
					 $scope.alertMsg = "Domain already exist,Please enter new matching";
				}

			}

			$scope.addOrUpdateDomain = function(record,action) {
				if(action == "Update"){
					methodType = "PUT";
				}
				else if(action == "Add"){
					methodType = "POST"
				}
				$http({
					method : methodType,
					url : appConfig.appUri + "domains",
					headers : {
						"Content-type" : "application/json"
					},
					data : record
				})
						.then(
								function mySuccess(response) {
									$scope.result = "Success";
									updatingGridBasedOnStatus(dataToPass.action,response.data,$scope.result);
								},
								function myError(response) {
									showAlert("Something went wrong while fetching data!!!");
								});
			}
			if (dataToPass.action == "Add") {
				$scope.domainName = "";
				$scope.AccountName = "";
				$scope.deliveryManagers = "";
			} else if (dataToPass.action == "Update") {
				$scope.isDisabled = true;
				$scope.id = dataToPass.id;
				$scope.domainId = dataToPass.domainId;
				$scope.domainName = dataToPass.domainName;
				$scope.AccountInfo = {};
				$scope.AccountInfo.accountName = dataToPass.AccountName;
				$scope.AccountId = dataToPass.AccountId;
				var accounts1 = myFactory.getAccounts();
				for (var i = 0; i < accounts1.length; i++) {
					if (accounts1[i].accountName == dataToPass.AccountName) {
						$scope.AccountInfo = accounts1[i];
					}
				}
				for (key in dataToPass.deliveryManagers) {
					$scope.selectedEmployeeNames.push(dataToPass.deliveryManagers[key].employeeName);
				}
			}
			$scope.ifEmployeeNameExists = function(empName) {
				return $scope.selectedEmployeeNames.includes(empName);
			}
			$scope.validateFields = function(action){
				var domainId = $scope.domainId;
				var AccountName = $scope.AccountInfo;
				var domainName = $scope.domainName;
				var deliveryManagers = $scope.employeeModel;
				if(action == "Add"){
				
					if(AccountName == undefined){
						$scope.alertMsg = "Please select a Account";
					document.getElementById('AccountInfo').focus();
					}else if(domainName == "" || undefined){
						$scope.alertMsg = "Please enter a Domain Name";
						document.getElementById('domainName').focus();
					}else if(domainName !="" && !validateTextFields(domainName)){
							$scope.alertMsg = "Please enter alphabets only";
							document.getElementById('domainName').focus();
					}else if(deliveryManagers.length <= 0){
						$scope.alertMsg = "Please select a delivery Lead";
						document.getElementById('selectDeliveryLeads').focus();
					}else {
						$scope.alertMsg = "";
						var record = {
								"domainName" : $scope.domainName,
								"accountId" : $scope.AccountInfo.accountId,
								"deliveryManagers" : $scope.deliveryHeads()
							};
						$scope.addOrUpdateDomain(record,action);
					}
				}else{
					$scope.alertMsg = "";
					var AccountName = $scope.AccountInfo;
					var domainName = $scope.domainName;
					var deliveryManagers1 = $scope.deliveryHeads();
					if(AccountName == undefined){
						$scope.alertMsg = "Please select a Account";
					document.getElementById('AccountInfo').focus();
					}else if(domainName == "" || undefined) {
						$scope.alertMsg = "Please enter a Domain Name";
						document.getElementById('domainName').focus();
					}else if(domainName !="" && !validateTextFields(domainName)){
						$scope.alertMsg = "Please enter alphabets only";
						document.getElementById('domainName').focus();
					}else if(deliveryManagers.length <= 0){
						$scope.alertMsg = "Please select a delivery Lead";
						document.getElementById('selectDeliveryLeads').focus();
					}else {
						$scope.alertMsg = "";
						var record = {
								"domainName" : $scope.domainName,
								"accountName" : $scope.AccountInfo,
								"accountId": $scope.AccountId,
								"deliveryManagers" : $scope.deliveryHeads(),
								"id" : $scope.id,
						        "domainId" : dataToPass.domainId
							};
						if (isDataUpdated() != true) {
							$mdDialog.show($mdDialog.alert({
								skipHide: true,
								title: 'Attention',
								textContent: 'There is no data change to Update',
								ok: 'ok'
							}))
						}
						else {
							$scope.addOrUpdateDomain(record, action);
						}
					}
				}
			};
		}

	});