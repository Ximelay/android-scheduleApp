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

**Stud-Informer** — мобильное приложение для Android, разработанное для студентов и преподавателей Иркутского регионального колледжа педагогического образования ([ИРКПО](https://irkpo.ru/www/)). Оно предоставляет удобный доступ к расписанию занятий, данным об успеваемости + экспорт оценок, заметкам к урокам, интеграции с Moodle, а также поддерживает уведомления и переключение между темами.

- **Цель**: Упростить управление учебным процессом через интуитивно понятный интерфейс.
- **Основные функции**:
  - Просмотр расписания с сворачиваемыми днями и заметками.
  - Отслеживание успеваемости (оценки, посещаемость).
  - Интеграция с Moodle через WebView.
  - Push-уведомления об изменениях расписания.
  - Экспорт успеваемости с выбором данных в .`xlsx` или `.pdf` форматы
  - Поддержка переключения тем.
- **Платформа**: Android 8.0+ (API 26+).
- **Язык**: Java(17) + Kotlin(2.2.0).


<img src="https://github.com/user-attachments/assets/76d61a03-068e-43ca-9c1e-29c51ce84d75" style="width: 30%; height: auto; max-width: 600px;" alt="Главный экран приложения">
<img src="https://github.com/user-attachments/assets/e77f273f-0b95-4038-a9aa-6ad2698bd52f" style="width: 30%; height: auto; max-width: 600px;" alt="Показ расписания">
<img src="https://github.com/user-attachments/assets/30346d9e-3635-4b2c-9c4f-0037f68e9f39" style="width: 30%; height: auto; max-width: 600px;" alt="Добавление заметки к занятию">
<img src="https://github.com/user-attachments/assets/b425c29d-c622-4c3c-acd8-47dfe19eebd9" style="width: 30%; height: auto; max-width: 600px;" alt="Демонстрация работы уведомлений">
<img src="https://github.com/user-attachments/assets/d102222d-81ef-4e7c-a296-0f5bc32be381" style="width: 30%; height: auto; max-width: 600px;" alt="Все предметы в полугодии">
<img src="https://github.com/user-attachments/assets/251a96dc-e019-46a9-90f4-a4c2c04fe976" style="width: 30%; height: auto; max-width: 600px;" alt="Показ занятий">
<img src="https://github.com/user-attachments/assets/702b7464-2e36-4960-9e9a-cf1f4969289a" style="width: 30%; height: auto; max-width: 600px;" alt="Экран экспорта успеваемости">
<img src="https://github.com/user-attachments/assets/816abbd4-8a9a-4321-b499-89a97189fc65" style="width: 30%; height: auto; max-width: 600px;" alt="Экран управления избранным">
<img src="https://github.com/user-attachments/assets/b1befcc8-c5d3-4d7b-b6d4-84f5919dcf0e" style="width: 30%; height: auto; max-width: 600px;" alt="Окно 'О программе'">

## Технологии

- **Фреймворки и библиотеки**:
  - [Retrofit](https://square.github.io/retrofit/)
  - [Room](https://developer.android.com/training/data-storage/room)
  - [RecyclerView](https://developer.android.com/develop/ui/views/layout/recyclerview)
  - [WorkManager](https://developer.android.com/develop/background-work/background-tasks/persistent/getting-started)
  - [Material Components](https://material.io/develop/android)
  - [Jetpack Compose](https://developer.android.com/jetpack/compose)
  - [Gson](https://github.com/google/gson)
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

### Установка
1. Клонируйте репозиторий:
   ```bash
   git clone https://github.com/Ximelay/android-scheduleApp.git
   ```
3. Откройте проект в Android Studio и синхронизируйте `build.gradle`.

### Запуск
1. Подключите устройство или запустите эмулятор.

2. Создайте файл `local.properties`:
    ```bash
   cp local.properties.example local.properties
   ```
3. Заполните все переменные в `local.properties` кроме `sdk.dir`.

4. Выполните:
   ```bash
   Run > Run 'app'
   ```

## Лицензия

Проект распространяется под лицензией [BSD 3-Clause](https://github.com/Ximelay/android-scheduleApp/blob/main/LICENSE).

## Контакты

- Автор: [Ximelay](https://github.com/Ximelay)
- Вопросы: Открывайте [issue](https://github.com/Ximelay/android-scheduleApp/issues) или пишите в обсуждения.
