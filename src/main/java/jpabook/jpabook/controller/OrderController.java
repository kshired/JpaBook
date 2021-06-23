package jpabook.jpabook.controller;

import jpabook.jpabook.domain.Member;
import jpabook.jpabook.domain.Order;
import jpabook.jpabook.domain.item.Item;
import jpabook.jpabook.repository.OrderSearch;
import jpabook.jpabook.service.ItemService;
import jpabook.jpabook.service.MemberService;
import jpabook.jpabook.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    @GetMapping(value = "/order")
    public String createForm(Model model) {
        List<Member> members = memberService.findMembers();
        List<Item> items = itemService.findItems();

        model.addAttribute("members", members);
        model.addAttribute("items", items);

        return "order/orderForm";
    }

    @PostMapping(value = "/order")
    public String order(@RequestParam("memberId") Long memberId,
                        @RequestParam("itemId") Long itemId, @RequestParam("count") int count) {
        orderService.order(memberId,itemId,count);

        return "redirect:/orders";
    }

    @GetMapping(value = "/orders")
    public String list(@ModelAttribute OrderSearch orderSearch, Model model){
        List<Order> orders = orderService.findOrders(orderSearch);
        model.addAttribute("orders",orders);

        return "order/orderList";
    }

    @PostMapping(value = "/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable Long orderId){
        orderService.cancelOrder(orderId);

        return "redirect:/orders";
    }
}
