package isa.projekat.Projekat.repository;

import isa.projekat.Projekat.model.rent_a_car.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface LocationRepository extends JpaRepository<Location, Long> {
}
