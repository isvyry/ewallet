package ua.svyry.ewallet.ui.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
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
    public ResponseEntity<List<CardResponseModel>> getAllByWallet(@PathVariable Long walletId,
                                                                 @RequestParam(defaultValue = "0") Integer pageNumber,
                                                                 @RequestParam(defaultValue = "20") Integer pageSize) {
        List<CardResponseModel> responseModel = cardService.getCardDetailsByWalletId(walletId,
                        PageRequest.of(pageNumber, pageSize))
                .stream().map(c -> conversionService.convert(c, CardResponseModel.class))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<CardResponseModel>> getAllByCustomer(@PathVariable Long customerId,
                                                              @RequestParam(defaultValue = "0") Integer pageNumber,
                                                              @RequestParam(defaultValue = "20") Integer pageSize) {
        List<CardResponseModel> responseModel = cardService.getCardDetailsByCustomerId(customerId,
                        PageRequest.of(pageNumber, pageSize))
                .stream().map(c -> conversionService.convert(c, CardResponseModel.class))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
