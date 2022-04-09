Pigeon Appointments is a GUI based desktop application meant for scheduling and tracking both appointments and clients,
with consideration given to users operating in timezones across the globe and using different languages (although the
initial release only supports English and French on a limited scale).
- Project Github Page: https://github.com/builditbear/pigeon-appointments

- Author: Brandon Chavez
- Author Contact Info: bchav23@wgu.edu or brandon.nan.chavez@gmail.com
- Application Version: 1.0
- Publish Date: 04/08/2022
- Written using Jetbrains IntelliJ IDEA 2022.1 CE EAP
- Java Version: OpenJDK Runtime Environment Corretto-17.0.2.8.1 (build 17.0.2+8-LTS)
- JavaFX Version: 17.0.2
- Additional Report from Part A3f: I wrote a report for viewing a list of appointment schedules by Customer.
- MySQL Connector Driver Version: 8.0.28

INSTALLATION INSTRUCTIONS:
1) Unzip the provided zip folder into your directory of choice.
2) Open the root folder of the unzipped project in IntelliJ. Elect to "Trust" the project if prompted.
3) Make sure that your SQL Database is either running on the same machine (The program is by default configured to access
a local database @ jdbc:mysql://localhost:3306/client_schedule) or else change the dbURL constant to the server's address
in db_interface.ConnectionManager.java before running the program.
4) Select the Run icon or run with Control-R (on a Mac).
5) Accept the default run configuration if prompted. The program will run after installing Maven dependencies,
greeting you with the Login Screen. That's it!