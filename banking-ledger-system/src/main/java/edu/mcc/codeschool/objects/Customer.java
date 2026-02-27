package edu.mcc.codeschool.objects;

public class Customer {
    private String name;
    private String DOB;
    private String phoneNumber;
    private String address;
    private String city;
    private String state;
    private Integer zipCode;

    public String getName() {
        return name;
    }

    public Customer setName(String name) {
        this.name = name;
        return this;
    }

    public String getDOB() {
        return DOB;
    }

    public Customer setDOB(String DOB) {
        this.DOB = DOB;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Customer setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public Customer setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getCity() {
        return city;
    }

    public Customer setCity(String city) {
        this.city = city;
        return this;
    }

    public String getState() {
        return state;
    }

    public Customer setState(String state) {
        this.state = state;
        return this;
    }

    public Integer getZipCode() {
        return zipCode;
    }

    public Customer setZipCode(Integer zipCode) {
        this.zipCode = zipCode;
        return this;
    }
}
