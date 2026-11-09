# Walkthrough: Поддержка `Flow<T>` в Retrofit (`:retrofit-adapters:flow`)

Реализована полноценная поддержка `kotlinx.coroutines.flow.Flow<T>` в качестве возвращаемого типа методов API-интерфейсов Retrofit в рамках отдельного модуля `:retrofit-adapters:flow`.
Код прошел полный аудит на строгое соответствие правилам `AGENTS.md` и документам в `docs/instructions/`, включая проверку совместимости с **Android API Level 21 (Lollipop 5.0)** и защитные проверки отмены (Cancellation Guards).

---

## 1. Выполненные доработки и соблюдение инструкций

### 1.1 Защита отмены асинхронных вызовов (Cancellation Guards)
- В соответствии с правилом 7 `AGENTS.md`:
  > *In any asynchronous callbacks (onFailure), always check `if (call.isCanceled)` return before resuming a coroutine or throwing errors.*
- В обоих адаптерах (`BodyFlowCallAdapter` и `ResponseFlowCallAdapter`) в методы `onFailure` и `onResponse` добавлены проверки:
  ```kotlin
  if (call.isCanceled) return
  ```
  Это предотвращает попытку возобновить уже отмененную корутину или пробросить лишний `IOException("Canceled")`.

### 1.2 Совместимость с Android API 21 и устранение `Type.getTypeName()`
- Метод `java.lang.reflect.Type.getTypeName()` появился в Android только начиная с **API level 26 (Android 8.0 Oreo)** / **API 28**.
- На устройствах с **Android API 21–25** вызов `responseType.typeName` приводит к фатальному `java.lang.NoSuchMethodError: No interface method getTypeName()Ljava/lang/String; in class Ljava/lang/reflect/Type;`.
- **Исправление**:
  - `responseType.typeName` заменено на приватный хелпер:
    ```kotlin
    private fun isUnit(type: Type): Boolean {
      return type == Unit::class.java || (type is Class<*> && type.name == "kotlin.Unit")
    }
    ```
    где `Class.getName()` поддерживается с Android API 1 / Java 1.0.
  - В `retrofit-adapters/flow/build.gradle` подключен плагин `java-library`, что автоматически активировало проверку **Animal Sniffer** по сигнатурам `android-api-level-21:5.0.1_r2` и `java18:1.0`.
  - Задача `:retrofit-adapters:flow:animalsnifferMain` подтвердила 0 нарушений сигнатур.

### 1.3 Соблюдение правил AI-агента (`AGENTS.md` и `03-code-style-and-quality.md`)
- **Атрибуция классов**: каждый объявленный класс и интерфейс строго помечен комментарием:
  ```kotlin
  // Developed by BROgrammer
  ```
- **ErrorProne `UnusedException` & `MissingFail`**:
  - Все переменные исключений в блоках `catch` названы `expected` в полном соответствии с правилом `UnusedException`.
  - В тестовых блоках `try/catch` в конце блока `try` всегда вызывается `fail(...)`.
- **Запрет на Wildcard-импорты**:
  - В файлах исходного кода и тестов отсутствуют wildcard-импорты (`import .*`), все классы импортированы явно.
- **Совместимость версий и зависимостей**:
  - Байткод скомпилирован под Java 1.8 (`jvmTarget = '1.8'`).
  - Не добавлены никакие новые сторонние зависимости, используется объявленная в проекте `libs.kotlinx.coroutines`.

### 1.4 Архитектурная точность модуля
- **Типизация адаптера**:
  - `BodyFlowCallAdapter<T>` реализует `CallAdapter<T, Flow<T>>`, возвращая строго типизированный поток тела ответа без принудительной нуллабельности в сигнатуре.
  - `ResponseFlowCallAdapter<T>` реализует `CallAdapter<T, Flow<Response<T>>>`.
- **Семантика Cold Stream и отмена**:
  - Тест отмены корутины (`cancellationCancelsOkHttpCall`) напрямую валидирует состояние `okHttpCall.isCanceled() == true` в диспетчере OkHttp.
- **Синхронизация документации**:
  - [AGENTS.md](../AGENTS.md) и [01-architecture-and-modules.md](../docs/instructions/01-architecture-and-modules.md) обновлены: новый модуль `:retrofit-adapters:flow` добавлен в общую карту монорепозитория.

---

## 2. Результаты тестирования

Все 13 тестов в [`FlowCallAdapterFactoryTest`](../retrofit-adapters/flow/src/test/java/retrofit2/adapter/flow/FlowCallAdapterFactoryTest.kt) успешно пройдены:

| Тест | Проверяемое поведение | Результат |
| :--- | :--- | :--- |
| `bodySuccess` | Успешный HTTP 200 возвращает тело ответа в `Flow<String>` | **PASS** |
| `body404ThrowsHttpException` | HTTP 404 выбрасывает `HttpException` с кодом 404 | **PASS** |
| `bodyFailureThrowsIOException` | Сбой соединения выбрасывает `IOException` | **PASS** |
| `bodyThrowsOnNull` | HTTP 204 при объявленном non-null теле выбрасывает `KotlinNullPointerException` | **PASS** |
| `responseSuccess` | Успешный ответ эмитится как `Response<String>` со статусом 200 | **PASS** |
| `response404DoesNotThrow` | HTTP 404 не выбрасывает ошибку, а возвращает `Response<String>` со статусом 404 | **PASS** |
| `responseFailureThrowsIOException` | Сетевой сбой при `Flow<Response<String>>` выбрасывает `IOException` | **PASS** |
| `unitSuccess` | HTTP 204 успешно возвращает `Unit` для `Flow<Unit>` | **PASS** |
| `coldStreamExecutesPerCollect` | Запрос не отправляется до `collect()`; каждый `collect()` выполняет новый сетевой запрос | **PASS** |
| `cancellationCancelsOkHttpCall` | Отмена корутины коллектора переводит вызов OkHttp в статус `isCanceled() == true` | **PASS** |
| `rawFlowTypeThrows` | Непараметризованный `Flow` выбрасывает `IllegalStateException` | **PASS** |
| `rawResponseTypeThrows` | Непараметризованный `Flow<Response>` выбрасывает `IllegalStateException` | **PASS** |
| `nonFlowTypeReturnsNull` | Фабрика возвращает `null` для не-Flow типов | **PASS** |

---

## 3. Итоговая валидация

- **`animalsnifferMain`**: проверка соответствия сигнатурам Android API Level 21 и Java 1.8 — **0 violations**.
- **`spotlessCheck` / `spotlessApply`**: код отформатирован с помощью Google ktfmt.
- **`:retrofit-adapters:flow:check`**: компиляция, ErrorProne, AnimalSniffer, Spotless и тесты успешно завершены (**BUILD SUCCESSFUL**).
- **`:retrofit:kotlin-test:test`**: регрессионные тесты ядра Retrofit успешно завершены (**BUILD SUCCESSFUL**).
