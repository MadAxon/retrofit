## Выбранный инструментарий
Antigravity + gemini flash 3.8

## Использованные промпты
**Сессия формирования инструкций**
> [/grill-me](slashCommand;grill-me) Проанализируй проект, необходимо определить структуру инструкции и добавить инструкции в проект так, чтобы любой инженер и любой AI-агент одинаково корректно работали с кодовой базой.
---
> **Какой архитектурный подход к структуре инструкций предпочтителен для кодовой базы Retrofit?**
>> (Recommended) Гибридная модульная структура: единое ядро правил в AGENTS.md (с детальными разделами в docs/instructions/), адаптеры для ведущих AI-агентов (CLAUDE.md, .cursorrules / .cursor/rules, .github/copilot-instructions.md) и синхронизированный CONTRIBUTING.md для разработчиков.

> **Как поступить с существующим разделом 'No Generative Tools' в CONTRIBUTING.md?**
>> (Recommended) Переработать раздел об AI в CONTRIBUTING.md: зафиксировать культуру ответственного использования AI-ассистентов в форке (строгая валидация тестами, форматирование Spotless, ревью человеком), с предупреждением о строгой политике апстрима Square при отправке PR в официальный репозиторий.

> **На каком языке предпочтительно составить инструкции и документацию в репозитории?**
>> (Recommended) На английском языке (English) — международный стандарт для open-source, репозиториев Retrofit и максимальной совместимости со всеми LLM и AI-инструментами.

> **Какой набор специализированных разделов сформировать в директории docs/instructions/?**
>> (Recommended) Полный модульный набор (5 документов): 1) Architecture & Modules, 2) Environment & Build Commands, 3) Code Style & Static Analysis (Spotless, ErrorProne, AnimalSniffer), 4) Testing Guidelines (MockWebServer, Truth), 5) Agent & Git Workflow (trunk, atomic commits, verification protocol).

> **Как оформить адаптеры для специфичных AI-инструментов (CLAUDE.md, Cursor, Copilot)?**
>> (Recommended) Высокоэффективные fast-path адаптеры: содержат ключевые команды в один клик (build, test, spotlessApply), критические ограничения (Java 8 / Android 21, branch trunk) и ссылки на AGENTS.md и docs/instructions/ для детальных правил. Поддержка как .cursorrules, так и современных .cursor/rules/*.mdc.

> **Стоит ли добавить ссылку на новые инструкции для разработчиков в основной README.md?**
>> (Recommended) Добавить компактный раздел 'Development & AI Guidelines' в конец README.md с ссылками на CONTRIBUTING.md, AGENTS.md и docs/instructions/.
---
---
Далее схема
> Мой промпт
>> Ответ агента
---
>> В рамках выполнения задачи и прохождения интервью `/grill-me` был проведён детальный анализ кодовой базы **Retrofit** (`MadAxon/retrofit`), определена и развернута **гибридная модульная структура инструкций**, обеспечивающая одинаково точную, согласованную и безопасную работу как инженеров-разработчиков, так и любых AI-агентов (Cursor, Claude Code, GitHub Copilot, Gemini/Antigravity и др.).
>> ---
>> ### Архитектура созданной системы инструкций
>> Система построена по многоуровневому принципу:
```
                      ┌────────────────────────┐
                      │       README.md        │  (Точка входа проекта)
                      └───────────┬────────────┘
                                  │
          ┌───────────────────────┴───────────────────────┐
          ▼                                               ▼
┌──────────────────┐                            ┌───────────────────┐
│ CONTRIBUTING.md  │ (Для инженеров)            │     AGENTS.md     │ (Единое ядро для всех AI)
└─────────┬────────┘                            └─────────┬─────────┘
          │                                               │
          │         ┌───────────────────────────┐         │
          ├────────►│    docs/instructions/     │◄────────┤
          │         │ 01-architecture-and-...   │         │
          │         │ 02-environment-and-...    │         │
          │         │ 03-code-style-and-...     │         │
          │         │ 04-testing-guide.md       │         │
          │         │ 05-agent-and-git-...      │         │
          │         └───────────────────────────┘         │
          │                                               │
          ▼                                               ▼
  Инженеры команды                      AI-адаптеры с быстрым контекстом:
                                        ├── CLAUDE.md
                                        ├── .cursorrules & .cursor/rules/retrofit.mdc
                                        └── .github/copilot-instructions.md
```
>> ---
>> ### Список добавленных и обновлённых файлов
>> #### 1. Единое универсальное ядро инструкций
>> * [`AGENTS.md`](AGENTS.md) — центральный документ открытого стандарта для AI-агентов и инженеров. Содержит быстрый справочник CLI-команд (сборка, тесты, форматирование Spotless, статический анализ ErrorProne), карту монорепозитория, критические ограничения платформы и протокол верификации изменений.
>> #### 2. Модульная база знаний ([`docs/instructions/`](docs/instructions))
>> * [`01-architecture-and-modules.md`](docs/instructions/01-architecture-and-modules.md) — архитектура модулей (`retrofit`, `retrofit-adapters`, `retrofit-converters`, `retrofit-mock`, `website`), конвейер динамических прокси (`ServiceMethod`, `RequestFactory`, `Converter`, `CallAdapter`), корутины Kotlin и Multi-Release JAR (`java14`, `java16`).
>> * [`02-environment-and-build.md`](docs/instructions/02-environment-and-build.md) — требования к окружению (JDK 17+, таргетинг на Java 1.8), полный список команд Gradle для сборки, выборочного тестирования модулей, документации и решения сетевых таймаутов.
>> * [`03-code-style-and-quality.md`](docs/instructions/03-code-style-and-quality.md) — стандарты форматирования Spotless (`google-java-format`, `ktfmt`), блокирующие правила ErrorProne (запрет wildcard-импортов, обязательный `@Override`, строгие правила `fail()` в тестах) и проверка совместимости API через Animal Sniffer (Java 1.8 и Android API 21).
>> * [`04-testing-guide.md`](docs/instructions/04-testing-guide.md) — руководство по тестированию с использованием JUnit 4, Google Truth (`assertThat(...)`) и OkHttp `MockWebServer`, паттерны проверки адаптеров и конвертеров, обязательное правило создания reproducer-тестов для багов.
>> * [`05-agent-and-git-workflow.md`](docs/instructions/05-agent-and-git-workflow.md) — соглашения по ветвлению (основная ветка `trunk`, а не `main`/`master`), правила оформления коммитов и пошаговый протокол верификации изменений перед коммитом/PR.
>> #### 3. Адаптеры для конкретных AI-ассистентов
>> * [`CLAUDE.md`](CLAUDE.md) — контекстный адаптер для Claude Code с быстрым набором команд сборки, проверок и ссылками на базу знаний.
>> * [`.cursorrules`](.cursorrules) — корневой конфигуратор правил для Cursor IDE.
>> * [`.cursor/rules/retrofit.mdc`](.cursor/rules/retrofit.mdc) — современный формат правил Cursor с автоматическим применением (`alwaysApply: true`) для файлов `*.java`, `*.kt`, `*.gradle`.
>> * [`.github/copilot-instructions.md`](.github/copilot-instructions.md) — системные инструкции для GitHub Copilot.
>> #### 4. Обновление документации репозитория
>> * [`CONTRIBUTING.md`](CONTRIBUTING.md) — исходный пункт о полном запрете генеративного ИИ заменен на сбалансированную политику ответственного использования AI-ассистентов с обязательным ревью человеком, тестами и проверками Spotless, а также предупреждением об ограничениях официального апстрим-репозитория Square.
>> * [`README.md`](README.md) — добавлен раздел *Development & Contributing* со ссылками на [`CONTRIBUTING.md`](CONTRIBUTING.md), [`AGENTS.md`](AGENTS.md) и [`docs/instructions/`](docs/instructions).
>> ---
>> ### Ключевые инварианты, зафиксированные для агентов и инженеров
>> 1. **Главная ветка**: Только `trunk` (не `main` и не `master`).
>> 2. **Совместимость рантайма**: Байткод строго Java 1.8 (`-source 1.8 -target 1.8`). Сигнатуры проверяются Animal Sniffer против Java 8 и Android API 21 (Lollipop). Запрещено использовать API Java 9+ в основном коде.
>> 3. **Форматирование кода**: Обязательный прогон `./gradlew spotlessApply` перед завершением любой задачи.
>> 4. **Статический анализ**: Ошибки ErrorProne (`WildcardImport`, `MissingOverride`, `MissingFail`, `UnusedVariable` и др.) имеют статус фатальных ошибок компиляции.
>> 5. **Тестирование**: Фиксация багов начинается с воспроизводящего теста на базе `MockWebServer` и Google Truth.
---
> Добавь в инструкцию пункт, который обязывает ai агента помечать каждый новый класс, создаваемым агентом, комментарием "Developed by BROgrammer"
>> Пункт об обязательной маркировке новых классов добавлен во все ключевые конфигурационные файлы и базы знаний проекта.
>> ### Формат маркировки
>> Каждый новый класс (на Java или Kotlin), создаваемый AI-агентом, обязан предваряться комментарием:
>> ```java
>> // Developed by BROgrammer
>> public final class ExampleService {
>>   // ...
>> }
>> ```
>> или в Kotlin:
>> ```kotlin
>> // Developed by BROgrammer
>> class ExampleService {
>>   // ...
>> }
>> ```
>> ---
>> ### Обновленные файлы инструкций
>> 1. [`AGENTS.md`](AGENTS.md#L42-L44)
>>   * Добавлен пункт 6 в раздел **Non-Negotiable Constraints & Invariants**:
     >>> `6. Mandatory Class Attribution for AI Agents: Every new class (Java or Kotlin) created by an AI agent must be marked with the comment // Developed by BROgrammer immediately above the class declaration.`
>> 2. [`docs/instructions/03-code-style-and-quality.md`](docs/instructions/03-code-style-and-quality.md#L70-L88)
>>   * Добавлен специализированный раздел **5. AI Agent Attribution Requirement** с примерами оформления для Java и Kotlin.
>> 3. [`docs/instructions/05-agent-and-git-workflow.md`](docs/instructions/05-agent-and-git-workflow.md#L76-L78)
>>   * Добавлено правило 6 в раздел **Strict Rules for AI Agents**.
>> 4. [`CLAUDE.md`](CLAUDE.md#L30-L32)
>>   * В раздел **Critical Architecture & Rules** добавлено явное требование по атрибуции классов для Claude Code.
>> 5. [`.cursorrules`](.cursorrules#L25-L27)
>>   * В раздел **Key Instructions** добавлен пункт 6 для агента Cursor.
>> 6. [`.cursor/rules/retrofit.mdc`](.cursor/rules/retrofit.mdc#L18-L20)
>>   * В раздел **Core Constraints** добавлено строгое правило `Mandatory Class Attribution`.
>> 7. [`.github/copilot-instructions.md`](.github/copilot-instructions.md#L26-L29)
>>   * В раздел **Key Constraints** добавлен пункт 6 для GitHub Copilot.
---
> Запуш изменения в репозиторий
>> ...
---
**Сессия реализации новой фичи**
> [/plan](slashCommand;plan) Реализовать поддержку Flow<T> в качестве возвращаемого типа при объявлении методов в интерфейсе api. То есть реализовать CallAdapter.Factory для Flow<T>
>> Подготовил детальный план реализации в артефакте [flow_call_adapter_plan.md](artifacts/flow_call_adapter_plan.md)
---
> Подтверждаю план
>> Поддержка `Flow<T>` в качестве возвращаемого типа успешно реализована и протестирована.
>> ### Основные результаты:
>> 1. **Новый модуль [`:retrofit-adapters:flow`](retrofit-adapters/flow)**:
>>   - Добавлен в [settings.gradle](settings.gradle).
>>   - Настроен [build.gradle](retrofit-adapters/flow/build.gradle) с зависимостями `projects.retrofit` и `libs.kotlinx.coroutines`.
>>   - Добавлены [README.md](retrofit-adapters/flow/README.md) и [gradle.properties](retrofit-adapters/flow/gradle.properties).
>> 2. **Реализация [`FlowCallAdapterFactory`](retrofit-adapters/flow/src/main/java/retrofit2/adapter/flow/FlowCallAdapterFactory.kt)**:
>>   - Поддерживает возвращаемые типы `Flow<T>`, `Flow<Response<T>>`, `Flow<Unit>` и `Flow<T?>`.
>>   - Обеспечивает семантику **Cold Stream**: сетевой запрос запускается только при вызове терминальных операций (`collect`), каждая подписка выполняет независимый запрос (`call.clone()`).
>>   - Поддерживает **кооперативную отмену**: при отмене корутины (`Job.cancel()`) OkHttp-вызов немедленно отменяется (`call.cancel()`).
>>   - Валидирует параметризацию типов (выбрасывает понятные `IllegalStateException` при объявлении raw `Flow` или raw `Flow<Response>`).
>>   - Все созданные классы содержат обязательный комментарий `// Developed by BROgrammer`.
>> 3. **Верификация**:
>>   - Написан полный набор тестов в [`FlowCallAdapterFactoryTest`](retrofit-adapters/flow/src/test/java/retrofit2/adapter/flow/FlowCallAdapterFactoryTest.kt) (13 тестов, все прошли успешно).
>>   - Пройдены проверки Spotless и linters (`:retrofit-adapters:flow:check`).
>>   - Пройдены регрессионные тесты `:retrofit:kotlin-test:test`.
>>Подробный отчет и сценарии тестирования зафиксированы в артефакте [walkthrough.md](artifacts/walkthrough.md).
---
> Просмотри новый код, проанализируй инструкции для ai агента, строго соблюдая эти инструкции внеси необходимые правки в новый код
>> Провел подробный аудит нового кода на строгое соответствие всем инструкциям для AI-агентов (`AGENTS.md` и ...руководствам в `docs/instructions/`) и внес необходимые правки:
>>### 1. Соблюдение правил статического анализа и стиля кода
>> - **ErrorProne `UnusedException`**: Во всех блоках `catch` в тестах (`FlowCallAdapterFactoryTest.kt`) переменные исключений переименованы в `expected` в соответствии с разделом 3 [`03-code-style-and-quality.md`](docs/instructions/03-code-style-and-quality.md) и [`04-testing-guide.md`](docs/instructions/04-testing-guide.md).
>> - **ErrorProne `MissingFail`**: Все тесты с проверкой выбрасываемых исключений строго завершают блок `try` вызовом `fail(...)`.
>> - **Запрет на Wildcard-импорты**: Проверено и подтверждено отсутствие wildcard-импортов (`.*`) во всех созданных файлах.
>> - **Обязательная атрибуция классов**: Все новые классы и интерфейсы (включая `FlowCallAdapterFactory`, `BodyFlowCallAdapter`, `ResponseFlowCallAdapter`, `FlowCallAdapterFactoryTest`, `Service`, `TestingParameterizedType`) снабжены комментарием `// Developed by BROgrammer` непосредственно перед объявлением.
>> ### 2. Улучшение типов и детерминированности тестов
>> - **Строгая типизация**: В [`FlowCallAdapterFactory.kt`](retrofit-adapters/flow/src/main/java/retrofit2/adapter/flow/FlowCallAdapterFactory.kt) сигнатура `BodyFlowCallAdapter` приведена к `CallAdapter<T, Flow<T>>` (вместо `Flow<T?>`), что гарантирует точное соответствие объявленному возвращаемому типу метода интерфейса.
>> - **Детерминированный тест отмены**: Тест `cancellationCancelsOkHttpCall` переписан без race condition: он ожидает попадания запроса в `client.dispatcher.runningCalls()`, отменяет корутину и проверяет `assertThat(okHttpCall.isCanceled()).isTrue()`.
>> ### 3. Актуализация документации проекта
>> - Обновлены [`AGENTS.md`](AGENTS.md) и [`01-architecture-and-modules.md`](docs/instructions/01-architecture-and-modules.md), куда добавлен модуль `:retrofit-adapters:flow` в общую карту монорепозитория.
>> ### 4. Верификация
>> - Выполнено форматирование Spotless: `./gradlew spotlessApply`.
>> - Пройдена полная проверка модуля: `./gradlew :retrofit-adapters:flow:check` (13 тестов, linters, spotless — **BUILD SUCCESSFUL**).
>> - Пройдены тесты ядра: `./gradlew :retrofit:kotlin-test:test` (**BUILD SUCCESSFUL**).
Результаты аудита и отчет обновлены в артефакте [walkthrough-1.md](artifacts/walkthrough-1.md).
---
> В FlowCallAdapterFactory есть код responseType.typeName, выполнится ли это выражение на android api 21?
>> **Нет, выражение `responseType.typeName` НЕ выполнится на Android API 21 и приведёт к аварийному завершению с `java.lang.NoSuchMethodError`.**
>> Отличное замечание! Это критически важный нюанс совместимости с Android:
>> ---
>> ### Почему это не работает на Android API 21
>> 1. **История метода `getTypeName()`**:
>>    - В стандартном JDK метод `java.lang.reflect.Type.getTypeName()` был добавлен в Java 8.
>>    - В Android API поддержка этого метода появилась только начиная с **API Level 26 (Android 8.0 Oreo)** / **API Level 28**.
>> 2. **Поведение на Android API 21–25**:
>>    - На версиях Android 5.0 (API 21) – 7.1 (API 25) в интерфейсе `java.lang.reflect.Type` этого метода просто нет.
>>    - При попытке вызова Kotlin-свойства `.typeName` виртуальная машина ART/Dalvik выбросит:
>>      ```text
>>      java.lang.NoSuchMethodError: No interface method getTypeName()Ljava/lang/String; in class Ljava/lang/reflect/Type;
>>      ```
>> 3. **Подтверждение через Animal Sniffer**:
>>    При запуске проверки по сигнатуре `android-api-level-21:5.0.1_r2` инструмент немедленно выдаёт ошибку:
>>    ```text
>>    [Undefined reference | android-api-level-21-5.0.1_r2] retrofit2.adapter.flow.(FlowCallAdapterFactory.kt)
>>      String java.lang.reflect.Type.getTypeName()
>>    ```
>> ---
>> ### Что было исправлено
>> 1. **В [`FlowCallAdapterFactory.kt`](retrofit-adapters/flow/src/main/java/retrofit2/adapter/flow/FlowCallAdapterFactory.kt)** небезопасный вызов заменен на полностью совместимый с Android API 21+:
>>    ```kotlin
>>    val isUnit =
>>      responseType == Unit::class.java ||
>>        (responseType is Class<*> && responseType.name == "kotlin.Unit")
>>    ```
>>    Метод `Class.getName()` (`Class.name`) поддерживается во всех версиях Android, начиная с API Level 1 (и Java 1.0).
>> 2. **В [`retrofit-adapters/flow/build.gradle`](retrofit-adapters/flow/build.gradle)** подключен плагин `java-library`:
>>    ```groovy
>>    apply plugin: 'java-library'
>>    apply plugin: 'org.jetbrains.kotlin.jvm'
>>    ```
>>    Благодаря этому для модуля `:retrofit-adapters:flow` автоматически активировалась проверка **Animal Sniffer** по сигнатурам `android-api-level-21:5.0.1_r2` и `java18:1.0`, настроенная в корневом `build.gradle`.
>> ---
>> ### Верификация
>> - Задача `:retrofit-adapters:flow:animalsnifferMain` завершилась успешно (**0 нарушений сигнатур**).
>> - Задача `:retrofit-adapters:flow:check` (Spotless, ErrorProne, AnimalSniffer, все 13 тестов) — **BUILD SUCCESSFUL**.
---
> Закрепи данное решение в инструкции, чтобы избежать в будущем подобных ошибок
>> Решение и правила предотвращения подобных ошибок зафиксированы в документации репозитория:
>> ### 1. Добавленные правила в [`AGENTS.md`](AGENTS.md)
>> В раздел **2. Non-Negotiable Constraints & Invariants** добавлены явные ограничения:
>> - **Ограничения Android API 21**: четко указано, что runtime Android 5.0 (API 21) не содержит ряда методов Java 8 (включая `java.lang.reflect.Type.getTypeName()`, `Method.isDefault()`, `java.time.*`, `Stream`, `Optional`).
>> - **Прямой запрет на `Type.getTypeName()` / `type.typeName`**: для получения имени типа строго предписано использовать `Class.getName()` либо `type.toString()`.
>> - **Обязательное применение плагина `java-library`**: все новые библиотеки и модули адаптеров/конвертеров (включая Kotlin-модули) обязаны применять плагин `java-library`, чтобы инструмент **Animal Sniffer** автоматически валидировал их байткод против сигнатур `android-api-level-21:5.0.1_r2` и `java18:1.0` при сборке (`./gradlew animalsnifferMain`).
>> ### 2. Расширенные инструкции в [`docs/instructions/03-code-style-and-quality.md`](docs/instructions/03-code-style-and-quality.md)
>> В раздел **4. API Compatibility: Animal Sniffer** внесено подробное описание:
>> - Таблица и перечень методов Java 8, отсутствующих в рантайме Android 21.
>> - Описание фатальной ошибки `NoSuchMethodError`, возникающей при вызове `Type.getTypeName()`.
>> - Рекомендуемые и безопасные альтернативы: `type instanceof Class ? ((Class<?>) type).getName() : type.toString()` или прямое сравнение `type == Target.class`.
>> - Команда верификации модуля: `./gradlew :<module>:animalsnifferMain`.
>> Все файлы документации проверены и отформатированы в соответствии со стандартами Spotless.
---
> В инструкции внесены изменения, какие правки можно внести в код?
>> В соответствии с обновленными инструкциями и требованиями проекта в новый код внесены следующие ключевые правки:
>> ---
>> ### 1. Добавлены Cancellation Guards (`AGENTS.md`, п. 7)
>> Согласно правилу:
>> _In any asynchronous callbacks (onFailure), always check `if (call.isCanceled)` return before resuming a coroutine or throwing errors._
>> В оба адаптера ([`BodyFlowCallAdapter`](retrofit-adapters/flow/src/main/java/retrofit2/adapter/flow/FlowCallAdapterFactory.kt#L72) и [`ResponseFlowCallAdapter`](retrofit-adapters/flow/src/main/java/retrofit2/adapter/flow/FlowCallAdapterFactory.kt#L133)) в методы обратного вызова `onFailure` и `onResponse` добавлена проверка:
>> ```kotlin
>> override fun onFailure(call: Call<T>, t: Throwable) {
>>   if (call.isCanceled) return
>>   continuation.resumeWithException(t)
>> }
>>
>> override fun onResponse(call: Call<T>, response: Response<T>) {
>>   if (call.isCanceled) return
>>   // ...
>> }
>> ```
>> **Зачем это нужно**: при отмене корутины OkHttp прерывает вызов и передает в `onFailure` ошибку `IOException("Canceled")`. Защита предотвращает попытку возобновить уже завершенную корутину ненужным сетевым исключением, позволяя механизму корутин отработать штатно через `CancellationException`.
>> ---
>> ### 2. Полная изоляция от `Type.getTypeName()` и безопасный хелпер проверки `Unit`
>> Вместо небезопасного вызова `responseType.typeName` логика вынесена в приватный хелпер:
>> ```kotlin
>> private fun isUnit(type: Type): Boolean {
>>   return type == Unit::class.java || (type is Class<*> && type.name == "kotlin.Unit")
>> }
>> ```
>> **Зачем это нужно**:
>> - `Class.getName()` (`Class.name`) доступен начиная с Android API 1 и Java 1.0.
>> - Исключается даже потенциальный вызов `Type.getTypeName()`, что гарантирует 100% совместимость с Android API Level 21+.
>> ---
>> ### 3. Автоматическая верификация через Animal Sniffer в `build.gradle`
>> В [`retrofit-adapters/flow/build.gradle`](retrofit-adapters/flow/build.gradle) добавлен плагин  `java-library`:
>> ```groovy
>> apply plugin: 'java-library'
>> apply plugin: 'org.jetbrains.kotlin.jvm'
>> ```
>> **Зачем это нужно**:
>> - Позволяет Gradle и корневому `build.gradle` автоматически подключать плагин `ru.vyarus.animalsniffer` к модулю `:retrofit-adapters:flow`.
>> - Любая будущая попытка использовать API, отсутствующий в `android-api-level-21:5.0.1_r2` или `java18:1.0`, будет мгновенно заблокирована компилятором при выполнении `./gradlew animalsnifferMain` и `./gradlew check`.
>> ---
>> ### Итоговая верификация
>> - `./gradlew :retrofit-adapters:flow:animalsnifferMain` — **0 нарушений сигнатур**.
>> - `./gradlew :retrofit-adapters:flow:check` — **BUILD SUCCESSFUL** (Spotless, ErrorProne, AnimalSniffer, все 13 тестов пройдены).
>> - `./gradlew :retrofit:kotlin-test:test` — **BUILD SUCCESSFUL** (регрессионные тесты ядра пройдены).
---
> Создай PR для этой задачи
>> ...

**Сессия рефакторинга объёмного файла**
> [/plan](slashCommand;plan) Отрефактори класс Utils, разбей логику на пакетно-приватные специализированные классы, строго соблюдай инструкции проекта
>> Подготовлен детальный план рефакторинга в артефакте: План рефакторинга класса [refactoring_plan_utils.md](artifacts/refactoring_plan_utils.md)
>> ### Ключевое решение для согласования:
>> * **Судьба класса [`retrofit2.Utils`](retrofit/src/main/java/retrofit2/Utils.java)**:
>>   * **Вариант 1 (Рекомендуемый)**: Сохранить класс `Utils` как пакетно-приватный делегирующий фасад (все его методы делегируют вызовы в `Types`, `Exceptions`, `OkHttpUtils`, `KotlinUtils`, `Annotations`), переведя все внутренние вызовы в библиотеке на новые классы. Это гарантирует 100% совместимость и отсутствие скрытых регрессий.
>>  * **Вариант 2**: Полностью удалить класс `Utils.java` после миграции всех вызовов.
>> Пожалуйста, ознакомьтесь с планом и нажмите **Proceed** для начала реализации или укажите предпочтительный вариант.
--
> Рассмотрим первый вариант, не переводи вызовы в библиотеке на новые классы, оставь для них Utils
>> Рефакторинг класса [`Utils`](retrofit/src/main/java/retrofit2/Utils.java) выполнен по выбранному первому варианту: логика вынесена в специализированные пакетно-приватные классы, а `Utils` сохранён как делегирующий фасад, благодаря чему все существующие вызовы в библиотеке продолжают работать без изменений.
>> ---
>> ### 1. Созданные специализированные классы (пакет `retrofit2`)
>> Все новые классы объявлены пакетно-приватными (`final class`), неинстанцируемыми и содержат обязательную атрибуцию `// Developed by BROgrammer`:
>> * [`Types.java`](retrofit/src/main/java/retrofit2/Types.java)
>>   * Рефлексия generic-типов (`getRawType`, `resolve`, `getSupertype`, `getParameterUpperBound`, `getParameterLowerBound`, `hasUnresolvableType`, `equals`, `checkNotPrimitive`).
>>   * Безопасное форматирование строкового представления типов `typeToString` без использования `Type.getTypeName()` (для совместимости с Android API 21).
>>   * Реализации `ParameterizedTypeImpl`, `GenericArrayTypeImpl`, `WildcardTypeImpl` с обязательными аннотациями `@Override`.
>> * [`Exceptions.java`](retrofit/src/main/java/retrofit2/Exceptions.java)
>>   * Фабричные методы информативных исключений `methodError` и `parameterError`.
>>   * Метод проверки фатальных ошибок JVM `throwIfFatal` (`VirtualMachineError`, `ThreadDeath`, `LinkageError`).
>> * [`OkHttpUtils.java`](retrofit/src/main/java/retrofit2/OkHttpUtils.java)
>>   * Буферизация OkHttp `ResponseBody` в память (`buffer`).
>> * [`KotlinUtils.java`](retrofit/src/main/java/retrofit2/KotlinUtils.java)
>>   * Безопасная runtime-проверка типа `kotlin.Unit` (`isUnit`) без сбоев `NoClassDefFoundError` при отсутствии Kotlin в classpath.
>> * [`Annotations.java`](retrofit/src/main/java/retrofit2/Annotations.java)
>>   * Поиск аннотаций в массивах (`isAnnotationPresent`).
>> ---
>> ### 2. Рефакторинг [`Utils.java`](retrofit/src/main/java/retrofit2/Utils.java)
>> Класс [`Utils`](retrofit/src/main/java/retrofit2/Utils.java) сокращён с ~560 строк до компактного фасада (~100 строк):
>> * Все методы перенаправляют вызовы в соответствующие специализированные классы (`Types`, `Exceptions`, `OkHttpUtils`, `KotlinUtils`, `Annotations`).
>> * `Utils.ParameterizedTypeImpl` наследует `Types.ParameterizedTypeImpl` для прозрачной совместимости с существующим инстанцированием в [`HttpServiceMethod`](retrofit/src/main/java/retrofit2/HttpServiceMethod.java).
>> * Места вызовов в библиотеке ([`RequestFactory`](retrofit/src/main/java/retrofit2/RequestFactory.java), [`OkHttpCall`](retrofit/src/main/java/retrofit2/OkHttpCall.java), [`ParameterHandler`](retrofit/src/main/java/retrofit2/ParameterHandler.java) и др.) остались нетронутыми.
>> ---
>> ### 3. Модульные тесты (`retrofit/java-test`)
>> Для всех новых специализированных классов добавлены изолированные тесты с атрибуцией `// Developed by BROgrammer`:
>> * [`TypesTest.java`](retrofit/java-test/src/test/java/retrofit2/TypesTest.java)
>> * [`ExceptionsTest.java`](retrofit/java-test/src/test/java/retrofit2/ExceptionsTest.java)
>> * [`OkHttpUtilsTest.java`](retrofit/java-test/src/test/java/retrofit2/OkHttpUtilsTest.java)
>> * [`KotlinUtilsTest.java`](retrofit/java-test/src/test/java/retrofit2/KotlinUtilsTest.java)
>> * [`AnnotationsTest.java`](retrofit/java-test/src/test/java/retrofit2/AnnotationsTest.java)
>> ---
>> ### 4. Верификация соблюдения инструкций проекта ([AGENTS.md](AGENTS.md))
>> 1. **Атрибуция ИИ**: Комментарий `// Developed by BROgrammer` добавлен ко всем созданным классам и вложенным типам.
>> 2. **Совместимость с Java 8 и Android API 21**: Проверена утилитой Animal Sniffer (`./gradlew :retrofit:animalsnifferMain`) — успешно, без предупреждений и ошибок.
>> 3. **Статический анализ ErrorProne**: Отсутствуют wildcard-импорты, добавлены все `@Override`, корректно обработаны все исключения.
>> 4. **Форматирование Spotless**: Код отформатирован с помощью Google Java Format (`./gradlew spotlessApply`), проверка `spotlessCheck` завершена без замечаний.
>> 5. **Мульти-JDK тестирование**: Все тесты успешно выполнены на JDK 8, 11, 17 и 25.
---
> Нет теста для метода getParameterLowerBound
В тесте parameterBounds проверяется только String и Integer, хотя в классе types есть проверка на WildcardType
>> В класс тестов [`TypesTest.java`](retrofit/java-test/src/test/java/retrofit2/TypesTest.java) добавлены детальные проверки для `getParameterUpperBound` и `getParameterLowerBound`:
>> 1. **`getParameterUpperBound`**:
>>   * **`getParameterUpperBound_plainType`**: извлечение границ для обычных типов (`Map<String, Integer>`).
>>    * **`getParameterUpperBound_wildcardType`**: извлечение верхней границы для `WildcardType` (`List<? extends CharSequence>` $\rightarrow$ `CharSequence.class`).
>>   * **`getParameterUpperBound_outOfBoundsThrows`**: валидация выхода индекса за допустимый диапазон (индекс `>= length` и `< 0`).
>> 2. **`getParameterLowerBound`**:
>>   * **`getParameterLowerBound_plainType`**: извлечение нижней границы для обычных типов (`Map<String, Integer>`).
>>   * **`getParameterLowerBound_wildcardType`**: извлечение нижней границы для `WildcardType` (`List<? super String>` $\rightarrow$ `String.class`).
>> Все проверки протестированы как для прямого вызова [`Types`](retrofit/src/main/java/retrofit2/Types.java), так и через фасадный метод [`Utils`](retrofit/src/main/java/retrofit2/Utils.java). Код отформатирован с помощью Spotless, успешно скомпилирован и проверен на всех поддерживаемых версиях JDK (8, 11, 17, 25).
---
> Закрепим этот случай в инструкции, при описании инструкции используй общие понятия, не конкретизируй правило только для Types
>> Правило сформулировано в обобщенных терминах инженерии тестирования и внесено в основные инструкции проекта:
>> ### 1. Добавлено инвариантное правило №8 в [`AGENTS.md`](AGENTS.md) (Раздел 2: *Non-Negotiable Constraints & Invariants*):
>>> **8. Exhaustive Branch & Polymorphic Input Test Coverage**:
>>> * Unit tests must comprehensively cover all logical branches, conditional type inspections (e.g., polymorphic variants, subtype checks), and boundary conditions. Testing only standard/default inputs while leaving specialized branches untested is strictly forbidden.
>>> * When methods exist in complementary or symmetric pairs (e.g., upper/lower bounds, encode/decode, serialize/deserialize), unit tests must provide balanced, symmetric test coverage for both counterparts.
>> ### 2. Расширен раздел 6 в руководстве по тестированию [`docs/instructions/04-testing-guide.md`](docs/instructions/04-testing-guide.md):
>>> **6. Exhaustive Branch & Polymorphic Input Coverage**
>>> When writing or updating tests for any class, utility, converter, or adapter:
>>> 1. **Polymorphic & Conditional Branches**: If an algorithm or method contains branching logic based on runtime type inspection, polymorphic interfaces, or internal conditional checks (e.g. specialized subtype handling), every branch must have explicit, dedicated test cases. Verifying only standard or baseline variants while leaving specialized conditional branches untested is unacceptable.
>>> 2. **Symmetric & Complementary Method Parity**: Whenever methods exist in complementary pairs (e.g., upper/lower bounds, read/write, serialize/deserialize, forward/reverse mappings), unit tests must provide equal, symmetric test coverage for both counterparts.
>>> 3. **Boundary & Exceptional Conditions**: Always test out-of-bounds indices, boundary edge cases, nullability constraints, and invalid arguments with expected exceptions.
>> Форматирование проверено с помощью `./gradlew spotlessCheck` — все проверки пройдены успешно.
---
> Сделай rebase с ветки trunk и создай PR 
>> ...

**Реализация недостающих тестов в функционале самой библиотеки**
> [/plan](slashCommand;plan) В KotlinExtensionsTest существует только один тест. Напиши дополнительные тесты, покарывающие все кейсы использования coroutine
>> Я составил детальный план реализации тестов в артефакте:
[kotlin_extensions_coroutine_tests_plan.md](artifacts/kotlin_extensions_coroutine_tests_plan.md).
>> План охватывает все 4 расширения корутин из [KotlinExtensions.kt](retrofit/src/main/java/retrofit2/KotlinExtensions.kt) (`Call<T>.await()`, `Call<T?>.await()`, `Call<Unit>.await()`, `Call<T>.awaitResponse()`) для всех возможных исходов (успешный ответ с телом, пустой/null-ответ, HTTP-ошибки 404/500, сбои сети и отмена корутины).
>> Пожалуйста, ознакомьтесь с планом и подтвердите его выполнение (нажмите «Proceed» или отправьте подтверждение в чат).
---
> Подтверждаю план
>> Тесты для всех кейсов использования корутин в [KotlinExtensionsTest.kt](retrofit/kotlin-test/src/test/java/retrofit2/KotlinExtensionsTest.kt) успешно написаны и верифицированы.
>> Подробный отчёт представлен в артефакте [walkthrough-2.md](artifacts/walkthrough-2.md).
>> ### Краткий обзор добавленных тестов (всего 24 теста):
>> 1. **`Call<T : Any>.await()`**:
>>    - Успешный ответ (200 OK) с не-null телом.
>>    - Успешный ответ с null-телом (204 No Content) — проверка генерации `KotlinNullPointerException` с валидацией имени сервиса и метода в тексте ошибки.
>>    - HTTP-ошибки (404 Not Found, 500 Server Error) с проверкой `HttpException` и error body.
>>    - Сетевой сбой (`IOException`).
>>    - Отмена корутины с проверкой отмены `Call` и нижележащего OkHttp `Call`.
>>    - Пограничный случай: вызов на `Call` без тега `Invocation` при null-теле (выброс `NullPointerException`).
>> 2. **`Call<T?>.await()` (`awaitNullable`)**:
>>    - Успешный ответ с телом (200 OK).
>>    - Успешный ответ с null-телом (204 No Content) — возвращает `null` без исключения.
>>    - HTTP-ошибка (404 Not Found).
>>    - Сетевой сбой (`IOException`).
>>    - Отмена корутины.
>> 
>> 3. **`Call<Unit>.await()` (`awaitUnit`)**:
>>    - Успешное завершение с телом (200 OK).
>>    - Успешное завершение без тела (204 No Content).
>>    - HTTP-ошибка (404 Not Found).
>>    - Сетевой сбой (`IOException`).
>>    - Отмена корутины.
>> 
>> 4. **`Call<T>.awaitResponse()`**:
>>    - Ответ 200 OK (проверка `isSuccessful == true`, кода и тела).
>>    - Ответ 204 No Content (`isSuccessful == true`, `body == null`).
>>    - Ответы с ошибками 404 и 500 — проверка, что исключение `HttpException` **не** выбрасывается, а возвращается `Response<T>` с `isSuccessful == false` и доступным `errorBody()`.
>>    - Сетевой сбой (`IOException`).
>>    - Отмена корутины.
>> ### Результаты проверок:
>> - `./gradlew :retrofit:kotlin-test:test`: **24 теста успешно пройдены (0 failures, 0 errors, 0 skipped)**.
>> - `./gradlew :retrofit:kotlin-test:spotlessCheck`: **BUILD SUCCESSFUL** (соответствие стилю форматирования).
---
> Просмотри инструкции, проанализируй удовлетворяет ли полностью новый код этим инструкциям, внеси изменения в новый код при необходимости
>> Я провёл детальный анализ всех инструкций проекта ([AGENTS.md](AGENTS.md), [03-code-style-and-quality.md](docs/instructions/03-code-style-and-quality.md), [04-testing-guide.md](docs/instructions/04-testing-guide.md), [05-agent-and-git-workflow.md](docs/instructions/05-agent-and-git-workflow.md)).
>> ### 1. Результаты анализа соответствия инструкциям
>> В ходе ревизии были выявлены следующие моменты, требовавшие доработки в [KotlinExtensionsTest.kt](retrofit/kotlin-test/src/test/java/retrofit2/KotlinExtensionsTest.kt):
>> 1. **Библиотека ассертов (Truth вместо JUnit)**:
>>   - *Инструкция* ([04-testing-guide.md](retrofit/docs/instructions/04-testing-guide.md) §1): запрещает использование стандартных ассертов JUnit (`assertTrue`, `assertEquals`, `assertNotNull`) в пользу Google Truth (`assertThat`).
>>   - *Было*: использовались `assertTrue(call.isCanceled)` и `assertNotNull(...)`.
>>   - *Исправлено*: заменено на `assertThat(call.isCanceled).isTrue()`, `assertThat(rawCall.isCanceled()).isTrue()`, `assertThat(...).isNotNull()`. Стандартные JUnit-ассерты исключены из импортов (оставлен только `fail()`).
>> 2. **Правило ErrorProne `UnusedException`**:
>>   - *Инструкция* ([03-code-style-and-quality.md](docs/instructions/03-code-style-and-quality.md) §3, [04-testing-guide.md](docs/instructions/04-testing-guide.md) §3): перехватываемые исключения в тестах должны именоваться `expected` либо `ignored`.
>>   - *Было*: переменная исключения называлась `e`.
>>   - *Исправлено*: все блоки `catch` переименованы в `catch (expected: ...)`.
>> 3. **Симметричность и полнота покрытия (Symmetric Parity)**:
>>   - *Инструкция* ([AGENTS.md](AGENTS.md) §2.7, [04-testing-guide.md](docs/instructions/04-testing-guide.md) §6): требует симметричного тестирования комплементарных методов и веток.
>>   - *Добавлено*: 
>>    - Тесты для HTTP 500 для всех функций: `awaitNullableThrowsHttpException500`, `awaitUnitThrowsHttpException500`.
>>     - Проверка обработки метода `HEAD` для `Call<Unit>`: `awaitUnitHeadSuccess()`.
>> 4. **Обязательная атрибуция созданных классов AI**:
>>   - *Инструкция* ([AGENTS.md](AGENTS.md) §2.6): каждый новый класс/интерфейс должен предваряться комментарием `// Developed by BROgrammer`.
>>   - *Проверено*: интерфейс `Service` и класс `CallWithoutInvocation` помечены данным комментарием.
>> 5. **Форматирование кода (Spotless)**:
>>   - *Инструкция* ([AGENTS.md](AGENTS.md) §2.3): форматирование `ktfmt` Google Style.
>>   - *Выполнено*: применён `spotlessApply` и пройден `spotlessCheck`.
>> ---
>> ### 2. Результаты верификации
>> - **Запуск тестов модуля `:retrofit:kotlin-test`**:
>>   ```powershell
>>   .\gradlew.bat :retrofit:kotlin-test:test
>>   ```
>>  **Результат**: `BUILD SUCCESSFUL` — все **27 тестов** в [KotlinExtensionsTest.kt](retrofit/kotlin-test/src/test/java/retrofit2/KotlinExtensionsTest.kt) успешно пройдены (0 failures, 0 errors, 0 skipped).
>>- **Проверка форматирования**:
>>   ```powershell
>>   .\gradlew.bat :retrofit:kotlin-test:spotlessCheck
>>   ```
>>  **Результат**: `BUILD SUCCESSFUL` — код полностью удовлетворяет правилам форматирования репозитория.
>> Подробное описание изменений зафиксировано в артефакте [walkthrough-2.md](artifacts/walkthrough-2.md).
---
> assertThat(expected).isNotNull() всегда true, измени на более полезную проверку.
>> Все тривиальные проверки вида `assertThat(expected).isNotNull()` заменены на содержательные и точные проверки в [KotlinExtensionsTest.kt](retrofit/kotlin-test/src/test/java/retrofit2/KotlinExtensionsTest.kt):
>> ### Что было изменено:
>> 1. **Сетевые сбои (`IOException`)**:
>>   Для `awaitThrowsIoException`, `awaitNullableThrowsIoException`, `awaitUnitThrowsIoException` и `awaitResponseThrowsIoException` вместо `assertThat(expected).isNotNull()` теперь проверяется точная причина разрыва соединения от OkHttp:
>>    ```kotlin
>>    assertThat(expected).hasMessageThat().contains("unexpected end of stream")
>>    ```
>> 2. **Точная передача инстанса исключения из `onFailure`**:
>>   Добавлен тест `awaitPropagatesExactFailure`, подтверждающий, что исключение, переданное в `Callback.onFailure`, действительно разворачивается через продолжение корутины (с учётом восстановления стека корутин):
>>    ```kotlin
>>    val actual = expected.cause ?: expected
>>    assertThat(actual).isSameInstanceAs(failure)
>>    assertThat(expected).hasMessageThat().isEqualTo("Custom network error")
>>    ```
>> 3. **Случай отсутствия тега `Invocation` при null-теле**:
>>    В тесте `awaitThrowsNullPointerExceptionWhenInvocationMissing` теперь проверяется:
>>    ```kotlin
>>    assertThat(expected).isNotInstanceOf(KotlinNullPointerException::class.java)
>>    assertThat(expected.message).isNull()
>>    ```
>> Это гарантирует, что при отсутствии тега выбрасывается именно стандартный Kotlin/JVM null-assertion `NullPointerException` (с `message == null`), а не составленный Retrofit `KotlinNullPointerException`.
>> ---
>> ### Результаты проверки:
>> - **`./gradlew :retrofit:kotlin-test:test`**: **28 тестов успешно пройдено** (0 failures, 0 errors, 0 skipped).
>> - **`./gradlew :retrofit:kotlin-test:spotlessCheck`**: **BUILD SUCCESSFUL** (соответствие `ktfmt` Google style).
>> Подробный отчёт обновлен в артефакте [walkthrough-2.md](artifacts/walkthrough-2.md).
---
> В некоторых тестах ничего не проверяется, отсутствуют какие-либо assert
>> Тесты без ассертов выявлены и исправлены.
>> ### Что было обнаружено и исправлено:
>> В трёх тестах для методов с типом возвращаемого значения `Unit` (`awaitUnitSuccess`, `awaitUnit204Success` и `awaitUnitHeadSuccess`) ранее вызывался только `runBlocking { service.unit().await() }` без ассертов.
>> Для них добавлены явные проверки возвращаемого значения и отправленных сервером HTTP-запросов:
>> 1. **`awaitUnitSuccess`**:
>>    ```kotlin
>>    val result = runBlocking { service.unit().await() }
>>    assertThat(result).isEqualTo(Unit)
>> 
>>    val request = server.takeRequest()
>>    assertThat(request.path).isEqualTo("/")
>>    assertThat(request.method).isEqualTo("GET")
>>    ```
>> 2. **`awaitUnit204Success`**:
>>    ```kotlin
>>    val result = runBlocking { service.unit().await() }
>>    assertThat(result).isEqualTo(Unit)
>> 
>>    val request = server.takeRequest()
>>    assertThat(request.path).isEqualTo("/")
>>    assertThat(request.method).isEqualTo("GET")
>>    ```
>> 3. **`awaitUnitHeadSuccess`**:
>>    ```kotlin
>>    val result = runBlocking { service.headUnit().await() }
>>    assertThat(result).isEqualTo(Unit)
>> 
>>    val request = server.takeRequest()
>>    assertThat(request.path).isEqualTo("/")
>>    assertThat(request.method).isEqualTo("HEAD")
>>    ```
>> Теперь абсолютно каждый из **28 тестов** в [KotlinExtensionsTest.kt](retrofit/kotlin-test/src/test/java/retrofit2/KotlinExtensionsTest.kt) содержит явные проверки: возвращаемые значения, тип и сообщение исключений, флаги отмены либо параметры перехваченного сетевого запроса.
>> ### Результаты проверки:
>> - **`./gradlew :retrofit:kotlin-test:test`**: **28 тестов успешно пройдено** (0 failures, 0 errors, 0 skipped).
>> - **`./gradlew :retrofit:kotlin-test:spotlessCheck`**: **BUILD SUCCESSFUL**.
---
> Добавь проверки сценариев при HTTP 200 с null-body, а также граничные кейсы с отменённым Call и повторный вызов на уже выполненном Call.
>> Все запрошенные сценарии успешно реализованы и проверены в [KotlinExtensionsTest.kt](retrofit/kotlin-test/src/test/java/retrofit2/KotlinExtensionsTest.kt) (общее количество тестов выросло до **39**).
>> ### Реализованные сценарии:
>> 1. **HTTP 200 с null-body (когда конвертер десериализует тело в `null`)**:
>>   Добавлен вспомогательный конвертер `NullReturningConverterFactory`:
>>   - `awaitThrowsOnHttp200WithNullBody`: `Call<String>.await()` при HTTP 200 с null-телом генерирует `KotlinNullPointerException` с валидацией сообщения (`"Response from ... was null but response body type was declared as non-null"`).
>>   - `awaitNullableReturnsNullOnHttp200WithNullBody`: `Call<String?>.await()` возвращает `null` без исключения.
>>   - `awaitResponseReturns200WithNullBody`: `Call<String>.awaitResponse()` возвращает `Response` с `isSuccessful == true`, `code() == 200` и `body() == null`.
>> 2. **Граничные кейсы с заранее отменённым `Call` (`call.cancel()`)**:
>>   - `awaitThrowsOnAlreadyCanceledCall`: вызов `await()` на отменённом `Call` выбрасывает `IOException("Canceled")`.
>>   - `awaitNullableThrowsOnAlreadyCanceledCall`: вызов `await()` (`Call<T?>`) на отменённом `Call` выбрасывает `IOException("Canceled")`.
>>   - `awaitUnitThrowsOnAlreadyCanceledCall`: вызов `await()` (`Call<Unit>`) на отменённом `Call` выбрасывает `IOException("Canceled")`.
>>   - `awaitResponseThrowsOnAlreadyCanceledCall`: вызов `awaitResponse()` на отменённом `Call` выбрасывает `IOException("Canceled")`.
>> 3. **Повторный вызов на уже выполненном `Call`**:
>>   - `awaitThrowsOnAlreadyExecutedCall`: повторный вызов `await()` на уже выполненном через корутины вызове выбрасывает `IllegalStateException("Already executed.")`.
>>   - `awaitThrowsWhenCallAlreadyExecutedSynchronously`: вызов `await()` на `Call`, предварительно выполненном синхронно через `call.execute()`, выбрасывает `IllegalStateException("Already executed.")`.
>>   - `awaitResponseThrowsOnAlreadyExecutedCall`: повторный вызов `awaitResponse()` на выполненном `Call` выбрасывает `IllegalStateException("Already executed.")`.
>>   - `awaitClonedCallSucceedsAfterOriginalExecuted`: подтверждение стандартного контракта Retrofit — вызов `call.clone().await()` после первого выполнения отрабатывает успешно.
>> ---
>> ### Результаты верификации:
>> - **`./gradlew :retrofit:kotlin-test:test`**: **39 тестов успешно пройдено** (0 failures, 0 errors, 0 skipped).
>> - **`./gradlew :retrofit:kotlin-test:spotlessCheck`**: **BUILD SUCCESSFUL** (полное соответствие `ktfmt` Google style).
>> Подробный отчёт зафиксирован в артефакте [walkthrough-2.md](artifacts/walkthrough-2.md).
---
> Сделай rebase от ветки trunk и создай PR в репозитории
>> ...

## Выводы:
1. Чтобы модель чётко следовала инструкциям нужно написать об этом в промте, из примеров выше это заметно. Причём в сущности модель соблюдает инструкции, если её не просить, об этом свидетельствует наличие обязательной пометки комментарием любого нового класса, но в деталях она уже может быть невнимательна.
2. Модель пишет небольшую долю ложноположительных тестов. Здесь заведомо точно необходим второй агент, проверяющий написаный первым агентом код.
3. Модель уверенно справляется с дополнением инструкций, если при обсуждении вывелась ошибка/потенциальный баг/некорректный подход к написанию текстов или просто плохая практика в коде. Пока что не понятно как действовать с точки зрения разработчика: во-первых, изменения в инструкциях агента в рамках бизнесовой задачи - видится плохой практикой, во-вторых, каждый кейс обнаружения ошибки можно записать в инструкцию - но тогда инструкция будет сильно разрастаться, это влияет на восприятие промта моделями и на сложность поддержки инструкций в актуальном состоянии.
4. В одном из промтов модель ошиблась с реализацией кэлбека onFailure. Как оказалось, модель посчитала исходный код в одном из модулей единственным правильным источником правды, тогда как в реальности это был легаси код, применение которого было некорректно для нового функционала. Отдельно написав правило для подобного сценария и попросив модель ещё раз провести анализ, удалось добится исправления кода. Наличие второй модели, проверяющей код первой - выглядит ещё более убедительным пунктом.
5. Если обновить инструкцию и попросить модель проанализировать и поправить код, то модель попытается дополнительно отрефакторить изменяемый класс, код которого вообще не относится к дополненной инструкции. Нужно явно написать об этом в промпте.
6. Модель грамотно справляется с созданием PR и автоудалением коммитов, если случайно коммит из одного PR попал в другой, где совершенно другие изменения.
7. В описании МР модель генерит какие-то странные артефакты, не воспринимаемые github, пока не удалось выяснить причину.
8. Модель изменяет код по новой задаче с текущей ветки (где уже были работы по предыдущей задаче), в рабочем проекте стоит рассмотреть дополнение к инструкции, где бы оговаривалось создание отдельной ветки с main и последующего checkout перед непосредственной реализацией задачи.
9. Агент может спровоцировать антивирус на троянскую угрозу. Здесь либо добавить exe в исключения, либо дать инструкции модели, чего делать нельзя. 
10. Генерируемые моделью артефакты (директория artifacts) генерируются то на русском, то на английском языке. В данном случае это не принципиально, но нужно при необходимости добавлять эту инфу в промпт.
11. В заключении можно добавить, что в общем случае модель поддерживает архитектурные решения (новая фича - новый модуль), стиль кода, обязательство написания тестов на новый функционал, паттерны и рекомендации, но склонна к ошибкам, требующих внимания второго агента-ревьювера. Инструкции сгенерированы среднестатистические, составить идеальные инструкции с первого промта не получится, поэтому очевидно требуются дополнительные итераций для того, чтобы "причесать" правила под текущий проект.
