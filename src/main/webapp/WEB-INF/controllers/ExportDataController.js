myApp
		.directive('fileModel', [ '$parse', function($parse) {
			return {
				restrict : 'A',
				link : function(scope, element, attrs) {
					var model = $parse(attrs.fileModel);
					var modelSetter = model.assign;
					element.bind('change', function() {
						scope.$apply(function() {
							modelSetter(scope, element[0].files[0]);
						});
					});
				}
			};
		} ])
		.controller(
				'exportDataController',
				function($scope, myFactory, $mdDialog, $http, appConfig,
						$timeout) {

					$scope.file = '';

					$scope.refreshPage = function() {
						$scope.file = '';
						$('#upload-file-info').html("");
					}
					$scope.uploadFiles = function() {
						var file = $scope.file;
						var empId = myFactory.getEmpId();
						if (file == "" || file.length == 0) {
							showAlert('Please choose a file to import data...');
							$scope.refreshPage();
						} else if (file.name.indexOf(".xls") == -1) {
							showAlert("Please choose an excel file to import data");
							$scope.refreshPage();
						} else {
							showProgressDialog('Please wait while data is imported from file...!!!');
							var formData = new FormData();
							formData.append('file', file);
							$http.post(appConfig.appUri+ "employee/fileUpload?empId="+empId,
								formData,{
										transformRequest : angular.identity,
										headers : {'Content-Type' : undefined },
										transformResponse : [ function(data) {
																return data;
																} ]})
									.then(function mySuccess(response) {
												$mdDialog.hide();
												showAlert(response.data);
												$scope.refreshPage();
											}, function myError(response) {
												$mdDialog.hide();
												showAlert('Something went wrong while importing the data from file...');
												$scope.refreshPage();
											});
						}
					}
					function showAlert(message) {
						$mdDialog.show($mdDialog.alert().parent(
								angular.element(document
										.querySelector('#popupContainer')))
								.clickOutsideToClose(true).textContent(message)
								.ariaLabel('Alert Dialog').ok('Ok'));
					}

					function showProgressDialog(msg) {
						$mdDialog.show({
							templateUrl : 'templates/progressDialog.html',
							controller : ProgressController,
							parent : angular.element(document.body),
							clickOutsideToClose : false,
							locals : {
								dataToPass : msg
							}
						});
					}

					function ProgressController($scope, dataToPass) {
						$scope.progressText = dataToPass;
					}

				});