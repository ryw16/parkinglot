package com.ryanwei.app;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ParkingLotAppTest {

  private ParkingLotApp app;
  private TableView<Ticket> tableView;
  private TextField licensePlateField;
  private ObservableList<Ticket> ticketList;

  @BeforeEach
  public void setUp() {
    app = new ParkingLotApp();
    tableView = new TableView<>();
    licensePlateField = new TextField();
    ticketList = FXCollections.observableArrayList();

    app.decisionInput = licensePlateField;
    app.ticketTable = tableView;
    app.ticketList = ticketList;
    app.parkingLot = new ParkingLot(50, 3);
  }

  @Test
  public void testHandleParkIn() {
    licensePlateField.setText("a");
    app.handleParkIn(licensePlateField.getText());
    assertEquals(1, tableView.getItems().size());
    assertEquals("a", tableView.getItems().get(0).getLicensePlate());
    assertEquals(2, app.parkingLot.getAvailableSpots());
  }

  @Test
  public void testHandleParkOut() {
    Vehicle vehicle = new Vehicle("a");
    Ticket ticket = new Ticket(vehicle);
    app.parkingLot.add(ticket);
    app.ticketList.add(ticket);

    licensePlateField.setText("a");
    app.handleParkOut(licensePlateField.getText());
    assertEquals(0, tableView.getItems().size());
    assertEquals(3, app.parkingLot.getAvailableSpots());
  }

  @Test
  public void testSaveTicketListToJson() throws IOException {
    Vehicle vehicle = new Vehicle("a");
    Ticket ticket = new Ticket(vehicle);
    app.parkingLot.add(ticket);
    app.ticketList.add(ticket);

    String filePath = "test_save_state.json";
    app.saveTicketListToJson();
    assertTrue(Files.exists(Paths.get(filePath)));

    String savedJson = new String(Files.readAllBytes(Paths.get(filePath)));
    assertTrue(savedJson.contains("a"));

    Files.delete(Paths.get(filePath));
  }

  @Test
  public void testLoadTicketListFromJson() throws IOException {
    String ticketJson = "[{\"licensePlate\":\"a\",\"spot\":0,\"inTime\":1716749409766,\"outTime\":0,\"fee\":0.0,\"formattedInTime\":\"26-05-2024 11:50:09\",\"formattedOutTime\":\"\"}]";
    String filePath = "test_load_state.json";
    Files.write(Paths.get(filePath), ticketJson.getBytes());

    app.loadTicketListFromJson();

    assertEquals(1, tableView.getItems().size());
    assertEquals("a", tableView.getItems().get(0).getLicensePlate());
    assertEquals(2, app.parkingLot.getAvailableSpots());

    Files.delete(Paths.get(filePath));
  }
}
