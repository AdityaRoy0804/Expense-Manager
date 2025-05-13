Expense Manager – Java Swing + MySQL

A desktop-based Expense Manager application built using Java Swing for the GUI and JDBC with MySQL for persistent data storage. This application allows users to track, manage, and analyze their daily expenses efficiently.

Java | MySQL

Features

• Add, update, and delete expense records

• Categorize expenses (e.g., Food, Travel, Utilities)

• View daily, weekly, or monthly expense summaries

• Persistent storage using MySQL and JDBC

• User-friendly GUI built with Java Swing

Tech Stack

• Programming Language: Java (OOP-based)

• GUI: Java Swing

• Database: MySQL

• Database Connector: JDBC


Getting Started

Prerequisites

• Java JDK 8 or higher

• MySQL installed and running

• MySQL JDBC Driver (Connector/J)

• IDE like IntelliJ IDEA or Eclipse

Setup Instructions

1.Clone the Repository:

   git clone https://github.com/AdityaRoy0804/Expense-Manager.git
   
2.Import Project:

   Open the project in your preferred IDE and add MySQL JDBC connector JAR.
   
3.Set Up Database:

   Run the database.sql file in your MySQL client to create required tables.
   
4.Update DB Credentials in your Java file (e.g., DBConnection.java):

5.   String url = "jdbc:mysql://localhost:3306/expense_db";
6.   
   String username = "root";

   String password = "your_password";
   
6.Run the Application:

   Run the main class (e.g., Main.java) to launch the GUI.
   
Screenshots



Contributing
Feel free to fork this repo and submit pull requests. Suggestions and bug reports are welcome!
License
This project is open-source and available under the MIT License.
Visit Repository: https://github.com/AdityaRoy0804/Expense-Manager
