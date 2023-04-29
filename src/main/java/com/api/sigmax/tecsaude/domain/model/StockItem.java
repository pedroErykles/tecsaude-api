package com.api.sigmax.tecsaude.domain.model;

import com.api.sigmax.tecsaude.domain.dtos.CreateStockDto;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;

@Entity
@Table(name = "TB_BATCH")
@NoArgsConstructor
@Data
@ToString
@EqualsAndHashCode
public class StockItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private Integer quantity;
    private String itemType;
    private Instant createdAt;
    private Instant updatedAt;

    public StockItem(CreateStockDto dto){
        this.name = dto.name();
        this.quantity = dto.quantity();
        this.itemType = dto.itemType();
    }

    public StockItem(CreateStockDto dto, Long id){
        this.id = id;
        this.name = dto.name();
        this.quantity = dto.quantity();
        this.itemType = dto.itemType();
    }

    public void updateStock(Integer addTo, Integer removeFrom){
        this.quantity += addTo;
        this.quantity -= removeFrom;
    }
}
