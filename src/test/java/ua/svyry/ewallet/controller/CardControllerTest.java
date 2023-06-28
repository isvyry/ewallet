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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ua.svyry.ewallet.service.AuthenticationUtil;
import ua.svyry.ewallet.service.CardService;
import ua.svyry.ewallet.shared.CardDto;
import ua.svyry.ewallet.ui.controller.CardController;
import ua.svyry.ewallet.ui.model.CardResponseModel;
import ua.svyry.ewallet.ui.model.CreateCardRequestModel;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.Period;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CardController.class)
@ExtendWith(SpringExtension.class)
public class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    CardService cardService;
    @MockBean
    AuthenticationUtil authenticationUtil;
    @MockBean
    FormattingConversionService conversionService;

    @Test
    @WithMockUser(username = "email@gmail.com")
    public void testCreateCard() throws Exception {
        BigDecimal balance = BigDecimal.valueOf(500);
        String cardNumber = "1111222233334444";
        Long walletId = 1l;
        Date expirationDate = new Date(Instant.now().plus(Period.ofWeeks(10)).toEpochMilli());
        CreateCardRequestModel requestModel = new CreateCardRequestModel(balance, cardNumber, expirationDate,walletId);
        CardDto cardDto = CardDto.builder().build();
        CardResponseModel responseModel = CardResponseModel.builder()
                .id(1l)
                .balance(balance)
                .cardNumber(cardNumber)
                .walletId(1l)
                .expirationDate(expirationDate)
                .build();

        Authentication auth = new UsernamePasswordAuthenticationToken("email@gmail.com", "");

        when(authenticationUtil.getAuthentication()).thenReturn(auth);
        when(conversionService.convert(requestModel, CardDto.class)).thenReturn(cardDto);
        when(cardService.createCard(cardDto, auth)).thenReturn(cardDto);
        when(conversionService.convert(cardDto, CardResponseModel.class)).thenReturn(responseModel);

        CardResponseModel result =  readAsCardResponseModel(mockMvc
                .perform(post("/cards")
                        .with(csrf())
                        .content(asJsonString(requestModel))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString());

        assertEquals(1l, result.getId());
        assertEquals(cardNumber, result.getCardNumber());
        assertEquals(balance, result.getBalance());
        assertEquals(expirationDate, result.getExpirationDate());
        assertEquals(walletId, result.getWalletId());

        verify(cardService, times(1)).createCard(cardDto, auth);
        verify(conversionService, times(2)).convert(any(), (Class<Object>) any());
    }

    @Test
    @WithMockUser(username = "email@gmail.com")
    public void testGetById() throws Exception {
        Long cardId = 1l;
        Long walletId = 1l;
        BigDecimal balance = BigDecimal.valueOf(500);
        String cardNumber = "1111222233334444";
        Date expirationDate = new Date(Instant.now().plus(Period.ofWeeks(10)).toEpochMilli());
        CardDto cardDto = CardDto.builder().build();
        CardResponseModel responseModel = CardResponseModel.builder()
                .id(1l)
                .balance(balance)
                .cardNumber(cardNumber)
                .walletId(walletId)
                .expirationDate(expirationDate)
                .build();

        when(cardService.getCardDetailsById(cardId)).thenReturn(cardDto);
        when(conversionService.convert(cardDto, CardResponseModel.class)).thenReturn(responseModel);

        CardResponseModel result =  readAsCardResponseModel(mockMvc
                .perform(get("/cards/" + cardId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString());

        assertEquals(1l, result.getId());
        assertEquals(cardNumber, result.getCardNumber());
        assertEquals(balance, result.getBalance());
        assertEquals(expirationDate, result.getExpirationDate());
        assertEquals(walletId, result.getWalletId());

        verify(cardService, times(1)).getCardDetailsById(cardId);
        verify(conversionService, times(1)).convert(cardDto, CardResponseModel.class);
    }

    @Test
    @WithMockUser(username = "email@gmail.com")
    public void testGetByWalletId() throws Exception {
        Long walletId = 1l;
        Pageable pageable = PageRequest.of(1, 20);
        BigDecimal balance = BigDecimal.valueOf(500);
        String cardNumber = "1111222233334444";
        Date expirationDate = new Date(Instant.now().plus(Period.ofWeeks(10)).toEpochMilli());
        CardDto cardDto = CardDto.builder().build();
        CardResponseModel responseModel = CardResponseModel.builder()
                .id(1l)
                .balance(balance)
                .cardNumber(cardNumber)
                .walletId(walletId)
                .expirationDate(expirationDate)
                .build();

        when(cardService.getCardDetailsByWalletId(walletId, pageable)).thenReturn(List.of(cardDto));
        when(conversionService.convert(cardDto, CardResponseModel.class)).thenReturn(responseModel);

        List<CardResponseModel> result =  readAsCardResponseModelArray(mockMvc
                .perform(get("/cards/wallets/" + walletId)
                        .with(csrf())
                        .param("pageNumber", "1")
                        .param("pageSize", "20"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString());

        assertEquals(1, result.size());

        CardResponseModel resultElem = result.get(0);

        assertEquals(1l, resultElem.getId());
        assertEquals(cardNumber, resultElem.getCardNumber());
        assertEquals(balance, resultElem.getBalance());
        assertEquals(expirationDate, resultElem.getExpirationDate());
        assertEquals(walletId, resultElem.getWalletId());

        verify(cardService, times(1)).getCardDetailsByWalletId(walletId, pageable);
        verify(conversionService, times(1)).convert(cardDto, CardResponseModel.class);
    }

    @Test
    @WithMockUser(username = "email@gmail.com")
    public void testGetByCustomerId() throws Exception {
        Long customerId = 1l;
        Pageable pageable = PageRequest.of(1, 20);
        BigDecimal balance = BigDecimal.valueOf(500);
        String cardNumber = "1111222233334444";
        Date expirationDate = new Date(Instant.now().plus(Period.ofWeeks(10)).toEpochMilli());
        CardDto cardDto = CardDto.builder().build();
        CardResponseModel responseModel = CardResponseModel.builder()
                .id(1l)
                .balance(balance)
                .cardNumber(cardNumber)
                .expirationDate(expirationDate)
                .build();

        when(cardService.getCardDetailsByCustomerId(customerId, pageable)).thenReturn(List.of(cardDto));
        when(conversionService.convert(cardDto, CardResponseModel.class)).thenReturn(responseModel);

        List<CardResponseModel> result =  readAsCardResponseModelArray(mockMvc
                .perform(get("/cards/customers/" + customerId)
                        .with(csrf())
                        .param("pageNumber", "1")
                        .param("pageSize", "20"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString());

        assertEquals(1, result.size());

        CardResponseModel resultElem = result.get(0);

        assertEquals(1l, resultElem.getId());
        assertEquals(cardNumber, resultElem.getCardNumber());
        assertEquals(balance, resultElem.getBalance());
        assertEquals(expirationDate, resultElem.getExpirationDate());

        verify(cardService, times(1)).getCardDetailsByCustomerId(customerId, pageable);
        verify(conversionService, times(1)).convert(cardDto, CardResponseModel.class);
    }

    @Test
    @WithMockUser(username = "email@gmail.com")
    public void testDeleteCard() throws Exception {
        Long cardId = 1l;

        Authentication auth = new UsernamePasswordAuthenticationToken("email@gmail.com", "");

        when(authenticationUtil.getAuthentication()).thenReturn(auth);

        mockMvc.perform(delete("/cards/" + cardId)
                        .with(csrf()))
                .andExpect(status().isNoContent());


        verify(cardService, times(1)).deleteCard(cardId, auth);
        verify(authenticationUtil, times(1)).getAuthentication();
    }

    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private CardResponseModel readAsCardResponseModel(final String result) {
        try {
            return new ObjectMapper().readValue(result, CardResponseModel.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<CardResponseModel> readAsCardResponseModelArray(final String result) {
        try {
            return new ObjectMapper().readValue(result, new TypeReference<List<CardResponseModel>>(){});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
