# Дипломный проект по специализации "Тестировщик ПО".
Цель проекта - произвести автоматизацию тестирования комплексного сервиса, взаимодействующего с СУБД и API Банка. 
<br>В процессе автоматизации выявить дефекты, описать их в файле отчёта, описать возникшие в ходе работы отклонения от плана работы.</br>
- [План проекта](https://github.com/taggertt/diplom/blob/main/docs/plan.md)
- [Отчёт о тестировании](https://github.com/taggertt/diplom/blob/main/docs/report.md)
- [Отчёт об автоматизации](https://github.com/taggertt/diplom/blob/main/docs/summary.md)

# Настройка запуска авто-тестов
1. Предустановленна Java: OpenJDK 11.
2. Установить Docker и Docker-compose.
3. Запустить в Docker контейнеры: MySQL, PostgreSQL, Node.Js.
   ```
   docker-compose up –d
   ```
5. Запустить Запустите jar-приложение (SUT), используя команду в терминале: "/artifacts java -jar aqa-shop.jar"

# Запуск Авто-тестов
Запуск авто-тестов производится командой ./gradlew test (./gradlew test -D selenide.headless=true для проведения в фоновом режиме).

# Получения отчёта Allure
1. Выполнить команду gradlew allureReport
2. Запустить авто-тесты ./gradlew test
3. Для просмотра отчёта Allure необходимо выполнить команду gradlew AllureServe и дождаться открытия отчёта в браузере.
