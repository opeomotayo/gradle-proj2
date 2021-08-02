
myApp.directive('highchartxyz', function () {
	
	
return {
    restrict: 'E',
    template: '<div></div>',
    replace: true,

    link: function (scope, element, attrs) {

        scope.$watch(function () { return attrs.chart; }, function () {

            if (!attrs.chart) return;

            var charts = JSON.parse(attrs.chart);

            $(element[0]).highcharts(charts);

        });
    }
};
});

myApp.controller('Ctrl', function ($scope, $http, $timeout,appConfig) {
	
	$timeout($scope.fetch, 1000);
	$scope.reports=["Employee Overview Report","Billability Report","Billability Monthly Trends"];
 	$scope.getReport = function(){

		if ($scope.report !== undefined) {
			return $scope.report;
		} else {
			return "Please select a report";
		}
	
 	}
 	
 	$scope.triggerRport1 = function(){
 		alert('series clicked');
 	}
 	$scope.triggerRport2 = function(){
 		alert('pie clicked');
 	}
 	function custom(){
 		alert('called');
 	}
 	$scope.customCall = function(){
 		alert('customCall called');
 	}
 	$scope.triggerRport = function(){

		if($scope.report== 'Employee Overview Report'){
		$http({
	        method : "GET",
	        url : appConfig.appUri + "reports/getEmployeesByFunctionalGroup1"
	    }).then(function mySuccess(response) {
	         $scope.options =response.data; 
	        
	        $scope.renderChart = {
	                chart: {
	                    type: 'pie'
	                },
	                title: {
	                    text:'test'
	                },
	                plotOptions: {
	                    pie: {
	                      //  allowPointSelect: true,
	                        cursor: 'pointer',
	                        dataLabels: {
	                            enabled: true,
	                            format: '<b>{point.name}</b>: {point.percentage:.1f} %',
	                            
	                        },
                            events: {
                                click: function(event) {
                                	custom();
                                }
                            }
                        
	                    },
	                    series: {
	                        cursor: 'pointer',
	                        point: {
	                            events: {
	                                click: function() {
	                                	customCall();
	                                }
	                            }
	                        }
	                    }
	                    },
	                series: [{
	                    data: $scope.options
	                }]
	            };
	}, function myError(response) {
	   
	    });
 	}else if($scope.report== 'Billability Report'){
		$http({
	        method : "GET",
	        url : appConfig.appUri + "reports/getBillabilityDetailsByAccount"
	    }).then(function mySuccess(response) {
	          $scope.options = response.data.seriesDataList;
	          /*[{
    	        name: 'Billable',
    	        data: [5, 3, 4, 7]
    	    }, {
    	        name: 'Shadow',
    	        data: [2, 2, 3, 2]
    	    }, {
    	        name: 'Reserved',
    	        data: [3, 4, 4, 2]
    	    },
    	    {
    	        name: 'Bench',
    	        data: [3, 4, 4, 2]
    	    }
    	    
    	    ];*/
	        $scope.catagories=response.data.categoriesList; //['Gap', 'Macys', 'X', 'Y'] 
	        $scope.renderChart = {
                	chart: {
            	        type: 'column'
            		},
                title: {
                	 text: 'Monthly Average Temperature'
                }, 
                    xAxis: {
                    	categories: $scope.catagories
                    },
                    yAxis: {
                    	min: 0,
                    
                        title: {
                            text: 'Total fruit consumption'
                        },
                        stackLabels: {
                            enabled: true,
                            style: {
                                fontWeight: 'bold',
                                color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray'
                            }
                        }
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
            	        headerFormat: '<b>{point.x}</b><br/>',
            	        pointFormat: '{series.name}: {point.y}<br/>Total: {point.stackTotal}'
            	    },
            	    plotOptions: {
            	        column: {
            	            stacking: 'normal',
            	            dataLabels: {
            	                enabled: true,
            	                color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white'
            	            }
            	        },
            	        series:{
            	        	 cursor: 'pointer',
            	        	 point: {
            	                    events: {
            	                        click: function () {
            	                            alert('Category: ' + this.category + ', value: ' + this.y);
            	                        }
            	                    }
            	                }
                        }
            	    },
            	    series: $scope.options
	               
            	      
                };
	}, function myError(response) {
	   
	    });
 	} else if($scope.report== 'Billability Monthly Trends'){
		$http({
	        method : "GET",
	        url : appConfig.appUri + "reports/getBillabilityDetailsByMonth"
	    }).then(function mySuccess(response) {
	          $scope.options = response.data.seriesDataList;
	          /*[{
    	        name: 'Billable',
    	        data: [5, 3, 4, 7]
    	    }, {
    	        name: 'Shadow',
    	        data: [2, 2, 3, 2]
    	    }, {
    	        name: 'Reserved',
    	        data: [3, 4, 4, 2]
    	    },
    	    {
    	        name: 'Bench',
    	        data: [3, 4, 4, 2]
    	    }
    	    
    	    ];*/
	        $scope.catagories=response.data.categoriesList; //['Gap', 'Macys', 'X', 'Y'] 
	        $scope.renderChart = {
                	chart: {
            	        type: 'line'
            		},
                title: {
                	 text: 'Monthly Average Temperature'
                }, 
                    xAxis: {
                    	categories: $scope.catagories
                    },
                    yAxis: {
                    	min: 0,
                    
                        title: {
                            text: 'Total fruit consumption'
                        },
                        stackLabels: {
                            enabled: true,
                            style: {
                                fontWeight: 'bold',
                                color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray'
                            }
                        }
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
            	        headerFormat: '<b>{point.x}</b><br/>',
            	        pointFormat: '{series.name}: {point.y}<br/>Total: {point.stackTotal}'
            	    },
            	    plotOptions: {
            	        column: {
            	            stacking: 'normal',
            	            dataLabels: {
            	                enabled: true,
            	                color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white'
            	            }
            	        },
            	        series:{
                        	events:{
                        		click: function(event) {
                        			alert('test');
                        		}
                        	}
                        }
            	       
            	    },
            	   series: $scope.options
            	   };
	}, function myError(response) {
	   
	    });
 	}
 	}
});
