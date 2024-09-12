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
   
4. Запустить jar-приложение (SUT) используя команду в терминале:
 - для MySQL  

```
java "-Dspring.datasource.url=jdbc:mysql://localhost:3306/app" -jar aqa-shop.jar
```
 - для PostgreSQL

```
java "-Dspring.datasource.url=jdbc:postgresql://localhost:5432/app" -jar aqa-shop.jar
```
# Запуск Авто-тестов в Терминале
 - для MySQL
 
 ```
./gradlew clean test "-Ddb.url=jdbc:mysql://localhost:3306/app" -D selenide.headless=true
```
- для PostgreSQL

```
./gradlew clean test "-Ddb.url=jdbc:postgresql://localhost:5432/app" -D selenide.headless=true
```

# Получения отчёта Allure
1. Выполнить команду ./gradlew allureReport --clean
2. Для просмотра отчёта Allure необходимо выполнить команду gradlew AllureServe и дождаться открытия отчёта в браузере.
