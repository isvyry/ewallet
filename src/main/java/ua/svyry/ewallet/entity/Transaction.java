package ua.svyry.ewallet.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.util.Date;

@DiscriminatorColumn(name = "transaction_type", discriminatorType = DiscriminatorType.STRING)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Audited
@AuditOverride(forClass = Auditable.class)
public class Transaction extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    protected BigDecimal amount;
    protected boolean isSuspicious;
    @ManyToOne
    protected Card card;

    protected boolean isSuccessful;

}
