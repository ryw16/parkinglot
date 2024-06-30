package com.ryanwei.app;

import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.beans.property.*;

public class Ticket {
  private IntegerProperty spot;
  private LongProperty inTime;
  private LongProperty outTime;
  private DoubleProperty fee;
  private SimpleStringProperty formattedInTime;
  private SimpleStringProperty formattedOutTime;
  private int hourlyRate;
  private long totalTimeParked;
  private SimpleStringProperty formattedTotalTimeParkedHours;
  private Vehicle vehicle;

  public Ticket(Vehicle vehicle) {
    this.vehicle = vehicle;
    this.spot = new SimpleIntegerProperty(0);
    this.inTime = new SimpleLongProperty(0);
    this.outTime = new SimpleLongProperty(0);
    this.fee = new SimpleDoubleProperty(0.00);
    this.formattedInTime = new SimpleStringProperty("");
    this.formattedOutTime = new SimpleStringProperty("");
    this.formattedTotalTimeParkedHours = new SimpleStringProperty("");
    this.hourlyRate = 50;
  }

  // Getters and Setters
  public Vehicle getVehicle() {
    return vehicle;
  }

  public void setVehicle(Vehicle vehicle) {
    this.vehicle = vehicle;
  }

  public String getLicensePlate() {
    return this.vehicle.getLicensePlate();
  }

  public int getSpot() {
    return spot.get();
  }

  public void setSpot(int spot) {
    this.spot.set(spot);
  }

  public long getInTime() {
    return inTime.get();
  }

  public void setInTime(long inTime) {
    this.inTime.set(inTime);
    setFormattedInTime(getFormattedInTime());
  }

  public long getOutTime() {
    return outTime.get();
  }

  public void setOutTime(long outTime) {
    this.outTime.set(outTime);
    setFormattedOutTime(getFormattedOutTime());
  }

  public double getFee() {
    return fee.get();
  }

  public String getFeeWithCurrency() {
    return String.format("$%.2f", fee.get());
  }

  public void setFee(double fee) {
    this.fee.set(fee);
  }

  public int getHourlyRate() {
    return hourlyRate;
  }

  public void setHourlyRate(int hourlyRate) {
    this.hourlyRate = hourlyRate;
  }

  public long getTotalTimeParked() {
    return totalTimeParked;
  }

  public void setTotalTimeParked(long totalTimeParked) {
    this.totalTimeParked = totalTimeParked;
    setFormattedTotalTimeParkedHours(getFormattedTotalTimeParkedHours());

  }

  public String getFormattedInTime() {
    if (inTime.get() == 0) {
      return "";
    }
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    return dateFormat.format(new Date(inTime.get()));
  }

  public void setFormattedInTime(String formattedInTime) {
    this.formattedInTime.set(formattedInTime);
  }

  public String getFormattedOutTime() {
    if (outTime.get() == 0) {
      return "";
    }
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    return dateFormat.format(new Date(outTime.get()));
  }

  public void setFormattedOutTime(String formattedOutTime) {
    this.formattedOutTime.set(formattedOutTime);
  }

  public String getFormattedTotalTimeParkedHours() {
    double totalTimeParkedHours = totalTimeParked / (1000.0 * 60 * 60);
    return String.format("%.2f", totalTimeParkedHours);
  }

  public void setFormattedTotalTimeParkedHours(String formattedTotalTimeParkedHours) {
    this.formattedTotalTimeParkedHours.set(formattedTotalTimeParkedHours);
  }

  // Properties
  public SimpleStringProperty licensePlateProperty() {
    return new SimpleStringProperty(this.getLicensePlate());
  }

  public IntegerProperty spotProperty() {
    return spot;
  }

  public LongProperty inTimeProperty() {
    return inTime;
  }

  public LongProperty outTimeProperty() {
    return outTime;
  }

  public SimpleStringProperty formattedInTimeProperty() {
    return formattedInTime;
  }

  public SimpleStringProperty formattedOutTimeProperty() {
    return formattedOutTime;
  }

  public DoubleProperty feeProperty() {
    return fee;
  }

  public SimpleStringProperty formattedTotalTimeParkedHoursProperty() {
    return formattedTotalTimeParkedHours;
  }
}
