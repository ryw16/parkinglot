package com.ryanwei.app;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.FileWriter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class ParkingLotApp extends Application {
  ParkingLot parkingLot;
  TextField decisionInput;
  Label availableSpotsLabel;
  Label statusLabel;
  TableView<Ticket> ticketTable;
  ObservableList<Ticket> ticketList;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    primaryStage.setTitle("Parking Lot System");

    parkingLot = new ParkingLot(50, 3);
    ticketList = FXCollections.observableArrayList();

    TabPane tabPane = new TabPane();

    Tab tabparking = new Tab("Parking Lot");
    tabparking.setContent(createParkingLotTab());
    tabparking.setClosable(false);

    Tab tabhistory = new Tab("Park Out History");
    VBox emptyVBox = new VBox();
    emptyVBox.setAlignment(Pos.CENTER);
    emptyVBox.getChildren().add(new Label("This is an empty tab"));
    tabhistory.setContent(emptyVBox);
    tabhistory.setClosable(false);

    tabPane.getTabs().addAll(tabparking, tabhistory);

    Scene scene = new Scene(tabPane, 2000, 1000);
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  private VBox createParkingLotTab() {
    GridPane grid = initGrid();

    Ticket[] savedData = loadTicketListFromJson();
    if (savedData != null) {
      ticketList.addAll(savedData);
      for (Ticket ticket : savedData) {
        parkingLot.add(ticket);
      }
      updateAvailableSpots();
    }

    VBox vbox = new VBox(10);
    vbox.setPadding(new Insets(10, 10, 10, 10));
    vbox.getChildren().addAll(grid);
    return vbox;
  }

  private GridPane initGrid() {
    decisionInput = new TextField();
    decisionInput.setPromptText("Enter license plate");

    Label decisionLabel = new Label("License Plate:");
    Button addButton = new Button("Park In");
    Button removeButton = new Button("Exit");
    Button saveButton = new Button("Save");

    availableSpotsLabel = new Label("Available spots: " + parkingLot.getAvailableSpots());

    addButton.setOnAction(e -> {
      String license = decisionInput.getText();
      if (!license.isEmpty()) {
        handleParkIn(license);
      }
    });

    removeButton.setOnAction(e -> {
      String license = decisionInput.getText();
      if (!license.isEmpty()) {
        System.out.println("Removing ticket with license plate: " + license);
        handleParkOut(license);
      }
    });

    saveButton.setOnAction(e -> saveTicketListToJson());

    GridPane grid = new GridPane();

    grid.setAlignment(Pos.CENTER);
    grid.setPadding(new Insets(10));
    grid.setVgap(10);
    grid.setHgap(10);

    grid.add(decisionLabel, 0, 0);
    grid.add(decisionInput, 1, 0);
    grid.add(addButton, 2, 0);
    grid.add(removeButton, 3, 0);
    grid.add(saveButton, 4, 0);

    grid.add(availableSpotsLabel, 0, 1, 4, 1);

    statusLabel = new Label("");
    grid.add(statusLabel, 0, 2, 4, 1);

    initTable(ticketList);
    grid.add(ticketTable, 0, 3, 5, 1);
    return grid;
  }

  private void initTable(ObservableList<Ticket> list) {
    ticketTable = new TableView<Ticket>(list);
    ticketTable.setPrefWidth(1600);
    ticketTable.setPrefHeight(600);

    TableColumn<Ticket, String> licensePlateCol = new TableColumn<>("License Plate");
    licensePlateCol.setCellValueFactory(new PropertyValueFactory<>("licensePlate"));
    licensePlateCol.setPrefWidth(250);

    TableColumn<Ticket, String> parkInTimeCol = new TableColumn<>("Park In Time");
    parkInTimeCol.setCellValueFactory(new PropertyValueFactory<>("formattedInTime"));
    parkInTimeCol.setPrefWidth(250);

    TableColumn<Ticket, String> parkOutTimeCol = new TableColumn<>("Park Out Time");
    parkOutTimeCol.setCellValueFactory(new PropertyValueFactory<>("formattedOutTime"));
    parkOutTimeCol.setPrefWidth(250);

    TableColumn<Ticket, String> hourlyRateCol = new TableColumn<>("Hourly Rate");
    hourlyRateCol.setCellValueFactory(new PropertyValueFactory<>("hourlyRate"));
    hourlyRateCol.setPrefWidth(250);

    TableColumn<Ticket, String> totalTimeParkedCol = new TableColumn<>("Total Time Parked (hours)");
    totalTimeParkedCol.setCellValueFactory(new PropertyValueFactory<>("formattedTotalTimeParkedHours"));
    totalTimeParkedCol.setPrefWidth(250);

    TableColumn<Ticket, String> feeCol = new TableColumn<>("Fee");
    feeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFeeWithCurrency()));
    feeCol.setPrefWidth(250);

    ticketTable.getColumns().addAll(licensePlateCol, parkInTimeCol, parkOutTimeCol, hourlyRateCol, totalTimeParkedCol,
        feeCol);
  }

  public void handleParkIn(String license) {
    System.out.println("Parking in: " + license);
    if (parkingLot.findTicket(license) != null) {
      System.out.println("Ticket " + license + " is already parked.");
      statusLabel.setText("Ticket " + license + " is already parked.");
      return;
    }

    if (parkingLot.getAvailableSpots() > 0) {
      Vehicle v = new Vehicle(license);
      Ticket ticket = new Ticket(v);
      v.setTicket(ticket);
      parkingLot.add(ticket);
      ticketList.add(ticket);

      SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyy HH:mm:ss");
      String formattedInTime = dateFormat.format(new Date());
      ticket.setFormattedInTime(formattedInTime);
      ticket.setInTime(System.currentTimeMillis());

      ticket.setHourlyRate(50);

      updateAvailableSpots();

      decisionInput.clear();
    } else {
      System.out.println("Parking lot is full. Cannot add more vehicles");
      statusLabel.setText("Parking lot is full. Cannot add more vehicles");
    }
  }

  private void updateAvailableSpots() {
    int availableSpots = parkingLot.getAvailableSpots();
    availableSpotsLabel.setText("Available spots: " + availableSpots);
  }

  public void handleParkOut(String license) {
    System.out.println("Parking out: " + license);
    Ticket vehicleToRemove = parkingLot.findTicket(license);
    if (vehicleToRemove != null) {
      if (vehicleToRemove.getOutTime() != 0) {
        System.out.println("Vehicle " + license + " has already parked out.");
        statusLabel.setText("Vehicle " + license + " has already parked out.");
        return;
      }

      long parkOutTime = System.currentTimeMillis();
      vehicleToRemove.setOutTime(parkOutTime);

      SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
      String formattedOutTime = dateFormat.format(new Date(parkOutTime));
      vehicleToRemove.setFormattedOutTime(formattedOutTime);

      System.out.println("Vehicle " + license + " parked out.");

      parkingLot.remove(vehicleToRemove);
      updateAvailableSpots();

      long totalTimeParkedMillis = parkOutTime - vehicleToRemove.getInTime();

      vehicleToRemove.setTotalTimeParked(totalTimeParkedMillis);
      vehicleToRemove.setFormattedTotalTimeParkedHours(vehicleToRemove.getFormattedTotalTimeParkedHours());

      if (parkingLot.getAvailableSpots() > 0) {
        statusLabel.setText("");
        ticketTable.refresh();
      }

      statusLabel.setText("");
      return;
    } else {
      System.out.println("Vehicle " + license + " has already been parked out.");
      statusLabel.setText("Vehicle " + license + " has already been parked out");
    }
  }

  public void saveTicketListToJson() {
    try {
      Writer writer = new FileWriter("ticket.json");
      Gson gson = new GsonBuilder().registerTypeAdapter(Ticket.class, new TicketAdapter()).create();
      Ticket[] activeTickets = ticketList.stream()
          .filter(ticket -> ticket.getOutTime() == 0)
          .toArray(Ticket[]::new);
      String json = gson.toJson(activeTickets);
      writer.write(json);
      writer.close();
      System.out.println("Tickets saved successfully.");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public Ticket[] loadTicketListFromJson() {
    Ticket[] tickets = null;

    try {
      if (Files.exists(Paths.get("ticket.json"))) {
        String ticketString = new String(Files.readAllBytes(Paths.get("ticket.json")));
        if (ticketString.length() > 0) {
          Gson gson = new GsonBuilder().registerTypeAdapter(Ticket.class, new TicketAdapter()).create();
          Ticket[] allTickets = gson.fromJson(ticketString, Ticket[].class);
          // Filter out vehicles that are parked out
          tickets = Arrays.stream(allTickets)
              .filter(ticket -> ticket.getOutTime() == 0)
              .toArray(Ticket[]::new);
        }
      }
    } catch (FileNotFoundException e) {
      // Handle file not found exception
      System.err.println("File 'ticket.json' not found.");
    } catch (IOException e) {
      // Handle IO exception
      System.err.println("Error reading 'ticket.json'.");
    } catch (JsonSyntaxException e) {
      // Handle JSON syntax exception
      System.err.println("Error parsing JSON in 'ticket.json'.");
    } catch (Exception e) {
      // Handle any other exception
      System.err.println("An error occurred: " + e.getMessage());
    }
    return tickets;
  }

}