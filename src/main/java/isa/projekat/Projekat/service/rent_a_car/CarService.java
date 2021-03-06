package isa.projekat.Projekat.service.rent_a_car;

import isa.projekat.Projekat.model.airline.Order;
import isa.projekat.Projekat.model.rent_a_car.*;
import isa.projekat.Projekat.model.user.User;
import isa.projekat.Projekat.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class CarService {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private RentCarRepository rentCarRepository;

    @Autowired
    private CarTypeRepository carTypeRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RentReservationRepository rentReservationRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public Page<Cars> findAll(PageRequest pageRequest){
        return carRepository.findAll(pageRequest);
    }

    @Transactional(readOnly = true)
    public Page<Cars> findByRentACarId( Long id,PageRequest pageRequest) { return carRepository.findByRentACarId(id,pageRequest);}

    @Transactional(readOnly = true)
    public Cars findByCarId( long id){
        Optional<Cars> cars =  carRepository.findById(id);
        if (cars.isPresent())
            return cars.get();
        else
            return null;
    }


    /*
    @SuppressWarnings("Duplicates")
    @Transactional(readOnly = true)
    public List<TransferData> findStatisticsDaily(User user){

        int info = 30;

        Date nowTime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nowTime);
        calendar.add(Calendar.DATE,-30);
        Point tmp = new Point();

        SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd");

        String strDate = sm.format(new Date());

        TransferData td = new TransferData();

        for (int i = 0; i < 30; i++){
            calendar.add(Calendar.DATE,+1);
            td.setUnits(rentReservationRepository.findStatisticsDaily(sm.format(calendar.getTime())));
            td.setDate(calendar.getTimeInMillis());
        }
        return null;
    }

    @SuppressWarnings("Duplicates")
    @Transactional(readOnly = true)
    public List<TransferData> findMonthlyStatistics(User user){

        int info = 30;

        Date nowTime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nowTime);
        calendar.add(Calendar.MONTH,-12);
        Point tmp = new Point();

        SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd");

        String strDate = sm.format(new Date());

        TransferData td = new TransferData();

        for (int i = 0; i < 30; i++){
            calendar.add(Calendar.MONTH,+1);
            td.setUnits(rentReservationRepository.findStatisticsDaily(sm.format(calendar.getTime())));
            td.setDate(calendar.getTimeInMillis());
        }
        return null;
    }
    @SuppressWarnings("Duplicates")
    @Transactional(readOnly = true)
    public List<TransferData> findYearlyStatistics(User user){

        int info = 30;

        Date nowTime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nowTime);
        calendar.add(Calendar.YEAR,-2);
        Point tmp = new Point();

        SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd");

        String strDate = sm.format(new Date());

        TransferData td = new TransferData();

        for (int i = 0; i < 5; i++){
            calendar.add(Calendar.YEAR,+1);
            td.setUnits(rentReservationRepository.findStatisticsDaily(sm.format(calendar.getTime())));
            td.setDate(calendar.getTimeInMillis());
        }
        return null;
    } */

    @Transactional
    public boolean addCars(Cars cars, User user, Long id){

        Optional<RentACar> optionalRentACar = rentCarRepository.findById(id);

        if ( !optionalRentACar.isPresent())
            return  false;

        if (!optionalRentACar.get().getAdmins().contains(user))
            return  false;

        Cars carToAdd = new Cars();

        carToAdd.setRentACar(optionalRentACar.get());

        setCar(carToAdd,cars);

        carRepository.save(carToAdd);
        return  true;
    }

    @Transactional
    public boolean editCar(Cars cars, User user){

        Optional<Cars> optionalCars = carRepository.findById(cars.getId());
        if (!optionalCars.isPresent())
            return false;
        Optional<RentACar> optionalRentACar = rentCarRepository.findById(optionalCars.get().getId());

        if (!optionalRentACar.isPresent()){
            return  false;
        }
        else if (!optionalRentACar.get().getAdmins().contains(user))
             return  false;

        Cars fromDatabase = optionalCars.get();

        setCar(fromDatabase,cars);

        carRepository.save(fromDatabase);
        return  true;
    }

    @Transactional
    public boolean removeCar(Long id, Long idrent, User user){

        Optional<RentACar> optionalRentACar = rentCarRepository.findById(idrent);
        Optional<Cars> optionalCars = carRepository.findById(id);

        if (!optionalRentACar.isPresent() || !optionalCars.isPresent()){
            return  false;
        }
        else if (!optionalRentACar.get().getAdmins().contains(user))
                return  false;
        else {
            Cars toRemove = optionalCars.get();

            optionalRentACar.get().getCars().remove(toRemove);
            carRepository.delete(toRemove);
            return true;
        }
    }

    @Transactional
    public boolean reserveCar(Long id, Long idrent, User user, RentReservation rentReservation, Long idOrder){

        Optional<RentACar> optionalRentACar = rentCarRepository.findById(idrent);
        Optional<Cars> optionalCars = carRepository.findById(id);

        if (!optionalRentACar.isPresent() || !optionalCars.isPresent()){
            return  false;
        }

        RentReservation newReservation = new RentReservation();
        Cars rentedCar = optionalCars.get();

        //Order order = orderRepository.getOne(idOrder);
        Optional<Order> orderOptional = orderRepository.findById(idOrder);
        if (!orderOptional.isPresent()){
            return false;
        }
        Order order = orderOptional.get();
        // not same user
        if (!order.getPlacedOrder().equals(user))
            return false;

        SimpleDateFormat f = new SimpleDateFormat("yyy-MM-dd");
        String start = f.format(rentReservation.getStartDate());
        String end = f.format(rentReservation.getEndDate());


        List<Cars> available = listAvailableWithDateWithoutPage(idrent, rentedCar.getType().getId(), BigDecimal.valueOf(0),
                BigDecimal.valueOf(Integer.MAX_VALUE), start , end, rentReservation.getNumberOfPeople());
        boolean isNotAvailable = true;

        for (Cars car: available) {
            if (car.getId().equals(rentedCar.getId())) {
                isNotAvailable = false;
            }
        }
        // Someone already ordered it in the meanwhile
        if (isNotAvailable){
            return  false;
        }

        order.setRentReservation(newReservation);
        newReservation.setOrder(order);
        newReservation.setEndDate(rentReservation.getEndDate());
        newReservation.setEndLocation(rentReservation.getEndLocation());
        newReservation.setStartDate(rentReservation.getStartDate());
        newReservation.setStartLocation(rentReservation.getStartLocation());
        newReservation.setNumberOfPeople(rentReservation.getNumberOfPeople());
        newReservation.setUser(user);
        newReservation.setRentedCar(newReservation.getRentedCar());
        newReservation.setFastReservation(false);
        newReservation.setRentedCar(rentedCar);

        // Price without % discount
        newReservation.setPrice(BigDecimal.valueOf(getDifferenceDays(rentReservation.getStartDate(),rentReservation.getEndDate())).multiply(rentedCar.getDailyPrice()));

        rentReservationRepository.save(newReservation);

        return true;
    }


    private static long getDifferenceDays(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }


    @Transactional
    public boolean quickReserve(Long idOrder, Long idReservation, User user){


        Order ord = orderRepository.getOne(idOrder);

        RentReservation rentReservation = rentReservationRepository.getOne(idReservation);

       if (ord == null || rentReservation == null||! ord.getPlacedOrder().equals(user))
           return  false;


        if (rentReservation.getOrder() != null)
            return false; // someone already ordered it

        ord.setRentReservation(rentReservation);

        rentReservation.setOrder(ord);

        rentReservation.setUser(user);

        rentReservationRepository.save(rentReservation);

        return true;
    }

    @Transactional(readOnly = true)
    public List<RentReservation> listQuickReservations(Long idrent){
        String date = java.time.LocalDate.now().toString();
        return rentReservationRepository.listQuick(idrent, date);
    }

    @Transactional(readOnly = true)
    public Page<Cars> listAvailableWithDateOnly(Long idrent, PageRequest pageRequest, Long carType, String start, String end, Integer passengers){
        return listAvailableWithDate(idrent,pageRequest, carType, BigDecimal.valueOf(0),BigDecimal.valueOf(200000), start, end, passengers);
    }

    @Transactional(readOnly = true)
    public Boolean checkEdibility(Long idrent, Long id){

        SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd");

        String strDate = sm.format(new Date());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, 10);
        String endDate = sm.format(calendar.getTime());

        if (carRepository.isTheCarRentedInTheFuture(idrent,id,strDate, endDate) != null){
            return true;
        }else {
            return false;
        }

    }


    @Transactional(readOnly = true)
    public  Page<Cars> listAvailableWithDate(Long idrent, PageRequest pageRequest, Long carType, BigDecimal min, BigDecimal max, String start, String end, Integer passengers){
        return carRepository.filterCars(carType,passengers,start,end,idrent,min,max,pageRequest);
        }
    @Transactional(readOnly = true)
    public List<Cars> listAvailableWithDateWithoutPage(Long idrent, Long carType, BigDecimal min, BigDecimal max, String start, String end, Integer passengers){
        return carRepository.filterCarsList(carType,passengers,start,end,idrent,min,max);
    }



    // Checks that the Rent a car object and Car object is present as well as the does the person have privileges
    public Boolean everythingPresent(Optional<RentACar> optionalRentACar, Optional<Cars> optionalCars, User user, boolean neededAdmin){
        if (!optionalRentACar.isPresent() || !optionalCars.isPresent()){
            return  false;
        }
        if (neededAdmin)
            if (!optionalRentACar.get().getAdmins().contains(user))
                return  false;
        return true;
    }



    private void setCar(Cars toSet, Cars dataFrom){

        toSet.setDailyPrice(dataFrom.getDailyPrice());
        toSet.setFastReserved(dataFrom.getFastReserved());
        toSet.setMark(dataFrom.getMark());
        toSet.setMaxPassengers(dataFrom.getMaxPassengers());
        toSet.setModel(dataFrom.getModel());
        toSet.setName(dataFrom.getName());
        toSet.setNumberOfBags(dataFrom.getNumberOfBags());
        toSet.setNumberOfDoors(dataFrom.getNumberOfDoors());
        toSet.setRegistrationNumber(dataFrom.getRegistrationNumber());
        Optional<CarType> oType = carTypeRepository.findById(dataFrom.getType().getId());
        CarType type = oType.isPresent() ? oType.get() : null;
        toSet.setType(type);
    }

}
