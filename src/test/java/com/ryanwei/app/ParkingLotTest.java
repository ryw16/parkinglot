package com.ryanwei.app;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ParkingLotTest {
  private ParkingLot parkingLot;

  @Before
  public void setUp() {
    parkingLot = new ParkingLot(50, 3); // Example: hourly rate of 50 and 3 parking spaces
  }

  @Test
  public void testAddTicket() {
    Assert.assertEquals(3, parkingLot.getAvailableSpots());

    Vehicle vehicle = new Vehicle("a");
    Ticket ticket = new Ticket(vehicle);

    parkingLot.add(ticket);

    Assert.assertEquals(2, parkingLot.getAvailableSpots());
    Assert.assertEquals(ticket, parkingLot.findTicket("a"));
  }

  @Test
  public void testRemoveTicket() {
    Vehicle vehicle = new Vehicle("a");
    Ticket ticket = new Ticket(vehicle);
    parkingLot.add(ticket);

    Assert.assertEquals(2, parkingLot.getAvailableSpots());

    double fee = parkingLot.remove(ticket);

    Assert.assertEquals(3, parkingLot.getAvailableSpots());
    Assert.assertNull(parkingLot.findTicket("a"));
    Assert.assertTrue(fee >= 0); // Fee can be 0 if parked for a very short time
  }

  @Test
  public void testFindTicket() {
    Vehicle vehicle1 = new Vehicle("a");
    Vehicle vehicle2 = new Vehicle("b");
    Ticket ticket1 = new Ticket(vehicle1);
    Ticket ticket2 = new Ticket(vehicle2);

    parkingLot.add(ticket1);
    parkingLot.add(ticket2);

    Assert.assertEquals(ticket1, parkingLot.findTicket("a"));
    Assert.assertEquals(ticket2, parkingLot.findTicket("b"));
  }

  @Test
  public void testGetAvailableSpots() {
    Assert.assertEquals(3, parkingLot.getAvailableSpots());

    Vehicle vehicle = new Vehicle("a");
    Ticket ticket = new Ticket(vehicle);

    parkingLot.add(ticket);

    Assert.assertEquals(2, parkingLot.getAvailableSpots());
  }

  @Test
  public void testSetHourlyRate() {
    parkingLot.setHourlyRate(100);

    Vehicle vehicle = new Vehicle("a");
    Ticket ticket = new Ticket(vehicle);

    parkingLot.add(ticket);

    double fee = parkingLot.remove(ticket);

    Assert.assertTrue(fee > 0);
  }
}
