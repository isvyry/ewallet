package ua.svyry.ewallet.ui.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCustomerRequestModel {

    @NotBlank(message = "firstName should be provided")
    private String firstName;

    @NotBlank(message = "lastName should be provided")
    private String lastName;
    @Email(message = "email should be in appropriate email-style format")
    @NotBlank(message = "email should be provided")
    private String email;

    //@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&-+=()])(?=\\S+$).{8,20}$")
    @NotBlank(message = "password should be provided")
    private String password;
}
