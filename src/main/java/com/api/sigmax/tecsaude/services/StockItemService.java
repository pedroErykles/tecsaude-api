package com.api.sigmax.tecsaude.services;

import com.api.sigmax.tecsaude.domain.model.StockItem;
import com.api.sigmax.tecsaude.repositories.StockItemRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class StockItemService {

    Logger log = LoggerFactory.getLogger(StockItemService.class);

    private final StockItemRepository stockItemRepository;

    public StockItemService(StockItemRepository stockItemRepository){
        this.stockItemRepository = stockItemRepository;
    }

    public List<StockItem> findAll(){
        return stockItemRepository.findAll();
    }

    public Optional<StockItem> findById(Long id){
        return stockItemRepository.findById(id);
    }

    @Transactional
    public StockItem save(StockItem stockItem){
        stockItem.setCreatedAt(Instant.now());
        log.info("Item added to stock: {} ", stockItem.toString());
        return stockItemRepository.save(stockItem);
    }

    @Transactional
    public StockItem update(StockItem stockItem){
        Instant createdAt = findById(stockItem.getId()).get().getCreatedAt();
        stockItem.setCreatedAt(createdAt);
        stockItem.setUpdatedAt(Instant.now());
        log.info("Item updated: {} ", stockItem.toString());
        return stockItemRepository.save(stockItem);
    }

    @Transactional
    public void delete(Long id){
        var optional = findById(id);
        log.info("Item removed from stock: {}", optional.get().toString());
        stockItemRepository.deleteById(id);
    }

}
