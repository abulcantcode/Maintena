package com.example.maintena.Model;

// This is the object that the API response is parsed into
public class Details {

    String registrationNumber, taxStatus, taxDueDate, motStatus, make, yearOfManufacture, engineCapacity, co2Emissions, fuelType, markedForExport, colour, typeApproval, dateOfLastV5CIssued, motExpiryDate, wheelplan, monthOfFirstRegistration;

    public Details(String registrationNumber, String taxStatus, String taxDueDate, String motStatus, String make, String yearOfManufacture, String engineCapacity, String co2Emissions, String fuelType, String markedForExport, String colour, String typeApproval, String dateOfLastV5CIssued, String motExpiryDate, String wheelplan, String monthOfFirstRegistration) {
        this.registrationNumber = registrationNumber;
        this.taxStatus = taxStatus;
        this.taxDueDate = taxDueDate;
        this.motStatus = motStatus;
        this.make = make;
        this.yearOfManufacture = yearOfManufacture;
        this.engineCapacity = engineCapacity;
        this.co2Emissions = co2Emissions;
        this.fuelType = fuelType;
        this.markedForExport = markedForExport;
        this.colour = colour;
        this.typeApproval = typeApproval;
        this.dateOfLastV5CIssued = dateOfLastV5CIssued;
        this.motExpiryDate = motExpiryDate;
        this.wheelplan = wheelplan;
        this.monthOfFirstRegistration = monthOfFirstRegistration;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getTaxStatus() {
        return taxStatus;
    }

    public void setTaxStatus(String taxStatus) {
        this.taxStatus = taxStatus;
    }

    public String getTaxDueDate() {
        return taxDueDate;
    }

    public void setTaxDueDate(String taxDueDate) {
        this.taxDueDate = taxDueDate;
    }

    public String getMotStatus() {
        return motStatus;
    }

    public void setMotStatus(String motStatus) {
        this.motStatus = motStatus;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getYearOfManufacture() {
        return yearOfManufacture;
    }

    public void setYearOfManufacture(String yearOfManufacture) {
        this.yearOfManufacture = yearOfManufacture;
    }

    public String getEngineCapacity() {
        return engineCapacity;
    }

    public void setEngineCapacity(String engineCapacity) {
        this.engineCapacity = engineCapacity;
    }

    public String getCo2Emissions() {
        return co2Emissions;
    }

    public void setCo2Emissions(String co2Emissions) {
        this.co2Emissions = co2Emissions;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getMarkedForExport() {
        return markedForExport;
    }

    public void setMarkedForExport(String markedForExport) {
        this.markedForExport = markedForExport;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getTypeApproval() {
        return typeApproval;
    }

    public void setTypeApproval(String typeApproval) {
        this.typeApproval = typeApproval;
    }

    public String getDateOfLastV5CIssued() {
        return dateOfLastV5CIssued;
    }

    public void setDateOfLastV5CIssued(String dateOfLastV5CIssued) {
        this.dateOfLastV5CIssued = dateOfLastV5CIssued;
    }

    public String getMotExpiryDate() {
        return motExpiryDate;
    }

    public void setMotExpiryDate(String motExpiryDate) {
        this.motExpiryDate = motExpiryDate;
    }

    public String getWheelplan() {
        return wheelplan;
    }

    public void setWheelplan(String wheelplan) {
        this.wheelplan = wheelplan;
    }

    public String getMonthOfFirstRegistration() {
        return monthOfFirstRegistration;
    }

    public void setMonthOfFirstRegistration(String monthOfFirstRegistration) {
        this.monthOfFirstRegistration = monthOfFirstRegistration;
    }
}
