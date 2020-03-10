package com.bank.vyom.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.bank.vyom.model.PrimaryAccount;
import com.bank.vyom.model.Recipient;
import com.bank.vyom.model.SavingsAccount;
import com.bank.vyom.model.User;
import com.bank.vyom.service.TransactionService;
import com.bank.vyom.service.UserService;



@Controller
@RequestMapping("/transfer")
public class TransferController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/betweenAccounts", method = RequestMethod.GET)
    public String betweenAccounts(Model model) {
        model.addAttribute("transferFrom", "");
        model.addAttribute("transferTo", "");
        model.addAttribute("amount", "");

        return "betweenAccounts";
    }

    @RequestMapping(value = "/betweenAccounts", method = RequestMethod.POST)
    public String betweenAccountsPost(
            @ModelAttribute("transferFrom") String transferFrom,
            @ModelAttribute("transferTo") String transferTo,
            @ModelAttribute("amount") String amount,
            Principal principal,
            Model model
    ) throws Exception {
    	if(amount!= null && !amount.isEmpty() &&  Double.parseDouble(amount) > 0) {
    		User user = userService.findByUsername(principal.getName());
            //PrimaryAccount primaryAccount = user.getPrimaryAccount();
            //SavingsAccount savingsAccount = user.getSavingsAccount();
            String status = transactionService.betweenAccountsTransfer(transferFrom, transferTo,  Double.parseDouble(amount), principal);
            if(status.equals("success")) {
            	return "redirect:/userFront";
            }
            model.addAttribute("betweenAccountserror", status);
    	}
    	model.addAttribute("betweenAccountserror", "While transferring from the savings account a minimum balance of 5000 needs to be maintained in savings account");
        return "betweenAccounts";
    }
    
    @RequestMapping(value = "/recipient", method = RequestMethod.GET)
    public String recipient(Model model, Principal principal) {
        List<Recipient> recipientList = transactionService.findRecipientList(principal);

        Recipient recipient = new Recipient();

        model.addAttribute("recipientList", recipientList);
        model.addAttribute("recipient", recipient);

        return "recipient";
    }

    @RequestMapping(value = "/recipient/save", method = RequestMethod.POST)
    public String recipientPost(@ModelAttribute("recipient") Recipient recipient, Principal principal) {

        User user = userService.findByUsername(principal.getName());
        recipient.setUser(user);
        transactionService.saveRecipient(recipient);

        return "redirect:/transfer/recipient";
    }

    @RequestMapping(value = "/recipient/edit", method = RequestMethod.GET)
    public String recipientEdit(@RequestParam(value = "recipientName") String recipientName, Model model, Principal principal){

        Recipient recipient = transactionService.findRecipientByName(recipientName);
        List<Recipient> recipientList = transactionService.findRecipientList(principal);

        model.addAttribute("recipientList", recipientList);
        model.addAttribute("recipient", recipient);

        return "recipient";
    }

    @RequestMapping(value = "/recipient/delete", method = RequestMethod.GET)
    @Transactional
    public String recipientDelete(@RequestParam(value = "recipientName") String recipientName, Model model, Principal principal){

        transactionService.deleteRecipientByName(recipientName);

        List<Recipient> recipientList = transactionService.findRecipientList(principal);

        Recipient recipient = new Recipient();
        model.addAttribute("recipient", recipient);
        model.addAttribute("recipientList", recipientList);


        return "recipient";
    }

    @RequestMapping(value = "/toSomeoneElse",method = RequestMethod.GET)
    public String toSomeoneElse(Model model, Principal principal) {
        List<Recipient> recipientList = transactionService.findRecipientList(principal);

        model.addAttribute("recipientList", recipientList);
        model.addAttribute("accountType", "");

        return "toSomeoneElse";
    }

    @RequestMapping(value = "/toSomeoneElse",method = RequestMethod.POST)
    public String toSomeoneElsePost(@ModelAttribute("recipientName") String recipientName, @ModelAttribute("accountType") String accountType, @ModelAttribute("amount") String amount, Principal principal, Model model) {
    	if(amount!= null && !amount.isEmpty() &&  Double.parseDouble(amount) > 0) {
    		User user = userService.findByUsername(principal.getName());
            Recipient recipient = transactionService.findRecipientByName(recipientName);
            String status = transactionService.toSomeoneElseTransfer(recipient, accountType, Double.parseDouble(amount), user.getPrimaryAccount(), user.getSavingsAccount(), principal);
            if(status.equals("success")) {
            	return "redirect:/userFront";
            }
            model.addAttribute("toSomeoneElseerror", status);
            
    	}else {
    		model.addAttribute("toSomeoneElseerror", "While transferring the amount to the destination account number amount should not be 0");
            
    	}
    	return "toSomeoneElse";
    	
    }
}
