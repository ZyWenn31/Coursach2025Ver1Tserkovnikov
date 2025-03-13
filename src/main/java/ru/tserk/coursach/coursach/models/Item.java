package ru.tserk.coursach.coursach.models;

import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "item")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private int item_id;

    @NotEmpty(message = "Название товара не может быть пустым")
    @Column(name = "item_label")
    private String label;

    @NotNull(message = "Введите цену товара")
    @Column(name = "price")
    private Integer item_price;

    @NotEmpty(message = "Описание не может быть пустым")
    @Column(name = "description")
    private String description;

    @NotNull(message = "Укажите категорию")
    @ManyToOne
    @JoinColumn(name = "category", referencedColumnName = "category_id")
    private Category category_id;

    @Column(name = "image")
    private String image;

    @OneToMany(mappedBy = "itemId", cascade = CascadeType.REMOVE)
    private List<OrderItem> orderItemList = new ArrayList<>();

    @OneToMany(mappedBy = "itId", cascade = CascadeType.REMOVE)
    private List<ShopItem> shopItemList = new ArrayList<>();

    @OneToMany(mappedBy = "item_id", cascade = CascadeType.REMOVE)
    private List<Cart_item> cartItemList = new ArrayList<>();


    public Item() {
    }

    public Item(String label, int item_price, String description) {
        this.label = label;
        this.item_price = item_price;
        this.description = description;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getItem_price() {
        return item_price;
    }

    public void setItem_price(Integer price) {
        this.item_price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory_id() {
        return category_id;
    }

    public void setCategory_id( Category category_id) {
        this.category_id = category_id;
    }

    public List<OrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }

    public List<ShopItem> getShopItemList() {
        return shopItemList;
    }

    public void setShopItemList(List<ShopItem> shopItemList) {
        this.shopItemList = shopItemList;
    }

    public List<Cart_item> getCartItemList() {
        return cartItemList;
    }

    public void setCartItemList(List<Cart_item> cartItemList) {
        this.cartItemList = cartItemList;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Item{" +
                "item_id=" + item_id +
                ", label='" + label + '\'' +
                ", item_price=" + item_price +
                ", description='" + description + '\'' +
                ", category_id=" + category_id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return item_id == item.item_id && Objects.equals(label, item.label) && Objects.equals(item_price, item.item_price) && Objects.equals(description, item.description) && Objects.equals(category_id, item.category_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item_id, label, item_price, description, category_id);
    }
}
