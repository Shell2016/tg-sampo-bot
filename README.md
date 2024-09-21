# tg-sampo-bot

Телеграм бот для записи на коллективные сампо(для соблюдения баланса партнеров и партнерш на танцевальных тренировках).

Telegram API осуществляющий непосредственное общение с телеграммом вынесен в отдельный микросервис.
Здесь обычный REST сервис с одним эндпоинтом.

Использованы:  
- Gradle
- Spring Boot Data Jpa
- PostgreSQL
- Redis
- Liquibase
- Docker
- Testcontainers
- Loki
- Prometheus
- Grafana
- OpenFeign

Настроен деплой через docker-hub и Github Actions.

https://t.me/SampoRegistrationBot

Разный интерфейс и функционал для пользователей с ролями USER и ADMIN:

User interface

![User interface](/images/user.png)

Admin interface

![User interface](/images/admin.png)

Регистрация и списки

![User interface](/images/signup.png)
