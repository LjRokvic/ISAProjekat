package isa.projekat.Projekat.model.rent_a_car;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name =  "CarType")
public class CarType implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name= "name", nullable = false)
    private String name;


    public CarType() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
