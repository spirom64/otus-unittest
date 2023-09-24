package otus.study;

import otus.study.cashmachine.bank.dao.AccountDao;
import otus.study.cashmachine.bank.dao.CardsDao;
import otus.study.cashmachine.bank.service.AccountService;
import otus.study.cashmachine.bank.service.CardService;
import otus.study.cashmachine.bank.service.impl.AccountServiceImpl;
import otus.study.cashmachine.bank.service.impl.CardServiceImpl;
import otus.study.cashmachine.machine.data.CashMachine;
import otus.study.cashmachine.machine.data.MoneyBox;
import otus.study.cashmachine.machine.service.CashMachineService;
import otus.study.cashmachine.machine.service.MoneyBoxService;
import otus.study.cashmachine.machine.service.impl.CashMachineServiceImpl;
import otus.study.cashmachine.machine.service.impl.MoneyBoxServiceImpl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;


public class Main {
    static AccountDao accountDao = new AccountDao();

    static AccountService accountService;
    static CardsDao cardsDao;
    static CardService cardService;
    static MoneyBoxService moneyBoxService;
    static CashMachineService cashMachineService;


    static {
        accountService = new AccountServiceImpl(accountDao);
        cardsDao = new CardsDao();
        cardService = new CardServiceImpl(accountService, cardsDao);
        moneyBoxService = new MoneyBoxServiceImpl();
        cashMachineService = new CashMachineServiceImpl(cardService, accountService, moneyBoxService);
    }

    public static void main(String[] args) {
        MoneyBox moneyBox = new MoneyBox();
        CashMachine cashMachine = new CashMachine(moneyBox);

        BigDecimal initialSum = cashMachineService.checkBalance(cashMachine, "1111", "0000");
        System.out.println("Initial sum " + initialSum);

        List<Integer> takenAmount = cashMachineService.getMoney(cashMachine, "1111", "0000", BigDecimal.valueOf(4000));
        System.out.println("Taken notes " + takenAmount);

        initialSum = cashMachineService.checkBalance(cashMachine, "1111", "0000");
        System.out.println("New sum " + initialSum);

        cashMachineService.putMoney(cashMachine, "1111", "0000", Arrays.asList(0, 0, 0, 1));
        initialSum = cashMachineService.checkBalance(cashMachine, "1111", "0000");
        System.out.println("New sum " + initialSum);

        cashMachineService.changePin("1111", "0000", "0001");

        System.out.println("");
    }
}