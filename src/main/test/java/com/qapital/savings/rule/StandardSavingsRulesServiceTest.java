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
        transactions.add(new Transaction(5L, 1L, -2.99d, "Apple Itunes", new LocalDate(2015, 7, 7)));
        transactions.add(new Transaction(6L, 1L, 1945.00d, "Salary", new LocalDate(2015, 7, 25)));
        given(transactionsService.latestTransactionsForUser(1L)).willReturn(transactions);
    }

    @Test
    public void singleGoalGuiltyPleasureRuleWithWrongLocation() throws Exception {

        //When
        SavingsRule guiltyPleasureRule = SavingsRule.createGuiltyPleasureRule(1L, 1L, "Starbucks", 3.00d);
        guiltyPleasureRule.addSavingsGoal(1L);
        List<SavingsEvent> savingsEvents = savingsRulesService.executeRule(guiltyPleasureRule);

        //Then
        Assert.assertTrue(savingsEvents.size() == 0);
    }

    @Test
    public void singleGoalGuiltyPleasureRuleWithRightLocation() throws Exception {

        //When
        SavingsRule guiltyPleasureRule = SavingsRule.createGuiltyPleasureRule(1L, 1L, "Apple Itunes", 3.00d);
        guiltyPleasureRule.addSavingsGoal(1L);
        List<SavingsEvent> savingsEvents = savingsRulesService.executeRule(guiltyPleasureRule);

        //Then
        Assert.assertTrue(savingsEvents.size() == 1);
        Assert.assertEquals(new Double(3d), savingsEvents.get(0).getAmount());
    }

    @Test
    public void twoGoalsGuiltyPleasureRuleWithRightLocation() throws Exception {

        //When
        SavingsRule guiltyPleasureRule = SavingsRule.createGuiltyPleasureRule(1L, 1L, "Apple Itunes", 3.00d);
        guiltyPleasureRule.addSavingsGoal(1L);
        guiltyPleasureRule.addSavingsGoal(2L);
        List<SavingsEvent> savingsEvents = savingsRulesService.executeRule(guiltyPleasureRule);

        //Then
        Assert.assertTrue(savingsEvents.size() == 2);
        Assert.assertEquals(new Double(1.5d), savingsEvents.get(0).getAmount());
        Assert.assertEquals(new Double(1.5d), savingsEvents.get(1).getAmount());
    }

    @Test
    public void goalsAreMissingFromRequest() throws Exception {

        //When
        SavingsRule roundupRule = SavingsRule.createRoundupRule(2L, 1L, 2.00d);
        List<SavingsEvent> savingsEvents = savingsRulesService.executeRule(roundupRule);

        //Then
        Assert.assertTrue(savingsEvents.size() == 0);
    }

    @Test
    public void noTransactionsInRequest() throws Exception {

        //Given
        given(transactionsService.latestTransactionsForUser(1L)).willReturn(Collections.emptyList());

        //When
        SavingsRule roundupRule = SavingsRule.createRoundupRule(2L, 1L, 2.00d);
        roundupRule.addSavingsGoal(1L);
        List<SavingsEvent> savingsEvents = savingsRulesService.executeRule(roundupRule);

        //Then
        Assert.assertTrue(savingsEvents.size() == 0);
    }

    @Test
    public void singleGoalRoundingRule() throws Exception {

        //When
        SavingsRule roundupRule = SavingsRule.createRoundupRule(2L, 1L, 2.00d);
        roundupRule.addSavingsGoal(1L);
        List<SavingsEvent> savingsEvents = savingsRulesService.executeRule(roundupRule);

        //Then
        Assert.assertTrue(savingsEvents.size() == 1);
        Assert.assertEquals(new Double(1.01d), getRoundedDouble(savingsEvents));
    }

    @Test
    public void twoGoalsRoundingRule() throws Exception {

        //When
        SavingsRule roundupRule = SavingsRule.createRoundupRule(2L, 1L, 2.00d);
        roundupRule.addSavingsGoal(1L);
        roundupRule.addSavingsGoal(2L);
        List<SavingsEvent> savingsEvents = savingsRulesService.executeRule(roundupRule);

        //Then
        Assert.assertTrue(savingsEvents.size() == 2);
        Assert.assertEquals(new Double(0.5D), getRoundedDouble(savingsEvents));
    }

    private Double getRoundedDouble(List<SavingsEvent> savingsEvents) {
        return new Double((double) Math.round(savingsEvents.get(0).getAmount() * 100) / 100);
    }


}