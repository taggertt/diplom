package ru.netology.data;

import com.github.javafaker.Faker;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import lombok.SneakyThrows;
import java.util.Calendar;
import java.util.Locale;

public class DataHelper {

    // Выполнение SQL запросов с помощью QueryRunner
    private static final String url = System.getProperty("db.url");

    @SneakyThrows
    public static Connection getConn(){
        return DriverManager.getConnection(url, "app", "pass");
    }
    public static OrderEntity getLastOrder() {
        String orderSQL = "select * from order_entity o " +
                "where o.created = (" +
                "select max(created) from order_entity);";
        var runner = new QueryRunner();
        var connection = getConn();
        OrderEntity order = new OrderEntity();
        try {
           order = runner.query(connection, orderSQL, new BeanHandler<>(OrderEntity.class));
            if (order == null) {
                order = new OrderEntity();
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при работе с СУБД " + e.getMessage());
        }
        return order;
    }

    public static PaymentEntity getOrderPayment(OrderEntity order) {
        String paymentSQL = "select * from payment_entity p " +
                "where p.transaction_id = ?;";
        var runner = new QueryRunner();
        var connection = getConn();
        PaymentEntity payment = new PaymentEntity();
        try {
            payment = runner.query(connection, paymentSQL, new BeanHandler<>(PaymentEntity.class), order.getPayment_id());
            if (payment == null) {
                payment = new PaymentEntity();
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при работе с СУБД " + e.getMessage());
        }
        return payment;
    }

    public static CreditRequestEntity getOrderCreditRegistry(OrderEntity order) {
        String creditSQL = "select * from credit_request_entity c " +
                "where c.bank_id = ?;";
        var runner = new QueryRunner();
        var connection = getConn();
        CreditRequestEntity credit = new CreditRequestEntity();
        try {
            credit = runner.query(connection, creditSQL, new BeanHandler<>(CreditRequestEntity.class), order.getCredit_id());
            if (credit == null) {
                credit = new CreditRequestEntity();
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при работе с СУБД " + e.getMessage());
        }
        return credit;
    }

    // Cоздаем две переменные для тестирования данных как на русском так и на английском языке
    private static final Faker faker_en = new Faker(new Locale("en-US"));
    private static final Faker faker_ru = new Faker(new Locale("ru-RU"));



    // Методы для определения данных в каждом поле
    private static final String approvedCardNumber = "4444 4444 4444 4441";
    private static final String declinedCardNumber = "4444 4444 4444 4442";

    public static String getInvalidCardNumber () {
        String cardNumber;
        do {
            cardNumber = String.format("%016d", faker_en.number().randomNumber(16, false));
        } while (cardNumber.equals(approvedCardNumber) || cardNumber.equals(declinedCardNumber));
        return cardNumber;
    }

    public static String getEmptyCardNumber () {
        String cardNumber = "";
        return cardNumber;
    }
    public static String getValidMonth () {
        String validMonth = String.format("%2d", faker_en.number().numberBetween(1, 12)).replace(" ", "0");
        return validMonth;
    }

    public static String getEmptyMonth () {
        String month = "";
        return month;
    }

    public static String getNotValidMonth () {
        int numberMonth = Calendar.getInstance().get(Calendar.MONTH);
        String notValidMonth = Integer.toString(faker_en.number().numberBetween(00, numberMonth - 1));
        return notValidMonth;
    }

    public static String getNotValidMonth13 () {
        String month = Integer.toString(13);
        return month;
    }

    public static String getValidYear () {
        int numberYear = Calendar.getInstance().get(Calendar.YEAR);
        String validYear = Integer.toString(faker_en.number().numberBetween(numberYear + 1, numberYear + 5)).substring(2);
        return validYear;
    }

    public static String getCurrentYear () {
        int numberYear = Calendar.getInstance().get(Calendar.YEAR);
        String year = Integer.toString(numberYear);
        return year;
    }

    public static String getInvalidYear () {
        int numberYear = Calendar.getInstance().get(Calendar.YEAR);
        String year = Integer.toString(numberYear - 1).substring(2);
        return year;
    }

    public static String getInvalidYear6 () {
        int numberYear = Calendar.getInstance().get(Calendar.YEAR);
        String year = Integer.toString(numberYear + 6).substring(2);
        return year;
    }

    public static String getEmptyYear () {
        String year = "";
        return year;
    }
    public static String getEngHolder() {
        String holder = faker_en.name().name();
        return holder;
    }

    public static String getRuHolder() {
        String holder = faker_ru.name().name();
        return holder;
    }

    public static String getSpecSymbolHolder() {
        String holder = faker_en.regexify("[\\-\\=\\+\\<\\>\\!\\@\\#\\$\\%\\^\\&\\*\\1\\2\\3\\4\\5\\6\\{\\}]{8,15}");
        return holder;
    }

    public static String getEmptyHolder () {
        String holder = "";
        return holder;
    }

    public static String getValidCvc () {
        String cvc = faker_en.number().digits(3);
        return cvc;
    }

    public static String getInvalidCvc () {
        String cvc = faker_en.number().digits(2);
        return cvc;
    }

    public static String getEmptyCvc () {
        String cvc = "";
        return cvc;
    }

    // Методы создания полных данных для передачи в тестовые сценарии
    public static BankCard getApprovalCard_en () {
        return new BankCard(approvedCardNumber, getValidMonth(), getValidYear(), getEngHolder(), getValidCvc());
    }

    public static BankCard getDeclinedCard_en () {
        return new BankCard(declinedCardNumber, getValidMonth(), getValidYear(), getEngHolder(), getValidCvc());
    }

    public static BankCard getInvalidCard_en () {
        return new BankCard(getInvalidCardNumber(), getValidMonth(), getValidYear(), getEngHolder(), getValidCvc());
    }

    public static BankCard getApprovalCard_ru () {
        return new BankCard(approvedCardNumber, getValidMonth(), getValidYear(), getRuHolder(), getValidCvc());
    }

    public static BankCard getSpecCharBankCard () {
        return new BankCard(approvedCardNumber, getValidMonth(), getValidYear(), getSpecSymbolHolder(), getValidCvc());
    }

    public static BankCard getEmptyCardNUmberBankCard () {
        return new BankCard(getEmptyCardNumber(), getValidMonth(), getValidYear(), getEngHolder(), getValidCvc());
    }

    public static BankCard getEmptyMonthBankCard () {
        return new BankCard(approvedCardNumber, getEmptyMonth(), getValidYear(), getEngHolder(), getValidCvc());
    }

    public static BankCard getNotValidMonth13BankCard () {
        return new BankCard(approvedCardNumber, getNotValidMonth13(), getValidYear(), getEngHolder(), getValidCvc());
    }

    public static BankCard getNotValidMonthBankCard () {
        return new BankCard(approvedCardNumber, getNotValidMonth(), getCurrentYear(), getEngHolder(), getValidCvc());
    }

    public static BankCard getEmptyYearBankCard () {
        return new BankCard(approvedCardNumber, getValidMonth(), getEmptyYear(), getEngHolder(), getValidCvc());
    }

    public static BankCard getInvalidYearBankCard () {
        return new BankCard(approvedCardNumber, getValidMonth(), getInvalidYear(), getEngHolder(), getValidCvc());
    }

    public static BankCard getInvalidYear6BankCard () {
        return new BankCard(approvedCardNumber, getValidMonth(), getInvalidYear6(), getEngHolder(), getValidCvc());
    }

    public static BankCard getEmptyHolderBankCard () {
        return new BankCard(approvedCardNumber, getValidMonth(), getValidYear(), getEmptyHolder(), getValidCvc());
    }

    public static BankCard getInvalidCvcBankCard () {
        return new BankCard(approvedCardNumber, getValidMonth(), getValidYear(), getEngHolder(), getInvalidCvc());
    }

    public static BankCard getEmptyCvcBankCard () {
        return new BankCard(approvedCardNumber, getValidMonth(), getValidYear(), getEngHolder(), getEmptyCvc());
    }
}