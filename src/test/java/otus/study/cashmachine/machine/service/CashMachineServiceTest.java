package otus.study.cashmachine.machine.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import otus.study.cashmachine.TestUtil;
import otus.study.cashmachine.bank.dao.CardsDao;
import otus.study.cashmachine.bank.data.Card;
import otus.study.cashmachine.bank.service.AccountService;
import otus.study.cashmachine.bank.service.impl.CardServiceImpl;
import otus.study.cashmachine.machine.data.CashMachine;
import otus.study.cashmachine.machine.data.MoneyBox;
import otus.study.cashmachine.machine.service.impl.CashMachineServiceImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CashMachineServiceTest {

    @Spy
    @InjectMocks
    private CardServiceImpl cardService;

    @Mock
    private CardsDao cardsDao;

    @Mock
    private AccountService accountService;

    @Mock
    private MoneyBoxService moneyBoxService;

    private CashMachineServiceImpl cashMachineService;

    private static final CashMachine cashMachine = new CashMachine(new MoneyBox());

    private static final Long testAccountId = 100L;
    private static final String testCardNumber = "1111";
    private static final String testCardPin = "0000";

    @BeforeEach
    void init() {
        cashMachineService = new CashMachineServiceImpl(cardService, accountService, moneyBoxService);
    }

    @Test
    void getMoney() {
        ArgumentCaptor<BigDecimal> amountCaptor = ArgumentCaptor.forClass(BigDecimal.class);
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Integer> moneyBoxAmountCaptor = ArgumentCaptor.forClass(Integer.class);

        when(cardsDao.getCardByNumber(testCardNumber))
                .thenReturn(new Card(1L, testCardNumber, testAccountId, TestUtil.getHash(testCardPin)));
        when(accountService.getMoney(idCaptor.capture(), amountCaptor.capture()))
                .thenReturn(BigDecimal.TEN);
        when(moneyBoxService.getMoney(eq(cashMachine.getMoneyBox()), moneyBoxAmountCaptor.capture()))
                .thenReturn(List.of(1, 1, 1, 1));

        List<Integer> result = cashMachineService.getMoney(
                cashMachine, testCardNumber, testCardPin, BigDecimal.TEN);

        assertEquals(testAccountId, idCaptor.getValue());
        assertEquals(BigDecimal.TEN, amountCaptor.getValue());
        assertEquals(10, moneyBoxAmountCaptor.getValue());
        assertEquals(List.of(1, 1, 1, 1), result);
        verify(cardService).getMoney(testCardNumber, testCardPin, BigDecimal.TEN);
    }

    @Test
    void putMoney() {
        List<Integer> testNotes = List.of(1, 1, 1, 1);
        List<BigDecimal> notesValues = List.of(
                new BigDecimal("5000"),
                new BigDecimal("1000"),
                new BigDecimal("500"),
                new BigDecimal("100"));
        Card testCard = new Card(1L, testCardNumber, testAccountId, TestUtil.getHash(testCardPin));
        when(cardsDao.getCardByNumber(testCardNumber))
                .thenReturn(testCard);

        BigDecimal currentNotesTotal = BigDecimal.ZERO;
        for(int i = 0; i < testNotes.size(); i++) {
            List<Integer> currentNotes = testNotes.subList(0, i + 1);
            List<Integer> currentNotesExtended = new ArrayList<>(currentNotes);
            currentNotesExtended
                    .addAll(Collections.nCopies(testNotes.size() - i - 1, 0));
            currentNotesTotal = currentNotesTotal.add(
                    notesValues.get(i).multiply(BigDecimal.valueOf(testNotes.get(i))));
            cashMachineService.putMoney(cashMachine ,testCardNumber, testCardPin, currentNotes);
            verify(moneyBoxService).putMoney(
                    cashMachine.getMoneyBox(),
                    currentNotesExtended.get(3),
                    currentNotesExtended.get(2),
                    currentNotesExtended.get(1),
                    currentNotesExtended.get(0));
            verify(cardService).putMoney(testCardNumber, testCardPin, currentNotesTotal);
        }
    }

    @Test
    void checkBalance() {
        Card testCard = new Card(1L, testCardNumber, testAccountId, TestUtil.getHash(testCardPin));
        when(cardsDao.getCardByNumber(testCardNumber))
                .thenReturn(testCard);
        cashMachineService.checkBalance(cashMachine, testCardNumber, testCardPin);
        verify(cardService).getBalance(testCardNumber, testCardPin);
        verify(accountService).checkBalance(testCard.getId());
    }

    @Test
    void changePin() {
        ArgumentCaptor<Card> cardCaptor = ArgumentCaptor.forClass(Card.class);

        when(cardsDao.getCardByNumber(testCardNumber))
                .thenReturn(new Card(1L, testCardNumber, testAccountId, TestUtil.getHash(testCardPin)));
        when(cardsDao.saveCard(cardCaptor.capture()))
                .thenReturn(new Card(1L, testCardNumber, testAccountId, TestUtil.getHash(testCardPin)));

        cashMachineService.changePin(testCardNumber, testCardPin, "1111");
        assertEquals(TestUtil.getHash("1111"), cardCaptor.getValue().getPinCode());
        verify(cardService).cnangePin(testCardNumber, testCardPin, "1111");
    }

    @Test
    void changePinWithAnswer() {
        Card cardMock = mock(Card.class);

        when(cardsDao.getCardByNumber(anyString()))
                .thenReturn(cardMock);
        when(cardService.cnangePin(anyString(), anyString(), anyString()))
                .thenAnswer(invocation -> {
                   when(cardMock.getPinCode())
                           .thenReturn(TestUtil.getHash(invocation.getArgument(1)));
                   return invocation.callRealMethod();
                });

        cashMachineService.changePin(testCardNumber, testCardPin, "1111");
        verify(cardService).cnangePin(testCardNumber, testCardPin, "1111");
        verify(cardMock).setPinCode(TestUtil.getHash("1111"));
        verify(cardsDao).saveCard(cardMock);
    }
}
