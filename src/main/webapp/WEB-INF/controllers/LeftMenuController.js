myApp.controller("leftmenuController",function($scope, myFactory, $compile){
	$scope.empId = myFactory.getEmpId();
	$scope.empName = myFactory.getEmpName();
	$scope.empEmailId = myFactory.getEmpEmailId();
	$scope.role = myFactory.getEmpRole();
	$scope.menuItems = myFactory.getMenuItems();
	$scope.manageGroup = [];
	$scope.reportsGroup = [];
	$scope.nonManageGroup = [];
	$scope.setTemplateUrl = function(path){
		var element = document.getElementById('main');
		path = "'"+path+"'";
		  var currentEle = event.currentTarget; 
		  $('.navbarMenu').removeClass('activeMenu');
		  $(currentEle).addClass('activeMenu');
		element.setAttribute("ng-include", path);
		var newTemplate = angular.element(element);
		$('#main').html(newTemplate);
		$compile($('#main'))($scope)
	}

			 $scope.manageGroup = [];
			 $scope.nonManageGroup = [];
	    for(var i = 0; i < $scope.menuItems.length; i++){
					if($scope.menuItems[i].menu.indexOf('Manage') !== -1 || $scope.menuItems[i].menu.indexOf('Pool') !== -1) {
						$scope.manageGroup.push($scope.menuItems[i]);
					}
					else if($scope.menuItems[i].menu.indexOf('Report') !== -1 || 
							$scope.menuItems[i].menu.indexOf('Efforts') !== -1 || 
							$scope.menuItems[i].menu.indexOf('Dashboard') !== -1 ||
							$scope.menuItems[i].menu.indexOf('Change') !== -1) {
						$scope.reportsGroup.push($scope.menuItems[i]);
					}
					else{
						$scope.nonManageGroup.push($scope.menuItems[i]);
					}
			}
	    $scope.custom = true;
	    $scope.customReport = true;
			$scope.toggleManage = function(){
				$scope.custom = $scope.custom === false ? true: false;
				if(!$scope.customReport){
					$scope.customReport = true;
				}
			}

			$scope.toggleReport = function(){
				$scope.customReport = $scope.customReport ===false ? true : false;
				if(!$scope.custom){
					$scope.custom = true;
				}
			}
			$scope.togglenonManage = function(){
				if(!$scope.custom){
					$scope.custom = true;
				}
				else if(!$scope.customReport){
					$scope.customReport = true;
				}
			}
});