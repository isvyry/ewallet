package ua.svyry.ewallet.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "eWallet API",
                version = "v1",
                contact = @Contact(
                        name = "Ihor Svyrydenko", email = "djol12345@gmail.com", url = "https://www.linkedin.com/in/ihor-svyrydenko-a345771ba/"
                ),
                description = "This is a functional e-wallet Java application that allows users to store, transfer, and manage funds through an exposed API."
        )
)
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class SpringdocOpenApiConfiguration {

}