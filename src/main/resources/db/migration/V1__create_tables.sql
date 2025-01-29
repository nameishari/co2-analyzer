CREATE TABLE sensor (
                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                        status VARCHAR(20),
                        name VARCHAR(255),
                        location VARCHAR(255),
                        created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                        updated_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE measurements (
                              id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                              sensor_id UUID REFERENCES sensor(id) ON DELETE CASCADE,
                              reading INTEGER NOT NULL,
                              time TIMESTAMP WITH TIME ZONE,
                              FOREIGN KEY (sensor_id) REFERENCES sensor(id)
);


CREATE TABLE alert_history (
                               id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               sensor_id UUID REFERENCES sensor(id) ON DELETE CASCADE,
                               start_time TIMESTAMP WITH TIME ZONE,
                               end_time TIMESTAMP WITH TIME ZONE,
                               FOREIGN KEY (sensor_id) REFERENCES sensor(id)
);

-- CREATE INDEX idx_measurements_sensor_id ON measurements(sensor_id);

-- CREATE INDEX idx_alert_history_sensor_id ON alert_history(sensor_id);
