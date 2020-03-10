package com.bank.vyom.service;

import java.security.Principal;
import java.util.List;

import com.bank.vyom.model.PrimaryAccount;
import com.bank.vyom.model.PrimaryTransaction;
import com.bank.vyom.model.Recipient;
import com.bank.vyom.model.SavingsAccount;
import com.bank.vyom.model.SavingsTransaction;


public interface TransactionService {
	List<PrimaryTransaction> findPrimaryTransactionList(String username);

    List<SavingsTransaction> findSavingsTransactionList(String username);

    void savePrimaryDepositTransaction(PrimaryTransaction primaryTransaction);

    void saveSavingsDepositTransaction(SavingsTransaction savingsTransaction);
    
    void savePrimaryWithdrawTransaction(PrimaryTransaction primaryTransaction);
    void saveSavingsWithdrawTransaction(SavingsTransaction savingsTransaction);
    
    String betweenAccountsTransfer(String transferFrom, String transferTo, double amount, Principal p) throws Exception;
    
    List<Recipient> findRecipientList(Principal principal);

    Recipient saveRecipient(Recipient recipient);

    Recipient findRecipientByName(String recipientName);

    void deleteRecipientByName(String recipientName);
    
    String toSomeoneElseTransfer(Recipient recipient, String accountType, double amount, PrimaryAccount primaryAccount, SavingsAccount savingsAccount, Principal principal);
}
