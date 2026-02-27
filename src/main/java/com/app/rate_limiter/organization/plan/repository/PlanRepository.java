package com.app.rate_limiter.organization.plan.repository;

import com.app.rate_limiter.organization.plan.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlanRepository extends JpaRepository<Plan, UUID> {
}
