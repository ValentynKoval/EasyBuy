package com.teamchallenge.easybuy.dto.shop.shopanalytics;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Shop analytics DTO for seller and admin dashboards")
public class ShopAnalyticsDTO {

    @Schema(description = "Shop ID", accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;

    @Min(0)
    @Schema(description = "Total shop page views")
    private Long totalViews;

    @Min(0)
    @Schema(description = "Unique visitors")
    private Long uniqueVisitors;

    @Min(0)
    @Schema(description = "Total orders")
    private Long totalOrders;

    @Min(0)
    @Schema(description = "Completed orders")
    private Long completedOrders;

    @Min(0)
    @Schema(description = "Cancelled orders")
    private Long cancelledOrders;

    @Min(0)
    @Schema(description = "Returned orders")
    private Long returnedOrders;

    @DecimalMin("0.00")
    @Schema(description = "Total revenue")
    private BigDecimal totalRevenue;

    @Schema(description = "Average order value", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal averageOrderValue;

    @Schema(description = "Conversion rate in percent", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal conversionRate;

    @Schema(description = "Cancellation rate in percent", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal cancellationRate;

    @Schema(description = "Return rate in percent", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal returnRate;

    @Schema(description = "Health score from 0 to 100", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer healthScore;

    @Schema(description = "Inactive days", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer inactiveDays;

    @Schema(description = "Dead shop flag", accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean deadShop;

    @Schema(description = "Analytics period start")
    private Instant periodStart;

    @Schema(description = "Analytics period end")
    private Instant periodEnd;

    @Schema(description = "Last order timestamp")
    private Instant lastOrderAt;

    @Schema(description = "Last activity timestamp")
    private Instant lastActivityAt;

    @Schema(description = "Last metrics calculation time", accessMode = Schema.AccessMode.READ_ONLY)
    private Instant lastCalculatedAt;

    @Schema(description = "Creation timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private Instant createdAt;

    @Schema(description = "Update timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private Instant updatedAt;
}

