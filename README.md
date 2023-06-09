This a school project for a Database Systems classthat demonstrates my proficiency with SQL. We interfaced a web application with the Chinook database for a digital media store. The skeleton framework for the web application was provided by the professor, but we implemented all of the SQL functionality. This was an open-ended project, but must include the following:

* All entities in the chinookdb must be surfaced in the web application
* The Employee entity must have all CRUD operations surfaced
* A tree UI of the Employee hierarchy
* Track search with at least two inputs must be implemented
* At least one report-like page must exist that makes use of a `GROUP BY` clause
* Implement at least one Redis cache

To test out this web application, download the entire repository and do the following:

1. Run /sql-project/src/main/java/edu/montana/csci/csci440/Server.java
2. Determine the address of the server from the output of running Server.java: "Started ServerConnector@285e598b{HTTP/1.1,[http/1.1]}{0.0.0.0:XXXX}". See screenshot "server-address.png"
3. Open a web browser and enter the following url, replacing XXXX with the server address from the previous step: "localhost:XXXX"
4. Browse the web application

Or to just view the SQL and/or Java code, navigate this repository to /sql-project/src/main/java/edu/montana/csci/csci440/model and find the various files corresponding to their web app pages.
