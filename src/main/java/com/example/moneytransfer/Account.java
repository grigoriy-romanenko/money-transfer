package com.example.moneytransfer;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class Account {

    @Id Long id;
    @Min(0) Long amount;
    @Version Integer version;

    public Account(Long id, Long amount) {
        this.id = id;
        this.amount = amount;
    }

}
