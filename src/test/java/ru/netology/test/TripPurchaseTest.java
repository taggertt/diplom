package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
//import io.qameta.allure.internal.shadowed.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.netology.data.*;
import ru.netology.page.TripPurchasePage;

//import static org.junit.jupiter.api.Assertions.*;
import java.sql.SQLException;
import org.assertj.core.api.SoftAssertions;

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
    void successPaymentTest() throws SQLException {
        SoftAssertions softly = new SoftAssertions();
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getApprovalCard_en();
        page.clickThePaymentButton();
        page.completeTheForm(card);
        try {
            page.haveApprovedNotification("Операция одобрена банком");
        } catch (AssertionError e) {
            softly.fail ("Ошибка вывода уведомления: " + e.getMessage());
        }
        OrderEntity newOrder = DataHelper.getLastOrder();
        PaymentEntity payment = DataHelper.getOrderPayment(newOrder);
        softly.assertThat(lastOrder.getId()).isNotEqualTo(newOrder.getId());
        softly.assertThat(approvedStatus).isEqualTo(payment.getStatus());
        softly.assertThat(45000).isEqualTo(payment.getAmount());
        softly.assertAll();
    }

    //2 Тест для первичной оплаты в кредит (approved)
    @Test
    void successCreditTest() throws SQLException {
        SoftAssertions softly = new SoftAssertions();
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getApprovalCard_en();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        try {
            page.haveApprovedNotification("Операция одобрена банком");
        } catch (AssertionError e) {
            softly.fail("Ошибка вывода уведомления: " + e.getMessage());
        }
        OrderEntity newOrder = DataHelper.getLastOrder();
        CreditRequestEntity credit = DataHelper.getOrderCreditRegistry(newOrder);
        softly.assertThat(lastOrder.getId()).isNotEqualTo(newOrder.getId());
        softly.assertThat(approvedStatus).isEqualTo(credit.getStatus());
        softly.assertAll();
    }

    //3 Тест для оплаты по карте со статусом "declined"
    @Test
    void declinedPaymentTest() throws SQLException {
        SoftAssertions softly = new SoftAssertions();
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getNotApprovalCard_en();
        page.clickThePaymentButton();
        page.completeTheForm(card);
        try {
            page.haveDeclinedNotification("Банк отказал в проведении операции");
        } catch (AssertionError e) {
            softly.fail ("Ошибка вывода уведомления: " + e.getMessage());
        }
        OrderEntity newOrder = DataHelper.getLastOrder();
        PaymentEntity payment = DataHelper.getOrderPayment(newOrder);
        softly.assertThat(lastOrder.getId()).isNotEqualTo(newOrder.getId());
        softly.assertThat(declinedStatus).isEqualTo(payment.getStatus());
        softly.assertAll();
    }

    //4 Тест для оплаты в кредит со статусом "declined"
    @Test
    void declinedCardCreditRequestTest() throws SQLException {
        SoftAssertions softly = new SoftAssertions();
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getNotApprovalCard_en();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        try {
            page.haveDeclinedNotification("Банк отказал в проведении операции");
        } catch (AssertionError e) {
            softly.fail ("Ошибка вывода уведомления: " + e.getMessage());
        }
        OrderEntity newOrder = DataHelper.getLastOrder();
        CreditRequestEntity credit = DataHelper.getOrderCreditRegistry(newOrder);
        softly.assertThat(lastOrder.getId()).isNotEqualTo(newOrder.getId());
        softly.assertThat(declinedStatus).isEqualTo(credit.getStatus());
        softly.assertAll();
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
    void notCardNumberPaymentTest() throws SQLException {
        SoftAssertions softly = new SoftAssertions();
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getAnEmptyNumberCard_en();
        page.clickThePaymentButton();
        page.completeTheForm(card);
        try {
            page.noHaveApprovedNotification("Операция одобрена банком");
        } catch (AssertionError e) {
            softly.fail("Ошибка вывода уведомления " + e.getMessage());
        }
        try {
            page.warningAboutAnInvalidCardNumber("Поле обязательно для заполнения");
        } catch (AssertionError e) {
            softly.fail("Ошибка вывода подсказки " + e.getMessage());
        }
        OrderEntity newOrder = DataHelper.getLastOrder();
        softly.assertThat(lastOrder.getId()).isEqualTo(newOrder.getId());
        softly.assertAll();
    }

    //8 Тест с пустой картой при оплате в кредит
    @Test
    void notCardNumberCreditRequestTest() throws SQLException {
        SoftAssertions softly = new SoftAssertions();
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getAnEmptyNumberCard_en();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        try {
            page.noHaveApprovedNotification("Операция одобрена банком");
        } catch (AssertionError e) {
            softly.fail("Ошибка вывода уведомления " + e.getMessage());
        }
        try {
            page.warningAboutAnInvalidCardNumber("Поле обязательно для заполнения");
        } catch (AssertionError e) {
            softly.fail("Ошибка вывода подсказки " + e.getMessage());
        }
        OrderEntity newOrder = DataHelper.getLastOrder();
        softly.assertThat(newOrder.getId()).isEqualTo(lastOrder.getId());
        softly.assertAll();
    }

    //9 Тест с пустым номером месяца при оплате по карте
    @Test
    void notMonthPaymentTest() throws SQLException {
        SoftAssertions softly = new SoftAssertions();
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.GettingAnEmptyCard();
        page.clickThePaymentButton();
        page.completeTheForm(card);
        try {
            page.noHaveApprovedNotification("Операция одобрена банком");
        } catch (AssertionError e) {
            softly.fail ("Ошибка вывода уведомления " + e.getMessage());
        }
        try {
            page.warningAboutAnInvalidMonth("Поле обязательно для заполнения");
        } catch (AssertionError e) {
            softly.fail ("Ошибка вывода подсказки " + e.getMessage());
        }
        OrderEntity newOrder = DataHelper.getLastOrder();
        softly.assertThat(lastOrder.getId()).isEqualTo(newOrder.getId());
        softly.assertAll();
    }

    //10 Тест с пустым номером месяца при оплате в кредит
    @Test
    void notMonthCreditTest() throws SQLException {
        SoftAssertions softly = new SoftAssertions();
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.GettingAnEmptyCard();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        try {
            page.noHaveApprovedNotification("Операция одобрена банком");
        } catch (AssertionError e) {
            softly.fail ("Ошибка вывода уведомления " + e.getMessage());
        }
        try {
            page.warningAboutAnInvalidMonth("Поле обязательно для заполнения");
        } catch (AssertionError e) {
            softly.fail("Ошибка вывода подсказки" + e.getMessage());
        }
        OrderEntity newOrder = DataHelper.getLastOrder();
        softly.assertThat(lastOrder.getId()).isEqualTo(newOrder.getId());
        softly.assertAll();
    }

    //11 Тест с вводом 13 в поле месяц при оплате по карте
    @Test
    void invalidMonth13PaymentTest() throws SQLException {
        SoftAssertions softly = new SoftAssertions();
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getAnInvalidCardMonth13();
        page.clickThePaymentButton();
        page.completeTheForm(card);
        try {
            page.noHaveApprovedNotification("Операция одобрена банком");
        } catch (AssertionError e) {
            softly.fail( "Ошибка вывода уведомления " + e.getMessage());
        }
        try {
            page.warningAboutAnInvalidMonth("неверный формат");
        } catch (AssertionError e) {
            softly.fail ("Ошибка вывода подсказки " + e.getMessage());
        }
        OrderEntity newOrder = DataHelper.getLastOrder();
        softly.assertThat(lastOrder.getId()).isEqualTo(newOrder.getId());
        softly.assertAll();
    }

    //12 Тест с вводом 13 в поле месяц при оплате в кредит
    @Test
    void invalidMonth13CreditTest() throws SQLException {
        SoftAssertions softly = new SoftAssertions();
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getAnInvalidCardMonth13();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        try {
            page.noHaveApprovedNotification("Операция одобрена банком");
        } catch (AssertionError e) {
            softly.fail( "Ошибка вывода уведомления " + e.getMessage());
        }
        try {
            page.warningAboutAnInvalidMonth("неверный формат");
        } catch (AssertionError e) {
            softly.fail ("Ошибка вывода подсказки " + e.getMessage());
        }
        OrderEntity newOrder = DataHelper.getLastOrder();
        softly.assertThat(lastOrder.getId()).isEqualTo(newOrder.getId());
        softly.assertAll();
    }

    //13 Тест с неверным месяцем по дате при оплате по карте
    @Test
    void invalidMonthPaymentTest() throws SQLException {
        SoftAssertions softly = new SoftAssertions();
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getAnInvalidCardFormat();
        page.clickThePaymentButton();
        page.completeTheForm(card);
        try {
            page.noHaveApprovedNotification("Операция одобрена банком");
        } catch (AssertionError e) {
            softly.fail("Ошибка вывода уведомления " + e.getMessage());
        }
        try {
            page.warningAboutAnInvalidMonth("Неверно указан срок действия карты");
        } catch (AssertionError e) {
            softly.fail("Ошибка вывода подсказки");
        }
        OrderEntity newOrder = DataHelper.getLastOrder();
        softly.assertThat(lastOrder.getId()).isEqualTo(newOrder.getId());
        softly.assertAll();
    }

    //14 Тест с неверным месяцем по дате при оплате в кредит
    @Test
    void invalidMonthCreditTest() throws SQLException {
        SoftAssertions softly = new SoftAssertions();
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getAnInvalidCardFormat();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        try {
            page.noHaveApprovedNotification("Операция одобрена банком");
        } catch (AssertionError e) {
            softly.fail("Ошибка вывода уведомления " + e.getMessage());
        }
        try {
            page.warningAboutAnInvalidMonth("Неверно указан срок действия карты");
        } catch (AssertionError e) {
            softly.fail("Ошибка вывода подсказки");
        }
        OrderEntity newOrder = DataHelper.getLastOrder();
        softly.assertThat(lastOrder.getId()).isEqualTo(newOrder.getId());
        softly.assertAll();
    }

    //15 Тест с пустым полем год при оплате по карте
    @Test
    void notYearPaymentTest() throws SQLException {
        SoftAssertions softly = new SoftAssertions();
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.gettingAnEmptyYear();
        page.clickThePaymentButton();
        page.completeTheForm(card);
        try {
            page.noHaveApprovedNotification("Операция одобрена банком");
        } catch (AssertionError e) {
            softly.fail ("Ошибка вывода уведомления " + e.getMessage());
        }
        try {
            page.warningAboutAnInvalidYear("Поле обязательно для заполнения");
        } catch (AssertionError e) {
            softly.fail ("Ошибка вывода подсказки " + e.getMessage());
        }
        OrderEntity newOrder = DataHelper.getLastOrder();
        softly.assertThat(lastOrder.getId()).isEqualTo(newOrder.getId());
        softly.assertAll();
    }

    //16 Тест с пустым полем год при оплате в кредит
    @Test
    void notYearCreditTest() throws SQLException {
        SoftAssertions softly = new SoftAssertions();
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.gettingAnEmptyYear();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        try {
            page.noHaveApprovedNotification("Операция одобрена банком");
        } catch (AssertionError e) {
            softly.fail ("Ошибка вывода уведомления " + e.getMessage());
        }
        try {
            page.warningAboutAnInvalidYear("Поле обязательно для заполнения");
        } catch (AssertionError e) {
            softly.fail ("Ошибка вывода подсказки " + e.getMessage());
        }
        OrderEntity newOrder = DataHelper.getLastOrder();
        softly.assertThat(lastOrder.getId()).isEqualTo(newOrder.getId());
        softly.assertAll();
    }

    //17 Тест с пустым полем Владелец при оплате по карте
    @Test
    void notHolderPaymentTest() throws SQLException {
        SoftAssertions softly = new SoftAssertions();
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.gettingAnEmptyHalfCardHolder();
        page.clickThePaymentButton();
        page.completeTheForm(card);
        try {
            page.noHaveApprovedNotification("Операция одобрена банком");
        } catch (AssertionError e) {
            softly.fail ("Ошибка вывода уведомления " + e.getMessage());
        }
        try {
            page.warningAboutAnInvalidHolder("Поле обязательно для заполнения");
        } catch (AssertionError e) {
            softly.fail("Ошибка вывода подсказки " + e.getMessage());
        }
        OrderEntity newOrder = DataHelper.getLastOrder();
        softly.assertThat(lastOrder.getId()).isEqualTo(newOrder.getId());
        softly.assertAll();
    }

    //18 Тест с пустым полем Владелец при оплате в кредит
    @Test
    void notHolderCreditTest() throws SQLException {
        SoftAssertions softly = new SoftAssertions();
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.gettingAnEmptyHalfCardHolder();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        try {
            page.noHaveApprovedNotification("Операция одобрена банком");
        } catch (AssertionError e) {
            softly.fail ("Ошибка вывода уведомления " + e.getMessage());
        }
        try {
            page.warningAboutAnInvalidHolder("Поле обязательно для заполнения");
        } catch (AssertionError e) {
            softly.fail("Ошибка вывода подсказки " + e.getMessage());
        }
        OrderEntity newOrder = DataHelper.getLastOrder();
        softly.assertThat(lastOrder.getId()).isEqualTo(newOrder.getId());
        softly.assertAll();
    }

    //19 Тест с пустым полем cvc/cvv при оплате по карте
    @Test
    void notCvcPaymentTest() throws SQLException {
        SoftAssertions softly = new SoftAssertions();
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.gettingAnEmptyCvcCode();
        page.clickThePaymentButton();
        page.completeTheForm(card);
        try {
            page.noHaveApprovedNotification("Операция одобрена банком");
        } catch (AssertionError e) {
            softly.fail ("Ошибка вывода уведомления " + e.getMessage());
        }
        try {
            page.warningAboutAnInvalidCvc("Поле обязательно для заполнения");
        } catch (AssertionError e) {
            softly.fail ("ОШибка вывода подсказки " + e.getMessage());
        }
        OrderEntity newOrder = DataHelper.getLastOrder();
        softly.assertThat(lastOrder.getId()).isEqualTo(newOrder.getId());
        softly.assertAll();
    }

    //20 Тест с пустым полем cvc/cvv при оплате в кредит
    @Test
    void notCvcCreditTest() throws SQLException {
        SoftAssertions softly = new SoftAssertions();
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.gettingAnEmptyCvcCode();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        try {
            page.noHaveApprovedNotification("Операция одобрена банком");
        } catch (AssertionError e) {
            softly.fail ("Ошибка вывода уведомления " + e.getMessage());
        }
        try {
            page.warningAboutAnInvalidCvc("Поле обязательно для заполнения");
        } catch (AssertionError e) {
            softly.fail ("ОШибка вывода подсказки " + e.getMessage());
        }
        OrderEntity newOrder = DataHelper.getLastOrder();
        softly.assertThat(lastOrder.getId()).isEqualTo(newOrder.getId());
        softly.assertAll();
    }

    //21 Тест с пустым полем год на 6 больше текущего при оплате по карте
    @Test
    void invalidYear6PaymentTest() throws SQLException {
        SoftAssertions softly = new SoftAssertions();
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.gettingTheWrongYear6();
        page.clickThePaymentButton();
        page.completeTheForm(card);
        try {
            page.noHaveApprovedNotification("Операция одобрена банком");
        } catch (AssertionError e) {
            softly.fail ("Ошибка вывода уведомления " + e.getMessage());
        }
        try {
            page.warningAboutAnInvalidYear("Неверно указан срок действия карты");
        } catch (AssertionError e) {
            softly.fail("Ошибка вывода подсказки " + e.getMessage());
        }
        OrderEntity newOrder = DataHelper.getLastOrder();
        softly.assertThat(lastOrder.getId()).isEqualTo(newOrder.getId());
        softly.assertAll();
    }

    //22 Тест с пустым полем год на 6 больше текущего при оплате в кредит
    @Test
    void invalidYear6CreditTest() throws SQLException {
        SoftAssertions softly = new SoftAssertions();
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.gettingTheWrongYear6();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        try {
            page.noHaveApprovedNotification("Операция одобрена банком");
        } catch (AssertionError e) {
            softly.fail ("Ошибка вывода уведомления " + e.getMessage());
        }
        try {
            page.warningAboutAnInvalidYear("Неверно указан срок действия карты");
        } catch (AssertionError e) {
            softly.fail("Ошибка вывода подсказки " + e.getMessage());
        }
        OrderEntity newOrder = DataHelper.getLastOrder();
        softly.assertThat(lastOrder.getId()).isEqualTo(newOrder.getId());
        softly.assertAll();
    }

    //23 Тест с истекшим годом при оплате по карте
    @Test
    void invalidYearPaymentTest() throws SQLException {
        SoftAssertions softly = new SoftAssertions();
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.gettingTheWrongYear();
        page.clickThePaymentButton();
        page.completeTheForm(card);
        try {
            page.noHaveApprovedNotification("Операция одобрена банком");
        } catch (AssertionError e) {
            softly.fail ("Ошибка вывода уведомления " + e.getMessage());
        }
        try {
            page.warningAboutAnInvalidYear("Истёк срок действия карты");
        } catch (AssertionError e) {
            softly.fail("Ошибка вывода подсказки");
        }
        OrderEntity newOrder = DataHelper.getLastOrder();
        softly.assertThat(lastOrder.getId()).isEqualTo(newOrder.getId());
        softly.assertAll();
    }

    //24 Тест с истекшим годом при оплате в кредит
    @Test
    void invalidYearCreditTest() throws SQLException {
        SoftAssertions softly = new SoftAssertions();
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.gettingTheWrongYear();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        try {
            page.noHaveApprovedNotification("Операция одобрена банком");
        } catch (AssertionError e) {
            softly.fail ("Ошибка вывода уведомления " + e.getMessage());
        }
        try {
            page.warningAboutAnInvalidYear("Истёк срок действия карты");
        } catch (AssertionError e) {
            softly.fail("Ошибка вывода подсказки");
        }
        OrderEntity newOrder = DataHelper.getLastOrder();
        softly.assertThat(lastOrder.getId()).isEqualTo(newOrder.getId());
        softly.assertAll();
    }

    //25 Тест с полем Владелец заполненным кириллицей для оплаты по карте
    @Test
    void invalidHolderCyrillicPaymentTest() throws SQLException {
        SoftAssertions softly = new SoftAssertions();
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getApprovalCard_ru();
        page.clickThePaymentButton();
        page.completeTheForm(card);
        try {
            page.noHaveApprovedNotification("Операция одобрена банком");
        } catch (AssertionError e) {
            softly.fail ("Ошибка вывода уведомления " + e.getMessage());
        }
        try {
            page.warningAboutAnInvalidHolder("неверный формат");
        } catch (AssertionError e) {
            softly.fail("Ошибка вывода подсказки " + e.getMessage());
        }
        OrderEntity newOrder = DataHelper.getLastOrder();
        softly.assertThat(lastOrder.getId()).isEqualTo(newOrder.getId());
        softly.assertAll();
    }

    //26 Тест с полем Владелец заполненным кириллицей для оплаты в кредит
    @Test
    void invalidHolderCyrillicCreditTest() throws SQLException {
        SoftAssertions softly = new SoftAssertions();
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getApprovalCard_ru();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        try {
            page.noHaveApprovedNotification("Операция одобрена банком");
        } catch (AssertionError e) {
            softly.fail ("Ошибка вывода уведомления " + e.getMessage());
        }
        try {
            page.warningAboutAnInvalidHolder("неверный формат");
        } catch (AssertionError e) {
            softly.fail("Ошибка вывода подсказки " + e.getMessage());
        }
        OrderEntity newOrder = DataHelper.getLastOrder();
        softly.assertThat(lastOrder.getId()).isEqualTo(newOrder.getId());
        softly.assertAll();
    }

    //27 Тест с полем Владелец заполненным спец символами для оплаты по карте
    @Test
    void invalidHolderSpecPaymentTest() throws SQLException {
        SoftAssertions softly = new SoftAssertions();
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.gettingSpecialCharacters();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        try {
            page.noHaveApprovedNotification("Операция одобрена банком");
        } catch (AssertionError e) {
            softly.fail ("Ошибка вывода уведомления " + e.getMessage());
        }
        try {
            page.warningAboutAnInvalidHolder("неверный формат");
        } catch (AssertionError e) {
            softly.fail ("Ошибка вывода подсказки " + e.getMessage());
        }
        OrderEntity newOrder = DataHelper.getLastOrder();
        softly.assertThat(lastOrder.getId()).isEqualTo(newOrder.getId());
        softly.assertAll();
    }

    //28 Тест с полем Владелец заполненным спец символами для оплаты в кредит
    @Test
    void invalidHolderSpecCreditTest() throws SQLException {
        SoftAssertions softly = new SoftAssertions();
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.gettingSpecialCharacters();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        try {
            page.noHaveApprovedNotification("Операция одобрена банком");
        } catch (AssertionError e) {
            softly.fail ("Ошибка вывода уведомления " + e.getMessage());
        }
        try {
            page.warningAboutAnInvalidHolder("неверный формат");
        } catch (AssertionError e) {
            softly.fail ("Ошибка вывода подсказки " + e.getMessage());
        }
        OrderEntity newOrder = DataHelper.getLastOrder();
        softly.assertThat(lastOrder.getId()).isEqualTo(newOrder.getId());
        softly.assertAll();
    }

    //29 Тест с вводом в поле номер карты несуществующего номера при оплате картой
    @Test
    void notExistCardPaymentTest() throws SQLException {
        SoftAssertions softly = new SoftAssertions();
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getANonExistentNumberCard_en();
        page.clickThePaymentButton();
        page.completeTheForm(card);
        try {
            page.haveDeclinedNotification("Банк отказал в проведении операции");
        } catch (AssertionError e) {
            softly.fail("Ошибка вывода уведомления " + e.getMessage());
        }
        OrderEntity newOrder = DataHelper.getLastOrder();
        softly.assertThat(lastOrder.getId()).isEqualTo(newOrder.getId());
        softly.assertAll();
    }

    //30 Тест с вводом в поле номер карты несуществующего номера при оплате по кредиту
    @Test
    void notExistCardCreditTest() throws SQLException {
        SoftAssertions softly = new SoftAssertions();
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getANonExistentNumberCard_en();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        try {
            page.haveDeclinedNotification("Банк отказал в проведении операции");
        } catch (AssertionError e) {
            softly.fail("Ошибка вывода уведомления " + e.getMessage());
        }
        OrderEntity newOrder = DataHelper.getLastOrder();
        softly.assertThat(lastOrder.getId()).isEqualTo(newOrder.getId());
        softly.assertAll();
    }

    //31 Тест с не полным номером карты для оплаты картой
    @Test
    void invalidCardNumberPaymentTest() throws SQLException {
        SoftAssertions softly = new SoftAssertions();
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getInvalidNumberCard_en();
        page.clickThePaymentButton();
        page.completeTheForm(card);
        try {
            page.noHaveApprovedNotification("Операция одобрена банком");
        } catch (AssertionError e) {
            softly.fail ("Ошибка вывода уведомления " + e.getMessage());
        }
        try {
            page.warningAboutAnInvalidCardNumber("неверный формат");
        } catch (AssertionError e) {
            softly.fail("Ошибка вывода подсказки " + e.getMessage());
        }
        OrderEntity newOrder = DataHelper.getLastOrder();
        softly.assertThat(lastOrder.getId()).isEqualTo(newOrder.getId());
        softly.assertAll();
    }

    //32 Тест с не полным номером карты для оплаты в кредит
    @Test
    void invalidCardNumberCreditRequestTest() throws SQLException {
        SoftAssertions softly = new SoftAssertions();
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.getInvalidNumberCard_en();
        page.clickThePaymentButton();
        page.completeTheForm(card);
        try {
            page.noHaveApprovedNotification("Операция одобрена банком");
        } catch (AssertionError e) {
            softly.fail ("Ошибка вывода уведомления " + e.getMessage());
        }
        try {
            page.warningAboutAnInvalidCardNumber("неверный формат");
        } catch (AssertionError e) {
            softly.fail("Ошибка вывода подсказки " + e.getMessage());
        }
        OrderEntity newOrder = DataHelper.getLastOrder();
        softly.assertThat(lastOrder.getId()).isEqualTo(newOrder.getId());
        softly.assertAll();
    }

    //33 Тест с вводом в поле cvc/cvv двузначного числа при оплате картой
    @Test
    void invalidCvcPaymentTest() throws SQLException {
        SoftAssertions softly = new SoftAssertions();
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.GettingInvalidCvcCode();
        page.clickThePaymentButton();
        page.completeTheForm(card);
        try {
            page.noHaveApprovedNotification("Операция одобрена банком");
        } catch (AssertionError e) {
            softly.fail ("Ошибка вывода уведомления " + e.getMessage());
        }
        try {
            page.warningAboutAnInvalidCvc("неверный формат");
        } catch (AssertionError e) {
            softly.fail("Ошибка вывода подсказки " + e.getMessage());
        }
        page.warningAboutAnInvalidCvc("неверный формат");
        OrderEntity newOrder = DataHelper.getLastOrder();
        softly.assertThat(lastOrder.getId()).isEqualTo(newOrder.getId());
        softly.assertAll();
    }

    //34 Тест с вводом в поле cvc/cvv двузначного числа при оплате по кредиту
    @Test
    void invalidCvcCreditTest() throws SQLException {
        SoftAssertions softly = new SoftAssertions();
        OrderEntity lastOrder = DataHelper.getLastOrder();
        BankCard card = DataHelper.GettingInvalidCvcCode();
        page.clickTheCreditButton();
        page.completeTheForm(card);
        try {
            page.noHaveApprovedNotification("Операция одобрена банком");
        } catch (AssertionError e) {
            softly.fail ("Ошибка вывода уведомления " + e.getMessage());
        }
        try {
            page.warningAboutAnInvalidCvc("неверный формат");
        } catch (AssertionError e) {
            softly.fail("Ошибка вывода подсказки " + e.getMessage());
        }
        OrderEntity newOrder = DataHelper.getLastOrder();
        softly.assertThat(lastOrder.getId()).isEqualTo(newOrder.getId());
        softly.assertAll();
    }
}
