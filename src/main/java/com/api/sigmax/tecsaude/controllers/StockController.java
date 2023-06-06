package com.api.sigmax.tecsaude.controllers;

import com.api.sigmax.tecsaude.domain.dtos.StockUpdateDto;
import com.api.sigmax.tecsaude.domain.dtos.CreateStockDto;
import com.api.sigmax.tecsaude.domain.model.StockItem;
import com.api.sigmax.tecsaude.responses.ErrorResponse;
import com.api.sigmax.tecsaude.services.StockItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Rota que retorna itens no estoque")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Itens listados com sucesso",
                            content = {
                                    @Content(
                                            array = @ArraySchema(
                                                    schema = @Schema(implementation = StockItem.class)
                                            )
                                    )
                            }
                    )
            }
    )
    public ResponseEntity<List<StockItem>> findAll(){
        return ResponseEntity.status(HttpStatus.OK).body(stockItemService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Rota que retorna um item por seu id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Item retornado com sucesso",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = StockItem.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Item não encontrado",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    }
            )
    })
    public ResponseEntity<?> findById(@PathVariable("id") Long id){
        var optional = stockItemService.findById(id);

        if(optional.isEmpty()){
            return new ResponseEntity<ErrorResponse>(new ErrorResponse(
                    "Item não encontrado",
                    HttpStatus.NOT_FOUND.value()
            ), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<StockItem>(optional.get(), HttpStatus.OK);
    }

    @PostMapping
    @Operation(
            summary = "Rota que salva um item novo"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Item salvo com sucesso",
                            content = {
                                    @Content(
                                            schema = @Schema(implementation = StockItem.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Dados enviados são inválidos ou algum dado obrigatório está faltando"
                    )
            }
    )
    public ResponseEntity<?> save(@Validated @RequestBody CreateStockDto dto){
        return new ResponseEntity<StockItem>(stockItemService.save(new StockItem(dto)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Rota para atualizar dados de um item")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Item atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = StockItem.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados enviados são inválidos ou algum dado obrigatório está faltando"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Item com o id enviado não existe",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )

    })
    public ResponseEntity<?> update(@Validated @RequestBody CreateStockDto dto, @PathVariable("id") Long id){
        var optional = stockItemService.findById(id);

        if(optional.isEmpty()){
            return new ResponseEntity<ErrorResponse>(new ErrorResponse(
                    "Item não encontrado",
                    HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<StockItem>(stockItemService.update(optional.get()), HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Rota que deleta um item do estoque")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Item deletado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Recurso não encontrado"
            )
    })
    public ResponseEntity<?> delete(@PathVariable Long id){
        var optional = stockItemService.findById(id);

        if(optional.isEmpty()){
            return new ResponseEntity<ErrorResponse>(new ErrorResponse(
                    "Recurso não encontrado",
                    HttpStatus.NOT_FOUND.value()
            ), HttpStatus.NOT_FOUND);

        } else{

            stockItemService.delete(id);
            return new ResponseEntity<>(HttpStatus.OK);

        }
    }
}
