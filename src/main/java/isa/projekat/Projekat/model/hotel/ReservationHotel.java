package isa.projekat.Projekat.model.hotel;

import com.fasterxml.jackson.annotation.JsonBackReference;
import isa.projekat.Projekat.model.airline.Order;
import isa.projekat.Projekat.model.user.User;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@Entity
@Table(name = "ReservationHotel")
public class ReservationHotel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private Date arrivalDate;

    @Column(nullable = false)
    private Date departureDate;

    @Column
    private Date reservationDate;

    @Column(nullable = false)
    private int nightsStaying;

    @Column(nullable = false)
    private int People;

    @Column
    private BigDecimal price;

    @OneToMany
    private List<HotelServices> services;

    @Column
    private boolean fast = false;


    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JsonBackReference(value = "hotels_bla")
    private User user;

    @ManyToOne
    @JsonBackReference(value = "rooms")
    private Order userOrder;

    @ManyToOne
    private Room room;

    public ReservationHotel(){
        super();
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<HotelServices> getServices() {
        return services;
    }

    public void setServices(List<HotelServices> services) {
        this.services = services;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(Date arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }

    public Date getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(Date reservationDate) {
        this.reservationDate = reservationDate;
    }

    public int getNightsStaying() {
        return nightsStaying;
    }

    public void setNightsStaying(int nightsStaying) {
        this.nightsStaying = nightsStaying;
    }

    public int getPeople() {
        return People;
    }

    public void setPeople(int people) {
        People = people;
    }

    public boolean isFast() {
        return fast;
    }

    public void setFast(boolean fast) {
        this.fast = fast;
    }

    public Order getUserOrder() {
        return userOrder;
    }

    public void setUserOrder(Order userOrder) {
        this.userOrder = userOrder;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}
