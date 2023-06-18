package ua.svyry.ewallet.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ua.svyry.ewallet.shared.CustomerDto;
import ua.svyry.ewallet.ui.model.CreateCustomerRequestModel;

@Component
public class CreateCustomerRequestModelToCustomerDtoConverter implements Converter<CreateCustomerRequestModel, CustomerDto> {
    @Override
    public CustomerDto convert(CreateCustomerRequestModel source) {
        return CustomerDto.builder()
                .firstName(source.getFirstName())
                .lastName(source.getLastName())
                .email(source.getEmail())
                .password(source.getPassword())
                .build();
    }
}
