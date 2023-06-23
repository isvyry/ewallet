package ua.svyry.ewallet.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ua.svyry.ewallet.service.CustomerService;
import ua.svyry.ewallet.shared.CustomerDto;
import ua.svyry.ewallet.ui.controller.CustomerController;
import ua.svyry.ewallet.ui.model.CreateCustomerRequestModel;
import ua.svyry.ewallet.ui.model.CustomerResponseModel;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
@ExtendWith(SpringExtension.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    CustomerService customerService;
    @MockBean
    FormattingConversionService conversionService;

    @Test
    @WithMockUser
    public void testCreateCustomer() throws Exception {
        Long customerId = 1l;
        String walletNumber = UUID.randomUUID().toString();
        Long walletId = 1l;
        String firstName = "John";
        String lastName = "Doe";
        String password = "password";
        String email = "email@gmail.com";
        CreateCustomerRequestModel requestModel = new CreateCustomerRequestModel(firstName, lastName, email, password);
        CustomerDto customerDto = CustomerDto.builder().build();
        CustomerResponseModel customerResponseModel = CustomerResponseModel.builder()
                .id(customerId)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .walletId(walletId)
                .walletNumber(walletNumber)
                .build();

        when(conversionService.convert(requestModel, CustomerDto.class)).thenReturn(customerDto);
        when(customerService.createCustomer(customerDto)).thenReturn(customerDto);
        when(conversionService.convert(customerDto, CustomerResponseModel.class)).thenReturn(customerResponseModel);

        CustomerResponseModel result = readAsCustomerResponseModel(mockMvc
                .perform(post("/customers")
                        .with(csrf())
                        .content(asJsonString(requestModel))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString());

        assertEquals(customerId, result.getId());
        assertEquals(firstName, result.getFirstName());
        assertEquals(lastName, result.getLastName());
        assertEquals(email, result.getEmail());
        assertEquals(walletId, result.getWalletId());
        assertEquals(walletNumber, result.getWalletNumber());

        verify(customerService, times(1)).createCustomer(customerDto);
        verify(conversionService, times(2)).convert(any(), (Class<Object>) any());
    }

    @Test
    @WithMockUser
    public void testGetCustomerById() throws Exception {
        Long customerId = 1l;
        String walletNumber = UUID.randomUUID().toString();
        Long walletId = 1l;
        String firstName = "John";
        String lastName = "Doe";
        String email = "email@gmail.com";
        CustomerDto customerDto = CustomerDto.builder().build();
        CustomerResponseModel customerResponseModel = CustomerResponseModel.builder()
                .id(customerId)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .walletId(walletId)
                .walletNumber(walletNumber)
                .build();

        when(customerService.getCustomerDetailsById(customerId)).thenReturn(customerDto);
        when(conversionService.convert(customerDto, CustomerResponseModel.class)).thenReturn(customerResponseModel);

        CustomerResponseModel result = readAsCustomerResponseModel(mockMvc
                .perform(get("/customers/" + customerId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString());

        assertEquals(customerId, result.getId());
        assertEquals(firstName, result.getFirstName());
        assertEquals(lastName, result.getLastName());
        assertEquals(email, result.getEmail());
        assertEquals(walletId, result.getWalletId());
        assertEquals(walletNumber, result.getWalletNumber());

        verify(customerService, times(1)).getCustomerDetailsById(customerId);
        verify(conversionService, times(1)).convert(customerDto, CustomerResponseModel.class);
    }

    @Test
    @WithMockUser
    public void testGetAllCustomers() throws Exception {
        Long customerId = 1l;
        String walletNumber = UUID.randomUUID().toString();
        Long walletId = 1l;
        String firstName = "John";
        String lastName = "Doe";
        String email = "email@gmail.com";
        Pageable pageable = PageRequest.of(1, 20);
        CustomerDto customerDto = CustomerDto.builder().build();
        CustomerResponseModel customerResponseModel = CustomerResponseModel.builder()
                .id(customerId)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .walletId(walletId)
                .walletNumber(walletNumber)
                .build();

        when(customerService.getAllCustomers(pageable)).thenReturn(List.of(customerDto));
        when(conversionService.convert(customerDto, CustomerResponseModel.class)).thenReturn(customerResponseModel);

        List<CustomerResponseModel> resultList = readAsCardResponseModelArray(mockMvc
                .perform(get("/customers")
                        .with(csrf())
                        .param("pageNumber", "1")
                        .param("pageSize", "20"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString());

        assertEquals(1, resultList.size());

        CustomerResponseModel result = resultList.get(0);

        assertEquals(customerId, result.getId());
        assertEquals(firstName, result.getFirstName());
        assertEquals(lastName, result.getLastName());
        assertEquals(email, result.getEmail());
        assertEquals(walletId, result.getWalletId());
        assertEquals(walletNumber, result.getWalletNumber());

        verify(customerService, times(1)).getAllCustomers(pageable);
        verify(conversionService, times(1)).convert(customerDto, CustomerResponseModel.class);
    }

    @Test
    @WithMockUser
    public void testUnblockCustomer() throws Exception {
        Long customerId = 1l;

        mockMvc.perform(post("/customers/" + customerId + "/unblock")
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andReturn();

        verify(customerService, times(1)).unblockCustomer(customerId);
    }

    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private CustomerResponseModel readAsCustomerResponseModel(final String result) {
        try {
            return new ObjectMapper().readValue(result, CustomerResponseModel.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<CustomerResponseModel> readAsCardResponseModelArray(final String result) {
        try {
            return new ObjectMapper().readValue(result, new TypeReference<List<CustomerResponseModel>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
