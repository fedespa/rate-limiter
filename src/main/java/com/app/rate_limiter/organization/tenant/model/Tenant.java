package com.app.rate_limiter.organization.tenant.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tenants")
@Builder
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Tenant A{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private TenantStatus status;

}
