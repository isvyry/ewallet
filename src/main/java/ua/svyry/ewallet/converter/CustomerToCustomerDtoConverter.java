package ua.svyry.ewallet.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ua.svyry.ewallet.entity.Customer;
import ua.svyry.ewallet.shared.CustomerDto;

@Component
@Transactional(readOnly = true)
public class CustomerToCustomerDtoConverter implements Converter<Customer, CustomerDto> {
    @Override
    public CustomerDto convert(Customer source) {
        CustomerDto response =  CustomerDto.builder()
                .userId(source.getId())
                .email(source.getEmail())
                .firstName(source.getFirstName())
                .encryptedPassword(source.getEncryptedPassword())
                .lastName(source.getLastName())
                .build();

        if (source.getWallet() != null) {
            response.setWalletNumber(source.getWallet().getWalletNumber().toString());
            response.setWalletId(source.getWallet().getId());
        }
        return response;
    }
}
