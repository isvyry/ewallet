package ua.svyry.ewallet.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ua.svyry.ewallet.entity.Customer;
import ua.svyry.ewallet.shared.CustomerDto;

@Component
public class CustomerDtoToCustomerConverter implements Converter<CustomerDto, Customer> {
    @Override
    public Customer convert(CustomerDto source) {
        return Customer.builder()
                .email(source.getEmail())
                .firstName(source.getFirstName())
                .lastName(source.getLastName())
                .build();
    }
}
