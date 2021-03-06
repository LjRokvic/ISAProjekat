package isa.projekat.Projekat.service.rent_a_car;

import isa.projekat.Projekat.model.rent_a_car.CarType;
import isa.projekat.Projekat.repository.CarTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CarTypeService {

    @Autowired
    private CarTypeRepository carTypeRepository;

    @Transactional(readOnly = true)
    public List<CarType> findAll(){ return carTypeRepository.findAll();}

}
