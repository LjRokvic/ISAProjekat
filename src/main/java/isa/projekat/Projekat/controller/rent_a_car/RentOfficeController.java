package isa.projekat.Projekat.controller.rent_a_car;

import isa.projekat.Projekat.aspects.AdminEnabledCheck;
import isa.projekat.Projekat.model.rent_a_car.RentOffice;
import isa.projekat.Projekat.model.user.User;
import isa.projekat.Projekat.security.TokenUtils;
import isa.projekat.Projekat.service.rent_a_car.RentOfficeService;
import isa.projekat.Projekat.service.user_auth.UserService;
import isa.projekat.Projekat.utils.PageRequestProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class RentOfficeController {


    @Autowired
    private RentOfficeService rentOfficeService;

    @Autowired
    private PageRequestProvider pageRequestProvider;

    @Autowired
    private TokenUtils jwtTokenUtils;

    @Autowired
    private UserService userService;

    @PermitAll
    @GetMapping(value = "api/office/findAll", produces = {MediaType.APPLICATION_JSON_VALUE})
    public Page<RentOffice> findAll(@RequestParam String page) {
        return rentOfficeService.findAll(pageRequestProvider.provideRequest(page));
    }

    @PermitAll
    @GetMapping(value = "api/office/findByIdAll",  produces = {MediaType.APPLICATION_JSON_VALUE})
    public Page<RentOffice> findAllByRentACarId(@RequestParam long id,@RequestParam String page) {

        return rentOfficeService.findAllByRentACarId(id,pageRequestProvider.provideRequest(page));
    }


    @PreAuthorize("hasRole('ROLE_ADMIN_RENT')")
    @AdminEnabledCheck
    @PostMapping(value = "api/office/{idrent}/edit", produces = {MediaType.APPLICATION_JSON_VALUE})
    public  boolean changed(@RequestBody RentOffice changed, @PathVariable Long idrent, HttpServletRequest httpServletRequest) {
        String authToken = jwtTokenUtils.getToken(httpServletRequest);
        String email = jwtTokenUtils.getUsernameFromToken(authToken);
        User user = userService.findByUsername(email);
        return rentOfficeService.editOffice(changed, user,idrent);
    }

    @RequestMapping(value = "api/office/{idrent}/{id}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public RentOffice findByIdAndRentACarId(@PathVariable long id,@PathVariable long idrent){
        return rentOfficeService.findByIdAndRentACarId(id,idrent);
    }

    @RequestMapping(value ="api/office/all", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<RentOffice> findAllLocations(@RequestParam Long id){
        return rentOfficeService.findAllByRentACarIdList(id);
    }



    @PreAuthorize("hasRole('ROLE_ADMIN_RENT')")
    @AdminEnabledCheck
    @PostMapping(value = "api/office/{rentid}/add", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map<String,String>> addOffice(@PathVariable long rentid, @RequestBody RentOffice rentOffice, HttpServletRequest httpServletRequest) {

        String authToken = jwtTokenUtils.getToken(httpServletRequest);
        String email = jwtTokenUtils.getUsernameFromToken(authToken);
        User user = userService.findByUsername(email);

        if (rentOfficeService.addOffice(rentOffice,rentid,user)){
            Map<String, String> result = new HashMap<>();
            result.put("result", "success");
            return ResponseEntity.accepted().body(result);
        }else {
            return ResponseEntity.status(401).build();
        }

    }



    @PreAuthorize("hasRole('ROLE_ADMIN_RENT')")
    @AdminEnabledCheck
    @PostMapping(value = "api/office/{idrent}/remove", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map<String,String>> removeOffice(@PathVariable Long idrent,@RequestParam long id ,HttpServletRequest httpServletRequest){
        User user = getUser(httpServletRequest);
        return responseTransaction(rentOfficeService.removeOffice(id,idrent,user));
    }




    @SuppressWarnings("Duplicates")
    private ResponseEntity<Map<String,String>> responseTransaction(Boolean resultOfTransaction ){
        Map<String, String> result = new HashMap<>();
        if(resultOfTransaction )
        {
            result.put("result", "success");
            return ResponseEntity.accepted().body(result);
        }
        else
        {
            result.put("result", "error");
            result.put("body","401, Unauthorized access");
            return ResponseEntity.accepted().body(result);
        }
    }

    private User getUser(HttpServletRequest httpServletRequest){
        String authToken = jwtTokenUtils.getToken(httpServletRequest);
        String email = jwtTokenUtils.getUsernameFromToken(authToken);
        User user = userService.findByUsername(email);
        return user;
    }



}
