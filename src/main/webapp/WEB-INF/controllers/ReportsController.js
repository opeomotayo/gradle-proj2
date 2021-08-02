myApp
.directive('excelExport',
	    function () {
	      return {
	        restrict: 'A',
	        scope: {
	        	fileName: "@",
	            data: "&exportData"
	        },
	        replace: true,
	        template: '<button class="btn btn-primary btn-ef btn-ef-3 btn-ef-3c mb-10" ng-click="download()">Export to Excel <i class="fa fa-download"></i></button>',
	        link: function (scope, element) {
	        	
	        	scope.download = function() {

	        		function datenum(v, date1904) {
	            		if(date1904) v+=1462;
	            		var epoch = Date.parse(v);
	            		return (epoch - new Date(Date.UTC(1899, 11, 30))) / (24 * 60 * 60 * 1000);
	            	};
	            	
	            	function getSheet(data, opts) {
	            		var ws = {};
	            		var range = {s: {c:10000000, r:10000000}, e: {c:0, r:0 }};
	            		for(var R = 0; R != data.length; ++R) {
	            			for(var C = 0; C != data[R].length; ++C) {
	            				if(range.s.r > R) range.s.r = R;
	            				if(range.s.c > C) range.s.c = C;
	            				if(range.e.r < R) range.e.r = R;
	            				if(range.e.c < C) range.e.c = C;
	            				var cell = {v: data[R][C] };
	            				if(cell.v == null) continue;
	            				var cell_ref = XLSX.utils.encode_cell({c:C,r:R});
	            				
	            				if(typeof cell.v === 'number') cell.t = 'n';
	            				else if(typeof cell.v === 'boolean') cell.t = 'b';
	            				else if(cell.v instanceof Date) {
	            					cell.t = 'n'; cell.z = XLSX.SSF._table[14];
	            					cell.v = datenum(cell.v);
	            				}
	            				else cell.t = 's';
	            				
	            				ws[cell_ref] = cell;
	            			}
	            		}
	            		if(range.s.c < 10000000) ws['!ref'] = XLSX.utils.encode_range(range);
	            		return ws;
	            	};
	            	
	            	function Workbook() {
	            		if(!(this instanceof Workbook)) return new Workbook();
	            		this.SheetNames = [];
	            		this.Sheets = {};
	            	}
	            	 
	            	var wb = new Workbook(), ws = getSheet(scope.data());
	            	/* add worksheet to workbook */
	            	wb.SheetNames.push(scope.fileName);
	            	wb.Sheets[scope.fileName] = ws;
	            	var wbout = XLSX.write(wb, {bookType:'xlsx', bookSST:true, type: 'binary'});

	            	function s2ab(s) {
	            		var buf = new ArrayBuffer(s.length);
	            		var view = new Uint8Array(buf);
	            		for (var i=0; i!=s.length; ++i) view[i] = s.charCodeAt(i) & 0xFF;
	            		return buf;
	            	}
	            	
	        		saveAs(new Blob([s2ab(wbout)],{type:"application/octet-stream"}), scope.fileName+'.xlsx');
	        		
	        	};
	        
	        }
	      };
	    }
	 )
.controller("reportsController", function($scope, $http, myFactory, $mdDialog, appConfig, $timeout, $compile) {
	$scope.records = [];
	$scope.searchId="";
	// Date picker related code
	var today = new Date();
	$scope.maxDate = today;
	$scope.fromDate = today;
	$scope.toDate = today;
	$scope.reportMsg ="Please generate a report for preview.";
	$scope.isDataAvailable  = false ;
	$scope.validateDates = function(dateValue, from) {
		if(from == "FromDate"){
			var toDat = $scope.toDate;
			var difference = daysBetween(dateValue, toDat);
			if(difference < 0 ){
				showAlert('From Date should not be greater than To Date');
				$scope.fromDate = today;
				$scope.toDate = today;
			}else{
				$scope.fromDate = dateValue;
				$scope.toDate = toDat;
			}
		}else if(from == "ToDate"){
			var fromDat = $scope.fromDate;
			var differene = daysBetween(fromDat, dateValue);
			if(differene < 0 ){
				showAlert('To Date should not be less than From Date');
				$scope.fromDate = today;
				$scope.toDate = today;
			}else{
				$scope.fromDate = fromDat;
				$scope.toDate = dateValue;
			}
		}
	};


	$scope.refreshPage = function(){
		$scope.fromDate = today;
		$scope.toDate = today;
		setDefaults();
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


	$scope.pdfUrl = "";
	$scope.scroll = 0;

	$scope.getNavStyle = function(scroll) {
		if (scroll > 100)
			return 'pdf-controls fixed';
		else
			return 'pdf-controls';
	}

	$scope.print = function(pdfFile){
		printJS({ printable: pdfFile });
	}

	var parentData = {
			"empId"		: "",
			"fromDate"	: getFormattedDate($scope.fromDate),
			"toDate"	: getFormattedDate($scope.toDate),
			"fromTime"	: $scope.fromTime,
			"toTime"	: $scope.toTime,
			"toEmail"	: [],
			"ccEmail"	: [],
			"bccEmail"	: []
	};

	$scope.validateEmpId = function(){
		var searchId = $scope.searchId;
		if(searchId !="" && isNaN(searchId)){
			showAlert('Please enter only digits');
			$scope.searchId = "";
			document.getElementById('searchId').focus();
		}else if(searchId != ""&& (searchId.length < 5 || searchId.length > 5)){
			showAlert('Employee ID should be 5 digits');
			$scope.searchId = "";
			document.getElementById('searchId').focus();
		}else if(searchId !="" && !checkEmpIdRange(searchId)){
			showAlert('Employee ID should be in between '+appConfig.empStartId+' - '+appConfig.empEndId);
			$scope.searchId = "";
			document.getElementById('searchId').focus();
		}
	};

	function checkEmpIdRange(searchId){
		return parseInt(searchId) >= appConfig.empStartId && parseInt(searchId) <= appConfig.empEndId;
	}
	
	$scope.showOrHide = function() {
		if (!$scope.isOverride) {
			$scope.fromTime = "";
			$scope.toTime = "";
		}
	}

	$scope.generateReport = function(){
		var isValid = true;
		if ($scope.isOverride) {
			var validationMessage = 'Please Enter ';
			if ($scope.searchId == "") {
				isValid = false;
				validationMessage = validationMessage + 'Employee ID';
			}
			if ($scope.fromTime == undefined || $scope.toTime == undefined) {
				if (!isValid) {
					validationMessage = validationMessage + ', ';
				}
				validationMessage = validationMessage + 'FromTime and ToTime';
				isValid = false;
			}
			if (!isValid) {
				showAlert(validationMessage);
			}
			
		}
		if (isValid) {
			deletePreviousReport();
			$scope.pdfUrl = "";
			$scope.reportMsg ="";
			parentData.empId = $scope.searchId;
			parentData.fromDate = getFormattedDate($scope.fromDate);
			parentData.toDate = getFormattedDate($scope.toDate);
			parentData.fromTime = changeTime($scope.fromTime);
			parentData.toTime = changeTime($scope.toTime);
			parentData.isOverride= $scope.isOverride;
			generatePdfReport(parentData);
			showProgressDialog();
			$timeout(function(){previewPdfReport();},6000);
		}
	};

	function changeTime(time) {
		var today = new Date(time);
		var hours = today.getHours();
		var minutes = Math.round(today.getMinutes());
		var ampm = hours >= 12 ? 'PM' : 'AM';
		var time = hours + ':' + minutes;
		return changeTimeFormat(time, ampm) + ' ' + ampm;
	}
	function changeTimeFormat(time, ampm) {
		function appendZero(n) {
			return (n < 10 ? '0' : '') + n;
		}
		var bits = time.split(':');
		if (ampm == 'PM') {
			bits[0] = bits[0] - 12;
		}
		return appendZero(bits[0]) + ':' + appendZero(bits[1]);
	}

	function deletePreviousReport(){
		var empId = "";
		if(parentData.empId != ""){
			empId = parentData.empId;
		}else{
			empId = $scope.searchId;
		}
		if(empId == "") empId = 0;
		var fileName = empId+"_"+parentData.fromDate+"_"+parentData.toDate;
		$http({
	        method : "GET",
	        url : appConfig.appUri + "deleteReport/" + fileName
	    }).then(function mySuccess(response) {
	 
	    }, function myError(response) {
	    
	    });
	}

	function showProgressDialog(){
		$('#home').addClass('md-scroll-mask');
		$mdDialog.show({
	      templateUrl: 'templates/progressDialog.html',
	      controller: ProgressController,
	      parent: angular.element(document.body),
	      clickOutsideToClose:false
	    });
	}

	function ProgressController($scope) {
		$scope.progressText = "Please wait!!! Report is being generated.";
	}

	function previewPdfReport(){
		var pdfTemplate = '';
		if($scope.pdfUrl != "No data available"){
			pdfTemplate = '<ng-pdf template-url="templates/pdf-viewer.html" canvasid="pdf" scale="page-fit" page=1 style="width:99%;border-radius:5px;"></ng-pdf>';
		}else{
			pdfTemplate = '<p style="color: #fff; font-size: 1.35em; text-align: center;vertical-align:middle;position:relative;top:50%;">No data available for the search criteria...</p>';
		}
		$("#pdfReportPreview").html($compile(pdfTemplate)($scope));
		$mdDialog.hide();
	}

	function generatePdfReport(data){
		var empId = "";
		if(data.empId == ""){
			empId = 0;
		}else{
			empId = data.empId;
		}
		var defaultURL= appConfig.appUri + "attendance/generatePdfReport/" + empId + "/" + data.fromDate + "/" +data.toDate;
		var overrideURL= appConfig.appUri +"attendance/generatePdfReport/" + empId + "/" + data.fromDate + "/" +data.toDate+ "/" +data.fromTime +"/"+data.toTime;
		var url = data.isOverride ? overrideURL:defaultURL;
		$http({   
	        method : "GET",
	        url : url
	    }).then(function mySuccess(response) {
	    	if(response.data[0] == "No data available"){
	    		$scope.pdfUrl = response.data;
	    		$scope.isDataAvailable  = false ;
	    	}else{
	    		$scope.pdfUrl = "reports/"+response.data[0];
	    		$scope.jsonToExport = response.data[1];
	    		excelDataFormation();
	    		$scope.isDataAvailable  = true ;
	    	}
	    }, function myError(response) {
	    	showAlert("Something went wrong while generating report!!!");
	    	$scope.pdfUrl = "";
	    });
	}

//	function generateExcelReport(id,data) {
//		debugger;
//		
//		var defaultURL= appConfig.appUri + "attendance/employeeLoginReportBasedOnDateTime?empId=" + id + "&fromDate=" + data.fromDate + "&toDate=" + data.toDate + "&fromTime=" + data.fromTime + "&toTime=" + data.toTime
//		$http({   
//	        method : "GET",
//	        url : defaultURL
//	    }).then(function mySuccess(response) {
//	    	if(response.data){
//	    		$scope.jsonToExport = response.data;
//	    		excelDataFormation();
//	    	}
//	    }, function myError(response) {
//	    	showAlert("Something went wrong while generating report!!!");
//	    	$scope.pdfUrl = "";
//	    });
//	}
	
	function excelDataFormation(){
		// Prepare Excel data:
		$scope.fileName = "report";
		$scope.exportData = [];
	  // Headers:
		
		$scope.exportData.push([ "ID", "Name", "Date", "Login Time", "Logout Time", "Total Hours"]);
	  // Data:
		angular.forEach($scope.jsonToExport, function(value, key) {
	    $scope.exportData.push([value.employeeId, value.employeeName,value.dateOfLogin, value.firstLogin,value.lastLogout,value.totalLoginTime]);
		});
	}
	
	function getFormattedDate(date){
		var day = date.getDate();
		var month = date.getMonth() + 1;
		var year = date.getFullYear();
		return year + '-' + (month < 10 ? "0" + month : month) + '-'
				+ (day < 10 ? "0" + day : day);
	}

	$scope.sendEmail = function(ev){
		$('#home').addClass('md-scroll-mask');
		parentData.toEmail = [];
		parentData.ccEmail = [];
		$mdDialog.show({
		      controller: DialogController,
		      templateUrl: 'templates/emailTemplate.html',
		      parent: angular.element(document.body),
		      targetEvent: ev,
		      clickOutsideToClose:true,
		      locals:{dataToPass: parentData},
		    })
		    .then(function(result) {
		    	if(result.data == "Success"){
		    		showAlert('Report has been emailed successfully to the recepient(s)');
		    		deleteReport($scope.pdfUrl);
		    	}
		    	else if(result.data == "Cancelled" || result.data == undefined){
		    	
		    	}
		    	else{
		    		showAlert("Something went wrong while sending email!!!");
		    	}
		    });
		  };

	  $scope.cancel = function() {
	    $mdDialog.hide('Cancelled');
	  };

	  function deleteReport(pdfReport){
		  var fileName = pdfReport.substring(pdfReport.indexOf("/")+1,pdfReport.indexOf("."));
		  $http({
		        method : "GET",
		        url : appConfig.appUri + "deleteReport/" + fileName
		    }).then(function mySuccess(response) {
		    	
		    }, function myError(response) {
		    	
		    });
		  setDefaults();
	  }

	  function setDefaults(){
		  var defaultTemplate = '<p id="reportMsg" style="color: #fff; font-size: 1.35em; opacity: 0.5; text-align:center;vertical-align:middle;position:relative;top:50%;'+
			  '">Please generate a report for preview.</p>';
		  $("#pdfReportPreview").html($compile(defaultTemplate)($scope));
		  $scope.searchId="";
	  }

	 function DialogController($scope, $mdDialog, dataToPass) {
		 $scope.toEmail = "";
		 $scope.ccEmail = "";
		 $scope.invalidMsg = "";
		 $scope.showLoader = false;

		 $scope.hide = function() {
			 $mdDialog.hide('Cancelled');
		 };

		 $scope.cancel = function() {
			 $mdDialog.hide('Cancelled');
		 };

		 $scope.send = function() {
			 if($scope.invalidMsg == ""){
				$scope.showLoader = true;
				var req = {
					method : 'POST',
					url : appConfig.appUri+"sendEmail",
					data : dataToPass
				}
				$http(req).then(
				 function onSuccess(response) {
					 $scope.showLoader = false;
					 $mdDialog.hide(response);
				 },function onError(response) {
					 $scope.showLoader = false;
					 $mdDialog.hide(response);
				});
			 }
		 };

		 $scope.validateEmail = function(from, elementId){
			 var emailId = "";
			 if(from == "TO"){
				 emailId = $scope.toEmail;
				 dataToPass.toEmail = [];
			 }else if(from == "CC"){
				 emailId = $scope.ccEmail;
				 dataToPass.ccEmail = [];
			 }
			 if(emailId != ""){
				 if(emailId.indexOf(",") != -1){
					 var emails = emailId.split(",");
					 for(var i=0;i<emails.length;i++){
						 if(emails[i].trim() != ""){
							 if(validateEmail(emails[i].trim())){
								 $scope.invalidMsg = "";
								 if(from == "TO") dataToPass.toEmail.push(emails[i].trim());
								 else if(from == "CC") dataToPass.ccEmail.push(emails[i].trim());
							 }else{
								$scope.invalidMsg = "Please enter only valid email id(s)!";
								document.getElementById(elementId).focus();
							 }
						 }
					 }
				 }else{
					 if(validateEmail(emailId.trim())){
						 $scope.invalidMsg = "";
						 if(from == "TO") dataToPass.toEmail.push(emailId.trim());
						 else if(from == "CC") dataToPass.ccEmail.push(emailId.trim());
					 }else{
						 $scope.invalidMsg = "Please enter only valid nisum email id(s)!";
						 document.getElementById(elementId).focus();
					 }
				 }
			 }
		 };

		 function validateEmail(emailId){
			 var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
			 if(re.test(emailId)){
		        if(emailId.indexOf("@nisum.com", emailId.length - "@nisum.com".length) !== -1){
		        	return true;
		        }
			 }
			 return false;
		 }

		 $scope.validateFields = function(){
			 var toEmail = $scope.toEmail;
			 if(toEmail == ""){
				 $scope.invalidMsg = "To Email is mandatory";
				 document.getElementById('toEmail').focus();
			 }else{
				 $scope.validateEmail("TO",'toEmail');
				 $scope.send();
			 }
		 };
	 }
});
