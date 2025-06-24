package com.aalhendi.account_ms.infrastructure.persistence;

import com.aalhendi.account_ms.domain.entities.Account;
import com.aalhendi.account_ms.domain.entities.NewAccount;
import com.aalhendi.account_ms.domain.valueobjects.AccountNumber;
import com.aalhendi.account_ms.domain.valueobjects.AccountStatus;
import com.aalhendi.account_ms.domain.valueobjects.AccountType;
import com.aalhendi.account_ms.domain.valueobjects.Balance;
import jakarta.persistence.*;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;
import org.springframework.data.domain.Persistable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * JPA entity for Account persistence.
 */
@Entity
// NOTE(aalhendi): GOD BLESS modern versions of Hibernate.
@SoftDelete(strategy = SoftDeleteType.DELETED, columnName = "is_deleted", converter = StateConverter.class)
@Table(name = "account")
public class AccountEntity implements Persistable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    @Column(name = "account_number", unique = true, nullable = false, length = 10)
    private String accountNumber;

    @Column(name = "account_type", nullable = false, length = 20)
    private String accountType;

    @Column(name = "balance", precision = 19, scale = 3, nullable = false)
    private BigDecimal balance;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Default constructor for JPA.
     */
    public AccountEntity() {
    }

    /**
     * Constructor with all fields.
     */
    public AccountEntity(Long id, String accountNumber, String accountType,
                         BigDecimal balance, Integer status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }


    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Extracts customer number from account number.
     * Account number format: CCCCCCCNNN (7-digit customer + 3-digit serial)
     * TODO(aalhendi): should return a value object instead of a string... eventually via common lib
     */
    public String getCustomerNumber() {
        if (Objects.isNull(accountNumber) || accountNumber.length() != 10) {
            return null;
        }
        return accountNumber.substring(0, 7);
    }

    /**
     * Checks if an account is active (status = 1).
     */
    public boolean isActive() {
        return Objects.nonNull(status) && status == 1;
    }

    /**
     * Updates the updatedAt timestamp to the current time.
     */
    public void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Converts this JPA entity to a domain Account object.
     */
    public Account toDomain() {
        return Account.reconstitute(
                this.id,
                new AccountNumber(this.accountNumber),
                AccountType.fromString(this.accountType),
                AccountStatus.fromCode(this.status),
                new Balance(this.balance),
                this.createdAt,
                this.updatedAt
        );
    }

    /**
     * Creates a JPA entity from a domain Account object.
     */
    public static AccountEntity fromDomain(Account domainAccount) {
        AccountEntity entity = new AccountEntity();
        entity.setId(domainAccount.getId());
        entity.setAccountNumber(domainAccount.getAccountNumber().value());
        entity.setAccountType(domainAccount.getAccountType().name());
        entity.setBalance(domainAccount.getBalance().value());
        entity.setStatus(domainAccount.getStatus().getCode());
        entity.setCreatedAt(domainAccount.getCreatedAt());
        entity.setUpdatedAt(domainAccount.getUpdatedAt());
        return entity;
    }

    /**
     * Creates a JPA entity from a domain NewAccount object.
     */
    public static AccountEntity fromDomain(NewAccount domainAccount) {
        AccountEntity entity = new AccountEntity();
        entity.setId(null); // new accounts don't have IDs yet
        entity.setAccountNumber(domainAccount.getAccountNumber().value());
        entity.setAccountType(domainAccount.getAccountType().name());
        entity.setBalance(domainAccount.getBalance().value());
        entity.setStatus(domainAccount.getStatus().getCode());
        entity.setCreatedAt(domainAccount.getCreatedAt());
        entity.setUpdatedAt(domainAccount.getUpdatedAt());
        return entity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (Objects.isNull(o) || getClass() != o.getClass()) return false;
        AccountEntity that = (AccountEntity) o;
        return Objects.equals(accountNumber, that.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }

    @Override
    public String toString() {
        return "AccountEntity[" +
                "id=" + id +
                ", accountNumber='" + accountNumber +
                ", accountType='" + accountType +
                ", balance=" + balance +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                "]";
    }

    @Override
    public boolean isNew() {
        // NOTE(aalhendi): This Object Marker approach assumes IDs are generated at DB.
        // This allows us to "safely" add our id to the AllArgs constructor. JPA will use the NoArgs constructor.
        // Little gotcha with using the all args constructors. It doesn't have id!
        // This is because JPA expects id to be generated. And I want to use fromDomain for *updates*.
        // JPA doesn't digest the concept of updates. It looks at entity graphs and not DBs. So it only has
        // a `save()` method. The `save()` method, under the hood, checks if the entity is new, if not, it *merges*.
        // `merge` is JPA-speak for update...
        // The compromise is I have to pass a Nullable ID whenever I convert from/to domain
        // or call the AllArgs constructor
        // Would love to have something like Changesets and Records/Entities. But whatever.
        return id == null;
    }
} 