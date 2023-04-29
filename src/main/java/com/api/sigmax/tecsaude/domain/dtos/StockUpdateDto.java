package com.api.sigmax.tecsaude.domain.dtos;

import lombok.NonNull;

public record StockUpdateDto(@NonNull Long id, @NonNull Integer addTo, @NonNull Integer removeFrom){

}