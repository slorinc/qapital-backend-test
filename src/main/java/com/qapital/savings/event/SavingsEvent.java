package com.qapital.savings.event;

import com.qapital.savings.rule.SavingsRule;
import com.qapital.savings.rule.SavingsRule.RuleType;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.Date;

/**
 * A Savings Event represents an event in the history of a Savings Goal.
 * Events can be either monetary (triggered by the application of Savings Rules,
 * manual transfers, interest payments or incentive payouts), or other events
 * of significance in the history of the goal, such as pausing or unpausing
 * Savings Rules or other users joining or leaving a shared goal.
 */
public class SavingsEvent {
	
	private Long id;
	private Long userId;
	private Long savingsGoalId;
	private Long savingsRuleId;
	private EventName eventName;
	private LocalDate date;
	private BigDecimal amount;
	private Long triggerId;
	private RuleType ruleType;
	private Long savingsTransferId;
	private Boolean cancelled;
	private Date created;

    public SavingsEvent() {}

	public SavingsEvent(Long userId, Long savingsGoalId, Long savingsRuleId, EventName eventName, LocalDate date, BigDecimal amount, Long triggerId, SavingsRule savingsRule) {
		this.userId = userId;
		this.savingsGoalId = savingsGoalId;
		this.savingsRuleId = savingsRuleId;
		this.eventName = eventName;
		this.date = date;
		this.amount = amount;
		this.triggerId = triggerId;
		this.ruleType = savingsRule.getRuleType();
		this.created = new Date();
	}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getUserId() {
		return userId;
	}
	
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public Long getSavingsGoalId() {
		return savingsGoalId;
	}
	
	public void setSavingsGoalId(Long savingsGoalId) {
		this.savingsGoalId = savingsGoalId;
	}
	
	public Long getSavingsRuleId() {
		return savingsRuleId;
	}
	
	public void setSavingsRuleId(Long savingsRuleId) {
		this.savingsRuleId = savingsRuleId;
	}

	public EventName getEventName() {
		return eventName;
	}
	
	public void setEventName(EventName eventName) {
		this.eventName = eventName;
	}
	
	public LocalDate getDate() {
		return date;
	}
	
	public void setDate(LocalDate date) {
		this.date = date;
	}
	
	public BigDecimal getAmount() {
		if (amount == null) {
			return BigDecimal.ZERO;
		}
		return amount;
	}
	
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	public Long getTriggerId() {
		return triggerId;
	}
	
	public void setTriggerId(Long triggerTransactionId) {
		this.triggerId = triggerTransactionId;
	}
	
	public RuleType getRuleType() {
		return ruleType;
	}
	
	public void setRuleType(RuleType ruleType) {
		this.ruleType = ruleType;
	}
	
	public Long getSavingsTransferId() {
		return savingsTransferId;
	}
	
	public void setSavingsTransferId(Long savingsTransferId) {
		this.savingsTransferId = savingsTransferId;
	}
	
	public Boolean isCancelled() {
		if (cancelled == null) {
			return false;
		}
		return cancelled;
	}
	
	public void setCancelled(Boolean cancelled) {
		this.cancelled = cancelled;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public enum EventName {
		manual, started, stopped, rule_application, ifttt_transfer, joined, withdrawal, internal_transfer, cancellation, incentive_payout, interest
	}
	
}
