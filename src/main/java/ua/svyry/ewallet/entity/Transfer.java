package ua.svyry.ewallet.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@DiscriminatorValue("Transfer")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Transfer extends Transaction {

    @ManyToOne
    private Card receiver;

}
