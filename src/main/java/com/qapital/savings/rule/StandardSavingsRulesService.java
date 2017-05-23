package com.qapital.savings.rule;

import com.qapital.bankdata.transaction.Transaction;
import com.qapital.bankdata.transaction.TransactionsService;
import com.qapital.savings.event.SavingsEvent;
import com.qapital.savings.event.SavingsEvent.EventName;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StandardSavingsRulesService implements SavingsRulesService {

    private final TransactionsService transactionsService;

    @Autowired
    public StandardSavingsRulesService(TransactionsService transactionsService) {
        this.transactionsService = transactionsService;
    }

    @Override
    public List<SavingsRule> activeRulesForUser(Long userId) {

        SavingsRule guiltyPleasureRule = SavingsRule.createGuiltyPleasureRule(1l, userId, "Starbucks", new BigDecimal(3.00d));
        guiltyPleasureRule.addSavingsGoal(1l);
        guiltyPleasureRule.addSavingsGoal(2l);
        SavingsRule roundupRule = SavingsRule.createRoundupRule(2l, userId, new BigDecimal(2.00d));
        roundupRule.addSavingsGoal(1l);

        List<SavingsRule> activeRules = new ArrayList<>();
        activeRules.add(guiltyPleasureRule);
        activeRules.add(roundupRule);

        return activeRules;
    }

    @Override
    public List<SavingsEvent> executeRule(SavingsRule savingsRule) {

        if (!savingsRule.getStatus().equals(SavingsRule.Status.active)) {
            // TODO : no specification on how to handle this, returning empty result set
            return Collections.emptyList();
        }
        List<SavingsEvent> results = new ArrayList<>();
        List<Transaction> transactions = transactionsService.latestTransactionsForUser(savingsRule.getUserId());
        for (Transaction transaction : transactions) {

            // execute only on expense transactions
            if (transaction.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                continue;
            }

            if (savingsRule.getRuleType().equals(SavingsRule.RuleType.roundup)) {

                results.addAll(getSavingsEventsIfRoundingRule(savingsRule, transaction));

            } else if (savingsRule.getRuleType().equals(SavingsRule.RuleType.guiltypleasure)) {
                results.addAll(getSavingsEventsIfGuiltyPleasure(savingsRule, transaction));
            } else {
                throw new UnsupportedOperationException("Unsupported Rule type");
            }

        }

        return results;

    }

    private List<SavingsEvent> getSavingsEventsIfGuiltyPleasure(SavingsRule savingsRule, Transaction transaction) {
        if (savingsRule.getPlaceDescription().equals(transaction.getDescription())) {
            BigDecimal savingAmount = savingsRule.getAmount();
            return createSavingEvents(savingsRule, savingAmount);
        }
        return Collections.emptyList();
    }

    private List<SavingsEvent> getSavingsEventsIfRoundingRule(SavingsRule savingsRule, Transaction transaction) {
        BigDecimal savingAmount;
        BigDecimal roundUpAmount = savingsRule.getAmount();
        BigDecimal amount = transaction.getAmount().negate();

        while (roundUpAmount.subtract(amount).compareTo(BigDecimal.ZERO) < 0) {
            roundUpAmount = roundUpAmount.add(savingsRule.getAmount());
        }

        savingAmount = roundUpAmount.subtract(amount);

        return createSavingEvents(savingsRule, savingAmount);
    }

    private List<SavingsEvent> createSavingEvents(SavingsRule savingsRule, BigDecimal savingAmount) {
        return savingsRule.getSavingsGoalIds().stream()
                .map(sg ->
                        new SavingsEvent(savingsRule.getUserId(),
                                sg,
                                savingsRule.getId(),
                                EventName.rule_application,
                                LocalDate.now(),
                                savingAmount.divide(new BigDecimal(savingsRule.getSavingsGoalIds().size()), 2, RoundingMode.DOWN),
                                null,
                                savingsRule)).collect(Collectors.toList());
    }
}
