package ua.svyry.ewallet.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

@DiscriminatorValue("Withdrawal")
@Entity
@NoArgsConstructor
@Audited
@AuditOverride(forClass = Auditable.class)
public class Withdrawal extends Transaction {
}
