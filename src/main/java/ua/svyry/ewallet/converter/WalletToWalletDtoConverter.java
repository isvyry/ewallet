package ua.svyry.ewallet.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ua.svyry.ewallet.entity.Wallet;
import ua.svyry.ewallet.shared.WalletDto;

@Component
public class WalletToWalletDtoConverter implements Converter<Wallet, WalletDto> {
    @Override
    public WalletDto convert(Wallet source) {
        return WalletDto.builder()
                .id(source.getId())
                .walletNumber(source.getWalletNumber().toString())
                .ownerId(source.getOwner().getId())
                .createdDate(source.getCreatedDate())
                .build();
    }
}
