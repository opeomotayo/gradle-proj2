myApp.controller("openPoolController", function($scope, $http, myFactory, $mdDialog, appConfig, exportUiGridService, $timeout) {
	$scope.records = [];
	var today = new Date();
	var getCellTemplate = '<p class="col-lg-12"><i ng-show="!row.entity.editrow" class="fa fa-pencil-square-o fa-2x" data-placement="center" title="Edit" onmouseenter="$(this).tooltip(\'show\')" aria-hidden="true" style="font-size:1.5em;margin-top:3px;cursor:pointer;" ng-click="grid.appScope.onEdit(row)"></i>'+
	'<i ng-show="row.entity.editrow" class="fa fa-save fa-2x" aria-hidden="true" style="font-size:1.5em;margin-top:3px;cursor:pointer;" data-placement="center" title="Save" onmouseenter="$(this).tooltip(\'show\')" ng-click="grid.appScope.saveRow(row.entity,\'Update\')"></i>'
	+'&nbsp;&nbsp;&nbsp;<i ng-show="row.entity.editrow" id="cancelEdit" class="fa fa-times fa-2x" aria-hidden="true" style="font-size:1.5em;margin-top:3px;cursor:pointer;" data-placement="center" title="Cancel" onmouseenter="$(this).tooltip(\'show\')" ng-click="grid.appScope.cancelEdit(row.entity)"></i></p>';
	
	$scope.gridOptions = {
			paginationPageSizes : [ 10, 20, 30, 40, 50, 100],
			paginationPageSize : 10,
		    pageNumber: 1,
			pageSize:10,
			enableFiltering:true,
			columnDefs : [ 
				{field : 'employeeId',displayName: 'Employee ID', enableColumnMenu: true, enableSorting: true, enableFiltering: true,width:'*'},
				{field : 'employeeName',displayName: 'Employee Name', enableColumnMenu: false, enableSorting: false, enableFiltering: true,width:'*'},
				{field : 'resourceRole',displayName: 'Role', enableColumnMenu: false, enableSorting: false,enableFiltering: true,width:'*'}, 
				{field : 'designation',displayName: 'Designation', enableColumnMenu: false,enableFiltering: false,width:'*'},
				{field : 'billableStatus',displayName: 'Billability', enableColumnMenu: false,enableSorting: false,enableFiltering: true ,width:'*', cellTemplate: '<div class="ui-grid-cell-contents" ng-if="!row.entity.editrow">{{COL_FIELD}}</div><div ng-if="row.entity.editrow" class="grid-Dropdown"><md-select ng-model="MODEL_COL_FIELD" name="empBillableStatus" append-to-body="true">'
                    + '<md-optgroup label="billable statuses "><md-option ng-value="billableStatus " ng-repeat="billableStatus in col.colDef.editDropdownOptionsArray">{{billableStatus}}</md-option>'
                    +'</md-optgroup></md-select></div>',editDropdownOptionsArray: [
                         'Trainee' ,
                        'Non-Billable'
                      ]
				}, 
				{field : 'billingStartDate',displayName: 'Start Date', enableColumnMenu: false, enableSorting: true, enableFiltering: true,width:'*',cellFilter: 'date:"dd-MMM-yyyy"'},
				{ name: 'Actions', displayName: 'Actions', cellTemplate: getCellTemplate, enableColumnMenu: false, enableSorting: false, enableFiltering: false, width:'*' }
				],
		enableGridMenu: true,
	    enableSelectAll: true,
	    exporterMenuExcel:false,
	    exporterMenuCsv:false,
	    exporterCsvFilename: 'openPoolReport.csv',
	    exporterExcelFilename:'OpenPoolReport',
	    exporterPdfDefaultStyle: {fontSize: 9},
	    exporterPdfTableStyle: {margin: [15, 15, 15, 15]},
	    exporterPdfTableHeaderStyle: {fontSize: 10, bold: true, italics: true, color: 'red'},
	    exporterPdfHeader: { text: "Open Pool  Report", style: 'headerStyle' },
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
	$scope.gridOptions.data = [];

	$scope.getOpenPoolRecords = function(type){
		$mdDialog.hide();
		if(type == "onload"){
			showProgressDialog("Fetching data please wait...");
		}
		$http({
	        method : "GET",
	        url : appConfig.appUri + 'resources/project/Nisum0000?status=Active'
	    }).then(function mySuccess(response) {
	    	$mdDialog.hide();
	        $scope.gridOptions.data = response.data.records;
	        if(response.data.records.length > 10){
	    		$scope.gridOptions.enablePaginationControls = true;
	    	}
	    	else{
	    		$scope.gridOptions.enablePaginationControls = false;
	    	}
	    }, function myError(response) {
	    	$mdDialog.hide();
	    	showAlert("Something went wrong while fetching data!!!");
	    	$scope.gridOptions.data = [];
	    });
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
	$scope.parentData = {
			"id":"",
			"employeeId": "",
			"employeeName": "",
			"role": "",
			"designation":"",
			"billableStatus":"",
			"billingStartDate":""
		};
	
    $scope.onEdit = function (rowd) {
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
      	
        $scope.parentData.employeeId = row.employeeId;
		$scope.parentData.employeeName = row.employeeName;
		$scope.parentData.id = row.id;
		$scope.parentData.role = row.resourceRole;
		$scope.parentData.designation = row.designation;
		$scope.parentData.billableStatus = row.billableStatus;
		$scope.parentData.billingStartDate = new Date(row.billingStartDate); 
    };
    
    $scope.rowCompare = function(row) {
    	var allRows = row.grid.rows;
    	for(var i=0; i<allRows.length; i++) {
    		var newIndex = $scope.gridOptions.data.indexOf(allRows[i].entity)
    		if(newIndex == $scope.previousRowIndex){
		    	 allRows[i].entity.billableStatus = $scope.previousRow.billableStatus;
		    	 $scope.previousRow = angular.copy(row.entity);
    		}
    	}
    }
    $scope.cancelEdit = function (row) {
        var index = $scope.gridOptions.data.indexOf(row);
        $scope.gridOptions.data[index].editrow = false;
		   row.billableStatus = $scope.parentData.billableStatus;
    };
	$scope.DataUpdate = function () {
		var data = $scope.parentData;
		$scope.previousData = {
			Billabilitystatus: data.billableStatus,
		},
			$scope.currentData = {
				Billabilitystatus: $scope.empBillableStatus,
			}
		var predata = JSON.stringify($scope.previousData);
		var curdata = JSON.stringify($scope.currentData);
		if (predata == curdata) {
			return false;
		}
		return true;
	}
    $scope.saveRow = function (row,action) {
        var index = $scope.gridOptions.data.indexOf(row);
        $scope.gridOptions.data[index].editrow = false;
		$scope.employeeName = row.employeeName;
		$scope.projectId = row.projectId;
		$scope.employeeId = row.employeeId;
		$scope.designation = row.designation;
		$scope.billingStartDate = row.billingStartDate;
		$scope.billingEndDate = row.billingEndDate;
		$scope.employeeRole = row.resourceRole;
		$scope.empBillableStatus = row.billableStatus;
		$scope.empAllocationStatus = row.status;
		$scope.id = row.id;
		var record = {"id":$scope.id,"employeeId":$scope.employeeId,"projectId":$scope.projectId,"billableStatus":$scope.empBillableStatus,"billingEndDate":$scope.billingEndDate,
				      "resourceRole":$scope.employeeRole,"billingStartDate":$scope.billingStartDate,"status":$scope.empAllocationStatus};
       if(action == 'Update'){
			if ($scope.DataUpdate() != true) {
				$mdDialog.show($mdDialog.alert({
					skipHide: true,
					title: 'Attention',
					textContent: 'There is no data change to Update',
					ok: 'ok'
				}))
			}else {
				updateOpenPoolRecord(record, action,row);
			}
    	   $scope.previousRow = angular.copy(row);
       }
    }
	function updateOpenPoolRecord(record, action,row){
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
			getOpenPoolRecords();
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
    		   row.billableStatus = $scope.parentData.billableStatus;
    	   }
    	   $scope.previousRow = angular.copy(row);
	}
		
}, function myError(response){
	   $scope.result = "Error";
		$mdDialog.show($mdDialog.alert({
			skipHide: true,
			textContent: "Something Went Wrong !!!" ,
			ok: 'ok'
		})).then(function () {
			$scope.myForm.$setPristine();
		})
	   row.billableStatus = $scope.parentData.billableStatus;
});
		
}
    
	
	
});