package com.bank.vyom.service.impl;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bank.vyom.dao.PrimaryAccountDao;
import com.bank.vyom.dao.PrimaryTransactionDao;
import com.bank.vyom.dao.RecipientDao;
import com.bank.vyom.dao.SavingsAccountDao;
import com.bank.vyom.dao.SavingsTransactionDao;
import com.bank.vyom.model.PrimaryAccount;
import com.bank.vyom.model.PrimaryTransaction;
import com.bank.vyom.model.Recipient;
import com.bank.vyom.model.SavingsAccount;
import com.bank.vyom.model.SavingsTransaction;
import com.bank.vyom.model.User;
import com.bank.vyom.service.AccountService;
import com.bank.vyom.service.TransactionService;
import com.bank.vyom.service.UserService;


@Service
public class TransactionServiceImpl implements TransactionService {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PrimaryTransactionDao primaryTransactionDao;
	
	@Autowired
	private SavingsTransactionDao savingsTransactionDao;
	
	@Autowired
	private PrimaryAccountDao primaryAccountDao;
	
	@Autowired
	private SavingsAccountDao savingsAccountDao;
	
	@Autowired
	private RecipientDao recipientDao;
	
	@Autowired
	private AccountService accountService;
	

	public List<PrimaryTransaction> findPrimaryTransactionList(String username){
        User user = userService.findByUsername(username);
        List<PrimaryTransaction> primaryTransactionList = user.getPrimaryAccount().getPrimaryTransactionList();

        return primaryTransactionList;
    }

    public List<SavingsTransaction> findSavingsTransactionList(String username) {
        User user = userService.findByUsername(username);
        List<SavingsTransaction> savingsTransactionList = user.getSavingsAccount().getSavingsTransactionList();

        return savingsTransactionList;
    }

    public void savePrimaryDepositTransaction(PrimaryTransaction primaryTransaction) {
        primaryTransactionDao.save(primaryTransaction);
    }

    public void saveSavingsDepositTransaction(SavingsTransaction savingsTransaction) {
        savingsTransactionDao.save(savingsTransaction);
    }
    
    public void savePrimaryWithdrawTransaction(PrimaryTransaction primaryTransaction) {
        primaryTransactionDao.save(primaryTransaction);
    }

    public void saveSavingsWithdrawTransaction(SavingsTransaction savingsTransaction) {
        savingsTransactionDao.save(savingsTransaction);
    }
    
    public String betweenAccountsTransfer(String transferFrom, String transferTo, double amount, Principal principal/*PrimaryAccount primaryAccount, SavingsAccount savingsAccount*/) throws Exception {
        String status = "success";
    	if (transferFrom.equalsIgnoreCase("Primary") && transferTo.equalsIgnoreCase("Savings")) {
    		status = accountService.withdraw("Primary", amount, principal);
    		if(status.equals("success")) {
    			status = accountService.deposit("Savings", amount, principal.getName());
    		}
            /*primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().subtract(new BigDecimal(amount)));
            savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().add(new BigDecimal(amount)));
            primaryAccountDao.save(primaryAccount);
            savingsAccountDao.save(savingsAccount);

            Date date = new Date();

            PrimaryTransaction primaryTransaction = new PrimaryTransaction(date, "Between account transfer from "+transferFrom+" to "+transferTo, "Account", "Finished", Double.parseDouble(amount), primaryAccount.getAccountBalance(), primaryAccount);
            primaryTransactionDao.save(primaryTransaction);*/
        } else if (transferFrom.equalsIgnoreCase("Savings") && transferTo.equalsIgnoreCase("Primary")) {
        	status = accountService.withdraw("Savings", amount, principal);
    		if(status.equals("success")) {
    			status = accountService.deposit("Primary", amount, principal.getName());
    		}
            /*primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().add(new BigDecimal(amount)));
            savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().subtract(new BigDecimal(amount)));
            primaryAccountDao.save(primaryAccount);
            savingsAccountDao.save(savingsAccount);

            Date date = new Date();

            SavingsTransaction savingsTransaction = new SavingsTransaction(date, "Between account transfer from "+transferFrom+" to "+transferTo, "Transfer", "Finished", Double.parseDouble(amount), savingsAccount.getAccountBalance(), savingsAccount);
            savingsTransactionDao.save(savingsTransaction);
            
          */
        } else {
            throw new Exception("Invalid Transfer");
        }
    	return status;
    }
    
    public List<Recipient> findRecipientList(Principal principal) {
        String username = principal.getName();
        List<Recipient> recipientList = recipientDao.findAll().stream() 			//convert list to stream
                .filter(recipient -> username.equals(recipient.getUser().getUsername()))	//filters the line, equals to username
                .collect(Collectors.toList());

        return recipientList;
    }

    public Recipient saveRecipient(Recipient recipient) {
        return recipientDao.save(recipient);
    }

    public Recipient findRecipientByName(String recipientName) {
        return recipientDao.findByName(recipientName);
    }

    public void deleteRecipientByName(String recipientName) {
        recipientDao.deleteByName(recipientName);
    }
    
    public String toSomeoneElseTransfer(Recipient recipient, String accountType, double amount, PrimaryAccount primaryAccount, SavingsAccount savingsAccount, Principal principal) {
    	String status = "success";
    	if(accountType.equalsIgnoreCase("Primary")) {
    		status = accountService.withdraw("Primary", amount, principal);
    		
    	}else if(accountType.equalsIgnoreCase("Savings")){
    		status = accountService.withdraw("Savings", amount, principal);
    		
    	}
    	if(status.equals("success")) {
			User user = userService.findByUsername(recipient.getName());
			if(user!=null ) {
				if(user.getPrimaryAccount()!=null && user.getPrimaryAccount().getAccountNumber()==Long.parseLong(recipient.getAccountNumber())) {
					status = accountService.deposit("Primary", amount,  recipient.getName());
				}else if(user.getSavingsAccount()!=null && user.getSavingsAccount().getAccountNumber()==Long.parseLong(recipient.getAccountNumber())) {
					status = accountService.deposit("Savings", amount,  recipient.getName());
				}
				
			}
		}
    	/*if (accountType.equalsIgnoreCase("Primary")) {
            primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().subtract(new BigDecimal(amount)));
            primaryAccountDao.save(primaryAccount);

            Date date = new Date();

            PrimaryTransaction primaryTransaction = new PrimaryTransaction(date, "Transfer to recipient "+recipient.getName(), "Transfer", "Finished", Double.parseDouble(amount), primaryAccount.getAccountBalance(), primaryAccount);
            primaryTransactionDao.save(primaryTransaction);
        } else if (accountType.equalsIgnoreCase("Savings")) {
            savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().subtract(new BigDecimal(amount)));
            savingsAccountDao.save(savingsAccount);

            Date date = new Date();

            SavingsTransaction savingsTransaction = new SavingsTransaction(date, "Transfer to recipient "+recipient.getName(), "Transfer", "Finished", Double.parseDouble(amount), savingsAccount.getAccountBalance(), savingsAccount);
            savingsTransactionDao.save(savingsTransaction);
        }*/
    	return status;
    }
}
