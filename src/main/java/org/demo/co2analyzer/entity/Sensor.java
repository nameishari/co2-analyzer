package org.demo.co2analyzer.entity;

import jakarta.persistence.*;
import lombok.*;
import org.demo.co2analyzer.api.dto.response.SensorResponse;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "sensor")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Sensor {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column
    private SensorStatus status;

    @Column
    private String name;

    @Column
    private String location;

    @Column(updatable = false, nullable = false)
    @CreatedDate
    @Setter(AccessLevel.NONE)
    private Instant createdAt;

    @Column
    @LastModifiedDate
    @Setter(AccessLevel.NONE)
    private Instant updatedAt;

    public SensorResponse toSensorResponse() {
       return new SensorResponse(id, name, location, status);
    }
}
