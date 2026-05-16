package com.installment.storage;

import com.installment.entity.Bill;
import com.installment.entity.InstallmentOrder;
import com.installment.entity.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataStorage {
    public static final Map<String, User> USER_MAP = new ConcurrentHashMap<>();
    public static final Map<String, InstallmentOrder> ORDER_MAP = new ConcurrentHashMap<>();
    public static final Map<String, Bill> BILL_MAP = new ConcurrentHashMap<>();
}
