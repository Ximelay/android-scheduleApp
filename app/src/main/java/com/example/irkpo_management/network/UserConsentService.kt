package com.example.irkpo_management.network

import android.content.Context
import android.provider.Settings
import com.example.irkpo_management.BuildConfig
import com.example.irkpo_management.db.CreateUserConsentRequest
import com.example.irkpo_management.db.UserConsent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.UUID

class UserConsentService(private val context: Context) {

    private val supabaseApi: SupabaseApi = ApiClient.getSupabaseApi()
    private val apiKey: String = BuildConfig.SUPABASE_ANON_KEY
    private val authHeader: String = "Bearer ${BuildConfig.SUPABASE_ANON_KEY}"

    fun getDeviceId(): String {
        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: UUID.randomUUID().toString()
    }

    fun getIpAddress(): String {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val networkInterface = interfaces.nextElement()
                val addresses = networkInterface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val address = addresses.nextElement()
                    if (!address.isLoopbackAddress && address is InetAddress) {
                        val hostAddress = address.hostAddress
                        if (hostAddress != null && hostAddress.indexOf(':') < 0) {
                            return hostAddress
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * Создать новое согласие пользователя
     * @param userId UUID пользователя (если не указан - будет сгенерирован на сервере)
     */
    fun createConsent(
        userId: String? = null,
        onSuccess: (UserConsent) -> Unit,
        onError: (String) -> Unit
    ) {
        val deviceId = getDeviceId()
        val ipAddress = getIpAddress()

        val request = CreateUserConsentRequest(
            userId = userId,
            ipAddress = ipAddress,
            deviceId = deviceId,
            consentVersion = 1
        )

        supabaseApi.createUserConsent(apiKey, authHeader, request)
            .enqueue(object : Callback<List<UserConsent>> {
                override fun onResponse(
                    call: Call<List<UserConsent>>,
                    response: Response<List<UserConsent>>
                ) {
                    if (response.isSuccessful) {
                        val consents = response.body()
                        if (!consents.isNullOrEmpty()) {
                            onSuccess(consents[0])
                        } else {
                            onError("Не удалось получить созданное согласие")
                        }
                    } else {
                        onError("Ошибка: ${response.code()} - ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<List<UserConsent>>, t: Throwable) {
                    onError("Ошибка сети: ${t.message}")
                }
            })
    }

    /**
     * Проверить согласие на сервере и обновить локальное время проверки
     */
    fun verifyConsentOnServer(
        userId: String,
        onResult: (Boolean, UserConsent?) -> Unit,
        onError: (String) -> Unit
    ) {
        supabaseApi.getUserConsent(apiKey, authHeader, userId, "*")
            .enqueue(object : Callback<List<UserConsent>> {
                override fun onResponse(
                    call: Call<List<UserConsent>>,
                    response: Response<List<UserConsent>>
                ) {
                    if (response.isSuccessful) {
                        val consents = response.body()
                        if (!consents.isNullOrEmpty()) {
                            // Согласие найдено на сервере - обновляем время последней проверки
                            updateLastCheckTime()
                            onResult(true, consents[0])
                        } else {
                            // Согласие не найдено - нужно запросить заново
                            onResult(false, null)
                        }
                    } else {
                        onError("Ошибка: ${response.code()} - ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<List<UserConsent>>, t: Throwable) {
                    onError("Ошибка сети: ${t.message}")
                }
            })
    }

    /**
     * Сохранить информацию о согласии в SharedPreferences
     */
    fun saveConsentLocally(userId: String, consentTimestamp: String) {
        val prefs = context.getSharedPreferences("user_consent", Context.MODE_PRIVATE)
        val currentTime = System.currentTimeMillis()
        val appVersion = getAppVersion()

        prefs.edit().apply {
            putString("user_id", userId)
            putString("consent_timestamp", consentTimestamp)
            putBoolean("consent_given", true)
            putLong("last_check_time", currentTime)
            putString("app_version", appVersion)
            apply()
        }
    }

    private fun getAppVersion(): String {
        return try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "unknown"
        } catch (_: Exception) {
            "unknown"
        }
    }

    /**
     * Проверить, нужно ли показывать диалог согласия
     * Показываем только если:
     * - Согласие не дано вообще
     * - Версия приложения изменилась
     *
     * Для проверки через 24 часа используйте shouldCheckConsentOnServer()
     */
    fun shouldShowConsentDialog(): Boolean {
        val prefs = context.getSharedPreferences("user_consent", Context.MODE_PRIVATE)
        val consentGiven = prefs.getBoolean("consent_given", false)

        // Если согласие не дано вообще - показываем диалог
        if (!consentGiven) {
            return true
        }

        val savedAppVersion = prefs.getString("app_version", null)
        val currentAppVersion = getAppVersion()

        // Проверяем, изменилась ли версия приложения
        val versionChanged = savedAppVersion != currentAppVersion

        return versionChanged
    }

    /**
     * Проверить, нужно ли проверить согласие на сервере
     * Возвращает true, если прошло более 24 часов с последней проверки
     */
    fun shouldCheckConsentOnServer(): Boolean {
        val prefs = context.getSharedPreferences("user_consent", Context.MODE_PRIVATE)
        val consentGiven = prefs.getBoolean("consent_given", false)

        // Если согласие не дано вообще - не нужно проверять на сервере
        if (!consentGiven) {
            return false
        }

        val lastCheckTime = prefs.getLong("last_check_time", 0)
        val currentTime = System.currentTimeMillis()
        val oneDayInMillis = 24 * 60 * 60 * 1000L // 24 часа

        // Проверяем, прошло ли более 24 часов
        return (currentTime - lastCheckTime) > oneDayInMillis
    }

    /**
     * Обновить время последней проверки согласия
     * Вызывается после успешной проверки в Supabase
     */
    fun updateLastCheckTime() {
        val prefs = context.getSharedPreferences("user_consent", Context.MODE_PRIVATE)
        val currentTime = System.currentTimeMillis()
        val appVersion = getAppVersion()

        prefs.edit().apply {
            putLong("last_check_time", currentTime)
            putString("app_version", appVersion)
            apply()
        }
    }

    /**
     * Проверить, есть ли локально сохраненное согласие
     * @deprecated Используйте shouldShowConsentDialog() для более точной проверки
     */
    fun hasLocalConsent(): Boolean {
        val prefs = context.getSharedPreferences("user_consent", Context.MODE_PRIVATE)
        return prefs.getBoolean("consent_given", false)
    }

    /**
     * Получить локально сохраненный user_id
     */
    fun getLocalUserId(): String? {
        val prefs = context.getSharedPreferences("user_consent", Context.MODE_PRIVATE)
        return prefs.getString("user_id", null)
    }

    /**
     * Удалить локально сохраненное согласие
     */
    fun clearLocalConsent() {
        val prefs = context.getSharedPreferences("user_consent", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}

