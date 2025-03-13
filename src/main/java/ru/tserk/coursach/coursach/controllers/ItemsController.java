package ru.tserk.coursach.coursach.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.tserk.coursach.coursach.models.Item;
import ru.tserk.coursach.coursach.models.ShopItem;
import ru.tserk.coursach.coursach.services.CategoryService;
import ru.tserk.coursach.coursach.services.ItemService;
import ru.tserk.coursach.coursach.services.ShopItemService;
import ru.tserk.coursach.coursach.services.ShopService;
import ru.tserk.coursach.coursach.util.EditItemValidator;
import ru.tserk.coursach.coursach.util.ItemValidator;

import javax.validation.Valid;

@Controller
@RequestMapping("/items")
public class ItemsController {

    private final ItemValidator itemValidator;
    private final ItemService itemService;
    private final CategoryService categoryService;
    private final ShopService shopService;
    private final ShopItemService shopItemService;
    private final EditItemValidator editItemValidator;

    @Autowired
    public ItemsController(ItemValidator itemValidator, ItemService itemService, CategoryService categoryService, ShopService shopService, ShopItemService shopItemService, EditItemValidator editItemValidator) {
        this.itemValidator = itemValidator;
        this.itemService = itemService;
        this.categoryService = categoryService;
        this.shopService = shopService;
        this.shopItemService = shopItemService;
        this.editItemValidator = editItemValidator;
    }

    //Главная страница учетов товаров
    @GetMapping("")
    public String itemsMainPage(){
        return "/items/ItemsMainPage";
    }

    //Страница создания новых товаров
    @GetMapping(value = "/add")
    public String newItem(@ModelAttribute("item") Item item, Model model){
        model.addAttribute("categories", categoryService.findAll());
        return "/items/newItem";
    }

    //ДОБАВИТЬ ПРОСМОТОР ВСЕХ СОЗДАННЫХ ТОВАРОВ
    @PostMapping("/add")
    public String postNewItem(@ModelAttribute("item")@Valid Item item, @RequestParam("imageFile") MultipartFile file,  BindingResult bindingResult, Model model){
        itemValidator.validate(item, bindingResult);
        if (bindingResult.hasErrors()){
            model.addAttribute("categories", categoryService.findAll());
            return "items/newItem";
        }

        itemService.saveWithPhoto(item, file);

        return "redirect:/items/add";
    }

    //Страница удаление товара
    @GetMapping("/del")
    public String delSearchPage(){
        return "items/searchPageForDelete";
    }

    @PostMapping("/del")
    public String delSearchedPage(@RequestParam("label") String label, Model model){
        model.addAttribute("items", itemService.searchItemByLabel(label));
        return "items/searchedPageForDelete";
    }

    @GetMapping("/del/{id}")
    public String itemPreDelInfo(@PathVariable("id")int id, Model model){
        model.addAttribute("item", itemService.findOneItem(id));
        model.addAttribute("shops", shopService.findAllShopByItemId(shopItemService.findAllByItem(itemService.findOneItem(id))));
        return "items/itemPreDelInfo";
    }

    @PostMapping("/del/{id}")
    public String postDelPerson(@PathVariable("id") int id, @ModelAttribute("item")Item item){
        itemService.delete(id);
        return "redirect:/items/del";
    }

    //Страница редактирования товара
    @GetMapping("/edit")
    public String editItemSearchPage(){
        return "items/searchPageForEdit";
    }

    @PostMapping("/edit")
    public String editItemSearchedPage(@RequestParam("label") String label, Model model){
        model.addAttribute("items", itemService.searchItemByLabel(label));
        return "items/searchedPageForEdit";
    }

    @GetMapping("/edit/{id}")
    public String ItemPagePerEdit(@PathVariable("id")int id, Model model){
        model.addAttribute("item", itemService.findOneItem(id));
        model.addAttribute("categories", categoryService.findAll());
        return "items/itemPreEditInfo";
    }

    @PostMapping("/edit/{id}")
    public String updatePerson(@ModelAttribute("item")@Valid Item item, BindingResult bindingResult,
                               @PathVariable("id")int id, @RequestParam(value = "imageFile", required = false) MultipartFile file,
                               Model model){
        editItemValidator.validate(item, bindingResult, id);
        if (bindingResult.hasErrors()){
            item.setItem_id(id);
            model.addAttribute("categories", categoryService.findAll());
            return "items/itemPreEditInfo";
        }

        if (file != null && !file.isEmpty()){
            itemService.updateItemWithPhoto(id, file, item);
        } else itemService.updateItem(id, item);

        return "redirect:/items/edit";
    }

    @GetMapping("/up")
    public String distributeAdd(){
        return "items/distribute/chooseShop";
    }

    @PostMapping("/up")
    public String postSearchShop(@RequestParam("address") String address, Model model){
        model.addAttribute("shops", shopService.searchShopByAddress(address));
        return "items/distribute/searchedShop";
    }

    @GetMapping("/up/{id}")
    public String ShopItemPage(@PathVariable("id") int id, Model model){
        model.addAttribute("shopItems",
                shopItemService.findShopItemsByShopId(shopService.findShopById(id)));
        model.addAttribute("thisShop", shopService.findShopById(id));
        return "items/distribute/ShopItemPage";
    }

    @GetMapping("/up/{id}/add/search")
    public String searchItem(@PathVariable("id")int id, Model model){
        model.addAttribute("thisShop", shopService.findShopById(id));
        return "items/distribute/SearchItemPage";
    }

    @PostMapping("/up/{id}/add/search")
    public String searchedItem(@PathVariable("id")int id, Model model, @RequestParam("label") String label){
        model.addAttribute("items", itemService.searchItemByLabel(label));
        model.addAttribute("thisShop", shopService.findShopById(id));
        return "items/distribute/SearchedItemPage";
    }


    @GetMapping("/up/{id}/add/search/{itemId}")
    public String addItemInShop(@PathVariable("id")int id, @PathVariable("itemId") int itemId, Model model){
        model.addAttribute("thisShop", shopService.findShopById(id));
        model.addAttribute("item", itemService.findOneItem(itemId));
        return "items/distribute/add/addPage";
    }


    @PostMapping("/up/{id}/add/search/{itemId}")
    public String addItemInShopPost(@PathVariable("id")int id, @PathVariable("itemId") int itemId, @RequestParam("count") int count){
        shopItemService.saveShopItem(new ShopItem(shopService.findShopById(id),itemService.findOneItem(itemId), count), shopService.findShopById(id), itemService.findOneItem(itemId));
        return "redirect:/items/up/"+id;
    }

    @GetMapping("/up/{id}/delete")
    public String deletePage(@PathVariable("id")int id, Model model){
        model.addAttribute("thisShop", shopService.findShopById(id));
        model.addAttribute("ShopItems", shopItemService.findAllShopItemByShopId(shopService.findShopById(id)));
        return "items/distribute/PreDeletePage";
    }

    @GetMapping("/up/{id}/delete/{itemId}")
    public String deleteShopItemPage (@PathVariable("id")int id, @PathVariable("itemId") int shopItemId, Model model){
        model.addAttribute("shopItem", shopItemService.findShopItemById(shopItemId));
        model.addAttribute("thisShop", shopService.findShopById(id));
        return "items/distribute/delete/deletePage";
    }

    @PostMapping("/up/{id}/delete/{itemId}")
    public String DeleteShopItem(@PathVariable("id")int id, @PathVariable("itemId") int shopItemId, @RequestParam("count") int count){
        shopItemService.deleteShopItem(count, shopItemId);
        return "redirect:/items/up/"+id;
    }

}
