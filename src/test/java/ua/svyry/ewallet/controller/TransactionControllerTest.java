package ua.svyry.ewallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ua.svyry.ewallet.service.AuthenticationUtil;
import ua.svyry.ewallet.service.TransactionService;
import ua.svyry.ewallet.shared.TransactionDto;
import ua.svyry.ewallet.ui.controller.TransactionController;
import ua.svyry.ewallet.ui.model.CreateTransactionRequestModel;
import ua.svyry.ewallet.ui.model.CreateTransferTransactionRequestModel;
import ua.svyry.ewallet.ui.model.TransactionResult;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
@ExtendWith(SpringExtension.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    TransactionService transactionService;
    @MockBean
    AuthenticationUtil authenticationUtil;
    @MockBean
    FormattingConversionService conversionService;

    @Test
    @WithMockUser(username = "email@gmail.com")
    public void testDeposit() throws Exception {

        BigDecimal transactionAmount = BigDecimal.valueOf(500);
        Long cardId = 1l;
        CreateTransactionRequestModel deposit = new CreateTransactionRequestModel(transactionAmount, cardId);
        TransactionDto transactionDto = TransactionDto.builder()
                .amount(transactionAmount)
                .cardId(cardId)
                .build();

        TransactionResult transactionResult = TransactionResult.builder()
                .id(1l)
                .cardId(cardId)
                .amount(transactionAmount)
                .isSuccessful(true)
                .isSuspicious(false)
                .transactionType("deposit")
                .build();

        Authentication auth = new UsernamePasswordAuthenticationToken("email", "");

        when(authenticationUtil.getAuthentication()).thenReturn(auth);
        when(conversionService.convert(deposit, TransactionDto.class)).thenReturn(transactionDto);
        when(transactionService.depositFunds(transactionDto, auth)).thenReturn(transactionDto);
        when(conversionService.convert(transactionDto, TransactionResult.class)).thenReturn(transactionResult);

        TransactionResult result =  readAsTransactionResult(mockMvc
                .perform(post("/transactions/deposit")
                        .with(csrf())
                        .content(asJsonString(deposit))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString());

        assertEquals(1l, result.getId());
        assertEquals(cardId, result.getCardId());
        assertEquals(transactionAmount, result.getAmount());
        assertEquals("deposit", result.getTransactionType());
        assertTrue(result.isSuccessful());
        assertFalse(result.isSuspicious());

        verify(authenticationUtil, times(1)).getAuthentication();
        verify(transactionService, times(1)).depositFunds(transactionDto, auth);
        verify(conversionService, times(2)).convert(any(), (Class<Object>) any());
    }

    @Test
    @WithMockUser(username = "email@gmail.com")
    public void testWithdraw() throws Exception {

        BigDecimal transactionAmount = BigDecimal.valueOf(500);
        Long cardId = 1l;
        CreateTransactionRequestModel deposit = new CreateTransactionRequestModel(transactionAmount, cardId);
        TransactionDto transactionDto = TransactionDto.builder()
                .amount(transactionAmount)
                .cardId(cardId)
                .build();

        TransactionResult transactionResult = TransactionResult.builder()
                .id(1l)
                .cardId(cardId)
                .amount(transactionAmount)
                .isSuccessful(true)
                .isSuspicious(false)
                .transactionType("withdrawal")
                .build();
        Authentication auth = new UsernamePasswordAuthenticationToken("email", "");

        when(authenticationUtil.getAuthentication()).thenReturn(auth);
        when(conversionService.convert(deposit, TransactionDto.class)).thenReturn(transactionDto);
        when(transactionService.withdrawFunds(transactionDto, auth)).thenReturn(transactionDto);
        when(conversionService.convert(transactionDto, TransactionResult.class)).thenReturn(transactionResult);

        TransactionResult result =  readAsTransactionResult(mockMvc
                .perform(post("/transactions/withdraw")
                        .with(csrf())
                        .content(asJsonString(deposit))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString());

        assertEquals(1l, result.getId());
        assertEquals(cardId, result.getCardId());
        assertEquals(transactionAmount, result.getAmount());
        assertEquals("withdrawal", result.getTransactionType());
        assertTrue(result.isSuccessful());
        assertFalse(result.isSuspicious());

        verify(authenticationUtil, times(1)).getAuthentication();
        verify(transactionService, times(1)).withdrawFunds(transactionDto, auth);
        verify(conversionService, times(2)).convert(any(), (Class<Object>) any());
    }
    @Test
    @WithMockUser(username = "email@gmail.com")
    public void testTransfer() throws Exception {

        BigDecimal transactionAmount = BigDecimal.valueOf(500);
        Long card1Id = 1l;
        Long card2Id = 2l;
        CreateTransferTransactionRequestModel deposit =
                new CreateTransferTransactionRequestModel(transactionAmount, card1Id, card2Id);
        TransactionDto transactionDto = TransactionDto.builder()
                .amount(transactionAmount)
                .cardId(card1Id)
                .receiverCardId(card2Id)
                .build();

        TransactionResult transactionResult = TransactionResult.builder()
                .id(1l)
                .cardId(card1Id)
                .receiverCardId(card2Id)
                .amount(transactionAmount)
                .isSuccessful(true)
                .isSuspicious(false)
                .transactionType("transfer")
                .build();

        Authentication auth = new UsernamePasswordAuthenticationToken("email", "");

        when(authenticationUtil.getAuthentication()).thenReturn(auth);
        when(conversionService.convert(deposit, TransactionDto.class)).thenReturn(transactionDto);
        when(transactionService.transferFunds(transactionDto, auth)).thenReturn(transactionDto);
        when(conversionService.convert(transactionDto, TransactionResult.class)).thenReturn(transactionResult);

        TransactionResult result =  readAsTransactionResult(mockMvc
                .perform(post("/transactions/transfer")
                        .with(csrf())
                        .content(asJsonString(deposit))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString());

        assertEquals(1l, result.getId());
        assertEquals(card1Id, result.getCardId());
        assertEquals(card2Id, result.getReceiverCardId());
        assertEquals(transactionAmount, result.getAmount());
        assertEquals("transfer", result.getTransactionType());
        assertTrue(result.isSuccessful());
        assertFalse(result.isSuspicious());

        verify(authenticationUtil, times(1)).getAuthentication();
        verify(transactionService, times(1)).transferFunds(transactionDto, auth);
        verify(conversionService, times(2)).convert(any(), (Class<Object>) any());
    }

    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private TransactionResult readAsTransactionResult(final String result) {
        try {
            return new ObjectMapper().readValue(result, TransactionResult.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
