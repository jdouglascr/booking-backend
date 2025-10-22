package com.marisoft.booking.resource;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ResourceServiceId implements Serializable {

    @Column(name = "id_resource")
    private Integer resourceId;

    @Column(name = "id_service")
    private Integer serviceId;
}