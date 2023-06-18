package ua.svyry.ewallet.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ua.svyry.ewallet.shared.CustomerDto;
import ua.svyry.ewallet.ui.model.CustomerResponseModel;

@Component
public class CustomerDtoToCustomerResponseModelConverter implements Converter<CustomerDto, CustomerResponseModel> {
    @Override
    public CustomerResponseModel convert(CustomerDto source) {
        return CustomerResponseModel.builder()
                .id(source.getUserId())
                .email(source.getEmail())
                .firstName(source.getFirstName())
                .lastName(source.getLastName())
                .walletId(source.getWalletId())
                .walletNumber(source.getWalletNumber())
                .build();
    }
}
