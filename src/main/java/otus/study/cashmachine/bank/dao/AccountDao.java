package otus.study.cashmachine.bank.dao;

import otus.study.cashmachine.bank.data.Account;
import otus.study.cashmachine.bank.db.Accounts;


public class AccountDao {
    public Account getAccount(Long accountId) {
        if (!Accounts.accounts.containsKey(accountId)) {
            throw new IllegalArgumentException("Account not found");
        }
        return Accounts.accounts.get(accountId);
    }

    public Account saveAccount(Account account) {
        if (account.getId() <= 0) {
            account.setId(Accounts.getNextId());
        }
        Accounts.accounts.put(account.getId(), account);
        return account;
    }
}
