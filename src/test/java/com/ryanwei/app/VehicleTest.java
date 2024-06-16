package com.ryanwei.app;

import java.text.SimpleDateFormat;
import org.junit.Assert;
import org.junit.Test;
import java.util.Date;

public class VehicleTest {

  @Test
  public void testGetLicense() {
    Vehicle vehicle = new Vehicle("a");
    Ticket v1 = new Ticket(vehicle);
    Assert.assertEquals(v1.getLicensePlate(), "a");
  }

  @Test
  public void testGetFee() {
    Vehicle vehicle = new Vehicle("a");
    Ticket v1 = new Ticket(vehicle);
    v1.setFee(10.0);
    double delta = 0.001; // Tolerance for floating-point comparison
    Assert.assertEquals(v1.getFee(), 10.0, delta);
  }

  @Test
  public void testGetHourlyRate() {
    Vehicle vehicle = new Vehicle("a");
    Ticket v1 = new Ticket(vehicle);
    v1.setHourlyRate(50);
    Assert.assertEquals(v1.getHourlyRate(), 50, 0);
  }

  @Test
  public void testGetTotalTimeParked() {
    Vehicle vehicle = new Vehicle("a");
    Ticket v1 = new Ticket(vehicle);
    long parkedTime = 3600000; // 1 hour in milliseconds
    v1.setTotalTimeParked(parkedTime);
    Assert.assertEquals(v1.getTotalTimeParked(), parkedTime);
  }

  @Test
  public void testGetSpot() {
    Vehicle vehicle = new Vehicle("a");
    Ticket v1 = new Ticket(vehicle);
    v1.setSpot(5);
    Assert.assertEquals(v1.getSpot(), 5);
  }

  @Test
  public void testGetInTime() {
    Vehicle vehicle = new Vehicle("a");
    Ticket v1 = new Ticket(vehicle);
    long inTime = System.currentTimeMillis();
    v1.setInTime(inTime);
    Assert.assertEquals(v1.getInTime(), inTime);
  }

  @Test
  public void testGetOutTime() {
    Vehicle vehicle = new Vehicle("a");
    Ticket v1 = new Ticket(vehicle);
    long outTime = System.currentTimeMillis();
    v1.setOutTime(outTime);
    Assert.assertEquals(v1.getOutTime(), outTime);
  }

  @Test
  public void testGetFormattedInTime() {
    Vehicle vehicle = new Vehicle("a");
    Ticket v1 = new Ticket(vehicle);
    long inTime = System.currentTimeMillis();
    v1.setInTime(inTime);
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    String expectedFormattedInTime = dateFormat.format(new Date(inTime));
    Assert.assertEquals(v1.getFormattedInTime(), expectedFormattedInTime);
  }

  @Test
  public void testGetFormattedOutTime() {
    Vehicle vehicle = new Vehicle("a");
    Ticket v1 = new Ticket(vehicle);
    long outTime = System.currentTimeMillis();
    v1.setOutTime(outTime);
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    String expectedFormattedOutTime = dateFormat.format(new Date(outTime));
    Assert.assertEquals(v1.getFormattedOutTime(), expectedFormattedOutTime);
  }

  @Test
  public void testGetFormattedTotalTimeParkedHours() {
    Vehicle vehicle = new Vehicle("a");
    Ticket v1 = new Ticket(vehicle);
    long totalTimeParkedMillis = 3600000; // 1 hour in milliseconds
    v1.setTotalTimeParked(totalTimeParkedMillis);
    String expectedFormattedTotalTimeParkedHours = String.format("%.2f", totalTimeParkedMillis / (1000.0 * 60 * 60));
    Assert.assertEquals(v1.getFormattedTotalTimeParkedHours(), expectedFormattedTotalTimeParkedHours);
  }

  @Test
  public void testGetFormattedTotalTimeParkedSeconds() {
    Vehicle vehicle = new Vehicle("a");
    Ticket v1 = new Ticket(vehicle);
    long totalTimeParkedMillis = 3600000; // 1 hour in milliseconds
    v1.setTotalTimeParked(totalTimeParkedMillis);
    long totalTimeParkedSeconds = totalTimeParkedMillis / 1000;
    String expectedFormattedTotalTimeParkedSeconds = totalTimeParkedSeconds + " seconds";
    Assert.assertEquals(v1.getFormattedTotalTimeParkedSeconds(), expectedFormattedTotalTimeParkedSeconds);
  }
}
