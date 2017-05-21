package com.qapital.savings.rule;

import com.qapital.savings.event.SavingsEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/savings/rule")
public class SavingsRulesController {

    @Autowired
    private SavingsRulesService savingsRulesService;

    @RequestMapping(value = "/active/{userId}", method = RequestMethod.GET)
    public List<SavingsRule> activeRulesForUser(@PathVariable Long userId) {
        return savingsRulesService.activeRulesForUser(userId);
    }

    @RequestMapping(value = "/executeRule", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<SavingsEvent> executeRule(@Valid @RequestBody SavingsRule savingsRule) {
        return savingsRulesService.executeRule(savingsRule);
    }



}
