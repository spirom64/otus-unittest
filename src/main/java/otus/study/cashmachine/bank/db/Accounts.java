package otus.study.cashmachine.bank.db;

import otus.study.cashmachine.bank.data.Account;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


public class Accounts {

    public static Map<Long, Account> accounts = new HashMap<>();
    static Long idSequence = 10L;

    static {
        accounts.put(1L, new Account(1, BigDecimal.valueOf(10000)));
        accounts.put(2L, new Account(2, BigDecimal.valueOf(1000)));
        accounts.put(3L, new Account(3, BigDecimal.valueOf(0)));
    }

    public static Long getNextId() {
        idSequence ++;
        return idSequence;
    }

}
