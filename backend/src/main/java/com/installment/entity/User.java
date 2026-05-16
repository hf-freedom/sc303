package com.installment.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class User {
    private String userId;
    private String userName;
    private Integer creditScore;
    private BigDecimal totalRiskLimit;
    private BigDecimal usedRiskAmount;
    private LocalDateTime createTime;

    public User() {
        this.creditScore = 100;
        this.totalRiskLimit = new BigDecimal("50000");
        this.usedRiskAmount = BigDecimal.ZERO;
        this.createTime = LocalDateTime.now();
    }

    public BigDecimal getAvailableRiskAmount() {
        return totalRiskLimit.subtract(usedRiskAmount);
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public Integer getCreditScore() { return creditScore; }
    public void setCreditScore(Integer creditScore) { this.creditScore = creditScore; }
    public BigDecimal getTotalRiskLimit() { return totalRiskLimit; }
    public void setTotalRiskLimit(BigDecimal totalRiskLimit) { this.totalRiskLimit = totalRiskLimit; }
    public BigDecimal getUsedRiskAmount() { return usedRiskAmount; }
    public void setUsedRiskAmount(BigDecimal usedRiskAmount) { this.usedRiskAmount = usedRiskAmount; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
