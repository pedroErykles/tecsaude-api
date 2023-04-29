package com.api.sigmax.tecsaude.repositories;

import com.api.sigmax.tecsaude.domain.model.StockItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockItemRepository extends JpaRepository<StockItem, Long> {
}
