Prepay Credit Servlet
=========================

This folder contains the Servlet source code. The android application no longer uses this servlet, however I have left
the code here for now for other people's benefit.

Returns your 3 Ireland 3Pay usage information in JSON format.

You can deploy this to any Servlet container e.g. Tomcat.

Usage information can be accessed via HTTP POST and GET, although POST is recommended to avoid credentials appearing in
GET request logs.

* GET:  
```
  http://<url>/My3WebService/FetchUsage?username=<mobile number>&password=<my3 password>
```  

* POST:  
  ```
  http://<url>/My3WebService/FetchUsage  
  ```  
  Post data:  
```
username=<mobile number>  
password=<my3 password>  
```