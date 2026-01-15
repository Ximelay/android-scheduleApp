package com.example.irkpo_management

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import android.widget.Toast
import com.example.irkpo_management.network.UserConsentService
import com.example.irkpo_management.ui.MainScreen
import com.example.irkpo_management.ui.fragments.UserConsentDialog
import com.example.irkpo_management.ui.theme.AppTheme

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TELEGRAM = BuildConfig.TELEGRAM_NIKNEIM
        private const val GITHUB = BuildConfig.NIKNEIM_GITHUB
        private const val GITHUB_REPOSITORY = BuildConfig.REPOSITORY_GITHUB
        private const val IRKPO_MOODLE = BuildConfig.IRKPO_MOODLE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE)
        val savedDarkMode = sharedPreferences.getBoolean("darkMode", false)

        AppCompatDelegate.setDefaultNightMode(
            if (savedDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

        setContent {
            var isDarkTheme by remember { mutableStateOf(savedDarkMode) }
            val consentService = remember { UserConsentService(this) }

            var consentRequired by remember { mutableStateOf(consentService.shouldShowConsentDialog()) }
            var isCheckingServer by remember { mutableStateOf(false) }
            var consentGranted by remember { mutableStateOf(!consentRequired) }

            LaunchedEffect(Unit) {
                if (!consentRequired && consentService.shouldCheckConsentOnServer()) {
                    // Если прошло 24 часа - проверяем на сервере
                    val localUserId = consentService.getLocalUserId()

                    if (localUserId != null) {
                        isCheckingServer = true
                        consentService.verifyConsentOnServer(
                            userId = localUserId,
                            onResult = { exists, _ ->
                                isCheckingServer = false
                                if (!exists) {
                                    // Согласие не найдено на сервере - требуем согласие
                                    consentRequired = true
                                    consentGranted = false
                                }
                            },
                            onError = { _ ->
                                isCheckingServer = false
                                // При ошибке сети не показываем диалог, используем локальное согласие
                                consentService.updateLastCheckTime()
                            }
                        )
                    }
                }
            }

            AppTheme(darkTheme = isDarkTheme) {
                // Пока согласие не получено ИЛИ идет проверка - показываем только диалог/загрузку
                if (!consentGranted || isCheckingServer) {
                    UserConsentDialog(
                        showDialog = consentRequired,
                        onDismiss = {
                            // Не позволяем закрыть диалог без выбора
                        },
                        onAccept = { _ ->
                            consentRequired = false
                            consentGranted = true
                            Toast.makeText(
                                this,
                                "Согласие принято. Спасибо!",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onDecline = {
                            Toast.makeText(
                                this,
                                "Без согласия приложение не может работать",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }
                    )
                } else {
                    MainScreen(
                    onScheduleClick = {
                        startActivity(Intent(this, ScheduleActivity::class.java))
                    },
                    onPerformanceClick = {
                        startActivity(Intent(this, PerformanceActivity::class.java))
                    },
                    onMoodleClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("$IRKPO_MOODLE")).apply {
                            setPackage("com.moodle.moodlemobile")
                        }
                        try {
                            startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("$IRKPO_MOODLE")))
                        }
                    },
                    onExportClick = {
                        startActivity(Intent(this, ExportActivity::class.java))
                    },
                    onFavoritesClick = {
                        startActivity(Intent(this, FavoritesManagementActivity::class.java))
                    },
                    onAboutClick = {
                        showAboutDialog()
                    },
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = { isChecked ->
                        sharedPreferences.edit().apply {
                            putBoolean("darkMode", isChecked)
                            apply()
                        }
                        isDarkTheme = isChecked

                        AppCompatDelegate.setDefaultNightMode(
                            if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                            else AppCompatDelegate.MODE_NIGHT_NO
                        )
                    }
                )
                }
            }
        }
    }

    private fun showAboutDialog() {
        val versionName = try {
            packageManager.getPackageInfo(packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            "Неизвестно"
        }

        val aboutMessage = """
            Автор: $GITHUB
            Версия: $versionName
            Приложенение создано для студентов и преподавателей ИРКПО
            Предложения по улучшению можете писать в Telegram: $TELEGRAM
        """.trimIndent()

        val spannableMessage = SpannableString(aboutMessage)

        val githubStart = aboutMessage.indexOf(GITHUB)
        val githubEnd = githubStart + GITHUB.length
        spannableMessage.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/$GITHUB")))
                }
            },
            githubStart,
            githubEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val telegramStart = aboutMessage.indexOf(TELEGRAM)
        val telegramEnd = telegramStart + TELEGRAM.length
        spannableMessage.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    try {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("tg://resolve?domain=$TELEGRAM")))
                    } catch (e: ActivityNotFoundException) {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/$TELEGRAM")))
                    }
                }
            },
            telegramStart,
            telegramEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val messageTextView = TextView(this).apply {
            text = spannableMessage
            movementMethod = LinkMovementMethod.getInstance()
            setPadding(40, 20, 40, 20)
        }

        AlertDialog.Builder(this)
            .setTitle("О Программе")
            .setView(messageTextView)
            .setPositiveButton("Перейти к исходному коду") { _, _ ->
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_REPOSITORY)))
            }
            .setNegativeButton("Закрыть") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}