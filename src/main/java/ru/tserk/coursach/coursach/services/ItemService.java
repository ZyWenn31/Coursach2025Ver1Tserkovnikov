package ru.tserk.coursach.coursach.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.tserk.coursach.coursach.models.Item;
import ru.tserk.coursach.coursach.repositories.ItemRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true) // delete read only
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Optional<Item> findItemByLabel(String label){
        return itemRepository.findByLabel(label);
    }

    @Transactional // delete
    public void save(Item item){
        itemRepository.save(item);
    }

    // add @Transactional
    public Item findOneItem(int id){
        return itemRepository.findById(id).orElse(null);
    }

    public List<Item> searchItemByLabel(String item_label){
        if (item_label.isEmpty()){
            return List.of();
        }
        return itemRepository.findByLabelStartingWith(item_label);
    }

    public  List<Item> findAll(){
        return itemRepository.findAll();
    }


    @Transactional
    public void saveWithPhoto(Item item, MultipartFile file){
        String UPLOAD_DIR = "uploads/";
        if (file.isEmpty()) {
            item.setImage(null);
        } else{
            try {
                // Создаем папку, если её нет
                Files.createDirectories(Paths.get(UPLOAD_DIR));

                // Генерируем уникальное имя файла
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path path = Paths.get(UPLOAD_DIR + fileName);

                // Сохраняем файл
                Files.write(path, file.getBytes());
                item.setImage(fileName);

            } catch (IOException e) {
                System.out.println("Ошибка загрузки картинки");
            }
        }
        itemRepository.save(item);
    }


    @Transactional // delete
    public void delete(int id){
        itemRepository.deleteById(id);
    }

    @Transactional // delete
    public void updateItem(int id, Item itemFields){
        Item existingItem = itemRepository.findById(id).orElse(null);

        existingItem.setItem_price(itemFields.getItem_price());
        existingItem.setCategory_id(itemFields.getCategory_id());
        existingItem.setDescription(itemFields.getDescription());
        existingItem.setLabel(itemFields.getLabel());

        itemRepository.save(existingItem);
    }

    @Transactional
    public void updateItemWithPhoto(int id, MultipartFile file, Item item){

        String UPLOAD_DIR = "uploads/";

        Item existingItem = itemRepository.findById(id).orElse(null);

        existingItem.setItem_price(item.getItem_price());
        existingItem.setCategory_id(item.getCategory_id());
        existingItem.setDescription(item.getDescription());
        existingItem.setLabel(item.getLabel());


        try {
            // Генерируем уникальное имя файла
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get(UPLOAD_DIR + fileName);

            Path pathToDelete = Paths.get(UPLOAD_DIR + itemRepository.getById(id).getImage());
            Files.delete(pathToDelete);

            Files.write(path, file.getBytes());

            existingItem.setImage(fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        itemRepository.save(existingItem);
    }
}
