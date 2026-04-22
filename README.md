DaskaTraz - Платформа за управление на академичния успех
DaskaTraz е съвременна софтуерна система, насочена към дигитализацията на образователния процес. Проектът решава проблемите с разпределянето на времето на учащите чрез автоматизирано проследяване на успеха, централизирано планиране на ангажиментите и визуална обратна връзка чрез интерактивни графики.

Целева аудитория
* Ученици: Инструмент за следене на личния прогрес, предстоящи изпити и текущи оценки чрез визуален статистически анализ (графики) и академичен календар.

* Учители: Интуитивна платформа за дигитално въвеждане на оценки и мониторинг на успеха на класовете в реално време, намаляваща административната тежест.

Технологичен стек
* Сървърна част (Backend): Java 17+, Spring Boot, Spring Security (JWT), Spring Data JPA, Hibernate.

* Клиентска част (Frontend): Vue.js, Chart.js (за графики), FullCalendar.js (за събития).

* База данни: PostgreSQL (с миграции и seed скриптове).

* Архитектура: RESTful API, Layered Architecture, Stateless.

Архитектура на системата
Системата е изградена на принципа на Слоестата архитектура (Layered Architecture), разделена на три основни нива:

1. Presentation Layer (Controllers): Входна точка за REST заявките. Използва унифициран модел ApiResponse<T> за отговори.

2. Service Layer (Business Logic): Съдържа логиката на приложението. Интензивно използва Java Stream API за филтриране, мапване и агрегация на данни (напр. пресмятане на среден успех в реално време).

3. Persistence Layer (Repositories): Управлява връзката с базата данни чрез JPA Entities.


Сигурност

* Комуникацията е Stateless.

* Автентикацията и оторизацията се управляват чрез JSON Web Tokens (JWT).

* Паролите се криптират чрез BCryptPasswordEncoder.

Структура на проекта
DaskaTraz/
│
├── vue-frontend/                 # Клиентска част (Vue.js)
│   ├── src/
│   │   ├── api/axios.js          # API комуникация
│   │   ├── router/index.js       # Маршрутизация
│   │   └── views/                # Vue компоненти (Login, Register, Dashboards)
│
├── ElDnevniko (Backend)/         # Сървърна част (Spring Boot)
│   ├── src/main/java/com/example/ElDnevniko/
│   │   ├── config/               # Security, JWT, Beans
│   │   ├── controllers/          # REST Endpoints
│   │   ├── mappers/              # DTO <-> Entity мапинг (MapStruct)
│   │   ├── repositories/         # Spring Data JPA интерфейси
│   │   ├── exceptions/           # Глобална обработка на грешки (GlobalExceptionHandler)
│   │   ├── services/             # Бизнес логика и Schedulers
│   │   └── domain/               # Entities и DTOs
│
└── database/                     # База данни
    ├── migrations/
    ├── schema.sql                # Структура на таблиците
    └── seed.sql                  # Първоначални данни


Бизнес логика (Services)
Приложението разполага с три основни услуги (Services), които управляват бизнес правилата:
1. AuthService
Управлява сложния процес на регистрация и жизнения цикъл на потребителя.
* Създаване на акаунти, избор на роли (Ученик/Учител) и свързване с конкретни класове/предмети.

* Имплементира UserDetailsService за интеграция със Spring Security.

* Изпращане и валидиране на имейли с 6-цифрени кодове чрез JavaMailSender.

* Автоматично изчисляване на номерата в клас по азбучен ред при регистрация на нов ученик.

2. TeacherService
Капсулира логиката на електронния дневник за учителите.

* Строга валидация за достъп (validateTeacherAuthority) – учител има достъп само до оценките на учениците, на които реално преподава.

* Пресмятане на среден успех на ниво ученик и на ниво клас.

* Добавяне и редактиране на оценки.


3. StudentService
Управлява профила на ученика.

* Генерира точки (GradeGraphPointDto) за визуализиране на прогреса на успеха във времето.

* Автоматизирана система за известия: При въвеждане на слаба оценка (под 3.00), системата автоматично изпраща предупредителен имейл на ученика.

Автоматизирани задачи (Schedulers)
* DatabaseCleanup: Всяка неделя изчиства базата данни от невалидирани (изоставени) потребителски профили, по-стари от една седмица.


Клиентска част (Frontend Views)
* Login / Register / RegisterRole / Student /Teacher: Управление на достъпа.

* EmailVerify: Екран за въвеждане на код за потвърждение на имейл.

* StudentDashboard: Показва изучаваните предмети, оценките и интерактивна графика за прогреса на ученика.

* TeacherDashboard: Показва списък с предмети/класове, списък с ученици, техния среден успех и интерфейс за нанасяне/корекция на оценки.

База данни (PostgreSQL)
Основната таблица в системата е users (име, имейл, хеширана парола, роля, статус). Системата поддържа:

* email_verifications за управление на токени.

* classes, students, teachers, subjects за управление на академичната структура.

* grades за съхранение на конкретните оценки с timestamps.

* Връзката между учители, предмети и класове (Мany-to-Many) се реализира чрез свързващата таблица teachers_subjects (TeacherSubjectClassEntity), която дефинира точно кой учител по кой предмет преподава на даден клас.


API Endpoints (Обобщение)
Всички отговори следват стандарта: {"success": true/false, "data": {...}, "message": "..."}.

Authentication (/api/auth)

* POST /register, /choose-role, /register-student, /register-teacher

* POST /send-email-verification, /verify, /login

* GET /classes, /subjects

Учителски панел (/api/teacher) - Изисква ROLE_TEACHER

* GET /subject, /subjects/{id}/classes, /subjects/{subId}/classes/{classId}

* POST /grades

* PATCH /grades/{gradeId}

Ученически панел (/api/student) - Изисква ROLE_STUDENT

* GET /grades, /subject

* GET /{subjectId}/gradeGraph