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
import java.util.Date;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ParkingLotApp extends Application {
  ParkingLot parkingLot;
  TextField decisionInput;
  Label availableSpotsLabel;
  Label statusLabel;
  TableView<Ticket> ticketTable;
  TableView<Ticket> historyTable;
  ObservableList<Ticket> ticketList;
  ObservableList<Ticket> exitedTicketList;
  private static final String DB_URL = "jdbc:sqlite:parkinglot.db";

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    primaryStage.setTitle("Parking Lot System");

    parkingLot = new ParkingLot(50, 3);
    ticketList = FXCollections.observableArrayList();
    exitedTicketList = FXCollections.observableArrayList();

    TabPane tabPane = new TabPane();

    Tab tabparking = new Tab("Parking Lot");
    tabparking.setContent(createParkingLotTab());
    tabparking.setClosable(false);

    Tab tabhistory = new Tab("Park Out History");
    tabhistory.setContent(createHistoryTab());
    tabhistory.setClosable(false);

    tabPane.getTabs().addAll(tabparking, tabhistory);

    Scene scene = new Scene(tabPane, 2000, 1000);
    primaryStage.setScene(scene);
    primaryStage.show();

    createDatabaseTables();
    loadTicketListFromDatabase();
  }

  private VBox createParkingLotTab() {
    GridPane grid = initGrid();

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
        handleParkOut(license);
      }
    });

    saveButton.setOnAction(e -> saveTicketListToDatabase());

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

    ticketTable = new TableView<>(ticketList);
    initTable(ticketTable, ticketList);
    grid.add(ticketTable, 0, 3, 5, 1);
    return grid;
  }

  private void initTable(TableView<Ticket> table, ObservableList<Ticket> list) {
    table.setPrefWidth(1600);
    table.setPrefHeight(600);

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

    table.getColumns().addAll(licensePlateCol, parkInTimeCol, parkOutTimeCol, hourlyRateCol, totalTimeParkedCol,
        feeCol);
  }

  private VBox createHistoryTab() {
    historyTable = new TableView<>(exitedTicketList);
    initTable(historyTable, exitedTicketList);

    VBox vbox = new VBox(10);
    vbox.setPadding(new Insets(10, 10, 10, 10));
    vbox.setAlignment(Pos.CENTER);
    vbox.getChildren().add(historyTable);
    return vbox;
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

      SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
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

      ticketList.remove(vehicleToRemove);
      exitedTicketList.add(vehicleToRemove);

      if (parkingLot.getAvailableSpots() > 0) {
        statusLabel.setText("");
        ticketTable.refresh();
        historyTable.refresh();
      }

      statusLabel.setText("");
      return;
    } else {
      System.out.println("Vehicle " + license + " has already been parked out.");
      statusLabel.setText("Vehicle " + license + " has already been parked out");
    }
  }

  private void createDatabaseTables() {
    try (Connection conn = DriverManager.getConnection(DB_URL);
        Statement stmt = conn.createStatement()) {
      String sql = "CREATE TABLE IF NOT EXISTS tickets (" +
          "licensePlate TEXT PRIMARY KEY, " +
          "spot INTEGER, " +
          "inTime INTEGER, " +
          "outTime INTEGER, " +
          "fee REAL, " +
          "formattedInTime TEXT, " +
          "formattedOutTime TEXT, " +
          "formattedTotalTimeParkedHours TEXT)";
      stmt.execute(sql);
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  private void saveTicketListToDatabase() {
    String sql = "INSERT OR REPLACE INTO tickets(licensePlate, spot, inTime, outTime, fee, formattedInTime, formattedOutTime, formattedTotalTimeParkedHours) VALUES(?,?,?,?,?,?,?,?)";

    try (
        Connection conn = DriverManager.getConnection(DB_URL);
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      for (Ticket ticket : ticketList) {
        insertRecord(pstmt, ticket);
      }

      for (Ticket ticket : exitedTicketList) {
        insertRecord(pstmt, ticket);
      }

      System.out.println("Tickets saved successfully to SQLite.");
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  private void insertRecord(PreparedStatement pstmt, Ticket ticket) {
    try {
      pstmt.setString(1, ticket.getLicensePlate());
      pstmt.setInt(2, ticket.getSpot());
      pstmt.setLong(3, ticket.getInTime());
      pstmt.setLong(4, ticket.getOutTime());
      pstmt.setDouble(5, ticket.getFee());
      pstmt.setString(6, ticket.getFormattedInTime());
      pstmt.setString(7, ticket.getFormattedOutTime());
      pstmt.setString(8, ticket.getFormattedTotalTimeParkedHours());
      pstmt.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  private void loadTicketListFromDatabase() {
    String sql = "SELECT * FROM tickets";

    try (Connection conn = DriverManager.getConnection(DB_URL);
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        String licensePlate = rs.getString("licensePlate");
        int spot = rs.getInt("spot");
        long inTime = rs.getLong("inTime");
        long outTime = rs.getLong("outTime");
        double fee = rs.getDouble("fee");
        String formattedInTime = rs.getString("formattedInTime");
        String formattedOutTime = rs.getString("formattedOutTime");
        String formattedTotalTimeParkedHours = rs.getString("formattedTotalTimeParkedHours");

        Vehicle vehicle = new Vehicle(licensePlate);
        Ticket ticket = new Ticket(vehicle);
        ticket.setSpot(spot);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setFee(fee);
        ticket.setFormattedInTime(formattedInTime);
        ticket.setFormattedOutTime(formattedOutTime);
        ticket.setFormattedTotalTimeParkedHours(formattedTotalTimeParkedHours);

        if (outTime == 0) {
          ticketList.add(ticket);
          parkingLot.add(ticket);
        } else {
          exitedTicketList.add(ticket);
        }
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }
}
