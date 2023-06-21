package ua.svyry.ewallet.ui.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.svyry.ewallet.service.CustomerService;
import ua.svyry.ewallet.shared.CustomerDto;
import ua.svyry.ewallet.ui.model.CreateCustomerRequestModel;
import ua.svyry.ewallet.ui.model.CustomerResponseModel;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final ConversionService conversionService;

    @PostMapping
    public ResponseEntity<CustomerResponseModel> createCustomer(@RequestBody
                                                                      @Valid
                                                                      CreateCustomerRequestModel customerRequestModel) {
        CustomerDto request = conversionService.convert(customerRequestModel, CustomerDto.class);
        CustomerResponseModel responseModel = conversionService.convert(customerService.createCustomer(request),
                CustomerResponseModel.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseModel);
    }

    @PostMapping("/{id}/unblock")
    public ResponseEntity<Void> unblockCustomer(@PathVariable Long id) {
        customerService.unblockCustomer(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseModel> getCustomerById(@PathVariable Long id) {
        CustomerResponseModel responseModel = conversionService.convert(customerService.getCustomerDetailsById(id),
                CustomerResponseModel.class);
        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponseModel>> getAllCustomers(@RequestParam(defaultValue = "0")
                                                                           Integer pageNumber,
                                                                       @RequestParam(defaultValue = "20")
                                                                           Integer pageSize) {
        List<CustomerResponseModel> responseModels = customerService
                .getAllCustomers(PageRequest.of(pageNumber, pageSize))
                .stream().map(c -> conversionService.convert(c, CustomerResponseModel.class))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responseModels);
    }

//    @PutMapping("/{id}") TODO
//    public ResponseEntity<CustomerResponseModel> updateCustomer(@PathVariable Long id, @RequestBody
//                                                                      @Valid
//                                                                      UpdateCustomerRequestModel customerRequestModel) {
//        CustomerDto request = mapper.map(customerRequestModel, CustomerDto.class);
//        CustomerResponseModel responseModel = mapper.map(customerService.createCustomer(request),
//                CustomerResponseModel.class);
//        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
//    }
}
