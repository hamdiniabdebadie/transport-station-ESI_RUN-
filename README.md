Transport Agency Management System

Java OOP project for managing a transport agency using Java and JavaFX.

Overview

This project simulates a public transport management system where employees and users can interact with transport services, fare media, complaints, and validation systems.

The project was developed to practice:

Object-Oriented Programming (OOP)
Inheritance and polymorphism
Exception handling
JavaFX graphical interfaces
File handling
MVC-style organization
Main Features
User management (Usager)
Employee management (Employe)
Transport fare media management
Ticket validation system
Complaint management system
Activity logging
Payment methods support
Transport stations and services
JavaFX graphical interface
Project Structure
src/
 ├── transport/
 │    ├── core/
 │    │     ├── Personne.java
 │    │     ├── Employe.java
 │    │     ├── Usager.java
 │    │     ├── Ticket.java
 │    │     ├── TransportService.java
 │    │     └── ...
 │    │
 │    ├── control/
 │    │     ├── DashboardController.java
 │    │     ├── UsersController.java
 │    │     └── ...
 │    │
 │    └── Main.java
Technologies Used
Java
JavaFX
IntelliJ IDEA
Object-Oriented Programming principles
OOP Concepts Used
Encapsulation
Inheritance
Polymorphism
Abstraction
Interfaces
Custom Exceptions
How to Run
Using IntelliJ IDEA
Open the project in IntelliJ IDEA.
Configure the JavaFX SDK.
Run Main.java.
JavaFX Configuration

Add the JavaFX SDK library path in VM options:

--module-path "path/to/javafx-sdk/lib" --add-modules javafx.controls,javafx.fxml
Files Included
activity.txt → Activity logs
complaints.txt → Complaint storage
media.txt → Fare media information
