package otus.study.cashmachine.machine.service;

import otus.study.cashmachine.machine.data.CashMachine;

import java.math.BigDecimal;
import java.util.List;

public interface CashMachineService {
    List<Integer> getMoney(CashMachine machine, String cardNum, String pin, BigDecimal amount);

    BigDecimal putMoney(CashMachine machine, String cardNum, String pin, List<Integer> notes);

    BigDecimal checkBalance(CashMachine machine, String cardNum, String pin);

    boolean changePin(String cardNum, String oldPin, String newPin);
}
