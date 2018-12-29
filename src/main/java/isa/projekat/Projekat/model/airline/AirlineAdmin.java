package isa.projekat.Projekat.model.airline;

import isa.projekat.Projekat.model.User;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "AirlineAdmins")
public class AirlineAdmin extends User {

    @ManyToOne
    private Airline administratedAirline;

    public Airline getAdministratedAirline() {
        return administratedAirline;
    }

    public void setAdministratedAirline(Airline administratedAirline) {
        this.administratedAirline = administratedAirline;
    }
}