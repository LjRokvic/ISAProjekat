package isa.projekat.Projekat.controller.airline;


import isa.projekat.Projekat.model.airline.*;
import isa.projekat.Projekat.model.user.User;
import isa.projekat.Projekat.security.TokenUtils;
import isa.projekat.Projekat.service.airline.FlightService;
import isa.projekat.Projekat.service.user_auth.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class FlightController {

    @Autowired
    private FlightService flightService;

    @Autowired
    private TokenUtils jwtTokenUtils;

    @Autowired
    private UserService userService;


    @RequestMapping(value = "api/flight/{id}/seatData", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('ROLE_USER')")
    public SeatData findFlightSeatData(@PathVariable Long id, HttpServletRequest req){

        return flightService.findSeatDataById(id);
    }

    @RequestMapping(value = "api/flight/{id}/", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public Flight findFlightById(@PathVariable Long id, HttpServletRequest req){
        return flightService.findById(id);
    }

    @RequestMapping(value = "api/flight/book", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity bookFlight(@RequestBody BookingData bookingData, HttpServletRequest req){
        String authToken = jwtTokenUtils.getToken(req);
        String email = jwtTokenUtils.getUsernameFromToken(authToken);

        return ResponseFormatter.format(flightService.bookFlight(bookingData,email),false);
    }

    @RequestMapping(value = "api/flight/quickBook", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity quickBook(@RequestBody QuickTicketData quickTicketData, HttpServletRequest req){
        String authToken = jwtTokenUtils.getToken(req);
        String email = jwtTokenUtils.getUsernameFromToken(authToken);

        return ResponseFormatter.format(flightService.quickBookFlight(quickTicketData,email),false);

    }

    @PostMapping(value = "api/order/{orderId}/confirm", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity confirmOrder(@PathVariable Long orderId, HttpServletRequest req){
        String authToken = jwtTokenUtils.getToken(req);
        String email = jwtTokenUtils.getUsernameFromToken(authToken);

        return ResponseFormatter.format(flightService.finishOrder(orderId,email),false);
    }

    @RequestMapping(value = "api/order/{orderId}/isOrdering", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('ROLE_USER')")
    public Boolean isOrdering(@PathVariable Long orderId, HttpServletRequest req){
        String authToken = jwtTokenUtils.getToken(req);
        String email = jwtTokenUtils.getUsernameFromToken(authToken);

        return flightService.isOrdering(orderId,email);
    }

    @RequestMapping(value = "api/order/confirmLast", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity confirmLastOrder(HttpServletRequest req){
        String authToken = jwtTokenUtils.getToken(req);
        String email = jwtTokenUtils.getUsernameFromToken(authToken);

        return ResponseFormatter.format(flightService.finishLastOrder(email),false);
    }

    @RequestMapping(value = "api/flight/search", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<FlightSearchResultData> searchFlight(@RequestBody FlightSearchData searchData, HttpServletRequest req){
        return flightService.searchFlights(searchData);
    }

    @RequestMapping(value = "api/flight/reservations", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<Reservation> findUserReservations(HttpServletRequest req){
        String authToken = jwtTokenUtils.getToken(req);
        String email = jwtTokenUtils.getUsernameFromToken(authToken);
        User user = userService.findByUsername(email);
        return flightService.findReservationsByUserId(user.getId());
    }

    @RequestMapping(value = "api/flight/allReservations", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<Reservation> findAllUserReservations(HttpServletRequest req){
        String authToken = jwtTokenUtils.getToken(req);
        String email = jwtTokenUtils.getUsernameFromToken(authToken);
        User user = userService.findByUsername(email);
        return flightService.findAllReservationsByUserId(user.getId());
    }

    @RequestMapping(value= "api/flight/allOrders", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<Order> findAllUserOrders(HttpServletRequest req){
        String authToken = jwtTokenUtils.getToken(req);
        String email = jwtTokenUtils.getUsernameFromToken(authToken);
        User user = userService.findByUsername(email);
        return flightService.findAllOrders(user.getId());
    }


    @GetMapping(value = "api/flight/confirm", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('ROLE_USER')")
    public Boolean confirm(@RequestParam Long id, HttpServletRequest req){
        String authToken = jwtTokenUtils.getToken(req);
        String email = jwtTokenUtils.getUsernameFromToken(authToken);
        User user = userService.findByUsername(email);
        return flightService.confirmFlight(id,user);
    }

    @GetMapping(value = "api/flight/decline", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('USER')")
    public Boolean decline(@RequestParam Long id, HttpServletRequest req){
        String authToken = jwtTokenUtils.getToken(req);
        String email = jwtTokenUtils.getUsernameFromToken(authToken);
        User user = userService.findByUsername(email);
        return flightService.declineFlight(id,user);
    }

    @GetMapping(value = "api/flight/cancelHotel", produces = {MediaType.APPLICATION_JSON_VALUE})
    public Boolean cancelHotel(@RequestParam Long id, HttpServletRequest req){
        String authToken = jwtTokenUtils.getToken(req);
        String email = jwtTokenUtils.getUsernameFromToken(authToken);
        User user = userService.findByUsername(email);
        return flightService.cancelHotel(id,user);
    }


    @GetMapping(value = "api/flight/cancelOrder", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('USER')")
    public Boolean cancelOrder(@RequestParam Long id, HttpServletRequest req){
        String authToken = jwtTokenUtils.getToken(req);
        String email = jwtTokenUtils.getUsernameFromToken(authToken);
        User user = userService.findByUsername(email);
        return flightService.cancelOrder(id,user);
    }

    @GetMapping(value = "api/flight/cancelRent", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('USER')")
    public Boolean cancelRent(@RequestParam Long id, HttpServletRequest req){
        String authToken = jwtTokenUtils.getToken(req);
        String email = jwtTokenUtils.getUsernameFromToken(authToken);
        User user = userService.findByUsername(email);
        return flightService.cancelRent(id,user);
    }

    @GetMapping(value = "api/flight/AllInvites")
    public List<Reservation> listToAccept(HttpServletRequest req){
        String authToken = jwtTokenUtils.getToken(req);
        String email = jwtTokenUtils.getUsernameFromToken(authToken);
        User user = userService.findByUsername(email);
        return flightService.findInvites(user);
    }


    @RequestMapping(value= "api/flight/{id}/order", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('ROLE_USER')")
    public Order findAllUserOrders(HttpServletRequest req, @PathVariable Long id){
        String authToken = jwtTokenUtils.getToken(req);
        String email = jwtTokenUtils.getUsernameFromToken(authToken);
        User user = userService.findByUsername(email);
        Order or =  flightService.findOrderById(id);
        if (or.getPlacedOrder().getId().equals(user.getId())){
            return or;
        }else {
            return null;
        }
    }

    @RequestMapping(value= "api/flight/allFutureOrders", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN','ROLE_ADMIN_AIRLINE','ROLE_ADMIN_HOTEL','ROLE_ADMIN_RENT')")
    public List<Order> findAllUserFutureOrders(HttpServletRequest req){
        String authToken = jwtTokenUtils.getToken(req);
        String email = jwtTokenUtils.getUsernameFromToken(authToken);
        User user = userService.findByUsername(email);
        return flightService.findAllFutureOrders(user.getId());
    }


    @RequestMapping(value= "api/flight/allNonFinishedOrders", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<Order> findAllUserNotFinishedOrders(HttpServletRequest req){
        String authToken = jwtTokenUtils.getToken(req);
        String email = jwtTokenUtils.getUsernameFromToken(authToken);
        User user = userService.findByUsername(email);
        return flightService.findAllNotFinished(user.getId());
    }

    @RequestMapping(value = "api/flight/futureReservations", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<Reservation> findFutureUserReservations(HttpServletRequest req){
        String authToken = jwtTokenUtils.getToken(req);
        String email = jwtTokenUtils.getUsernameFromToken(authToken);
        User user = userService.findByUsername(email);
        return flightService.findFutureReservationByUserId(user.getId());
    }


    @RequestMapping(value = "api/flight/reservationRent", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<Reservation> findRentReservations(HttpServletRequest req){
        String authToken = jwtTokenUtils.getToken(req);
        String email = jwtTokenUtils.getUsernameFromToken(authToken);
        User user = userService.findByUsername(email);
        return flightService.findRentReservations(user.getId());
    }








}
