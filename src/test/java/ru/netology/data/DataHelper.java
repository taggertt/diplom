package ru.netology.data;

import com.github.javafaker.Faker;
import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.*;
import java.util.Calendar;
import java.util.Locale;
import java.util.Properties;

public class DataHelper {

    // Создаем соединение с бд
    private static Connection connection;

    // Cоздаем две переменные для тестирования данных как на русском так и на английском языке
    private static final Faker faker_en = new Faker(new Locale("en-US"));
    private static final Faker faker_ru = new Faker(new Locale("ru-RU"));

    // Выполнение SQL запросов с помощью QueryRunner
    private static QueryRunner queryRunner = new QueryRunner();

    // Создаем переменный с данными по картам
    private static final String approvedCardNumber = "4444 4444 4444 4441";
    private static final String declinedCardNumber = "4444 4444 4444 4442";
    private static final String notExistCardNumber = "4444 4444 4444 4440";

    static {
        try (FileInputStream appPropertiesFile = new FileInputStream("./application.properties")) {
            Properties appProperties = new Properties();
            appProperties.load(appPropertiesFile);

            connection = DriverManager.getConnection(appProperties.getProperty("spring.datasource.url"),
                    appProperties.getProperty("spring.datasource.username"),
                    appProperties.getProperty("spring.datasource.password")
            );
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }
    }

    // Генерация данных на английском языке
    private static BankCard getBaseCard_en(String cardNumber) {
        String month = String.format("%2d", faker_en.number().numberBetween(1, 12)).replace(" ", "0");
        int numberYear = Calendar.getInstance().get(Calendar.YEAR);
        String year = Integer.toString(faker_en.number().numberBetween(numberYear + 1, numberYear + 5)).substring(2);
        String holder = faker_en.name().name();
        String cvc = faker_en.number().digits(3);
        return new BankCard(cardNumber, month, year, holder, cvc);
    }

    // Получить одобрение поездки
    public static BankCard getApprovalCard_en() {
        return getBaseCard_en(approvedCardNumber);
    }

    // Не одобрение поездки
    public static BankCard getNotApprovalCard_en() {
        return getBaseCard_en(declinedCardNumber);
    }

    // Неверный номер карты
    public static BankCard getInvalidNumberCard_en() {
        return getBaseCard_en("q" + approvedCardNumber.substring(1));
    }

    // Не существующий номер карты
    public static BankCard getANonExistentNumberCard_en() {
        return getBaseCard_en(notExistCardNumber);
    }

    // Пустой номер карты
    public static BankCard getAnEmptyNumberCard_en() {
        return getBaseCard_en("");
    }

    // Генерация данных на русском языке
    private static BankCard getBaseCard_ru(String cardNumber) {
        String month = String.format("%2d", faker_en.number().numberBetween(1, 12)).replace(" ", "0");
        int numberYear = Calendar.getInstance().get(Calendar.YEAR);
        String year = Integer.toString(faker_en.number().numberBetween(numberYear + 1, numberYear + 5)).substring(2);
        String holder = faker_ru.name().name();
        String cvc = faker_en.number().digits(3);
        return new BankCard(cardNumber, month, year, holder, cvc);
    }

    // Получаем одобрение поездки
    public static BankCard getApprovalCard_ru() {
        return getBaseCard_ru(approvedCardNumber);
    }

    public static BankCard getEmptyCard() {
        return new BankCard();
    }

    // Получаем неверно указан формат задания месяца
    public static BankCard getAnInvalidCardMonth13() {
        BankCard card = getBaseCard_en(approvedCardNumber);
        card.setExpireMonth(("13"));
        return card;
    }

    // Получаем неверно указан срок действия карты
    public static BankCard getAnInvalidCardFormat() {
        BankCard card = getBaseCard_en(approvedCardNumber);
        card.setExpireMonth(Integer.toString(faker_en.number().numberBetween(1, 7)));
        card.setExpireYear(Integer.toString(24));
        return card;
    }

    // Получаем пустое поле месяц
    public static BankCard GettingAnEmptyCard() {
        BankCard card = getBaseCard_en(approvedCardNumber);
        card.setExpireMonth("");
        return card;
    }

    // Получаем неверно указан срок действия карты

    public static BankCard gettingTheWrongYear() {
        BankCard card = getBaseCard_en(approvedCardNumber);
        int numberYear = Calendar.getInstance().get(Calendar.YEAR) % 1000;
        card.setExpireYear(String.valueOf(numberYear-1));
        return card;
    }
    public static BankCard gettingTheWrongYear6() {
        BankCard card = getBaseCard_en(approvedCardNumber);
        int numberYear = Calendar.getInstance().get(Calendar.YEAR) % 1000;
        card.setExpireYear(String.valueOf(numberYear+6));
        return card;
    }

    // Получаем пустое поле год
    public static BankCard gettingAnEmptyYear() {
        BankCard card = getBaseCard_en(approvedCardNumber);
        card.setExpireYear("");
        return card;
    }

    // Проверяем ввод спецсимволов в поле владелец
    public static BankCard gettingSpecialCharacters() {
        BankCard card = getBaseCard_en(approvedCardNumber);
        FakeValuesService fakeValuesService = new FakeValuesService(
                new Locale("en-US"), new RandomService());
        card.setHolderName(fakeValuesService.regexify("[\\-\\=\\+\\<\\>\\!\\@\\#\\$\\%\\^\\&\\*\\1\\2\\3\\4\\5\\6\\{\\}]{8,15}"));
        return card;
    }

    // Получаем пустое поле владелец
    public static BankCard gettingAnEmptyHalfCardHolder() {
        BankCard card = getBaseCard_en(approvedCardNumber);
        card.setHolderName("");
        return card;
    }

    // Получаем неверный формат cvc кода
    public static BankCard GettingInvalidCvcCode() {
        BankCard card = getBaseCard_en(approvedCardNumber);
        card.setCvcCode(faker_en.number().digits(2));
        return card;
    }

    // Получаем пустое поле cvc код
    public static BankCard gettingAnEmptyCvcCode() {
        BankCard card = getBaseCard_en(approvedCardNumber);
        card.setCvcCode("");
        return card;
    }


    public static OrderEntity getLastOrder() throws SQLException {
        String orderSQL = "select * from order_entity o " +
                "where o.created = (" +
                "select max(created) from order_entity);";
        OrderEntity order = queryRunner.query(connection, orderSQL, new BeanHandler<>(OrderEntity.class));
        if (order == null) {
            order = new OrderEntity();
        }
        return order;
    }

    public static PaymentEntity getOrderPayment(OrderEntity order) throws SQLException {
        String paymentSQL = "select * from payment_entity p " +
                "where p.transaction_id = ?;";
        PaymentEntity payment = queryRunner.query(connection, paymentSQL, new BeanHandler<>(PaymentEntity.class), order.getPayment_id());
        if (payment == null) {
            payment = new PaymentEntity();
        }
        return payment;
    }

    public static CreditRequestEntity getOrderCreditRegistry(OrderEntity order) throws SQLException {
        String creditSQL = "select * from credit_request_entity c " +
                "where c.bank_id = ?;";
        CreditRequestEntity credit = queryRunner.query(connection, creditSQL, new BeanHandler<>(CreditRequestEntity.class), order.getCredit_id());
        if (credit == null) {
            credit = new CreditRequestEntity();
        }
        return credit;
    }
}
