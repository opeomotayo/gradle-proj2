Project Details & Architecture:

MyTeam application functionality as follows:

     Project Management : Application will help for project management and tracking
     Teams : Application provide detail about team
     Reports : Application provide detail reports on projects and resource utilization
     Biometric Integration: Application is integrated with Biometric system to track employee login hours
     Mail Notification: Provides the option of sending email notifications !!!
     Import Employee Data: HR Admin can bulk import employee data. Bulk Import template EmployeeDetails.xlsx
Technology dependencies
     Spring Boot
     Spring MVC
     Spring Rest
     Spring Security
     Spring Data
     Mongo DB
     Email agent
     Jenkins build and deploy
     Angular JS
     Tomcat server
     Git
Application Stack

    I. Front-end technologies

         HTML: Application views built in HTML
         CSS: Application styling done in CSS
         Angular JS
         SPA: It’s a single page application built in Angular JS (JavaScript, BootStrap, HTML and CSS)
    II.         Back-end technologies

         Spring Boot
         Rest Services
         Google SSO integration.


 Approach to Integration with Upstream/Downstream Systems
      MyTeam application is integrated with Smart-i BioMetric System for attendance details. MyTeam application fetches attendance details from Smart-i BioMetric sql database.

MyTeam application details:

 Application url: http://10.3.45.11.xip.io:8080/myTeam/

 Code base url: https://gitlab.mynisum.com/hr/mytime

 Mongo Database:

     host=10.3.45.127
     port=27017
     database=myteamdb
     username=myteam
     password=xxxxx


       Attendance Database:

      driver=com.microsoft.sqlserver.jdbc.SQLServerDriver
     url = jdbc:sqlserver://10.3.45.218:1433;databaseName=smartiSCC

     username=sa

     password=xxxx


 Run In Local:

     Setup mongo db in local
     Run the initial setup db scripts , Available in code repository under DBScripts folder
     Set up the code in SDE
     Run MyTimeApplication spring boot java class or run using ./gradlew bootRun
     Access the application @ http://localhost:8080/myTeam/

 Deployment Details:
                 Run ./gradlew at project root in terminal, it will generate war file in build/libs folder.

                 Deploy the war file using tomcat admin console http://10.3.45.11.xip.io:8080/manager/html [Password protected nisum/xxxx]

