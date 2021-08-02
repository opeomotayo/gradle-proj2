
myApp.directive('hcPieChart', function () {
                return {
                    restrict: 'E',
                    template: '<div></div>',
                    link: function (scope, element) {
                    	let onLoadSearchedDate = getFormattedDate(scope.searchedReportDate);
                    	getEmployeeDetails(scope,element[0].baseURI+'reports/getPieChartReport?byType=ALL&onDate='+onLoadSearchedDate,'pie',element,"Billability For ALL");
                    	//getEmployeeDetails(scope,element[0].baseURI+'reports/billabilityByFunctionalGroup','column',element,"Billability By Functional Group");
                    	//getEmployeeDetails(scope,element[0].baseURI+'reports/getBillabilityDetailsByAccount','column',element,"Billability By Account");
                    	scope.getBillabilityReportData = function() {
                    		let searchedDate = getFormattedDate(scope.searchedReportDate);
                    		scope.gridOptions.data = [];
                    		scope.gridOptions.enablePaginationControls = false;
                    		let reportType = scope.reportType;
                        	if(reportType && searchedDate) {
                        		scope.errorMessage =false;
                                if(reportType == "Monthly Trends"){
                                	getEmployeeDetails(scope,element[0].baseURI+'reports/getBillabilityDetailsByMonth','line',element,"Billability Monthly Trends");
                                }
                                else {
                                     chartType = 'column';
                                     serviceName = 'getBarChartReport';
                                    if(reportType=='ALL'){
                                        reportTypeApiFormat = 'ALL';
                                        chartType = 'pie';
                                        serviceName = 'getPieChartReport';
                                    }else if(reportType=='I&A'){
                         				reportTypeApiFormat = 'I%26A';
        	            			}else if(reportType == "All Functional Orgs"){
        	            				reportTypeApiFormat = reportType.replace(/\s/g, "");
        	            			}else{
        	            				reportTypeApiFormat = reportType;
        	            			}
                                	getEmployeeDetails(scope,element[0].baseURI+'reports/'+serviceName+'?byType='+reportTypeApiFormat+'&onDate='+searchedDate,chartType,element,"Billability For "+reportType);
                                }
                        	}
                        	else {
                        		scope.errorMessage =true;
                        		scope.alertMsg = 'Please Select Valid Report Type or Date';
                        	}
                    	}
                    }
                }
            }).controller('chartsController', function ($scope, $http, myFactory,exportUiGridService, $mdDialog, appConfig) {

            					$scope.name = [];
            					$scope.records = [];
            	            	$scope.empSearchId = "";
            	            	$scope.reportType = "ALL";
            	            	$scope.reportTypeList = ["ALL","All Functional Orgs", "Account", "Monthly Trends"];
            	            	var today = new Date();
            	            	$scope.searchedReportDate = today;
            	            	$scope.maxReportDate = today;
            	            	$scope.errorMessage = false;


            	            	$scope.getAllOptions = function() {
            	        			$http({
            	        				method: "GET",
            	        				url: appConfig.appUri + "functionalGroups/getAllFunctionalGroups"
            	        			}).then(function mySuccess(response) {
            	        				let functionalGrpsList = $scope.reportTypeList.concat(response.data.records);
            	        				$scope.reportTypeList = functionalGrpsList.filter(e => e !== 'Delivery Org' && e !== 'Global Mobility');
            	        			}, function myError(response) {
            	        				showAlert("Something went wrong while fetching data!!!");
            	        				$scope.gridOptions.data = [];
            	        			});
            	            	}
            	            	$scope.employees = [];
            	            	$scope.projects = [];
            	            	$scope.gridOptions = {
            	            		paginationPageSizes : [ 10, 20, 30, 40, 50, 100],
            	            		paginationPageSize : 10,
            	            	    pageNumber: 1,
            	            	    enableFiltering:true,
            	            		pageSize:10,
            	            		columnDefs : [
            	            			{field : 'employeeId',displayName: 'Employee ID', enableColumnMenu: true, enableSorting: true, enableFiltering:true, width:'*'},
            	            			{field : 'employeeName',displayName: 'Employee Name', enableColumnMenu: false, enableSorting: false, enableFiltering:true,width:'*'},
            	            			{field : 'emailId',displayName: 'Email', enableColumnMenu: false, enableSorting: false, enableFiltering:true, width:'*'},
            	            			{field : 'projectName',displayName: 'Project Name', enableColumnMenu: false, enableSorting: false, enableFiltering:true, width:'*'},
            	            			{field : 'onBehalfOf',displayName: 'Behalf Of', enableColumnMenu: false, enableSorting: false, enableFiltering:true, width:'*'},
            	            			{field : 'billableStatus',displayName: 'Billable Status', enableColumnMenu: false, enableSorting: false, enableFiltering:false, width:'*'},
            	            			{field : 'billingStartDate',displayName: 'Billing Start Data', enableColumnMenu: false, enableSorting: false,cellFilter: 'date:"dd-MMM-yyyy"',enableFiltering:false, width:'*'},
            	            			{field : 'billingEndDate',displayName: 'Billing End Data', enableColumnMenu: false, enableSorting: false,cellFilter: 'date:"dd-MMM-yyyy"',enableFiltering:false, width:'*'},
            	            			{field : 'functionalGroup',displayName: 'Functional Group', enableColumnMenu: false, enableSorting: false, enableFiltering:false, width:'*'}
            	            		],
            	            		enableGridMenu: true,
            	            	    enableSelectAll: true,
            	            	    exporterMenuExcel:false,
            	            	    exporterMenuCsv:false,
            	            	    exporterCsvFilename: 'EmployeeDetails.csv',
            	            	    exporterExcelFilename:'EmployeeDetails',
            	            	    exporterPdfDefaultStyle: {fontSize: 9},
            	            	    exporterPdfTableStyle: {margin: [30, 30, 30, 30]},
            	            	    exporterPdfTableHeaderStyle: {fontSize: 10, bold: true, italics: true, color: 'red'},
            	            	    exporterPdfHeader: { text: "Employee Details", style: 'headerStyle' },
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
            	            	    exporterPdfMaxGridWidth: 500,
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
            	            	$scope.gridOptions.data = $scope.records;
            	            	$scope.gridOptions.enablePaginationControls = false;


            	            	$scope.getMyTeamDetails = function(seriesName, category, optionName,title){
            	            		let searchedDate = getFormattedDate($scope.searchedReportDate);
            	            		if(title.trim() == 'Billability For All Functional Orgs On '+searchedDate ||
            	            				title.trim() == 'Billability For All Functional Orgs On Today' ){
            	            			if(category=='I&A'){
            	            				category = 'I%26A';
            	            			}
                	            		$http({
                	            	        method : "GET",
                	            	        url : appConfig.appUri + "reports/fetchEmployeeDetailsByFGAndBillability?fGroup="+category+"&billableStatus="+seriesName+"&onDate="+searchedDate
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
            	            		}else if(title.trim() == 'Billability For ALL'){

                                                     	            		$http({
                                                     	            	        method : "GET",
                                                     	            	        url : appConfig.appUri + "reports/fetchEmployeesByBillabilityType?billableStatus="+seriesName+"&onDate="+searchedDate
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
                                           }else if(title.trim() == 'Billability For Account On '+ searchedDate ||
            	            				title.trim() == 'Billability For Account On Today'){
                	            		$http({
                	            	        method : "GET",
                	            	        url : appConfig.appUri + "reports/fetchEmployeeDetailsByAccountBillability?account="+category+"&billabilityStatus="+seriesName+"&onDate="+searchedDate
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
            	            		}else if(title.trim() == 'Billability Monthly Trends'){
                	            		$http({
                	            	        method : "GET",
                	            	        url : appConfig.appUri + "reports/fetchEmployeeDetailsByDateBillability?billabilityStatus="+seriesName+"&reportDate="+category
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
            	            		}else {
            	            			var fg = $scope.reportType == 'I&A' ? 'I%26A' : $scope.reportType;
                	            		$http({
                	            	        method : "GET",
                	            	        url : appConfig.appUri + "reports/fetchEmployeeDetailsByFGAccountAndBillability?fGroup="+fg+"&billableStatus="+seriesName+"&acccount="+category+"&onDate="+searchedDate
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
            	            		}

            	            	};

            	            	function showAlert(message) {
            	            		$mdDialog.show($mdDialog.alert().parent(
            	            				angular.element(document.querySelector('#popupContainer')))
            	            				.clickOutsideToClose(true).textContent(message).ariaLabel(
            	            						'Alert Dialog').ok('Ok'));
            	            	}
            	            	 $scope.drawPieChart = function(element,chartName,result,title,categoriesList){
            	            	 Highcharts.chart(element[0], {
                                     chart: {
                                            plotBackgroundColor: null,
                                            plotBorderWidth: null,
                                            plotShadow: false,
                                            type: 'pie',
                                            height: '300px'
                                        },
                                        title: {
                                            text: title
                                        },
                                        credits: {
                                            enabled: false
                                        },
                                       tooltip: false,
                                       plotOptions: {
                                            pie: {
                                                allowPointSelect: false,
                                                cursor: 'pointer',
                                                dataLabels: {
                                                    enabled: true,
                                                    format: '<b>{point.name}</b> {point.y} : {point.percentage:.1f} %'
                                                },
                                                showInLegend: false
                                            },
                                            series:{
                                                events:{

                                                    click: function(event) {
                                                        $scope.getMyTeamDetails(event.point.options.name,'ALL',event.point.options.name,title);
                                                    }
                                                }
                                            }
                                        },
                                        series: [{
                                            name: 'Percentage',
                                            colorByPoint: true,
                                            data: result
                                        }]
                                    });
            	            	 }
            	            	 $scope.drawChart = function(element,chartName,result,title,categoriesList){
            	            		Highcharts.chart(element[0], {
            	            			chart: {
            	            		        type: chartName,
            	            		        height: '275px'
            	            			},
            	            			title: {
            	            		    	 text: title
            	            		    },
            	            		    credits: {
            	            		        enabled: false
            	            		    },
            	            		    xAxis: {
            	            		    	categories: categoriesList,
            	            		    	labels:{
            	            		    		formatter: function() {
            	            		    	          if (chartName != 'pie' || chartName != 'line') {
            	            		    	        	  return this.value;
            	            		    	          } else{
            	            		    	        	  return '(Not Designated)';
            	            		    	          }
            	            		    	        }
            	            		    	},
            	            		    },
            	            			    legend: {
            	            			        align: 'right',
            	            			        x: -30,
            	            			        verticalAlign: 'top',
            	            			        y: 25,
            	            			        floating: true,
            	            			        backgroundColor: (Highcharts.theme && Highcharts.theme.background2) || 'white',
            	            			        borderColor: '#CCC',
            	            			        borderWidth: 1,
            	            			        shadow: false
            	            			    },
            	            			    tooltip: {
            	            			    	formatter: function() {
            	            			         return '<b>'+this.x+'</b><br/>'+
                                                (this.point.percent  ? this.series.name + ':' + this.point.y + ' ( '+this.point.percent.toFixed(2)+' % )' : this.series.name + ':'  + this.point.y);
            	            	                }
            	            			    },
            	            			    plotOptions: {
            	            			    	pie: {
            	            			            allowPointSelect: true,
            	            			            cursor: 'pointer',
            	            			            dataLabels: {
            	            			                enabled: true,
            	            			                format: '<b>{point.name}</b>: {point.percentage:.1f} %',
            	            			                style: {
            	            			                    color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
            	            			                }
            	            			            }
            	            			        },
            	            		            column: {
            	            		                dataLabels: {
            	            		                    enabled: true,
            	            		                    formatter: function () {

            	            		                        return this.point.y;
            	            		                    }
            	            		                }
            	            		            },
            	            			        series:{
            	            		            	events:{

            	            		            		click: function(event) {
            	            		            			$scope.getMyTeamDetails(event.point.series.name,event.point.category,event.point.options.name,title);
            	            		            		}
            	            		            	}
            	            		            }
            	            			    },
            	            		    series:  result
            	            	    });
            	            	}
            			});

function getEmployeeDetails(scope,uri,chart,element,title){
var result = [];
var categoriesList = [];
let displayTitle = '';
    if(title === 'Billability Monthly Trends') {
        displayTitle = title;
    }else if(title === 'Billability For ALL') {
          displayTitle = 'Billability For ALL' ;
    }
    else {
        displayTitle = getFormattedDate(scope.searchedReportDate) == getFormattedDate(new Date()) ? title+ " On Today" : title+ " On " +getFormattedDate(scope.searchedReportDate);
    }
	Highcharts.ajax({
        url: uri,
        success: function(data) {
        	if(chart=='line' || chart == 'column'){
        	result = data.seriesDataList;
        	categoriesList = data.categoriesList;
            scope.drawChart(element,chart,result,displayTitle,categoriesList);
        	}else if(chart == 'pie'){
            	result = data.seriesDataList;
            	//categoriesList = data;
            	scope.drawPieChart(element,chart,result,displayTitle,categoriesList);

            }
        }
    });
}
function getFormattedDate(date){
	var day = date.getDate();
	var month = date.getMonth() + 1;
	var year = date.getFullYear();
	return year + '-' + (month < 10 ? "0" + month : month) + '-'
			+ (day < 10 ? "0" + day : day);
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
