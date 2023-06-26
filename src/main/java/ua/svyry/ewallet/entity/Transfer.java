package ua.svyry.ewallet.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

@DiscriminatorValue("Transfer")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Audited
@AuditOverride(forClass = Auditable.class)
public class Transfer extends Transaction {

    @ManyToOne
    private Card receiverCard;

}
