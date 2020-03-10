package com.bank.vyom.service;

import java.security.Principal;

import com.bank.vyom.model.PrimaryAccount;
import com.bank.vyom.model.SavingsAccount;

public interface AccountService {
	PrimaryAccount createPrimaryAccount();
    SavingsAccount createSavingsAccount();
    String deposit(String accountType, double amount, String userName);
    String withdraw(String accountType, double amount, Principal principal);
    
    
}
