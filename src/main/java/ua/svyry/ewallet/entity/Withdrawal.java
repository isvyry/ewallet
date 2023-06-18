package ua.svyry.ewallet.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@DiscriminatorValue("Withdrawal")
@Entity
@NoArgsConstructor
public class Withdrawal extends Transaction {
}
