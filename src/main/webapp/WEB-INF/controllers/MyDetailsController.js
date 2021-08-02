myApp.controller("employeeController", function($scope, $http, myFactory, $mdDialog, appConfig) {
	$scope.records = [];
	$scope.empId = myFactory.getEmpId();
	$scope.empName = myFactory.getEmpName();
	$scope.empEmailId = myFactory.getEmpEmailId();
	$scope.avgLoginHrs = "";
	// Date picker related code
	var today = new Date();
	var dateLessThanToday = new Date(today.getTime() - (1 * 24 * 60 * 60 * 1000));
	var priorDt = new Date(dateLessThanToday.getTime() - (30 * 24 * 60 * 60 * 1000));
	$scope.maxDate = dateLessThanToday;
	$scope.fromDate = priorDt;
	$scope.toDate = dateLessThanToday;
	
	$scope.gridOptions = {
		paginationPageSizes : [ 10, 20, 30, 40, 50, 100],
		paginationPageSize : 10,
	    pageNumber: 1,
		pageSize:10,
		columnDefs : [ 
				{field : 'date',displayName: 'Date', enableColumnMenu: true, enableSorting: true, cellFilter:'date:"dd-MMM-yyyy"'},
				{field : 'loginTime',displayName: 'Login Time', enableColumnMenu: false, enableSorting: false}, 
				{field : 'logoutTime',displayName: 'Logout Time', enableColumnMenu: false, enableSorting: false}, 
				{field : 'durationAtWorkPlace',displayName: 'Duration at Work Place', enableColumnMenu: false, enableSorting: true} 
			]
	};
	$scope.gridOptions.data = [];
	
	$scope.getEmployeeData = function(){
		$scope.avgLoginHrs = "";
		var fromDate = getFormattedDate($scope.fromDate);
		var toDate = getFormattedDate($scope.toDate);
		var empId = $scope.empId;
		$http({
	        method : "GET",
	        url : appConfig.appUri + "effectiveLogin?employeeId=" + empId + "&fromDate="+ fromDate + "&toDate=" + toDate
	    }).then(function mySuccess(response) {
	    	var loginDataList = response.data.data;
	        $scope.gridOptions.data = loginDataList.reverse();
	        loginDataList.length == 0 ? $timeout(function(){showAlert('No data available');},600) : $scope.avgLoginHrs = response.data.averageTime + " Hrs";
	        if(loginDataList.length > 10){
	    		$scope.gridOptions.enablePaginationControls = true;
	    	}
	    	else{
	    		$scope.gridOptions.enablePaginationControls = false;
	    	}
	    }, function myError(response) {
	    	showAlert("Something went wrong while fetching data!!!");
	    	$scope.gridOptions.data = [];
	    });
	}
	
	$scope.refreshPage = function(){
		$scope.fromDate = priorDt;
		$scope.toDate = dateLessThanToday;
		$scope.getEmployeeData();
	};
	
	function getFormattedDate(date){
		var day = date.getDate();
		var month = date.getMonth() + 1;
		var year = date.getFullYear();
		return year + '-' + (month < 10 ? "0" + month : month) + '-'
				+ (day < 10 ? "0" + day : day);
	}

	$scope.validateDates = function(dateValue, from) {
		if(from == "FromDate"){
			var toDat = $scope.toDate;
			var difference = daysBetween(dateValue, toDat);
			if(difference < 0 ){
				showAlert('From Date should not be greater than To Date');
				$scope.fromDate = priorDt;
				$scope.toDate = dateLessThanToday;
			}else{
				$scope.fromDate = dateValue;
				$scope.toDate = toDat;
			}
		}else if(from == "ToDate"){
			var fromDat = $scope.fromDate;
			var differene = daysBetween(fromDat, dateValue);
			if(differene < 0 ){
				showAlert('To Date should not be less than From Date');
				$scope.fromDate = priorDt;
				$scope.toDate = dateLessThanToday;
			}else{
				$scope.fromDate = fromDat;
				$scope.toDate = dateValue;
			}
		}
	};
	
	
	function showAlert(message) {
		$mdDialog.show($mdDialog.alert().parent(
				angular.element(document.querySelector('#popupContainer')))
				.clickOutsideToClose(true).textContent(message).ariaLabel(
						'Alert Dialog').ok('Ok'));
	}
	
	function treatAsUTC(date) {
	    var result = new Date(date);
	    result.setMinutes(result.getMinutes() - result.getTimezoneOffset());
	    return result;
	}

	function daysBetween(fromDate, toDate) {
	    var millisecondsPerDay = 24 * 60 * 60 * 1000;
	    return Math.round((treatAsUTC(toDate) - treatAsUTC(fromDate)) / millisecondsPerDay);
	}
});
