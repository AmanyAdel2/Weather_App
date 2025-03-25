package com.amany.taks.alarm


import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amany.taks.R
import com.amany.taks.models.AlarmData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun CheckAlarmPermission() {
    val context = LocalContext.current
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    var showPermissionDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager?.canScheduleExactAlarms() == false) {
                showPermissionDialog = true
            }
        }
    }

    if (showPermissionDialog) {
        AlarmPermissionDialog(
            onDismissRequest = { showPermissionDialog = false },
            onConfirmation = {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                context.startActivity(intent)
                showPermissionDialog = false
            }
        )
    }
}



@Composable
fun SearchScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon on the screen
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "search",
            tint = Color(0xFF0F9D58)
        )
        // Text on the screen
        Text(text = "Search", color = Color.Black)
    }
}







@Composable
fun AlarmScreen(modifier: Modifier = Modifier) {
    var timeString by remember { mutableStateOf(CalendarHelperUtil.convertTimeFromMillis(Calendar.getInstance().timeInMillis)) }
    val openAlertDialog = remember { mutableStateOf(true) }
    val context = LocalContext.current

    if (!Settings.canDrawOverlays(context)) {
        if (openAlertDialog.value) {
            OverlayPermissionDialog(
                onDismissRequest = {
                    openAlertDialog.value = false
                },
                onConfirmation = {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:${context.packageName}")
                    )
                    context.startActivity(intent)
                    openAlertDialog.value = false
                }
            )
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3600L)
            timeString = CalendarHelperUtil.convertTimeFromMillis(Calendar.getInstance().timeInMillis)
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(top = 90.dp),
            fontSize = 84.sp,
            fontWeight = FontWeight.Medium,
            text = timeString
        )
        AlarmList(Modifier)
    }
}
@Composable
fun AlarmCard(
    modifier: Modifier = Modifier,
    alarm: AlarmData,
    onAlarmUpdate: (AlarmData) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = CalendarHelperUtil.convertTimeFromMillis(alarm.time),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = {
                val newTime = alarm.time + 60000 // Example: Adding 1 minute to the alarm time
                onAlarmUpdate(alarm.copy(time = newTime))
            }) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit Alarm"
                )
            }

            IconButton(onClick = {
                // Handle alarm deletion (if needed)
            }) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete Alarm"
                )
            }
        }
    }
}




@Composable
fun AlarmNotification(modifier: Modifier = Modifier, onDismiss: () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = CalendarHelperUtil.convertTimeFromMillis(Calendar.getInstance().timeInMillis),
            fontSize = 48.sp,
            color = Color.White,
            modifier = Modifier.padding(top = 48.dp)
        )

        Text(
            text = "Alarm",
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(Modifier.weight(1f))

        Image(
            painter = painterResource(R.drawable.ic_close), contentDescription = "Close",
            modifier = Modifier
                .size(120.dp)
                .padding(bottom = 50.dp)
                .clickable {
                    onDismiss()
                }
        )
    }
}

@Composable
@Preview
fun AlarmNotificationPreview() {
    AlarmNotification(Modifier) {}
}


class CalendarHelperUtil {
    companion object {
        @JvmStatic
        fun convertTimeFromMillis(timeInMillis: Long): String {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timeInMillis
            val currHour = calendar[Calendar.HOUR_OF_DAY]
            val currMinute = calendar[Calendar.MINUTE]
            val currSecond = calendar[Calendar.SECOND]
            return String.format("%02d:%02d", currHour, currMinute)
        }
    }
}


@Composable
fun AlarmAppScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
    val openOverlayDialog = remember { mutableStateOf(false) }
    val openAlarmDialog = remember { mutableStateOf(false) }

    // Check for overlay permission
    LaunchedEffect(Unit) {
        if (!Settings.canDrawOverlays(context)) {
            openOverlayDialog.value = true
        }
    }

    // Check for exact alarm permission (Android 12+)
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
            if (alarmManager?.canScheduleExactAlarms() == false) {
                if (!openAlarmDialog.value) {
                    openAlarmDialog.value = true
                }
            }
        }
    }


    if (openOverlayDialog.value) {
        OverlayPermissionDialog(
            onDismissRequest = { openOverlayDialog.value = false },
            onConfirmation = {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${context.packageName}")
                )
                context.startActivity(intent)
                openOverlayDialog.value = false
            }
        )
    }

    if (openAlarmDialog.value) {
        AlarmPermissionDialog(
            onDismissRequest = { openAlarmDialog.value = false },
            onConfirmation = {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                context.startActivity(intent)
                openAlarmDialog.value = false
            }
        )
    }

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        AlarmScreen(modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun AlarmPermissionDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit
) {
    AlertDialog(
        title = { Text(text = "Grant Alarm Permission") },
        text = { Text(text = "This app needs permission to schedule exact alarms.") },
        onDismissRequest = { /* not dismissible */ },
        confirmButton = {
            TextButton(onClick = { onConfirmation() }) {
                Text("Grant Permission")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text("Dismiss")
            }
        }
    )
}
@Composable
fun OverlayPermissionDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
) {
    AlertDialog(
        title = { Text(text = "Grant Overlay Permission") },
        text = { Text(text = "This app needs overlay permission to show alarm notifications.") },
        onDismissRequest = { /* Prevent dismissal */ },
        confirmButton = {
            TextButton(onClick = { onConfirmation() }) {
                Text("Grant Permission")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text("Dismiss")
            }
        }
    )
}

@Composable
fun AlarmCard(
    modifier: Modifier = Modifier,
    alarm: AlarmData,
    onDelete: (AlarmData) -> Unit,
    onEdit: (AlarmData) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = CalendarHelperUtil.convertTimeFromMillis(alarm.time),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            // Edit Alarm
            IconButton(onClick = { onEdit(alarm) }) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit Alarm"
                )
            }

            // Delete Alarm
            IconButton(onClick = { onDelete(alarm) }) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete Alarm"
                )
            }
        }
    }
}
@Composable
fun AlarmList(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val db = remember { AlarmDatabase.getDatabase(context).alarmDao() }
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
    var alarmList by remember { mutableStateOf<List<AlarmEntity>>(emptyList()) }

    // Load alarms from Room
    LaunchedEffect(Unit) {
        alarmList = db.getAllAlarms()
    }

    val picker = TimePickerDialog(
        context,
        { _, hour: Int, minute: Int ->
            val newCalendar = Calendar.getInstance()
            newCalendar.set(Calendar.HOUR_OF_DAY, hour)
            newCalendar.set(Calendar.MINUTE, minute)
            newCalendar.set(Calendar.SECOND, 0)
            newCalendar.set(Calendar.MILLISECOND, 0)
            val newAlarm = AlarmEntity(time = newCalendar.timeInMillis)

            // Save to Room
            CoroutineScope(Dispatchers.IO).launch {
                db.insertAlarm(newAlarm)
                alarmList = db.getAllAlarms() // Refresh UI
            }

            // Set the alarm
            alarmManager?.let {
                AlarmUtil.setAlarm(context, it, newAlarm.time, newAlarm.id)
            }
        },
        Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
        Calendar.getInstance().get(Calendar.MINUTE),
        false
    )

    Column(modifier) {
        if (alarmList.isNotEmpty()) {
            Text(
                text = "Alarms",
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                modifier = Modifier.padding(24.dp, 24.dp, 0.dp, 24.dp)
            )
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(alarmList) { alarm ->
                AlarmCard(
                    modifier,
                    AlarmData(alarm.id, alarm.time),
                    onDelete = { alarmToDelete ->
                        CoroutineScope(Dispatchers.IO).launch {
                            db.deleteAlarm(AlarmEntity(alarmToDelete.id, alarmToDelete.time))
                            alarmList = db.getAllAlarms() // Refresh UI
                        }
                        alarmManager?.let {
                            AlarmUtil.cancelAlarm(context, it, AlarmEntity(alarmToDelete.id, alarmToDelete.time)) // ✅ Correct Type
                        }
                    }
                    ,
                    onEdit = {
                        picker.show()
                    }
                )
            }
        }

        ElevatedCard(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 40.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
            onClick = { picker.show() }
        ) {
            Text(
                text = "Add New",
                fontWeight = FontWeight.Medium,
                fontSize = 22.sp,
                modifier = Modifier.padding(40.dp, 14.dp)
            )
        }
    }
}

class AlarmUtil {
    companion object {
        @JvmStatic
        fun setAlarm(
            context: Context,
            alarmManager: AlarmManager,
            timeInMillis: Long,
            alarmId: Int
        ) {
            val intent = Intent(context, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                alarmId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )

            // Set the alarm with AlarmManager
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                timeInMillis,
                pendingIntent
            )

            Toast.makeText(context, "Alarm Set", Toast.LENGTH_SHORT).show()
        }

        @JvmStatic
        fun cancelAlarm(
            context: Context,
            alarmManager: AlarmManager,
            alarm: AlarmEntity
        ) {
            val intent = Intent(context, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                alarm.id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )

            // Cancel the scheduled alarm
            alarmManager.cancel(pendingIntent)

            Toast.makeText(context, "Alarm Canceled", Toast.LENGTH_SHORT).show()
        }
    }
}

