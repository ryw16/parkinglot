package com.ryanwei.app;

import com.google.gson.*;

public class TicketAdapter implements JsonSerializer<Ticket>, JsonDeserializer<Ticket> {

  @Override
  public JsonElement serialize(Ticket src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("licensePlate", src.getLicensePlate());
    jsonObject.addProperty("spot", src.getSpot());
    jsonObject.addProperty("inTime", src.getInTime());
    jsonObject.addProperty("outTime", src.getOutTime());
    jsonObject.addProperty("fee", src.getFee());
    jsonObject.addProperty("formattedInTime", src.getFormattedInTime());
    jsonObject.addProperty("formattedOutTime", src.getFormattedOutTime());
    jsonObject.addProperty("formattedTotalTimeParkedHours", src.getFormattedTotalTimeParkedHours());
    return jsonObject;
  }

  @Override
  public Ticket deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();
    String licensePlate = jsonObject.get("licensePlate").getAsString();
    int spot = jsonObject.get("spot").getAsInt();
    long inTime = jsonObject.get("inTime").getAsLong();
    long outTime = jsonObject.get("outTime").getAsLong();
    double fee = jsonObject.get("fee").getAsDouble();
    String formattedInTime = jsonObject.get("formattedInTime").getAsString();
    String formattedOutTime = jsonObject.get("formattedOutTime").getAsString();
    String formattedTotalTimeParkedHours = jsonObject.get("formattedTotalTimeParkedHours").getAsString();

    Vehicle vehicle = new Vehicle(licensePlate);
    Ticket ticket = new Ticket(vehicle);
    ticket.setSpot(spot);
    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setFee(fee);
    ticket.setFormattedInTime(formattedInTime);
    ticket.setFormattedOutTime(formattedOutTime);
    ticket.setFormattedTotalTimeParkedHours(formattedTotalTimeParkedHours);

    return ticket;
  }
}
