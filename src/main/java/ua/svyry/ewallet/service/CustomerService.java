package ua.svyry.ewallet.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.svyry.ewallet.entity.Customer;
import ua.svyry.ewallet.entity.Wallet;
import ua.svyry.ewallet.repository.CustomerRepository;
import ua.svyry.ewallet.shared.CustomerDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CustomerService implements UserDetailsService {

    private final CustomerRepository customerRepository;
    private final WalletService walletService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ConversionService conversionService;

    public CustomerDto createCustomer(CustomerDto customerDetails) {
        Customer customer = conversionService.convert(customerDetails, Customer.class);

        customer.setEncryptedPassword(passwordEncoder.encode(customerDetails.getPassword()));

        Customer savedCustomer = customerRepository.save(customer);
        Wallet createdWallet = walletService.createWallet(savedCustomer);
        CustomerDto createdCustomerDetails = conversionService.convert(savedCustomer, CustomerDto.class);
        createdCustomerDetails.setWalletId(createdWallet.getId());
        createdCustomerDetails.setWalletNumber(createdWallet.getWalletNumber().toString());
        return createdCustomerDetails;
    }

    public void unblockCustomer(Long id) {
        Customer customer = getCustomerById(id);
        customer.setBlockedForTransactions(false);
        log.info(String.format("Unblocked customer[id: %s, walletId: %s, walletNumber: %s]",
                customer.getId(), customer.getWallet().getId(), customer.getWallet().getWalletNumber()));
        customerRepository.save(customer);
    }

    public int unblockAllCustomers() {
        return customerRepository.updateAllBlockedForTransactionsTrue();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customer = getCustomerByEmail(username);
        return new User(customer.getEmail(), customer.getEncryptedPassword(), true,
                true, true, !customer.isBlockedForTransactions(), new ArrayList<>());
    }

    public CustomerDto getUserDetailsByUsername(String username) {
        return conversionService.convert(getCustomerByEmail(username), CustomerDto.class);
    }

    public Customer getCustomerById(Long id) {
        Optional<Customer> customerQueryResult = customerRepository.findById(id);
        return customerQueryResult.orElseThrow(
                () -> new EntityNotFoundException(String.format("Cannot find customer with id: %s", id)));
    }
    public CustomerDto getCustomerDetailsById(Long id) {
        return conversionService.convert(getCustomerById(id), CustomerDto.class);
    }
    private Customer getCustomerByEmail(String username) {
        Optional<Customer> customerQueryResult = customerRepository.findByEmail(username);
        return customerQueryResult.orElseThrow(
                () -> new UsernameNotFoundException(String.format("Cannot find customer with email: %s", username)));
    }

    public List<CustomerDto> getAllCustomers(Pageable pageable) {
        Page<Customer> customers = customerRepository.findAll(pageable);
        return customers.stream().map(c -> conversionService.convert(c, CustomerDto.class))
                .collect(Collectors.toList());
    }
}
