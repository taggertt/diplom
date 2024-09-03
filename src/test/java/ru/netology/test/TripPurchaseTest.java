package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.netology.data.*;
import ru.netology.page.TripPurchasePage;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.SQLException;

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

    // Тест для первичной оплаты по карте (approved)
    @Test
    void successPaymentTest() throws SQLException {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getApprovalCard_en();
        page.clickThePaymentButton();
        page.completeTheForm(card);
        page.haveApprovedNotification();
        OrderEntity newOrder = DataHelper.getLastOrder();
        assertNotEquals(lastOrder.getId(), newOrder.getId());
        PaymentEntity payment = DataHelper.getOrderPayment(newOrder);
        assertEquals(approvedStatus, payment.getStatus());
        assertEquals(4500000, payment.getAmount());
    }

    // Тест для первичной оплаты в кредит (approved)
    @Test
    void successCreditTest() throws SQLException {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getApprovalCard_en();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        page.haveApprovedNotification();
        OrderEntity newOrder = DataHelper.getLastOrder();
        assertNotEquals(lastOrder.getId(), newOrder.getId());
        CreditRequestEntity credit = DataHelper.getOrderCreditRegistry(newOrder);
        assertEquals(approvedStatus, credit.getStatus());
    }

    // Тест для оплаты по карте со статусом "declined"
    @Test
    void declinedPaymentTest() throws SQLException {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getNotApprovalCard_en();
        page.clickThePaymentButton();
        page.completeTheForm(card);
        page.haveNotApprovedNotification();
        OrderEntity newOrder = DataHelper.getLastOrder();
        assertEquals(lastOrder.getId(), newOrder.getId());
        PaymentEntity payment = DataHelper.getOrderPayment(newOrder);
        assertEquals(declinedStatus, payment.getStatus());
    }

    // Тест для оплаты в кредит со статусом "declined"
    @Test
    void declinedCardCreditRequestTest() throws SQLException {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getNotApprovalCard_en();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        page.haveNotApprovedNotification();
        OrderEntity newOrder = DataHelper.getLastOrder();
        assertEquals(lastOrder.getId(), newOrder.getId());
        CreditRequestEntity credit = DataHelper.getOrderCreditRegistry(newOrder);
        assertEquals(declinedStatus, credit.getStatus());
    }

    // Тест ввод пустых полей с оплатой по карте
    @Test
    void clearFieldsErrorsPaymentTest() {
        page.clickThePaymentButton();
        page.clearForm();
        page.clickTheContinueButton();
        page.isWarnsVisible();
    }

    // Тест ввод пустых полей с оплатой по кредиту
    @Test
    void clearFieldsErrorsCreditRequestTest() {
        page.clickTheCreditButton();
        page.clearForm();
        page.clickTheContinueButton();
        page.isWarnsVisible();
    }

    // Тест с пустой картой при оплате по карте
    @Test
    void notCardNumberPaymentTest() throws SQLException {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getAnEmptyNumberCard_en();
        page.clickThePaymentButton();
        page.completeTheForm(card);
        page.haveNotApprovedNotification();
        page.warningAboutAnInvalidCardNumber("Поле обязательно для заполнения");
        OrderEntity newOrder = DataHelper.getLastOrder();
        assertEquals(lastOrder.getId(), newOrder.getId());
    }

    // Тест с пустой картой при оплате в кредит
    @Test
    void notCardNumberCreditRequestTest() throws SQLException {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getAnEmptyNumberCard_en();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        page.haveNotApprovedNotification();
        page.warningAboutAnInvalidCardNumber("Поле обязательно для заполнения");
        OrderEntity newOrder = DataHelper.getLastOrder();
        assertEquals(lastOrder.getId(), newOrder.getId());
    }

    // Тест с пустым номером месяца при оплате по карте
    @Test
    void notMonthPaymentTest() throws SQLException {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.GettingAnEmptyCard();
        page.clickThePaymentButton();
        page.completeTheForm(card);
        page.haveNotApprovedNotification();
        page.warningAboutAnInvalidMonth("Поле обязательно для заполнения");
        OrderEntity newOrder = DataHelper.getLastOrder();
        assertEquals(lastOrder.getId(), newOrder.getId());
    }

    // Тест с пустым номером месяца при оплате в кредит
    @Test
    void notMonthCreditTest() throws SQLException {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.GettingAnEmptyCard();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        page.haveNotApprovedNotification();
        page.warningAboutAnInvalidMonth("Поле обязательно для заполнения");
        OrderEntity newOrder = DataHelper.getLastOrder();
        assertEquals(lastOrder.getId(), newOrder.getId());
    }

    // Тест с вводом 13 в поле месяц при оплате по карте
    @Test
    void invalidMonth13PaymentTest() throws SQLException {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getAnInvalidCardMonth13();
        page.clickThePaymentButton();
        page.completeTheForm(card);
        page.haveNotApprovedNotification();
        page.warningAboutAnInvalidMonth("неверный формат");
        OrderEntity newOrder = DataHelper.getLastOrder();
        assertEquals(lastOrder.getId(), newOrder.getId());
    }

    // Тест с вводом 13 в поле месяц при оплате в кредит
    @Test
    void invalidMonth13CreditTest() throws SQLException {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getAnInvalidCardMonth13();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        page.haveNotApprovedNotification();
        page.warningAboutAnInvalidMonth("неверный формат");
        OrderEntity newOrder = DataHelper.getLastOrder();
        assertEquals(lastOrder.getId(), newOrder.getId());
    }

    // Тест с неверным месяцем по дате при оплате по карте
    @Test
    void invalidMonthPaymentTest() throws SQLException {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getAnInvalidCardFormat();
        page.clickThePaymentButton();
        page.completeTheForm(card);
        page.haveNotApprovedNotification();
        page.warningAboutAnInvalidMonth("Неверно указан срок действия карты");
        OrderEntity newOrder = DataHelper.getLastOrder();
        assertEquals(lastOrder.getId(), newOrder.getId());
    }

    // Тест с неверным месяцем по дате при оплате в кредит
    @Test
    void invalidMonthCreditTest() throws SQLException {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getAnInvalidCardFormat();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        page.haveNotApprovedNotification();
        page.warningAboutAnInvalidMonth("Неверно указан срок действия карты");
        OrderEntity newOrder = DataHelper.getLastOrder();
        assertEquals(lastOrder.getId(), newOrder.getId());
    }

    // Тест с пустым полем год при оплате по карте
    @Test
    void notYearPaymentTest() throws SQLException {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.gettingAnEmptyYear();
        page.clickThePaymentButton();
        page.completeTheForm(card);
        page.haveNotApprovedNotification();
        page.warningAboutAnInvalidYear("Поле обязательно для заполнения");
        OrderEntity newOrder = DataHelper.getLastOrder();
        assertEquals(lastOrder.getId(), newOrder.getId());
    }

    // Тест с пустым полем год при оплате в кредит
    @Test
    void notYearCreditTest() throws SQLException {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.gettingAnEmptyYear();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        page.haveNotApprovedNotification();
        page.warningAboutAnInvalidYear("Поле обязательно для заполнения");
        OrderEntity newOrder = DataHelper.getLastOrder();
        assertEquals(lastOrder.getId(), newOrder.getId());
    }

    // Тест с пустым полем Владелец при оплате по карте
    @Test
    void notHolderPaymentTest() throws SQLException {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.gettingAnEmptyHalfCardHolder();
        page.clickThePaymentButton();
        page.completeTheForm(card);
        page.haveNotApprovedNotification();
        page.warningAboutAnInvalidHolder("Поле обязательно для заполнения");
        OrderEntity newOrder = DataHelper.getLastOrder();
        assertEquals(lastOrder.getId(), newOrder.getId());
    }

    // Тест с пустым полем Владелец при оплате в кредит
    @Test
    void notHolderCreditTest() throws SQLException {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.gettingAnEmptyHalfCardHolder();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        page.haveNotApprovedNotification();
        page.warningAboutAnInvalidHolder("Поле обязательно для заполнения");
        OrderEntity newOrder = DataHelper.getLastOrder();
        assertEquals(lastOrder.getId(), newOrder.getId());
    }

    // Тест с пустым полем cvc/cvv при оплате по карте
    @Test
    void notCvcPaymentTest() throws SQLException {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.gettingAnEmptyCvcCode();
        page.clickThePaymentButton();
        page.completeTheForm(card);
        page.haveNotApprovedNotification();
        page.warningAboutAnInvalidCvc("Поле обязательно для заполнения");
        OrderEntity newOrder = DataHelper.getLastOrder();
        assertEquals(lastOrder.getId(), newOrder.getId());
    }

    // Тест с пустым полем cvc/cvv при оплате в кредит
    @Test
    void notCvcCreditTest() throws SQLException {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.gettingAnEmptyCvcCode();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        page.haveNotApprovedNotification();
        page.warningAboutAnInvalidCvc("Поле обязательно для заполнения");
        OrderEntity newOrder = DataHelper.getLastOrder();
        assertEquals(lastOrder.getId(), newOrder.getId());
    }

    // Тест с пустым полем год на 6 больше текущего при оплате по карте
    @Test
    void invalidYear6PaymentTest() throws SQLException {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.gettingTheWrongYear6();
        page.clickThePaymentButton();
        page.completeTheForm(card);
        page.haveNotApprovedNotification();
        page.warningAboutAnInvalidYear("Неверно указан срок действия карты");
        OrderEntity newOrder = DataHelper.getLastOrder();
        assertEquals(lastOrder.getId(), newOrder.getId());
    }

    // Тест с пустым полем год на 6 больше текущего при оплате в кредит
    @Test
    void invalidYear6CreditTest() throws SQLException {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.gettingTheWrongYear6();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        page.haveNotApprovedNotification();
        page.warningAboutAnInvalidYear("Неверно указан срок действия карты");
        OrderEntity newOrder = DataHelper.getLastOrder();
        assertEquals(lastOrder.getId(), newOrder.getId());
    }

    // Тест с истекшим годом при оплате по карте
    @Test
    void invalidYearPaymentTest() throws SQLException {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.gettingTheWrongYear();
        page.clickThePaymentButton();
        page.completeTheForm(card);
        page.haveNotApprovedNotification();
        page.warningAboutAnInvalidYear("Истёк срок действия карты");
        OrderEntity newOrder = DataHelper.getLastOrder();
        assertEquals(lastOrder.getId(), newOrder.getId());
    }

    // Тест с истекшим годом при оплате в кредит
    @Test
    void invalidYearCreditTest() throws SQLException {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.gettingTheWrongYear();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        page.haveNotApprovedNotification();
        page.warningAboutAnInvalidYear("Истёк срок действия карты");
        OrderEntity newOrder = DataHelper.getLastOrder();
        assertEquals(lastOrder.getId(), newOrder.getId());
    }

    // Тест с полем Владелец заполненным кириллицей для оплаты по карте
    @Test
    void invalidHolderCyrillicPaymentTest() throws SQLException {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getApprovalCard_ru();
        page.clickThePaymentButton();
        page.completeTheForm(card);
        page.haveNotApprovedNotification();
        page.warningAboutAnInvalidHolder("неверный формат");
        OrderEntity newOrder = DataHelper.getLastOrder();
        assertEquals(lastOrder.getId(), newOrder.getId());
    }

    // Тест с полем Владелец заполненным кириллицей для оплаты в кредит
    @Test
    void invalidHolderCyrillicCreditTest() throws SQLException {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getApprovalCard_ru();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        page.haveNotApprovedNotification();
        page.warningAboutAnInvalidHolder("неверный формат");
        OrderEntity newOrder = DataHelper.getLastOrder();
        assertEquals(lastOrder.getId(), newOrder.getId());
    }

    // Тест с полем Владелец заполненным спец символами для оплаты по карте
    @Test
    void invalidHolderSpecPaymentTest() throws SQLException {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.gettingSpecialCharacters();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        page.haveNotApprovedNotification();
        page.warningAboutAnInvalidHolder("неверный формат");
        OrderEntity newOrder = DataHelper.getLastOrder();
        assertEquals(lastOrder.getId(), newOrder.getId());
    }

    // Тест с полем Владелец заполненным спец символами для оплаты в кредит
    @Test
    void invalidHolderSpecCreditTest() throws SQLException {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.gettingSpecialCharacters();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        page.haveNotApprovedNotification();
        page.warningAboutAnInvalidHolder("неверный формат");
        OrderEntity newOrder = DataHelper.getLastOrder();
        assertEquals(lastOrder.getId(), newOrder.getId());
    }

    // Тест с вводом в поле номер карты несуществующего при оплате картой
    @Test
    void notExistCardPaymentTest() throws SQLException {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getANonExistentNumberCard_en();
        page.clickThePaymentButton();
        page.completeTheForm(card);
        page.haveDeclinedNotification();
        OrderEntity newOrder = DataHelper.getLastOrder();
        assertEquals(lastOrder.getId(), newOrder.getId());
    }

    // Тест с вводом в поле номер карты несуществующего при оплате по кредиту
    @Test
    void notExistCardCreditTest() throws SQLException {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getANonExistentNumberCard_en();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        page.haveDeclinedNotification();
        OrderEntity newOrder = DataHelper.getLastOrder();
        assertEquals(lastOrder.getId(), newOrder.getId());
    }

    // Тест с не полным номером карты
    @Test
    void invalidCardNumberTest() throws SQLException {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getInvalidNumberCard_en();
        page.clickThePaymentButton();
        page.completeTheForm(card);
        page.haveNotApprovedNotification();
        page.warningAboutAnInvalidCardNumber("формат");
        OrderEntity newOrder = DataHelper.getLastOrder();
        assertEquals(lastOrder.getId(), newOrder.getId());
    }

    // Тест с вводом в поле cvc/cvv двузначного числа при оплате картой
    @Test
    void invalidCvcPaymentTest() throws SQLException {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.GettingInvalidCvcCode();
        page.clickThePaymentButton();
        page.completeTheForm(card);
        page.haveNotApprovedNotification();
        page.warningAboutAnInvalidCvc("неверный формат");
        OrderEntity newOrder = DataHelper.getLastOrder();
        assertEquals(lastOrder.getId(), newOrder.getId());
    }

    // Тест с вводом в поле cvc/cvv двузначного числа при оплате по кредиту
    @Test
    void invalidCvcCreditTest() throws SQLException {
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.GettingInvalidCvcCode();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        page.haveNotApprovedNotification();
        page.warningAboutAnInvalidCvc("неверный формат");
        OrderEntity newOrder = DataHelper.getLastOrder();
        assertEquals(lastOrder.getId(), newOrder.getId());
    }
}
