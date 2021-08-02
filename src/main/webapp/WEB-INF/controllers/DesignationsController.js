myApp.controller("designationsController", function ($scope, appConfig, $http, uiGridConstants, $mdDialog) {

	var TEXT_ENABLE = 'Enable';
	var TEXT_DISABLE = 'Disable';
	var TEXT_ENABLED = 'Enabled';
	var TEXT_DISABLED = 'Disabled';

	var actionsCellTemplate = '<div class="action-cell-template text-align-center" ng-show="grid.appScope.showActions(row)"><span id="action-container"><md-switch ng-model="row.entity.active" aria-label="Finished?" ng-change="grid.appScope.changeDesignationStatus(row,$event)" class="no-margin"></md-switch>  <i class="fa fa-trash fa-2x delete-action-icon" aria-hidden="true" ng-show="!row.entity.active" ng-click="grid.appScope.deleteDesignation(row,$event)"></i></span></div>'


	$scope.recordsToShow = "Active";
	$scope.allRecords=[];
	$scope.activeRecords = [];
	$scope.inactiveRecords = [];

	$scope.gridOptions = {
		paginationPageSizes: [10, 20, 30, 40, 50, 100],
		paginationPageSize: 10,
		pageNumber: 1,
		pageSize: 10,
		enableFiltering: true,
		headerCellClass: 'text-align-center',
		columnDefs: [
			{
				field: 'designationName',
				displayName: 'Designation',
				enableColumnMenu: false,
				enableSorting: true,
				sort: { direction: uiGridConstants.ASC, priority: 1 },
				enableFiltering: true,
				headerCellClass: 'text-align-center'
			},
			{
				field: 'designationCount',
				displayName: 'Employee Count',
				type: 'number',
				enableColumnMenu: false,
				enableSorting: true,
				sortPriorityVisible:false,
				sort: { direction: uiGridConstants.DESC, priority: 0 },
				headerCellClass: 'text-align-center',
				cellClass: 'text-align-center'
			},
			{
				field: 'designationId',
				name: 'Actions',
				displayName: 'Actions',
				cellTemplate: actionsCellTemplate,
				enableColumnMenu: false,
				enableSorting: false,
				enableFiltering: false,
				headerCellClass: 'text-align-center'
			}
		],
		onRegisterApi: function (gridApi) {
			$scope.gridApi = gridApi;
		}
	};

	$scope.gridOptions.data = [];

	$scope.getDesignationsWithEmployeeCount = function () {
		$http.get(appConfig.appUri + "designations")
			.then(function (response) {
				let data = response.data.records;
				for (element of data) {
					$scope.allRecords.push(element);
					if (element.active) $scope.activeRecords.push(element)
					else $scope.inactiveRecords.push(element)
				}

				$scope.gridOptions.data = $scope.activeRecords;
				if (data.length > 10) {
					$scope.gridOptions.enablePaginationControls = true;
				}
				else {
					$scope.gridOptions.enablePaginationControls = false;
				}
			}, function (response) {
				showAlert("Something went wrong while fetching data!!!");
				$scope.gridOptions.data = [];
			});
	};

	$scope.showRecords = function (recordsToShow) {
		if (recordsToShow==="All") $scope.gridOptions.data = $scope.allRecords;
		else if (recordsToShow === "Active") $scope.gridOptions.data = $scope.activeRecords;
		else $scope.gridOptions.data = $scope.inactiveRecords;
	};

	$scope.showAddDesignationDialog = function () {
		$mdDialog
			.show(
				{
					controller: AddDesignationController,
					templateUrl: 'templates/addDesignation.html',
					parent: angular.element(document.body),
					clickOutsideToClose: false
				})
			.then(
				function (result) {
				});
	};

	function AddDesignationController($scope, $mdDialog) {

		$scope.addDesignationDialogTitle = "Add Designation";

		$scope.newDesignationName = "";
		$scope.newDesignationValidationMessage = "";

		$scope.addDesignation = function () {
			let designationName = $scope.newDesignationName.trim();
			if (isDesignationNameValid(designationName)) {
				$http({
					method: 'POST',
					url: appConfig.appUri + "designations",
					data: designationName,
					headers: {
						'Content-type': 'text/plain'
					}
				})
					.then(function (response) {
						let data = response.data.records;						
						addElementsToActiveRecords(data);
						addElementsToAllRecords(data);
						closeDialog();
						showAlert("Designation with name \'" + data.designationName + "\' created successfully");
					}, function (response) {
						$scope.newDesignationValidationMessage = response.data.description;
					});
			}
		}


		var closeDialog = function () {
			$scope.cancel();
		}

		$scope.cancel = function () {
			resetTempFields();
			$mdDialog.hide();
		}

		var isDesignationNameValid = function (designationName) {
			let pattern = /^(?!\d+$)(?:[a-zA-Z0-9][a-zA-Z0-9\s-]*)?$/;
			if (!Boolean(designationName)) {
				$scope.newDesignationValidationMessage = "Designation name can't be empty";
				return false;
			}
			else if (!pattern.test(designationName)) {
				$scope.newDesignationValidationMessage = "Designation name can contain alphanumeric, space and hyphen(-) value";
				return false;
			}
			return true;
		}

		var resetTempFields = function () {
			$scope.newDesignationName = "";
			$scope.newDesignationValidationMessage = "";
		}
	};

	$scope.changeDesignationStatus = function (row, ev) {
		let designationData = row.entity;
		let action = (!designationData.active) ? TEXT_DISABLE : TEXT_ENABLE;

		var confirmChangeDesignationStatus = $mdDialog.confirm()
			.title('Change Designation')
			.textContent(action + ' \'' + designationData.designationName + '\'?')
			.ariaLabel('Lucky day')
			.targetEvent(ev)
			.ok('CONFIRM')
			.cancel('cancel');

		$mdDialog.show(confirmChangeDesignationStatus)
			.then(function () {
				$http({
					method: 'PATCH',
					url: appConfig.appUri + "designations/status/" + designationData.designationId,
					data: action,
					headers: {
						'Content-type': 'text/plain'
					}
				})
					.then(function (response) {	
						console.log(response);		
						changeStatusInRecords(designationData);	
						showAlert("Designation \'" + designationData.designationName + "\' " + (designationData.active ? TEXT_ENABLED : TEXT_DISABLED));
					}, function (response) {
						console.log(response);
						designationData.active=!designationData.active;
						showAlert(response.data.description);
					});
			}, function () {
				designationData.active=!designationData.active;
			});
	}

	$scope.deleteDesignation = function (row, ev) {
		designationData = row.entity;
		var confirmDelete = $mdDialog.confirm()
			.title('Delete Designation')
			.textContent('Would you like to delete the designation \'' + designationData.designationName + '\'?')
			.ariaLabel('Lucky day')
			.targetEvent(ev)
			.ok('CONFIRM')
			.cancel('cancel');

		$mdDialog.show(confirmDelete)
			.then(function () {
				$http({
					method: 'DELETE',
					url: appConfig.appUri + "designations/" + designationData.designationId,
					responseType: 'text'
				})
					.then(function (response) {
						console.log(response);						
						removeElementsFromInactiveRecords(row.entity);
						removeElementsFromAllRecords(row.entity);
						showAlert("Designation \'" + row.entity.designationName + "\' deleted");
					}, function (response) {
						console.log(response);
						showAlert(response.data.description);
					});
			}, function () {
				//	alert("It Worked");
			});
	};

	$scope.showActions = function (row) {
		return row.entity.designationCount == 0;
	}

	var showAlert = function (message) {
		$mdDialog.show($mdDialog.alert().parent(
			angular.element(document
				.querySelector('#popupContainer')))
			.clickOutsideToClose(true).textContent(message)
			.ariaLabel('Alert Dialog').ok('Ok'));
	};

	var changeStatusInRecords=function (designation){
		if (designation.active) {
			removeElementsFromInactiveRecords(designation);
			addElementsToActiveRecords(designation);
		} else {							
			removeElementsFromActiveRecords(designation);
			addElementsToInactiveRecords(designation);
		}
	}

	var addElementsToAllRecords = function (element) {
		$scope.allRecords.push(element);
	}

	var addElementsToActiveRecords = function (element) {
		$scope.activeRecords.push(element);
	}

	var addElementsToInactiveRecords = function (element) {
		$scope.inactiveRecords.push(element)
	}

	var removeElementsFromAllRecords = function (element) {
		var index = $scope.allRecords.indexOf(element);
		$scope.allRecords.splice(index, 1);
	}

	var removeElementsFromActiveRecords = function (element) {
		var index = $scope.activeRecords.indexOf(element);
		$scope.activeRecords.splice(index, 1);
	}

	var removeElementsFromInactiveRecords = function (element) {
		var index = $scope.inactiveRecords.indexOf(element);
		$scope.inactiveRecords.splice(index, 1);
	}


});