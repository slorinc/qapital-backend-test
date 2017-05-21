package com.qapital.savings.rule;

import com.qapital.bankdata.transaction.Transaction;
import com.qapital.bankdata.transaction.TransactionsService;
import com.qapital.savings.event.SavingsEvent;
import com.qapital.savings.event.SavingsEvent.EventName;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        SavingsRule guiltyPleasureRule = SavingsRule.createGuiltyPleasureRule(1l, userId, "Starbucks", 3.00d);
        guiltyPleasureRule.addSavingsGoal(1l);
        guiltyPleasureRule.addSavingsGoal(2l);
        SavingsRule roundupRule = SavingsRule.createRoundupRule(2l, userId, 2.00d);
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
        // TODO check for NPE or wrap in Optional
        for (Transaction transaction : transactions) {

            // execute only on expense transactions
            if (transaction.getAmount() > 0) {
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
            Double savingAmount = savingsRule.getAmount();
            List<SavingsEvent> transactionResults = savingsRule.getSavingsGoalIds().stream()
                    .map(sg ->
                            new SavingsEvent(savingsRule.getUserId(),
                                    sg,
                                    savingsRule.getId(),
                                    EventName.rule_application,
                                    LocalDate.now(),
                                    savingAmount / savingsRule.getSavingsGoalIds().size(),
                                    null,
                                    savingsRule)).collect(Collectors.toList());
            return transactionResults;
        }
        return Collections.emptyList();
    }

    private List<SavingsEvent> getSavingsEventsIfRoundingRule(SavingsRule savingsRule, Transaction transaction) {
        Double savingAmount;
        Double roundUpAmount = savingsRule.getAmount();
        Double amount = -transaction.getAmount();

        while (roundUpAmount - amount < 0) {
            roundUpAmount += savingsRule.getAmount();
        }

        savingAmount = roundUpAmount - amount;

        return savingsRule.getSavingsGoalIds().stream()
                .map(sg ->
                        new SavingsEvent(savingsRule.getUserId(),
                                sg,
                                savingsRule.getId(),
                                EventName.rule_application,
                                LocalDate.now(),
                                savingAmount / savingsRule.getSavingsGoalIds().size(),
                                null,
                                savingsRule)).collect(Collectors.toList());
    }

}
