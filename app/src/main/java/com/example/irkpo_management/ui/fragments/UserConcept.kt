package com.example.irkpo_management.ui.fragments

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.irkpo_management.network.UserConsentService

/**
 * Диалоговое окно для получения согласия на обработку персональных данных
 *
 * @param showDialog Состояние видимости диалога
 * @param onDismiss Действие при закрытии диалога
 * @param onAccept Действие при принятии согласия (с userId)
 * @param onDecline Действие при отклонении согласия
 */
@Composable
fun UserConsentDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onAccept: (String) -> Unit,
    onDecline: () -> Unit
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { if (!isLoading) onDismiss() },
            title = {
                Text(
                    text = "Согласие на обработку персональных данных",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        Text(
                            text = "Настоящим я даю свое согласие на обработку моих персональных данных, включая:",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                            text = "• Идентификационные данные\n" +
                                   "• IP-адрес устройства\n" +
                                   "• Идентификатор устройства\n" +
                                   "• Дату и время согласия\n" +
                                   "• Информацию о расписании и успеваемости",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Text(
                            text = "Цели обработки данных:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        Text(
                            text = "• Предоставление доступа к функционалу приложения\n" +
                                   "• Персонализация пользовательского опыта\n" +
                                   "• Улучшение качества сервиса\n" +
                                   "• Обеспечение безопасности",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Text(
                            text = "Вы можете отозвать свое согласие в любое время через настройки приложения.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        isLoading = true
                        val consentService = UserConsentService(context)

                        consentService.createConsent(
                            userId = null, // UUID генерируется на сервере
                            onSuccess = { consent ->
                                isLoading = false
                                // Сохраняем согласие локально
                                consent.userId?.let { userId ->
                                    consent.consentTimestamp?.let { timestamp ->
                                        consentService.saveConsentLocally(userId, timestamp)
                                        onAccept(userId)
                                    }
                                }
                                Toast.makeText(
                                    context,
                                    "Согласие успешно сохранено",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onError = { error ->
                                isLoading = false
                                Toast.makeText(
                                    context,
                                    "Ошибка: $error",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        )
                    },
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Принимаю")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDecline,
                    enabled = !isLoading
                ) {
                    Text("Отклонить")
                }
            }
        )
    }
}


