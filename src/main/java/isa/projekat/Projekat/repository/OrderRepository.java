package isa.projekat.Projekat.repository;

import isa.projekat.Projekat.model.airline.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query(value = "SELECT o.* FROM orders o WHERE o.placed_order_id = ?1 AND o.finished IS FALSE", nativeQuery = true)
    List<Order> findAllByPlacedOrderIdAndFinishedIsFalse(Long id);


    @Query(value = "SELECT o.* FROM orders o WHERE o.placed_order_id = ?1", nativeQuery = true)
    List<Order> findAllByPlacedOrderId(Long id);


    @Query(value = "SELECT o.* FROM orders o LEFT OUTER JOIN orders_reservations os  ON o.id = os.order_id LEFT OUTER JOIN reservations r ON os.reservations_id = r.id" +
            " LEFT OUTER JOIN flights f ON r.flight_id = f.id WHERE" +
            " f.start_time >=?2 AND r.user_id = ?1", nativeQuery = true)
    List<Order> findAllFutureOrdersByUserIdAndDate(Long userId, String todayDate);

    @Override
    Optional<Order> findById(Long aLong);

    @Query(value = "SELECT o.* FROM orders o" +
            " LEFT OUTER JOIN orders_reservations oq ON oq.order_id = o.id" +
            " WHERE oq.reservations_id = ?1", nativeQuery = true)
    Order findByReservationId(Long id);

}
