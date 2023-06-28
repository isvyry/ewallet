package ua.svyry.ewallet.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ua.svyry.ewallet.shared.CustomerDto;
import ua.svyry.ewallet.ui.model.CreateUpdateCustomerRequestModel;

@Component
public class CreateUpdateCustomerRequestModelToCustomerDtoConverter
        implements Converter<CreateUpdateCustomerRequestModel, CustomerDto> {
    @Override
    public CustomerDto convert(CreateUpdateCustomerRequestModel source) {
        return CustomerDto.builder()
                .firstName(source.getFirstName())
                .lastName(source.getLastName())
                .email(source.getEmail())
                .password(source.getPassword())
                .build();
    }
}
