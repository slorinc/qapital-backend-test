package com.qapital.bankdata.transaction;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class StandardTransactionsService implements TransactionsService {

    @Override
    public List<Transaction> latestTransactionsForUser(Long userId) {
        return createDummyTransactions(userId);
    }

    private static List<Transaction> createDummyTransactions(Long userId) {

        List<Transaction> transactions = new ArrayList<>();

        transactions.add(new Transaction(1L,userId, new BigDecimal("-5.34"), "Starbucks", new LocalDate(2015,7,1)));
        transactions.add(new Transaction(2L,userId, new BigDecimal("-2.16"), "Starbucks", new LocalDate(2015,7,2)));
        transactions.add(new Transaction(3L,userId, new BigDecimal("-3.09"), "McDonald's", new LocalDate(2015,7,2)));
        transactions.add(new Transaction(4L,userId, new BigDecimal("-1.03"), "Starbucks", new LocalDate(2015,7,3)));
        transactions.add(new Transaction(5L,userId, new BigDecimal("-2.99"), "Apple Itunes", new LocalDate(2015,7,7)));
        transactions.add(new Transaction(6L,userId, new BigDecimal("1945.00"), "Salary", new LocalDate(2015,7,25)));
        transactions.add(new Transaction(7L,userId, new BigDecimal("-9.76"), "Amazon", new LocalDate(2015,7,8)));
        transactions.add(new Transaction(8L,userId, new BigDecimal("-59.45"), "Walmart", new LocalDate(2015,7,8)));
        transactions.add(new Transaction(9L,userId, new BigDecimal("-13.14"), "Papa Joe's", new LocalDate(2015,7,13)));
        transactions.add(new Transaction(10L,userId, new BigDecimal("-2.16"), "Starbucks", new LocalDate(2015,7,29)));
        transactions.add(new Transaction(11L,userId, new BigDecimal("-1.99"), "Apple Itunes", new LocalDate(2015,8,3)));

        return transactions;
    }


}
