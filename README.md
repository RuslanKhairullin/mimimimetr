Тестовое задание "Мимимиметр"

Предисловие: 

В фильме "Социальная сеть" есть такой эпизод: молодой Марк Цукерберг написал приложение, в котором можно было 
проголосовать за одну из двух предложенных студенток колледжа, кликнув по её фотографии. После первого выбора 
показывалась следующая пара девчонок. И так, пока не закончатся все пары. Пары подбираются случайно, но нет 
повторений пар. Когда пары закончатся пользователю показывается топ студенток, собранный из голосов всех 
пользователей. 

Приложение основано на вышесказанном, но вместо "девчонок" нам предстоит выбрать топ котиков) 

Написан на Java 17 с использованием Spring Boot 2.7.5 (web, security, data-jpa, telegramApi)

# Сборка приложения

# 1. Собираем приложение
```shell
./gradlew clean build
```

# 2. Запуск приложения 

Заполняем логины/пароли/токены в build/resources/main/application.properties

```shell
java -jar /путь/до/mimimimetr-версия.jar \
 --spring.config.location=file:/путь/до/application.properties
```

# 3. Особенности приложения

Первоначальная инициализация котиков происходит из папки images в resources. Каждому котику дается имя исходя из
названия картинки. 

Для запуска голосования, необходимо кликнуть на "Старт", далее выбор "лучшего" котика происходит путем клика по одному 
из предложенных имён котиков. Голосования будет идти до тех пор пока не пройдет цикл "каждый с каждым". В конце голосования
будет предложено кликнуть по кнопочке "ТОП" после которого будет выведен список топа котиков :) 


