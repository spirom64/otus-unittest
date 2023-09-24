package otus.study.cashmachine.machine.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;
import otus.study.cashmachine.machine.data.MoneyBox;
import otus.study.cashmachine.machine.service.impl.MoneyBoxServiceImpl;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


public class MoneyBoxServiceTest {

    private MoneyBoxService moneyBoxService;

    private MoneyBox moneyBox;

    @BeforeEach
    void init() {
        moneyBoxService = new MoneyBoxServiceImpl();
        moneyBox = new MoneyBox(100, 100, 100, 100);
    }

    @Test
    void charge7800() {
        int previousSum = moneyBoxService.checkSum(moneyBox);
        List<Integer> result = moneyBoxService.getMoney(moneyBox, 7800);
        assertEquals(7800, result.get(0) * 5000 + result.get(1) * 1000 + result.get(2) * 500 + result.get(3) * 100);
        assertEquals(previousSum - 7800, moneyBoxService.checkSum(moneyBox));
    }

    @Test
    void charge1001() {
        Exception thrown = assertThrows(IllegalStateException.class, () -> {
            moneyBoxService.getMoney(moneyBox, 1001);
        });
        assertEquals("Can't charge the required sum", thrown.getMessage());
    }

    @Test
    void chargeMoreThanHave() {
        int illegalSumToCharge = moneyBoxService.checkSum(moneyBox) + 100;
        Exception thrown = assertThrows(IllegalStateException.class, () -> {
            moneyBoxService.getMoney(moneyBox, illegalSumToCharge);
        });
        assertEquals("Not enough money", thrown.getMessage());
    }

    @Test
    void addNotes() {
        int initialSum = moneyBoxService.checkSum(moneyBox);
        moneyBoxService.putMoney(moneyBox, 1, 1, 1, 1);
        assertEquals(initialSum + 5000 + 1000 + 500 + 100, moneyBoxService.checkSum(moneyBox));
    }
}
