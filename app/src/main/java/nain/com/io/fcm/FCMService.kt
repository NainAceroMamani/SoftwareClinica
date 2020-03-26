package nain.com.io.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import nain.com.R
import nain.com.io.ApiService
import nain.com.ui.MainActivity
import nain.com.util.PreferenceHelper
import nain.com.util.PreferenceHelper.get
import nain.com.util.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FCMService : FirebaseMessagingService() {
    private val apiService by lazy {
        ApiService.create()
    }

    private val preferences by lazy {
        PreferenceHelper.defaultPrefs(this)
    }
    // genrar un nuevo Token Cuando fireabse nos cambia el token de usuario y no se cierra sesión pues tambien lo actualizamos en la api
    // este metodo ctr + o
    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)

        val jwt = preferences["jwt", ""]

        if(jwt.isEmpty())
            return

        val authHeader= "Bearer $jwt"

        val call = apiService.postToken(authHeader, newToken)
        call.enqueue(object: Callback<Void> {
            override fun onFailure(call: Call<Void>, t: Throwable) {
                toast(t.localizedMessage)
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if(response.isSuccessful) {
                    Log.d(TAG, "Token registrado correctamente")
                } else {
                    Log.d(TAG, "Hubo un problema al registro del Token")
                }
            }

        })
    }

    /**
         * Called when message is received.
         *
         * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
         */
        override fun onMessageReceived(remoteMessage: RemoteMessage) {
            // Se ejecuta cuando la aplicación esta abierta
            // There are two types of messages data messages and notification messages. Data messages are handled
            // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
            // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
            // is in the foreground. When the app is in the background an automatically generated notification is displayed.
            // When the user taps on the notification they are returned to the app. Messages containing both notification
            // and data payloads are treated as notification messages.

            // Handle FCM messages here.
            Log.d(TAG, "From: " + remoteMessage.from)

            // Check if message contains a data payload.
            if (remoteMessage.data.isNotEmpty()) {
                Log.d(TAG, "Message data payload: " + remoteMessage.data)

                handleNow()
            }

            // Check if message contains a notification payload.
            remoteMessage.notification.let {
                // si viene vacio el titulo del notification mostramos el nombre del app
                val title = remoteMessage.notification?.title ?: getString(R.string.app_name)
                val body = remoteMessage.notification?.body

                // si el cuerpo de la notificación no esta vacia la mostramos
                if(body != null)
                        sendNotification(title, body)

//                Log.d(TAG, "Notification Title: " + remoteMessage.notification?.title)
//                Log.d(TAG, "Notification Body: " + remoteMessage.notification?.body)
            }

            // sendNotification()
        }
        // [END receive_message]

        /**
         * Handle time allotted to BroadcastReceivers.
         */
        private fun handleNow() {
            Log.d(TAG, "Short lived task is done.")
        }

        /**
         * Create and show a simple notification containing the received FCM message.
         *
         * @param messageBody FCM message body received.
         */
        private fun sendNotification(messageTitle: String,messageBody: String) {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(
                this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT
            )

            val channelId = getString(R.string.default_notification_channel_id)
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_schedule)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager!!.createNotificationChannel(channel)
            }

            notificationManager!!.notify(0 /* ID of notification */, notificationBuilder.build())
        }

        companion object {
            private const val TAG = "FCMService"
        }
}