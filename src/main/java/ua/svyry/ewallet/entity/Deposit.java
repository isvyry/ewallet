package ua.svyry.ewallet.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

@DiscriminatorValue("Deposit")
@Entity
@NoArgsConstructor
@Audited
@AuditOverride(forClass = Auditable.class)
public class Deposit extends Transaction {

}
