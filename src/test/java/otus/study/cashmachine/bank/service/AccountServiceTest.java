package otus.study.cashmachine.bank.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import otus.study.cashmachine.bank.dao.AccountDao;
import otus.study.cashmachine.bank.data.Account;
import otus.study.cashmachine.bank.service.impl.AccountServiceImpl;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    @Mock
    private AccountDao accountDao;

    private AccountServiceImpl accountServiceImpl;

    private static final Long testAccountId = 1L;

    @BeforeEach
    void init() {
        accountServiceImpl = new AccountServiceImpl(accountDao);
    }

    @Test
    void createAccountMock() {
        when(accountDao.saveAccount(any()))
                .thenReturn(new Account(testAccountId, BigDecimal.TEN));

        Account result = accountServiceImpl.createAccount(BigDecimal.TEN);
        assertEquals(BigDecimal.TEN, result.getAmount());
    }

    @Test
    void createAccountCaptor() {
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);

        when(accountDao.saveAccount(accountCaptor.capture()))
                .thenReturn(new Account(testAccountId, BigDecimal.TEN));

        accountServiceImpl.createAccount(BigDecimal.TEN);
        assertEquals(accountCaptor.getValue().getAmount(), BigDecimal.TEN);
    }

    @Test
    void addSum() {
        when(accountDao.getAccount(anyLong()))
                .thenAnswer(invocation -> new Account(invocation.getArgument(0), BigDecimal.TEN));

        BigDecimal result = accountServiceImpl.putMoney(testAccountId, BigDecimal.TEN);
        assertEquals(BigDecimal.valueOf(20), result);
    }

    @Test
    void getSum() {
        when(accountDao.getAccount(anyLong()))
                .thenAnswer(invocation -> new Account(invocation.getArgument(0), BigDecimal.TEN));

        Exception thrown = assertThrows(IllegalArgumentException.class,
                () -> accountServiceImpl.getMoney(testAccountId, BigDecimal.valueOf(20)));
        assertEquals("Not enough money", thrown.getMessage());
    }

    @Test
    void getAccount() {
        when(accountDao.getAccount(anyLong()))
                .thenAnswer(invocation -> new Account(invocation.getArgument(0), BigDecimal.TEN));

        Account result = accountServiceImpl.getAccount(testAccountId);
        assertEquals(BigDecimal.TEN, result.getAmount());
        assertEquals(testAccountId, result.getId());
    }

    @Test
    void checkBalance() {
        when(accountDao.getAccount(anyLong()))
                .thenAnswer(invocation -> new Account(invocation.getArgument(0), BigDecimal.TEN));

        BigDecimal result = accountServiceImpl.checkBalance(testAccountId);
        assertEquals(BigDecimal.TEN, result);
    }
}
