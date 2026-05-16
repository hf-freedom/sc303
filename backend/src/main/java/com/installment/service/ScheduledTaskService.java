package com.installment.service;

import com.installment.entity.Bill;
import com.installment.entity.InstallmentOrder;
import com.installment.entity.User;
import com.installment.storage.DataStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduledTaskService {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTaskService.class);

    @Scheduled(cron = "0 0 9 * * ?")
    public void sendDueDateReminder() {
        log.info("执行到期提醒任务");
        LocalDate threeDaysLater = LocalDate.now().plusDays(3);
        for (Bill bill : DataStorage.BILL_MAP.values()) {
            if (!"PAID".equals(bill.getStatus()) && bill.getDueDate().equals(threeDaysLater)) {
                log.info("提醒用户 {}: 账单 {} 将在3天后到期，金额 {}",
                        bill.getUserId(), bill.getBillId(), bill.getRemainingAmount());
            }
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void updateOverdueStatus() {
        log.info("执行逾期状态更新任务");
        LocalDate today = LocalDate.now();
        for (Bill bill : DataStorage.BILL_MAP.values()) {
            if (!"PAID".equals(bill.getStatus()) && !"OVERDUE".equals(bill.getStatus())) {
                if (bill.getDueDate().isBefore(today)) {
                    bill.setStatus("OVERDUE");
                    BigDecimal penalty = bill.getTotalAmount()
                            .multiply(new BigDecimal("0.005"))
                            .setScale(2, RoundingMode.HALF_UP);
                    bill.setPenalty(bill.getPenalty().add(penalty));

                    User user = DataStorage.USER_MAP.get(bill.getUserId());
                    if (user != null) {
                        user.setCreditScore(Math.max(0, user.getCreditScore() - 5));
                        log.info("用户 {} 账单逾期，信用分降低5分，当前信用分: {}",
                                user.getUserId(), user.getCreditScore());
                    }
                    log.info("账单 {} 已逾期，产生罚金 {}", bill.getBillId(), penalty);
                }
            }
        }
    }

    @Scheduled(cron = "0 0 0 1 * ?")
    public void closeCompletedOrders() {
        log.info("执行账单关闭任务");
        for (InstallmentOrder order : DataStorage.ORDER_MAP.values()) {
            boolean allPaid = order.getBills().stream()
                    .allMatch(b -> "PAID".equals(b.getStatus()));
            if (allPaid && !"CLOSED".equals(order.getStatus())) {
                order.setStatus("CLOSED");
                User user = DataStorage.USER_MAP.get(order.getUserId());
                if (user != null) {
                    user.setUsedRiskAmount(user.getUsedRiskAmount()
                            .subtract(order.getTotalAmount()).max(BigDecimal.ZERO));
                }
                log.info("订单 {} 已结清，状态更新为已关闭", order.getOrderId());
            }
        }
    }

    public List<String> getReminders(String userId) {
        List<String> reminders = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate threeDaysLater = today.plusDays(3);

        for (Bill bill : DataStorage.BILL_MAP.values()) {
            if (userId.equals(bill.getUserId()) && !"PAID".equals(bill.getStatus())) {
                if (bill.getDueDate().isBefore(today)) {
                    reminders.add("【逾期】账单 " + bill.getPeriod() + " 期已逾期，请尽快还款！");
                } else if (bill.getDueDate().isBefore(threeDaysLater) || bill.getDueDate().isEqual(threeDaysLater)) {
                    reminders.add("【提醒】账单 " + bill.getPeriod() + " 期将在3天内到期");
                }
            }
        }
        return reminders;
    }
}
