![Build Status](https://img.shields.io/github/workflow/status/Ximelay/android-scheduleApp/CI)
![License](https://img.shields.io/github/license/Ximelay/android-scheduleApp)
![GitHub stars](https://img.shields.io/github/stars/Ximelay/android-scheduleApp?style=social)
![GitHub forks](https://img.shields.io/github/forks/Ximelay/android-scheduleApp?style=social)
![GitHub issues](https://img.shields.io/github/issues/Ximelay/android-scheduleApp)
![Codecov](https://img.shields.io/codecov/c/github/Ximelay/android-scheduleApp)
![Last Commit](https://img.shields.io/github/last-commit/Ximelay/android-scheduleApp)
![Repo size](https://img.shields.io/github/repo-size/Ximelay/android-scheduleApp)
![Language](https://img.shields.io/github/languages/top/Ximelay/android-scheduleApp)
![Commit Activity](https://img.shields.io/github/commit-activity/m/Ximelay/android-scheduleApp)
![Issues in last 30 days](https://img.shields.io/github/issues/detail/state/Ximelay/android-scheduleApp/new)
![Open Pull Requests](https://img.shields.io/github/issues-pr/Ximelay/android-scheduleApp)



## Обзор

**Stud-Informer** — мобильное приложение для Android, разработанное для студентов и преподавателей Иркутского регионального колледжа педагогического образования ([ИРКПО](https://irkpo.ru/www/)). Оно предоставляет удобный доступ к расписанию занятий, данным об успеваемости, заметкам, интеграции с Moodle, а также поддерживает уведомления и тёмную тему.

- **Цель**: Упростить управление учебным процессом через интуитивный интерфейс.
- **Основные функции**:
  - Просмотр расписания с сворачиваемыми днями и заметками.
  - Отслеживание успеваемости (оценки, посещаемость).
  - Создание и управление заметками с напоминаниями.
  - Интеграция с Moodle через WebView.
  - Push-уведомления об изменениях расписания.
  - Поддержка светлой/тёмной темы.
- **Платформа**: Android 8.0+ (API 26+).
- **Язык**: Java.

## Технологии

- **Фреймворки и библиотеки**:
  - Retrofit: HTTP-запросы к API.
  - Room: Локальная база данных для заметок.
  - RecyclerView: Отображение списков.
  - WorkManager: Фоновые задачи и уведомления.
  - Material Components: Современный UI.
  - Gson: Парсинг JSON.
- **Инструменты**:
  - Android Studio, Gradle.
  - JUnit, Espresso для тестирования.
- **Ресурсы**:
  - JSON-файлы (`groups.json`, `teachers.json`) для групп и преподавателей.
  - API: `https://irkpo.ru/mtr/api/` для расписания и успеваемости.

## Установка и запуск

### Требования
- Android Studio (последняя версия).
- Android SDK (API 26+).
- Устройство или эмулятор с Android 8.0+.
- Файлы `groups.json` и `teachers.json` в `/app/src/main/assets/`.

### Установка
1. Клонируйте репозиторий:
   ```bash
   git clone https://github.com/Ximelay/android-scheduleApp.git
   ```
2. Скопируйте JSON-файлы:
   ```bash
   mkdir app/src/main/assets
   cp groups.json teachers.json app/src/main/assets/
   ```
3. Откройте проект в Android Studio и синхронизируйте `build.gradle`.

### Запуск
1. Подключите устройство или запустите эмулятор.
2. Выполните:
   ```bash
   Run > Run 'app'
   ```
3. Убедитесь, что в `AndroidManifest.xml` указан:
   ```xml
   <application android:name=".utils.ThemeApplication" ...>
   ```
4. Проверьте разрешение `POST_NOTIFICATIONS`.

## Структура проекта

- **Основные модули**:
  - `/java/com.example.sheduleapp_v5/`: Activity (`MainActivity`, `ScheduleActivity`, `PerformanceActivity`, `MoodleActivity`).
  - `/adapters/`: Адаптеры для RecyclerView (`LessonAdapter`, `PerformanceAdapter`).
  - `/db/`: Room для заметок (`NoteDatabase`, `NoteDao`, `NoteEntity`, `NoteRepository`).
  - `/models/`: Модели данных (`DaySchedule`, `DisplayLessonItem`, `PerformanceResponse`).
  - `/network/`: Сетевые запросы (`ApiClient`, `ScheduleApi`, `PerformanceApi`).
  - `/utils/`: Утилиты (`GroupUtils`, `PreferenceManager`, `StickyHeaderDecoration`).
  - `/work/`: WorkManager (`ScheduleCheckWorker`, `ReminderWorker`).
- **Ресурсы**:
  - `/res/layout/`: XML-лейауты интерфейса.
  - `/assets/`: JSON-файлы (`groups.json`, `teachers.json`).

## Основные компоненты

- **MainActivity**: Точка входа, навигация, настройка темы, запуск фоновых задач.
- **ScheduleActivity**: Отображение расписания, выбор группы/преподавателя, заметки.
- **PerformanceActivity**: Просмотр успеваемости по номеру телефона.
- **MoodleActivity**: Доступ к Moodle через WebView.
- **LessonAdapter**: RecyclerView для расписания с анимацией и заметками.
- **NoteRepository**: Управление заметками через Room.
- **ScheduleCheckWorker**: Фоновая проверка изменений расписания.

## Тестирование

- **Сценарии**:
  - UI: Навигация, отображение расписания, добавление заметок.
  - API: Корректность ответов `/schedule` и `/student`.
  - База данных: Сохранение/удаление заметок.
  - Уведомления: Push-уведомления и очистка заметок.
  - Тема: Переключение светлой/тёмной темы.
- **Инструменты**:
  - JUnit для юнит-тестов.
  - Espresso для UI-тестов.
  - MockWebServer для API.
- **Запуск тестов**:
  ```bash
  Run > Run 'All Tests'
  ```

## Взаимодействие компонентов

```mermaid
graph TD
    A[MainActivity] -->|Запуск| B[ScheduleActivity]
    A -->|Запуск| C[PerformanceActivity]
    A -->|Запуск| D[MoodleActivity]
    A -->|Планирование| E[ScheduleCheckWorker]
    B -->|API-запрос| F[ApiClient/ScheduleApi]
    C -->|API-запрос| G[ApiClient/PerformanceApi]
    F -->|Ответ| H[ScheduleResponse]
    G -->|Ответ| I[PerformanceResponse]
    B -->|Отображение| J[LessonAdapter]
    C -->|Отображение| K[PerformanceAdapter]
    B -->|Сохранение| L[PreferenceManager]
    C -->|Сохранение| L
    B -->|Поиск| M[GroupUtils]
    B -->|Поиск| N[TeacherUtils]
    M -->|Данные| O[DataProvider]
    N -->|Данные| O
    J -->|Заметки| P[NoteRepository]
    P -->|База данных| Q[NoteDatabase]
    Q -->|DAO| R[NoteDao]
    R -->|Данные| S[NoteEntity]
    J -->|Обновление| T[LessonDiffUtil]
    J -->|Декорация| U[StickyHeaderDecoration]
    H -->|Данные| V[DaySchedule]
    V -->|Уроки| W[LessonIndex]
    W -->|Детали| X[LessonItem]
    J -->|Отображение| Y[DisplayLessonItem]
    Y -->|Детали| X
    I -->|Данные| Z[Plan/Period/PlanCell]
    AA[App] -->|Инициализация| AB[NotificationManager]
    AC[ThemeApplication] -->|Тема| AD[AppCompatDelegate]
    J -->|Напоминания| AE[ReminderScheduler]
    AE -->|Задачи| AF[ReminderWorker]
    AE -->|Очистка| AG[NoteCleanupWorker]
```

- **Поток данных**:
  - `MainActivity` запускает навигацию и фоновую проверку (`ScheduleCheckWorker`).
  - `ScheduleActivity` запрашивает данные через `ScheduleApi`, отображает через `LessonAdapter`.
  - `LessonAdapter` управляет заметками (`NoteRepository`) и напоминаниями (`ReminderScheduler`).
  - `PerformanceActivity` получает успеваемость через `PerformanceApi`.
  - `GroupUtils` и `TeacherUtils` загружают данные из JSON (`DataProvider`).


## Лицензия

Проект распространяется под лицензией [BSD 3-Clause](https://github.com/Ximelay/android-scheduleApp/blob/main/LICENSE).

## Контакты

- Автор: [Ximelay](https://github.com/Ximelay)
- Вопросы: Открывайте [issue](https://github.com/Ximelay/android-scheduleApp/issues) или пишите в обсуждения.