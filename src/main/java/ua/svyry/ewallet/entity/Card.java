package ua.svyry.ewallet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal balance;
    private String cardNumber;
    private Date expirationDate;
    @ManyToOne
    private Wallet wallet;
    private boolean isDeleted;
    @OneToMany(mappedBy = "card")
    private List<Transaction> cardTransactions;
    @OneToMany(mappedBy = "receiver")
    private List<Transfer> receivedTransfers;
}
