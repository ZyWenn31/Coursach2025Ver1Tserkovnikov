package ru.tserk.coursach.coursach.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.tserk.coursach.coursach.security.PersonDetails;
import ru.tserk.coursach.coursach.services.ItemService;
import ru.tserk.coursach.coursach.services.ShopItemService;
import ru.tserk.coursach.coursach.services.ShopService;

@Controller
@RequestMapping("/main")
public class UserController {


    private final ShopService shopService;
    private final ShopItemService shopItemService;
    private final ItemService itemService;

    public UserController(ShopService shopService, ShopItemService shopItemService, ItemService itemService) {
        this.shopService = shopService;
        this.shopItemService = shopItemService;
        this.itemService = itemService;
    }


    //Переход на главную страницу
    @GetMapping
    public String mainPage(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();

        model.addAttribute("person", personDetails.getPerson());
        model.addAttribute("role", personDetails.getPerson().getRole());
        return "user/mainPage";
    }

    @GetMapping("all")
    public String allItems(Model model){
        model.addAttribute("allItems", itemService.findAll());
        return "user/allItems";
    }

    //Страница поиска
    @GetMapping("/search")
    public String searchPage(){
        return "user/searchPage";
    }

    //Страница результатов
    @PostMapping("/search")
    public String postSearchPage(@RequestParam("label")String item_label,
                                 Model model){
        model.addAttribute("items", itemService.searchItemByLabel(item_label));

        return "user/searchedPage";
    }

    //Страница конкретной техники
    @GetMapping("/itemInfo/{id}")
    public String getItemInfo(@PathVariable("id") int id, Model model){
        model.addAttribute("item", itemService.findOneItem(id));
        model.addAttribute("shops", shopService.findAllShopByItemId(shopItemService.findAllByItem(itemService.findOneItem(id))));
        return "user/itemPage";
    }
}
