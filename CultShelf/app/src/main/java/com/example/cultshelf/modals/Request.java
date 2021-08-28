package com.example.cultshelf.modals;

public class Request {
    public String RequestStatus,ProductName,ClubName,RequestedBy,CurrentOwner;
    public Request(){

    }
    public Request(String ProductName,String ClubName,String RequestStatus,String RequestedBy,String CurrentOwner){
        this.ClubName = ClubName;
        this.ProductName = ProductName;
        this.RequestStatus = RequestStatus;
        this.RequestedBy = RequestedBy;
        this.CurrentOwner = CurrentOwner;
    }
}
