package ua.svyry.ewallet.shared;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WalletDto {

    private Long id;
    private String walletNumber;
    private Long ownerId;
    private Date createdDate;
}
