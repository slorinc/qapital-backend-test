package com.qapital.savings.rule;

import com.qapital.bankdata.transaction.Transaction;
import com.qapital.bankdata.transaction.TransactionsService;
import com.qapital.savings.event.SavingsEvent;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;

/**
 * Created by slorinc on 5/21/2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class StandardSavingsRulesServiceTest {

    @Mock
    private TransactionsService transactionsService;

    @InjectMocks
    private StandardSavingsRulesService savingsRulesService;


    @Before
    public void setUp() {
        // Given
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction(5L, 1L, new BigDecimal("-2.99"), "Apple Itunes", new LocalDate(2015, 7, 7)));
        transactions.add(new Transaction(6L, 1L, new BigDecimal("1945.00"), "Salary", new LocalDate(2015, 7, 25)));
        given(transactionsService.latestTransactionsForUser(1L)).willReturn(transactions);
    }

    @Test
    public void singleGoalGuiltyPleasureRuleWithWrongLocation() throws Exception {

        //When
        SavingsRule guiltyPleasureRule = SavingsRule.createGuiltyPleasureRule(1L, 1L, "Starbucks", new BigDecimal("3.00"));
        guiltyPleasureRule.addSavingsGoal(1L);
        List<SavingsEvent> savingsEvents = savingsRulesService.executeRule(guiltyPleasureRule);

        //Then
        Assert.assertTrue(savingsEvents.size() == 0);
    }

    @Test
    public void singleGoalGuiltyPleasureRuleWithRightLocation() throws Exception {

        //When
        SavingsRule guiltyPleasureRule = SavingsRule.createGuiltyPleasureRule(1L, 1L, "Apple Itunes", new BigDecimal("3.00"));
        guiltyPleasureRule.addSavingsGoal(1L);
        List<SavingsEvent> savingsEvents = savingsRulesService.executeRule(guiltyPleasureRule);

        //Then
        Assert.assertTrue(savingsEvents.size() == 1);
        Assert.assertEquals(new BigDecimal("3.00"), savingsEvents.get(0).getAmount());
    }

    @Test
    public void twoGoalsGuiltyPleasureRuleWithRightLocation() throws Exception {

        //When
        SavingsRule guiltyPleasureRule = SavingsRule.createGuiltyPleasureRule(1L, 1L, "Apple Itunes", new BigDecimal("3.00"));
        guiltyPleasureRule.addSavingsGoal(1L);
        guiltyPleasureRule.addSavingsGoal(2L);
        List<SavingsEvent> savingsEvents = savingsRulesService.executeRule(guiltyPleasureRule);

        //Then
        Assert.assertTrue(savingsEvents.size() == 2);
        Assert.assertEquals(new BigDecimal("1.50"), savingsEvents.get(0).getAmount());
        Assert.assertEquals(new BigDecimal("1.50"), savingsEvents.get(1).getAmount());
    }
    @Test
    public void threeGoalsGuiltyPleasureRuleWithRightLocation() throws Exception {

        //When
        SavingsRule guiltyPleasureRule = SavingsRule.createGuiltyPleasureRule(1L, 1L, "Apple Itunes", new BigDecimal("4.00"));
        guiltyPleasureRule.addSavingsGoal(1L);
        guiltyPleasureRule.addSavingsGoal(2L);
        guiltyPleasureRule.addSavingsGoal(3L);
        List<SavingsEvent> savingsEvents = savingsRulesService.executeRule(guiltyPleasureRule);

        //Then
        Assert.assertTrue(savingsEvents.size() == 3);
        Assert.assertEquals(new BigDecimal("1.33"), savingsEvents.get(0).getAmount());
        Assert.assertEquals(new BigDecimal("1.33"), savingsEvents.get(1).getAmount());
        Assert.assertEquals(new BigDecimal("1.33"), savingsEvents.get(2).getAmount());
    }

    @Test
    public void goalsAreMissingFromRequest() throws Exception {

        //When
        SavingsRule roundupRule = SavingsRule.createRoundupRule(2L, 1L, new BigDecimal("2.00"));
        List<SavingsEvent> savingsEvents = savingsRulesService.executeRule(roundupRule);

        //Then
        Assert.assertTrue(savingsEvents.size() == 0);
    }

    @Test
    public void noTransactionInQueue() throws Exception {

        //Given
        given(transactionsService.latestTransactionsForUser(1L)).willReturn(Collections.emptyList());

        //When
        SavingsRule roundupRule = SavingsRule.createRoundupRule(2L, 1L, new BigDecimal("2.00"));
        roundupRule.addSavingsGoal(1L);
        List<SavingsEvent> savingsEvents = savingsRulesService.executeRule(roundupRule);

        //Then
        Assert.assertTrue(savingsEvents.size() == 0);
    }

    @Test
    public void singleGoalRoundingRule() throws Exception {

        //When
        SavingsRule roundupRule = SavingsRule.createRoundupRule(2L, 1L, new BigDecimal("2.00"));
        roundupRule.addSavingsGoal(1L);
        List<SavingsEvent> savingsEvents = savingsRulesService.executeRule(roundupRule);

        //Then
        Assert.assertTrue(savingsEvents.size() == 1);
        Assert.assertEquals(new BigDecimal("1.01"), savingsEvents.get(0).getAmount());
    }

    @Test
    public void twoGoalsRoundingRule() throws Exception {

        //When
        SavingsRule roundupRule = SavingsRule.createRoundupRule(2L, 1L, new BigDecimal("2.00"));
        roundupRule.addSavingsGoal(1L);
        roundupRule.addSavingsGoal(2L);
        List<SavingsEvent> savingsEvents = savingsRulesService.executeRule(roundupRule);

        //Then
        Assert.assertTrue(savingsEvents.size() == 2);
        Assert.assertEquals(new BigDecimal("0.50"), savingsEvents.get(0).getAmount());
        Assert.assertEquals(new BigDecimal("0.50"), savingsEvents.get(1).getAmount());
    }

}