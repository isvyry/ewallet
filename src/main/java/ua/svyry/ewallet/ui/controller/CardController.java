package ua.svyry.ewallet.ui.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.svyry.ewallet.service.CardService;
import ua.svyry.ewallet.shared.CardDto;
import ua.svyry.ewallet.ui.model.CreateCardRequestModel;
import ua.svyry.ewallet.ui.model.CardResponseModel;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;
    private final ConversionService conversionService;

    @PostMapping
    public ResponseEntity<CardResponseModel> createCard(@RequestBody @Valid
                                                                  CreateCardRequestModel cardRequestModel) {
        CardDto request = conversionService.convert(cardRequestModel, CardDto.class);
        CardResponseModel responseModel = conversionService.convert(cardService.createCard(request),
                CardResponseModel.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardResponseModel> getById(@PathVariable Long id) {
        CardResponseModel responseModel = conversionService.convert(cardService.getCardDetailsById(id),
                CardResponseModel.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseModel);
    }

    @GetMapping("/wallet/{walletId}")
    public ResponseEntity<CardResponseModel> getAllByWallet(@PathVariable Long walletId,
                                                            @RequestParam(defaultValue = "0") Integer pageNumber,
                                                            @RequestParam(defaultValue = "20") Integer pageSize) {
        CardResponseModel responseModel = conversionService.convert(cardService.getCardDetailsByWalletId(walletId,
                        PageRequest.of(pageNumber, pageSize)),
                CardResponseModel.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseModel);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<CardResponseModel> getAllByCustomer(@PathVariable Long customerId,
                                                              @RequestParam(defaultValue = "0") Integer pageNumber,
                                                              @RequestParam(defaultValue = "20") Integer pageSize) {
        CardResponseModel responseModel = conversionService.convert(cardService.getCardDetailsByCustomerId(customerId,
                        PageRequest.of(pageNumber, pageSize)),
                CardResponseModel.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseModel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
