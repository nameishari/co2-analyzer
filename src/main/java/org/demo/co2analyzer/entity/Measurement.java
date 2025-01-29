package org.demo.co2analyzer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.demo.co2analyzer.api.dto.response.MeasurementResponse;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "measurements")
public class Measurement {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sensor_id", nullable = false)
    private Sensor sensor;

    @Column(nullable = false)
    private Integer reading;

    @Column(nullable = false)
    private OffsetDateTime time;

    public MeasurementResponse toMeasurementResponse() {
        return new MeasurementResponse(id, time, sensor.getId(), reading);
    }
}
