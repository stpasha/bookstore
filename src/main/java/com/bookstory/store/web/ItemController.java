package com.bookstory.store.web;

import com.bookstory.store.service.ItemService;
import com.bookstory.store.web.dto.ItemDTO;
import com.bookstory.store.web.dto.OrderDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/items")
@Slf4j
public class ItemController {

    private final ItemService service;

    public ItemController(ItemService service) {
        this.service = service;
    }

    @GetMapping
    public String listItemsForOrder(OrderDTO orderDTO, Model model) {
        log.info("list items for order: {}", orderDTO.getId());
        List<ItemDTO> items = service.getItemsByOrder(orderDTO);
        model.addAttribute("items", items);
        return "items";
    }


    @PostMapping
    public String createItem(@Valid @ModelAttribute ItemDTO itemDTO, Model model) {
        log.info("create item: {}", itemDTO);
        return service.createItem(itemDTO).map(createdItem -> {
            model.addAttribute("item", createdItem);
            log.info("created item: {}", createdItem);
            return "item";
        }).orElseGet(() -> {
            model.addAttribute("error", "Item creation failed. Please try again.");
            log.error("Item creation failed for: {}", itemDTO);
            return "error";
        });
    }
}
