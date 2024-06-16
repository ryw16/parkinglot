package com.ryanwei.app;

import java.util.ArrayList;

public class ParkingLot {
  private ArrayList<Ticket> spaces;
  private int hourlyRate;
  private int numSpace;
  private int availableSpots;

  public ParkingLot(int hourlyRate, int numSpace) {
    this.spaces = new ArrayList<Ticket>();
    for (int i = 0; i < numSpace; i++) {
      spaces.add(null);
    }
    this.hourlyRate = hourlyRate;
    this.numSpace = numSpace;
    this.availableSpots = numSpace;
  }

  public void setHourlyRate(int rate) {
    this.hourlyRate = rate;
  }

  public void add(Ticket ticket) {

    boolean spotFound = false;
    boolean alreadyParked = false;
    String licensePlate = ticket.getLicensePlate();

    for (Ticket v : spaces) {
      if (ticket != null && ticket.getLicensePlate().equals(licensePlate)) {
        alreadyParked = true;
        break;
      }
    }

    if (alreadyParked) {
      for (int i = 0; i < this.numSpace; i++) {
        if (this.spaces.get(i) == null) {
          this.spaces.set(i, ticket);
          ticket.setSpot(i);
          spotFound = true;
          break;
        }
      }

      if (spotFound) {
        System.out.println("Vehicle with license plate " + licensePlate + " parked successfully.");
        updateAvailableSpots(-1);
      } else {
        System.out.println("Sorry, all parking spots are taken.");
      }
    } else {
      System.out.println("Car with license plate " + licensePlate + " has already been parked.");
    }
  }

  public double remove(Ticket vehicle) {
    if (vehicle != null) {

      int spot = vehicle.getSpot();
      if (spot >= 0 && spot < numSpace) {
        this.spaces.set(spot, null);
        long currentTime = System.currentTimeMillis();
        long parkingDurationMillis = currentTime - vehicle.getInTime();
        double parkingDurationHours = (double) parkingDurationMillis / (1000 * 60 * 60);
        double fee = parkingDurationHours * hourlyRate;
        double formattedFee = Math.round(fee * 100.0) / 100.0;

        vehicle.setFee(formattedFee);
        vehicle.setOutTime(currentTime);
        updateAvailableSpots(1);
        return formattedFee;

      } else {
        System.out.println("Invalid parking spot index: " + spot);
      }
    } else {
      System.out.println("Cannot remove null vehicle.");
    }
    return 0.0;
  }

  public int getAvailableSpots() {
    return availableSpots;
  }

  private void updateAvailableSpots(int change) {
    this.availableSpots += change;
  }

  public Ticket findTicket(String licensePlate) {
    for (Ticket ticket : spaces) {
      if (ticket != null && ticket.getLicensePlate().equals(licensePlate)) {
        return ticket;
      }
    }
    return null;
  }
}