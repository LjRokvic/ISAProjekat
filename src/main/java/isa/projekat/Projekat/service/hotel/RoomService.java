package isa.projekat.Projekat.service.hotel;

import isa.projekat.Projekat.model.airline.Order;
import isa.projekat.Projekat.model.hotel.*;
import isa.projekat.Projekat.model.user.User;
import isa.projekat.Projekat.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private HotelRepository hotelRepository;


    @Autowired
    private HotelReservationRepository hotelReservationRepository;

    @Autowired
    private HotelServicesRepository hotelServicesRepository;


    @Autowired
    private OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public Page<Room> findAll(PageRequest pageRequest){
        return roomRepository.findAll(pageRequest);
    }
    @Transactional(readOnly = true)
    public Page<Room> findByHotelId( Long id,PageRequest pageRequest) { return roomRepository.findByHotelId(id,pageRequest);}

    @Transactional(readOnly = true)
    public Page<Room> findAvailableByHotelId(RoomSearchData roomSearchData, PageRequest pageRequest) {
            return roomRepository.returnRoomsThatAreAvailable(roomSearchData.getHotelId(),roomSearchData.getArrivalDate(),roomSearchData.getDepartureDate(),roomSearchData.getType(),roomSearchData.getNumberOfPeople(),roomSearchData.getNumberOfRooms(),roomSearchData.getNumberOfBeds(),roomSearchData.getMinPrice(),roomSearchData.getMaxPrice(), findDaysInBetween(roomSearchData.getArrivalDate(),roomSearchData.getDepartureDate()),pageRequest);

    }


    @Transactional(readOnly = true)
    public List<ReservationHotel> listQuickReservations(Long idHotel){
        String date = java.time.LocalDate.now().toString();
        return hotelReservationRepository.listQuick(idHotel, date);
    }


    private int findDaysInBetween(String arrivalString,String departureString){
        Date arrival;
        Date departure;
        try {
            arrival=new SimpleDateFormat("yyyy-MM-dd").parse(arrivalString);
            departure=new SimpleDateFormat("yyyy-MM-dd").parse(departureString);
        } catch (Exception e){
            return -1;
        }
        long diff = Math.abs(departure.getTime() - arrival.getTime());
        long days = (diff / (1000*60*60*24));
        int idays = (int) days;
        return (idays==0)?1:idays;
    }

    @Transactional(readOnly = true)
    public Integer getAvailableUnavailabeRooms(Long hotelId,Boolean areAvailable,String start,String end) {
        List<Room> rooms;
        if(areAvailable)
            rooms = roomRepository.returnRoomsAvailable(hotelId,start,end);
        else
            rooms = roomRepository.returnRoomsUnavailalbe(hotelId,start,end);
        if(rooms !=null)
            return  rooms.size();
        else
            return 0;
    }

    @Transactional(readOnly = true)
    public Room findById( Long id) {
        Optional<Room> oRoom = roomRepository.findById(id);
        if(oRoom.isPresent())
            return oRoom.get();
        else
            return null;
    }

    @Transactional
    public boolean editRoom(Room room, User user, Long hotelId) {

        if(!checkIfAdminAndCorrectAdmin(hotelId,user))
            return false;
        Room foundRoom = roomRepository.findById(room.getId()).get();
        foundRoom.setNumberOfRooms(room.getNumberOfRooms());
        foundRoom.setRoomType(room.getRoomType());
        foundRoom.setNumberOfPeople(room.getNumberOfPeople());
        foundRoom.setRoomNumber(room.getRoomNumber());
        foundRoom.setFloor(room.getFloor());
        foundRoom.setName(room.getName());
        foundRoom.setNumberOfBeds(room.getNumberOfBeds());
        roomRepository.save(foundRoom);
        return true;
    }

    @Transactional
    public int quickReserve(QuickReserveData quickReserveData,User user){
        ReservationHotel reservationHotel = hotelReservationRepository.findByUserOrder_Id(quickReserveData.getOrderId());
        Optional<Order> optionalOrder = orderRepository.findById(quickReserveData.getOrderId());
        if(reservationHotel != null)
            return 2;
        Optional<ReservationHotel> optionalReservationHotel = hotelReservationRepository.findById(quickReserveData.getReservationId());
        if(optionalReservationHotel.isPresent())
        {
            ReservationHotel found = optionalReservationHotel.get();
            if(found.getUser() == null && optionalOrder.isPresent())
            {
                Order order = optionalOrder.get();
                order.setReservationHotel(found);
                found.setUser(user);
                found.setUserOrder(order);
                orderRepository.save(order);
                hotelReservationRepository.save(found);
                return 0;
            }
            else
                return 4;
        }
        else
            return 4;
    }

    @Transactional
    public int reserveRoom(ReservationHotelData reservationHotelData, User user) {
        ReservationHotel reservationHotel = hotelReservationRepository.findByUserOrder_Id(reservationHotelData.getReservationId());
        if(reservationHotel != null)
            return 2;
        Room room = roomRepository.checkIfAvailableStill(reservationHotelData.getRoomId(),reservationHotelData.getArrivalDate(),reservationHotelData.getDepartureDate());
        Optional<Room> exists  = roomRepository.findById(reservationHotelData.getRoomId());
        Optional<Order> reservation = orderRepository.findById(reservationHotelData.getReservationId());
        Date arrival;
        Date departure;
        try {
            arrival=new SimpleDateFormat("yyyy-MM-dd").parse(reservationHotelData.getArrivalDate());
            departure=new SimpleDateFormat("yyyy-MM-dd").parse(reservationHotelData.getDepartureDate());
        } catch (Exception e){
            return -1;
        }
        long diff = Math.abs(departure.getTime() - arrival.getTime());
        long days = (diff / (1000*60*60*24));

        String services = reservationHotelData.getServices();
        List<HotelServices> servicesList = new ArrayList<>();
        BigDecimal cumulativePrice = new BigDecimal(0);

        if(room!=null)
        {
            Set<HotelPriceList> priceList = room.getHotel().getHotelPriceList();

            if(priceList != null)
                if(priceList.size()>0)
                    for(HotelPriceList stock : priceList){
                    if(stock.getRoomType().equals(room.getRoomType()))
                    {
                        cumulativePrice =  stock.getPrice().multiply(new BigDecimal(days));
                    }
            }
        }
        if(services!="")
        {
            if(services.contains(","))
            {
                String[] list = services.split(",");
                for (int i=0; i < list.length ; ++i)
                {
                    Long tmp = Long.parseLong(list[i]);
                    Optional<HotelServices> optional = hotelServicesRepository.findById(tmp);
                    if(optional.isPresent())
                    {
                        HotelServices newHotelService = optional.get();
                        cumulativePrice.add(newHotelService.getPrice());
                        servicesList.add(newHotelService);

                    }

                }
            }
            else {
                Long tmp = Long.parseLong(services);
                Optional<HotelServices> optional = hotelServicesRepository.findById(tmp);
                if(optional.isPresent())
                    servicesList.add(optional.get());
            }
        }


        if(!exists.isPresent() || !reservation.isPresent())
            return 3;
        if(room != null)
        {
            Order pendingOrder = reservation.get();
            Room roomFound = exists.get();
            ReservationHotel newReservation = new ReservationHotel();


            newReservation.setNightsStaying((int)days);
            newReservation.setPrice(cumulativePrice);
            newReservation.setUserOrder(pendingOrder);
            newReservation.setArrivalDate(arrival);
            newReservation.setPeople(roomFound.getNumberOfPeople());
            newReservation.setDepartureDate(departure);
            newReservation.setServices(servicesList);
            newReservation.setReservationDate(new Date());
            newReservation.setUser(user);
            newReservation.setRoom(roomFound);
            pendingOrder.setReservationHotel(newReservation);
            hotelReservationRepository.save(newReservation);
            orderRepository.save(pendingOrder);
            return 0;
        }
        else
            return 4;
    }


    @Transactional
    public boolean deleteRoom(Room room, User user, Long hotelId) {
        if(!checkIfAdminAndCorrectAdmin(hotelId,user))
            return false;

        Optional<Hotel> foundHotelOpt = hotelRepository.findById(hotelId);
        Optional<Room> foundRoomOpt = roomRepository.findById(room.getId());
        if (!foundRoomOpt.isPresent() || !foundHotelOpt.isPresent())
            return false;

        Room foundRoom = foundRoomOpt.get();
        Hotel hotel = foundHotelOpt.get();
        hotel.getRooms().remove(foundRoom);
        roomRepository.delete(foundRoom);
        return true;
    }


    @SuppressWarnings("Duplicates")
    private boolean checkIfAdminAndCorrectAdmin(Long id,User adminToCheck){
        if(adminToCheck.getAdministratedHotel() == null) {
            return false;
        }
        Hotel hotel = hotelRepository.findById(id).get();

        if(!hotel.getAdmins().contains(adminToCheck))
        {
            return false;
        }
        return true;
    }
}
