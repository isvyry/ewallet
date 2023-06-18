package ua.svyry.ewallet.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@DiscriminatorValue("Deposit")
@Entity
@NoArgsConstructor
public class Deposit extends Transaction {

}
