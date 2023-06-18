package ua.svyry.ewallet.ui.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponseModel {
    private Long id;

    private String firstName;
    private String lastName;
    private String email;

    private Long walletId;
    private String walletNumber;
}
