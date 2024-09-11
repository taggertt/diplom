package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.netology.data.*;
import ru.netology.page.TripPurchasePage;

import static org.junit.jupiter.api.Assertions.*;

public class TripPurchaseTest {

    private static final String approvedStatus = "APPROVED";
    private static final String declinedStatus = "DECLINED";
    private TripPurchasePage page = new TripPurchasePage();

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    //1 Тест для первичной оплаты по карте (approved)
    @Test
    void successPaymentTest() {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getApprovalCard_en();
        page.clickThePaymentButton();
        page.completeTheForm(card);
        assertAll(
                () -> page.haveApprovedNotification("Операция одобрена банком"),
                () -> {
                    OrderEntity newOrder = DataHelper.getLastOrder();
                    PaymentEntity payment = DataHelper.getOrderPayment(newOrder);
                    assertNotEquals(lastOrder.getId(), newOrder.getId());
                    assertEquals(approvedStatus, payment.getStatus());
                    assertEquals(45000, payment.getAmount());
                });
    }

    //2 Тест для первичной оплаты в кредит (approved)
    @Test
    void successCreditTest() {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getApprovalCard_en();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        assertAll(
                () -> page.haveApprovedNotification("Операция одобрена банком"),
                () -> {
                    OrderEntity newOrder = DataHelper.getLastOrder();
                    CreditRequestEntity credit = DataHelper.getOrderCreditRegistry(newOrder);
                    assertNotEquals(lastOrder.getId(), newOrder.getId());
                    assertEquals(approvedStatus, credit.getStatus());
                });
    }

    //3 Тест для оплаты по карте со статусом "declined"
    @Test
    void declinedPaymentTest() {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getDeclinedCard_en();
        page.clickThePaymentButton();
        page.completeTheForm(card);
        assertAll (
                () -> page.haveDeclinedNotification("Банк отказал в проведении операции"),
                () -> {
                    OrderEntity newOrder = DataHelper.getLastOrder();
                    PaymentEntity payment = DataHelper.getOrderPayment(newOrder);
                    assertNotEquals(lastOrder.getId(), newOrder.getId());
                    assertEquals(declinedStatus, payment.getStatus());
                });
    }

    //4 Тест для оплаты в кредит со статусом "declined"
    @Test
    void declinedCardCreditRequestTest() {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getDeclinedCard_en();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        assertAll (
                () -> page.haveDeclinedNotification("Банк отказал в проведении операции"),
                () -> {
                    OrderEntity newOrder = DataHelper.getLastOrder();
                    CreditRequestEntity credit = DataHelper.getOrderCreditRegistry(newOrder);
                    assertNotEquals(lastOrder.getId(), newOrder.getId());
                    assertEquals(declinedStatus, credit.getStatus());
                });
    }

    //5 Тест ввод пустых полей с оплатой по карте
    @Test
    void clearFieldsErrorsPaymentTest() {
        page.clickThePaymentButton();
        page.clearForm();
        page.clickTheContinueButton();
        page.isWarnsVisible();
    }

    //6 Тест ввод пустых полей с оплатой по кредиту
    @Test
    void clearFieldsErrorsCreditRequestTest() {
        page.clickTheCreditButton();
        page.clearForm();
        page.clickTheContinueButton();
        page.isWarnsVisible();
    }

    //7 Тест с пустой картой при оплате по карте
    @Test
    void notCardNumberPaymentTest() {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getEmptyCardNUmberBankCard();
        page.clickThePaymentButton();
        page.completeTheForm(card);
        assertAll(
                () -> page.warningAboutAnInvalidCardNumber("Поле обязательно для заполнения"),
                () -> {
                    OrderEntity newOrder = DataHelper.getLastOrder();
                    assertEquals(lastOrder.getId(), newOrder.getId());
                });
    }

    //8 Тест с пустой картой при оплате в кредит
    @Test
    void notCardNumberCreditRequestTest() {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getEmptyCardNUmberBankCard();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        assertAll(
                () -> page.warningAboutAnInvalidCardNumber("Поле обязательно для заполнения"),
                () -> {
                    OrderEntity newOrder = DataHelper.getLastOrder();
                    assertEquals(newOrder.getId(), lastOrder.getId());
                });
    }

    //9 Тест с пустым номером месяца при оплате по карте
    @Test
    void notMonthPaymentTest() {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getEmptyMonthBankCard();
        page.clickThePaymentButton();
        page.completeTheForm(card);
        assertAll (
                () -> page.warningAboutAnInvalidMonth("Поле обязательно для заполнения"),
                () -> {
                    OrderEntity newOrder = DataHelper.getLastOrder();
                    assertEquals(lastOrder.getId(), newOrder.getId());
                });
    }

    //10 Тест с пустым номером месяца при оплате в кредит
    @Test
    void notMonthCreditTest() {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getEmptyMonthBankCard();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        assertAll (
                () -> page.warningAboutAnInvalidMonth("Поле обязательно для заполнения"),
                () -> {
                    OrderEntity newOrder = DataHelper.getLastOrder();
                    assertEquals(lastOrder.getId(), newOrder.getId());
                });
    }

    //11 Тест с вводом 13 в поле месяц при оплате по карте
    @Test
    void invalidMonth13PaymentTest() {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getNotValidMonth13BankCard();
        page.clickThePaymentButton();
        page.completeTheForm(card);
        assertAll (
                () -> page.warningAboutAnInvalidMonth("неверный формат"),
                () -> {
                    OrderEntity newOrder = DataHelper.getLastOrder();
                    assertEquals(lastOrder.getId(), newOrder.getId());
                });
    }

    //12 Тест с вводом 13 в поле месяц при оплате в кредит
    @Test
    void invalidMonth13CreditTest() {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getNotValidMonth13BankCard();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        assertAll (
                () -> page.warningAboutAnInvalidMonth("неверный формат"),
                () -> {
                    OrderEntity newOrder = DataHelper.getLastOrder();
                    assertEquals(lastOrder.getId(), newOrder.getId());
                });
    }

    //13 Тест с неверным месяцем по дате при оплате по карте
    @Test
    void invalidMonthPaymentTest() {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getNotValidMonthBankCard();
        page.clickThePaymentButton();
        page.completeTheForm(card);
        assertAll (
                () -> page.warningAboutAnInvalidMonth("Неверно указан срок действия карты"),
                () -> {
                    OrderEntity newOrder = DataHelper.getLastOrder();
                    assertEquals(lastOrder.getId(), newOrder.getId());
                });
    }

    //14 Тест с неверным месяцем по дате при оплате в кредит
    @Test
    void invalidMonthCreditTest() {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getNotValidMonthBankCard();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        assertAll (
                () -> page.warningAboutAnInvalidMonth("Неверно указан срок действия карты"),
                () -> {
                    OrderEntity newOrder = DataHelper.getLastOrder();
                    assertEquals(lastOrder.getId(), newOrder.getId());
                });
    }

    //15 Тест с пустым полем год при оплате по карте
    @Test
    void notYearPaymentTest() {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getEmptyYearBankCard();
        page.clickThePaymentButton();
        page.completeTheForm(card);
        assertAll (
                () -> page.warningAboutAnInvalidYear("Поле обязательно для заполнения"),
                () -> {
                    OrderEntity newOrder = DataHelper.getLastOrder();
                    assertEquals(lastOrder.getId(), newOrder.getId());
                });
    }

    //16 Тест с пустым полем год при оплате в кредит
    @Test
    void notYearCreditTest() {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getEmptyYearBankCard();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        assertAll(
                () -> page.warningAboutAnInvalidYear("Поле обязательно для заполнения"),
                () -> {
                    OrderEntity newOrder = DataHelper.getLastOrder();
                    assertEquals(lastOrder.getId(), newOrder.getId());
                });
    }

    //17 Тест с пустым полем Владелец при оплате по карте
    @Test
    void notHolderPaymentTest() {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getEmptyHolderBankCard();
        page.clickThePaymentButton();
        page.completeTheForm(card);
        assertAll (
                () -> page.warningAboutAnInvalidHolder("Поле обязательно для заполнения"),
                () -> {
                    OrderEntity newOrder = DataHelper.getLastOrder();
                    assertEquals(lastOrder.getId(), newOrder.getId());
                });
    }

    //18 Тест с пустым полем Владелец при оплате в кредит
    @Test
    void notHolderCreditTest() {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getEmptyHolderBankCard();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        assertAll (
                () -> page.warningAboutAnInvalidHolder("Поле обязательно для заполнения"),
                () -> {
                    OrderEntity newOrder = DataHelper.getLastOrder();
                    assertEquals(lastOrder.getId(), newOrder.getId());
                });
    }

    //19 Тест с пустым полем cvc/cvv при оплате по карте
    @Test
    void notCvcPaymentTest() {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getEmptyCvcBankCard();
        page.clickThePaymentButton();
        page.completeTheForm(card);
        assertAll (
                () ->  page.warningAboutAnInvalidCvc("Поле обязательно для заполнения"),
                () -> {
                    OrderEntity newOrder = DataHelper.getLastOrder();
                    assertEquals(lastOrder.getId(), newOrder.getId());
                });
    }

    //20 Тест с пустым полем cvc/cvv при оплате в кредит
    @Test
    void notCvcCreditTest() {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getEmptyCvcBankCard();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        assertAll (
                () -> page.warningAboutAnInvalidCvc("Поле обязательно для заполнения"),
                () -> {
                    OrderEntity newOrder = DataHelper.getLastOrder();
                    assertEquals(lastOrder.getId(), newOrder.getId());
                });
    }

    //21 Тест с пустым полем год на 6 больше текущего при оплате по карте
    @Test
    void invalidYear6PaymentTest() {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getInvalidYear6BankCard();
        page.clickThePaymentButton();
        page.completeTheForm(card);
        assertAll (
                () -> page.warningAboutAnInvalidYear("Неверно указан срок действия карты"),
                () -> {
                    OrderEntity newOrder = DataHelper.getLastOrder();
                    assertEquals(lastOrder.getId(), newOrder.getId());
                });
    }

    //22 Тест с пустым полем год на 6 больше текущего при оплате в кредит
    @Test
    void invalidYear6CreditTest() {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getInvalidYear6BankCard();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        assertAll (
                () -> page.warningAboutAnInvalidYear("Неверно указан срок действия карты"),
                () -> {
                    OrderEntity newOrder = DataHelper.getLastOrder();
                    assertEquals(lastOrder.getId(), newOrder.getId());
                });
    }

    //23 Тест с истекшим годом при оплате по карте
    @Test
    void invalidYearPaymentTest() {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getInvalidYearBankCard();
        page.clickThePaymentButton();
        page.completeTheForm(card);
        assertAll (
                () -> page.warningAboutAnInvalidYear("Истёк срок действия карты"),
                () -> {
                    OrderEntity newOrder = DataHelper.getLastOrder();
                    assertEquals(lastOrder.getId(), newOrder.getId());
                });
    }

    //24 Тест с истекшим годом при оплате в кредит
    @Test
    void invalidYearCreditTest() {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getInvalidYearBankCard();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        assertAll (
                () -> page.warningAboutAnInvalidYear("Истёк срок действия карты"),
                () -> {
                    OrderEntity newOrder = DataHelper.getLastOrder();
                    assertEquals(lastOrder.getId(), newOrder.getId());
                });
    }

    //25 Тест с полем Владелец заполненным кириллицей для оплаты по карте
    @Test
    void invalidHolderCyrillicPaymentTest() {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getApprovalCard_ru();
        page.clickThePaymentButton();
        page.completeTheForm(card);
        assertAll (
                () -> page.warningAboutAnInvalidHolder("неверный формат"),
                () -> {
                    OrderEntity newOrder = DataHelper.getLastOrder();
                    assertEquals(lastOrder.getId(), newOrder.getId());
                });
    }

    //26 Тест с полем Владелец заполненным кириллицей для оплаты в кредит
    @Test
    void invalidHolderCyrillicCreditTest() {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getApprovalCard_ru();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        assertAll (
                () -> page.warningAboutAnInvalidHolder("неверный формат"),
                () -> {
                    OrderEntity newOrder = DataHelper.getLastOrder();
                    assertEquals(lastOrder.getId(), newOrder.getId());
                });
    }

    //27 Тест с полем Владелец заполненным спец символами для оплаты по карте
    @Test
    void invalidHolderSpecPaymentTest() {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getSpecCharBankCard();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        assertAll (
                () -> page.warningAboutAnInvalidHolder("неверный формат"),
                () -> {
                    OrderEntity newOrder = DataHelper.getLastOrder();
                    assertEquals(lastOrder.getId(), newOrder.getId());
                });
    }

    //28 Тест с полем Владелец заполненным спец символами для оплаты в кредит
    @Test
    void invalidHolderSpecCreditTest() {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getSpecCharBankCard();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        assertAll (
                () -> page.warningAboutAnInvalidHolder("неверный формат"),
                () -> {
                    OrderEntity newOrder = DataHelper.getLastOrder();
                    assertEquals(lastOrder.getId(), newOrder.getId());
                });
    }

    //29 Тест с вводом в поле номер карты несуществующего номера при оплате картой
    @Test
    void notExistCardPaymentTest() {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getInvalidCard_en();
        page.clickThePaymentButton();
        page.completeTheForm(card);
        assertAll(
                () -> page.haveDeclinedNotification("Банк отказал в проведении операции"),
                () -> {
                    OrderEntity newOrder = DataHelper.getLastOrder();
                    assertEquals(lastOrder.getId(), newOrder.getId());
                });
    }

    //30 Тест с вводом в поле номер карты несуществующего номера при оплате по кредиту
    @Test
    void notExistCardCreditTest() {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getInvalidCard_en();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        assertAll (
                () -> page.haveDeclinedNotification("Банк отказал в проведении операции"),
                () -> {
                    OrderEntity newOrder = DataHelper.getLastOrder();
                    assertEquals(lastOrder.getId(), newOrder.getId());
                });
    }

    //31 Тест с вводом в поле cvc/cvv двузначного числа при оплате картой
    @Test
    void invalidCvcPaymentTest() {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getInvalidCvcBankCard();
        page.clickThePaymentButton();
        page.completeTheForm(card);
        assertAll(
                () -> page.warningAboutAnInvalidCvc("неверный формат"),
                () -> {
                    OrderEntity newOrder = DataHelper.getLastOrder();
                    assertEquals(lastOrder.getId(), newOrder.getId());
                });
    }

    //32 Тест с вводом в поле cvc/cvv двузначного числа при оплате по кредиту
    @Test
    void invalidCvcCreditTest() {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getInvalidCvcBankCard();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        assertAll (
                () -> page.warningAboutAnInvalidCvc("неверный формат"),
                () -> {
                    OrderEntity newOrder = DataHelper.getLastOrder();
                    assertEquals(lastOrder.getId(), newOrder.getId());
                });
    }
}
