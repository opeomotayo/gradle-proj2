myApp.controller("profileController", function($scope, $http, myFactory, $mdDialog, appConfig,$window) {
	$scope.records = [];
	$scope.empId = myFactory.getEmpId();
	$scope.empName = myFactory.getEmpName();
	$scope.empEmailId = myFactory.getEmpEmailId();
	$scope.isResumeUploaded = false;
	
	$scope.getProfileData = function(){
		var empId = $scope.empId;
		$http({
	        method : "GET",
	        url : appConfig.appUri + "employees/employeeId/" + empId
	    }).then(function mySuccess(response) {
	        $scope.profile = response.data.records;
	        $scope.isResumeUploaded =  response.data.records.updateProfile ?  true : false; 
	      }, function myError(response) {
	    	showAlert("Something went wrong while fetching data!!!");
	    });
	}
	
	$scope.refreshPage = function(){
			$scope.getProfileData();
	};
	$scope.updateProfile = function(){
		$('#home').addClass('md-scroll-mask');
		$mdDialog.show({
		      controller: UpdateProfileController,
		      templateUrl: 'templates/updateProfile.html',
		      parent: angular.element(document.body),
		      clickOutsideToClose:false,
		      locals:{dataToPass: $scope.profile},
		    })
		    .then(function(result) {
		    	if(result == "Success") showAlert('Profile updated successfully');
		    	else if(result == "Error") showAlert('Profile updation failed!!!');
		    	$scope.refreshPage();
		    });
	};
	$scope.download = function(){
    				var empId = $scope.empId;
        		    var  urlRequest = appConfig.appUri+ "/employees/downloadFile/"+empId;
        		    $http({
        	        method : "GET",
        	        url : urlRequest,
        	        headers:{'Content-type': 'application/msword'},
                    responseType : 'arraybuffer',
        	             }).then(function mySuccess(response) {
        	                          var value=response.data;
        	                          var fileName=empId+".doc"
        	                          var file = new Blob([value], {type: 'application/msword'});
                                      var url = window.URL || window.webkitURL;
                                      var downloadLink = angular.element('<a></a>');
                                          downloadLink.attr('href',url.createObjectURL(file));
                                          downloadLink.attr('target','_self');
                                          downloadLink.attr('download', fileName);
                                          downloadLink[0].click();

        	                }, function myError(response) {
        	    	        showAlert(" Unable to find the profile , Please Upload profile  ");
        	                     });
        	};
	
	function UpdateProfileController($scope, $mdDialog, dataToPass) {
		$scope.profile = dataToPass;
		$scope.technologies=myFactory.getTechnologies();
		$scope.designations=myFactory.getDesignations();
		$scope.baseTechnology=(dataToPass.baseTechnology == null ? undefined: dataToPass.baseTechnology);
		$scope.mobileNumber=dataToPass.mobileNumber;
		$scope.alternateMobileNumber=dataToPass.alternateMobileNumber;
		$scope.personalEmailId=dataToPass.personalEmailId;
		$scope.technologyKnown=dataToPass.technologyKnown;
		$scope.designationEmp=(dataToPass.designation == null ? undefined: dataToPass.designation);
		$scope.uploadFile=dataToPass.uploadFile;
		$scope.cancel = function() {
		    $mdDialog.hide();
		};
		
		$scope.getSelectedTech = function(){
			if ($scope.baseTechnology !== undefined) {
				return $scope.baseTechnology;
			} else {
				return "Please select primary skill";
			}
		};
		$scope.getDesignationText = function(){
			if ($scope.designationEmp !== undefined) {
				return $scope.designationEmp;
			} else {
				return "Please select designation";
			}
		};
		$scope.uploadFile = function(){
			
                				var file =$scope.file;
                				var ext= this.file ? this.file.name.split(".").pop() : '';
                				if(this.file !== undefined  && (ext =="docx" || ext =="doc" ||  ext =="pdf")){
                    				var formData = new FormData();
                    				formData.append('file',$scope.file);
                    				
                                      var empid=$scope.profile.employeeId

                                    //  var record = {"employeeId":$scope.profile.employeeId, "mobileNumber": $scope.mobileNumber, "alternateMobileNumber": $scope.alternateMobileNumber, "personalEmailId": $scope.personalEmailId, "baseTechnology": $scope.baseTechnology, "technologyKnown": $scope.technologyKnown};
                                    var urlRequest  = "";
                    					urlRequest = appConfig.appUri+ "/employees/uploadProfile/"+$scope.profile.employeeId;

                    				var req = {
                    					method : 'POST',
                    					url : urlRequest,
                    					headers : {
                    						"Content-type" : undefined
                    					},
                    					transformRequest: angular.identity,
                    					data : formData
                    				}
                    				$http(req).then(function mySuccess(response) {
                    					$mdDialog.hide('Success');
                    					$scope.dataToPass=response.data;
                    				}, function myError(response){
                    					$mdDialog.hide('Error');
                    				});
                					
//                                    showAlert("Please Upload Resume in  Doc, Docx or Pdf format");
                                   }
                				else {
                					$scope.alertMsg = "Please Upload file in  Doc, Docx or Pdf format";
                				}



                		};
		$scope.validateFields = function(){
				var mobileNumber = $scope.mobileNumber;
				$scope.alertMsg = "";
				var record = {"employeeId":myFactory.getEmpId(), "mobileNumber": mobileNumber, "alternateMobileNumber": $scope.alternateMobileNumber, "personalEmailId": $scope.personalEmailId, "baseTechnology": $scope.baseTechnology, "technologyKnown": $scope.technologyKnown};
                var urlRequest  = "";
					urlRequest = appConfig.appUri+ "/employees/updateProfile";
				
				var req = {
					method : 'POST',
					url : urlRequest,
					headers : {
						"Content-type" : "application/json"
					},
					data : record
				}
				$http(req).then(function mySuccess(response) {
					$mdDialog.hide('Success');
					$scope.dataToPass=response.data;
				}, function myError(response){
					$mdDialog.hide('Error');
				});
			
				
		};
	}

	function showAlert(message) {
		$mdDialog.show($mdDialog.alert().parent(
				angular.element(document.querySelector('#popupContainer')))
				.clickOutsideToClose(true).textContent(message).ariaLabel(
						'Alert Dialog').ok('Ok'));
	}
});
