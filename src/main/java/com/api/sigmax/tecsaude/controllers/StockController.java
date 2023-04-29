package com.api.sigmax.tecsaude.controllers;

import com.api.sigmax.tecsaude.domain.dtos.StockUpdateDto;
import com.api.sigmax.tecsaude.domain.dtos.CreateStockDto;
import com.api.sigmax.tecsaude.domain.model.StockItem;
import com.api.sigmax.tecsaude.services.StockItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v2/batch")
public class StockController {

    private final StockItemService stockItemService;

    public StockController(StockItemService stockItemService){
        this.stockItemService = stockItemService;
    }

    @GetMapping
    public ResponseEntity<List<StockItem>> findAll(){
        return ResponseEntity.status(HttpStatus.OK).body(stockItemService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockItem> findById(@PathVariable("id") Long id){
        Optional<StockItem> optional = stockItemService.findById(id);
        return optional
                .map(stockItem -> ResponseEntity.status(HttpStatus.OK).body(stockItem))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<StockItem> save(@Validated @RequestBody CreateStockDto dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(stockItemService.save(new StockItem(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StockItem> update(@Validated @RequestBody CreateStockDto dto, @PathVariable("id") Long id){
        return stockItemService.findById(id).map(stockItem -> ResponseEntity
                .status(HttpStatus.OK).body(stockItemService.update(new StockItem(dto, id))))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/add-or-remove")
    public ResponseEntity<StockItem> addOrRemove(@RequestBody StockUpdateDto dto){
        Optional<StockItem> optional = stockItemService.findById(dto.id());
        if(optional.isPresent()){
            optional.get().updateStock(dto.addTo(), dto.removeFrom());
            return ResponseEntity.status(HttpStatus.OK).body(stockItemService.update(optional.get()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity delete(@PathVariable Long id){
        var optional = stockItemService.findById(id);
        if(optional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }else{
            stockItemService.delete(id);
            return ResponseEntity.ok(null);
        }
    }
}
