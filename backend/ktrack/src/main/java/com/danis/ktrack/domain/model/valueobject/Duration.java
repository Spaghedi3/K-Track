package com.danis.ktrack.domain.model.valueobject;


import com.danis.ktrack.domain.model.enums.TimeUnit;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Duration {
private long value;

@Enumerated(EnumType.STRING)
TimeUnit unit;
}
