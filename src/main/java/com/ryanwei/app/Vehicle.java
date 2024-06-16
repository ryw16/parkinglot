package com.ryanwei.app;

import javafx.beans.property.SimpleStringProperty;

public class Vehicle {
  private Ticket ticket;
  private SimpleStringProperty licensePlate;

  public Vehicle(String licensePlate) {
    this.licensePlate = new SimpleStringProperty(licensePlate);
  }

  public String getLicensePlate() {
    return licensePlate.get();
  }

  public void setLicensePlate(String licensePlate) {
    this.licensePlate.set(licensePlate);
  }

  public Ticket getTicket() {
    return ticket;
  }

  public void setTicket(Ticket ticket) {
    this.ticket = ticket;
  }
}
