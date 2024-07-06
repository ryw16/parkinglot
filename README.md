# Parking Lot System

This project is a Java-based parking lot management system with a graphical user interface (GUI) built using JavaFX. It automates the process of ticketing, entry, and exit for vehicles in a parking lot, tracking the availability of slots and calculating parking fees based on duration.

## features

- User-friendly interface for managing parking lot operations
- Dynamic updates on the status of the parking lot
- Upon vehicle entry, the system automatically generates a parking ticket containing essential details such as the vehicle registration number and the allocated slot number
- Calculates and displays the parking fee based on the time the vehicle was parked
- Saves the current state to a JSON file to restore the session
- Generates a status report listing all occupied slots along with the corresponding vehicle registration numbers
- Users can easily manage slot availability, including marking slots as unavailable for maintenance or other reasons
- Users are able to access two tabs (parking lot and history) to park in or parkout a vehicle and view the history of vehicles parked out already
- Used sqlite database to manage data

## Getting Started

Here are some instructions on how to set the project up and run it for testing purposes

## prerequesites

- Java Development Kit (JDK) 8 or later
- Maven for building the project

## how to run unit tests

mvn test

## running from command prompt

mvn javafx:run
