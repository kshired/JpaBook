package jpabook.jpabook.api;

import jpabook.jpabook.domain.Address;
import jpabook.jpabook.domain.Order;
import jpabook.jpabook.domain.OrderStatus;
import jpabook.jpabook.repository.OrderRepository;
import jpabook.jpabook.repository.OrderSearch;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

/*
* xToOne ( ManyToOne, OneToOne ) 관계 최적화
* Order
* Order -> Member
* Order -> Delivery
*/
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {
    private final OrderRepository orderRepository;

    /**
     * V1. 엔티티 직접 노출
     * - Hibernate5Module 모듈 등록, LAZY=null 처리
     * - 양방향 관계 문제 발생 -> @JsonIgnore ( order 또는 member에 ) 설정해야 함.
     * - 사용하지 말 것!
     */
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());

        // force LAZY 로딩 없이 사용하는 법
        for(Order order : all){
            order.getMember().getName(); //Lazy 강제 초기화
            order.getDelivery().getAddress(); //Lazy 강제 초기화
        }

        return all;
    }

    /**
     * V2. 엔티티를 조회해서 DTO 변환하여 Response ( fetch join x )
     * - 단점 : 지연로딩으로 인한 1 + N 문제 발생
     */
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
        // ORDER 2개
        // 1 ( ORDER 쿼리한번 -> ORDER 2개 ) + 회원 N(2번) + 배송 N(2번)
        // -> 1 + N 문제 발생!
        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());
        return orders.stream()
                .map(SimpleOrderDto::new)
                .collect(toList());
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate; //주문시간
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
        }
    }

}
