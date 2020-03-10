package com.bank.vyom.service.impl;

import java.math.BigDecimal;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bank.vyom.dao.PrimaryAccountDao;
import com.bank.vyom.dao.SavingsAccountDao;
import com.bank.vyom.model.PrimaryAccount;
import com.bank.vyom.model.PrimaryTransaction;
import com.bank.vyom.model.SavingsAccount;
import com.bank.vyom.model.SavingsTransaction;
import com.bank.vyom.model.User;
import com.bank.vyom.service.AccountService;
import com.bank.vyom.service.TransactionService;
import com.bank.vyom.service.UserService;

@Service
public class AccountServiceImpl implements AccountService {
	
	private static long nextAccountNumber = Instant.now().getEpochSecond();

    @Autowired
    private PrimaryAccountDao primaryAccountDao;

    @Autowired
    private SavingsAccountDao savingsAccountDao;

    @Autowired
    private UserService userService;
    
    @Autowired
    private TransactionService transactionService;

    public PrimaryAccount createPrimaryAccount() {
        PrimaryAccount primaryAccount = new PrimaryAccount();
        primaryAccount.setAccountBalance(new BigDecimal(0.0));
        primaryAccount.setAccountNumber(accountGen());

        primaryAccountDao.save(primaryAccount);

        return primaryAccountDao.findByAccountNumber(primaryAccount.getAccountNumber());
    }

    public SavingsAccount createSavingsAccount() {
        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setAccountBalance(new BigDecimal(0.0));
        savingsAccount.setAccountNumber(accountGen());

        savingsAccountDao.save(savingsAccount);

        return savingsAccountDao.findByAccountNumber(savingsAccount.getAccountNumber());
    }
    
    public String deposit(String accountType, double amount, String userName) {
    	String status = "success";
    	
        if(amount <= 0) {
    		return  "deposit to the account amount should not be 0";
    	}
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date todaysDate = new Date();
        User user = userService.findByUsername(userName);
        if (accountType.equalsIgnoreCase("Primary")) {
            PrimaryAccount primaryAccount = user.getPrimaryAccount();
            List<PrimaryTransaction> transactionList = transactionService.findPrimaryTransactionList(user.getUsername());
            double sum = amount;
            if((transactionList!=null) && (transactionList.size()>0)) {
            	for(PrimaryTransaction pt : transactionList) {
            		if((sdf.format(todaysDate).compareTo(sdf.format(pt.getDate())) == 0) &&  pt.getDescription().equalsIgnoreCase("Deposit to Primary Account")) {
            			sum = pt.getAmount()+sum;
            		}
            		
            	}
            }	
            if(sum <= 100000) {
            		primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().add(new BigDecimal(amount)));
                    primaryAccountDao.save(primaryAccount);

                    Date date = new Date();

                    PrimaryTransaction primaryTransaction = new PrimaryTransaction(date, "Deposit to Primary Account", "Account", "Finished", amount, primaryAccount.getAccountBalance(), primaryAccount);
                    transactionService.savePrimaryDepositTransaction(primaryTransaction);
            }else {
            		status = "Daily deposit limit is 100000 rupees";
            }
            
            
            
            
        } else if (accountType.equalsIgnoreCase("Savings")) {

        	SavingsAccount savingsAccount = user.getSavingsAccount();
            
            List<SavingsTransaction> transactionList = transactionService.findSavingsTransactionList(user.getUsername());
            double sum = amount;
            if((transactionList!=null) && (transactionList.size()>0)) {
            	for(SavingsTransaction pt : transactionList) {
            		if((sdf.format(todaysDate).compareTo(sdf.format(pt.getDate())) == 0) &&  pt.getDescription().equalsIgnoreCase("Deposit to Primary Account")) {
            			sum = pt.getAmount()+sum;
            		}
            		
            	}
            }	
            if(sum <= 100000) {
            		savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().add(new BigDecimal(amount)));
                    savingsAccountDao.save(savingsAccount);

                    Date date = new Date();
                    SavingsTransaction savingsTransaction = new SavingsTransaction(date, "Deposit to savings Account", "Account", "Finished", amount, savingsAccount.getAccountBalance(), savingsAccount);
                    transactionService.saveSavingsDepositTransaction(savingsTransaction);
            }else {
            		status = "Daily deposit limit is 100000 rupees";
            }
            
            
        }
        return status;
    }
    
    public String withdraw(String accountType, double amount, Principal principal) {
    	String status = "success";
    	if(amount >= 20000) {
    		return  "One time withdrawal limit is 20000";
    	}
        User user = userService.findByUsername(principal.getName());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date todaysDate = new Date();
        
        if (accountType.equalsIgnoreCase("Primary")) {
        	
            PrimaryAccount primaryAccount = user.getPrimaryAccount(); 
            List<PrimaryTransaction> transactionList = transactionService.findPrimaryTransactionList(user.getUsername());
            double sum = amount;
            if((transactionList!=null) && (transactionList.size()>0)) {
            	
            	
            	for(PrimaryTransaction pt : transactionList) {
            		if((sdf.format(todaysDate).compareTo(sdf.format(pt.getDate())) == 0) &&  pt.getDescription().equalsIgnoreCase("Withdraw from Primary Account")) {
            			sum = pt.getAmount()+sum;
            		}
            		
            	}
            }	
            if(sum <= 500000) {
            		if(Double.compare((-100000),primaryAccount.getAccountBalance().subtract(new BigDecimal(amount)).doubleValue())<0) {
                    	primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().subtract(new BigDecimal(amount)));
                        primaryAccountDao.save(primaryAccount);

                        Date date = new Date();

                        PrimaryTransaction primaryTransaction = new PrimaryTransaction(date, "Withdraw from Primary Account", "Account", "Finished", amount, primaryAccount.getAccountBalance(), primaryAccount);
                        transactionService.savePrimaryWithdrawTransaction(primaryTransaction);
                   
                    }else {
                    	status = "Current account cannot cross over draft limit of 100000";
                    }
            }else {
            		status = "Daily withdrawal limit is 500000 rupees";
            }
            
           
            
           
        } else if (accountType.equalsIgnoreCase("Savings")) {
        	
            SavingsAccount savingsAccount = user.getSavingsAccount();
            
            List<SavingsTransaction> transactionList = transactionService.findSavingsTransactionList(user.getUsername());
            double sum = amount;
            if((transactionList!=null) && (transactionList.size()>0)) {
            	
            	
            	for(SavingsTransaction pt : transactionList) {
            		if((sdf.format(todaysDate).compareTo(sdf.format(pt.getDate())) == 0) && pt.getDescription().equalsIgnoreCase("Withdraw from savings Account")) {
            			sum = pt.getAmount()+sum;
            		}
            		
            	}
            }
            if(sum <= 500000) {
            		if(savingsAccount.getAccountBalance().subtract(new BigDecimal(amount)).doubleValue() >= 5000  ) {
                    	savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().subtract(new BigDecimal(amount)));
                        savingsAccountDao.save(savingsAccount);

                        Date date = new Date();
                        SavingsTransaction savingsTransaction = new SavingsTransaction(date, "Withdraw from savings Account", "Account", "Finished", amount, savingsAccount.getAccountBalance(), savingsAccount);
                        transactionService.saveSavingsWithdrawTransaction(savingsTransaction);
                	}else {
                		status = "Savings account should have minimum 5000 balance";
                	}
            }else {
            		status = "Daily withdrawal limit is 500000 rupees";
            }
            
            
            
            
        }
        return status;
    }
    
    private long accountGen() {
        return ++nextAccountNumber;
    }

	

}
