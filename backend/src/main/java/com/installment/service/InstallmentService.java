package com.installment.service;

import com.installment.entity.Bill;
import com.installment.entity.InstallmentOrder;
import com.installment.entity.User;
import com.installment.storage.DataStorage;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InstallmentService {

    public List<Integer> getAvailableInstallments(String userId) {
        User user = DataStorage.USER_MAP.get(userId);
        if (user == null) {
            return Arrays.asList(3, 6, 12);
        }
        int credit = user.getCreditScore();
        if (credit >= 90) {
            return Arrays.asList(3, 6, 12, 24);
        } else if (credit >= 70) {
            return Arrays.asList(3, 6, 12);
        } else if (credit >= 50) {
            return Arrays.asList(3, 6);
        } else {
            return Arrays.asList(3);
        }
    }

    public InstallmentOrder createOrder(String userId, BigDecimal totalAmount, int installments) {
        User user = DataStorage.USER_MAP.computeIfAbsent(userId, k -> {
            User u = new User();
            u.setUserId(userId);
            u.setUserName("User_" + userId);
            return u;
        });

        if (user.getAvailableRiskAmount().compareTo(totalAmount) < 0) {
            throw new RuntimeException("超出风险额度限制");
        }

        List<Integer> available = getAvailableInstallments(userId);
        if (!available.contains(installments)) {
            throw new RuntimeException("不支持该分期期数");
        }

        InstallmentOrder order = new InstallmentOrder();
        order.setOrderId(UUID.randomUUID().toString());
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setInstallments(installments);
        order.setFeeRate(calculateFeeRate(installments));
        order.setTotalFee(totalAmount.multiply(order.getFeeRate()).setScale(2, RoundingMode.HALF_UP));

        List<Bill> bills = generateBills(order);
        order.setBills(bills);

        DataStorage.ORDER_MAP.put(order.getOrderId(), order);
        user.setUsedRiskAmount(user.getUsedRiskAmount().add(totalAmount));

        return order;
    }

    private BigDecimal calculateFeeRate(int installments) {
        if (installments <= 3) return new BigDecimal("0.02");
        if (installments <= 6) return new BigDecimal("0.04");
        if (installments <= 12) return new BigDecimal("0.07");
        return new BigDecimal("0.12");
    }

    private List<Bill> generateBills(InstallmentOrder order) {
        List<Bill> bills = new ArrayList<>();
        BigDecimal principalPerPeriod = order.getTotalAmount()
                .divide(new BigDecimal(order.getInstallments()), 2, RoundingMode.HALF_UP);
        BigDecimal feePerPeriod = order.getTotalFee()
                .divide(new BigDecimal(order.getInstallments()), 2, RoundingMode.HALF_UP);

        for (int i = 1; i <= order.getInstallments(); i++) {
            Bill bill = new Bill();
            bill.setBillId(UUID.randomUUID().toString());
            bill.setOrderId(order.getOrderId());
            bill.setUserId(order.getUserId());
            bill.setPeriod(i);
            bill.setPrincipal(principalPerPeriod);
            bill.setFee(feePerPeriod);
            bill.setTotalAmount(principalPerPeriod.add(feePerPeriod));
            bill.setDueDate(LocalDate.now().plusMonths(i));
            DataStorage.BILL_MAP.put(bill.getBillId(), bill);
            bills.add(bill);
        }
        return bills;
    }

    public Bill payBill(String billId, BigDecimal amount) {
        Bill bill = DataStorage.BILL_MAP.get(billId);
        if (bill == null) {
            throw new RuntimeException("账单不存在");
        }
        if ("PAID".equals(bill.getStatus())) {
            throw new RuntimeException("账单已支付");
        }

        BigDecimal remaining = bill.getRemainingAmount();
        if (amount.compareTo(remaining) >= 0) {
            bill.setPaidAmount(bill.getTotalAmount().add(bill.getPenalty()));
            bill.setStatus("PAID");
            bill.setPaidTime(LocalDateTime.now());
        } else {
            bill.setPaidAmount(bill.getPaidAmount().add(amount));
        }
        return bill;
    }

    public void earlyRepay(String orderId) {
        InstallmentOrder order = DataStorage.ORDER_MAP.get(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        BigDecimal totalPaidPrincipal = BigDecimal.ZERO;
        BigDecimal totalPaidFee = BigDecimal.ZERO;
        int paidCount = 0;

        for (Bill bill : order.getBills()) {
            if ("PAID".equals(bill.getStatus())) {
                totalPaidPrincipal = totalPaidPrincipal.add(bill.getPrincipal());
                totalPaidFee = totalPaidFee.add(bill.getFee());
                paidCount++;
            }
        }

        BigDecimal remainingPrincipal = order.getTotalAmount().subtract(totalPaidPrincipal);
        BigDecimal newTotalFee = remainingPrincipal.multiply(new BigDecimal("0.01"));

        for (Bill bill : order.getBills()) {
            if (!"PAID".equals(bill.getStatus())) {
                BigDecimal newFee = bill.getPrincipal().multiply(new BigDecimal("0.01"))
                        .setScale(2, RoundingMode.HALF_UP);
                bill.setFee(newFee);
                bill.setTotalAmount(bill.getPrincipal().add(newFee));
                bill.setStatus("PAID");
                bill.setPaidTime(LocalDateTime.now());
                bill.setPaidAmount(bill.getTotalAmount());
                bill.setRemark("提前还款手续费减免");
            }
        }

        order.setStatus("EARLY_REPAID");
    }

    public void refundOrder(String orderId, BigDecimal refundAmount) {
        InstallmentOrder order = DataStorage.ORDER_MAP.get(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        BigDecimal paidAmount = BigDecimal.ZERO;
        for (Bill bill : order.getBills()) {
            if ("PAID".equals(bill.getStatus())) {
                paidAmount = paidAmount.add(bill.getPaidAmount());
            }
        }

        BigDecimal toRefund = refundAmount.min(paidAmount);
        order.setStatus("REFUNDED");

        User user = DataStorage.USER_MAP.get(order.getUserId());
        if (user != null) {
            user.setUsedRiskAmount(user.getUsedRiskAmount()
                    .subtract(order.getTotalAmount()).max(BigDecimal.ZERO));
        }
    }

    public List<InstallmentOrder> getUserOrders(String userId) {
        return DataStorage.ORDER_MAP.values().stream()
                .filter(o -> userId.equals(o.getUserId()))
                .collect(Collectors.toList());
    }

    public List<Bill> getUserBills(String userId) {
        return DataStorage.BILL_MAP.values().stream()
                .filter(b -> userId.equals(b.getUserId()))
                .collect(Collectors.toList());
    }

    public User getUser(String userId) {
        return DataStorage.USER_MAP.computeIfAbsent(userId, k -> {
            User u = new User();
            u.setUserId(userId);
            u.setUserName("User_" + userId);
            return u;
        });
    }
}
