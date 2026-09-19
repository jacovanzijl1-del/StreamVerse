package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.theme.StreamBackground
import com.example.ui.theme.StreamBorder
import com.example.ui.theme.StreamCard
import com.example.ui.theme.StreamGreen
import com.example.ui.theme.StreamPrimary
import com.example.ui.theme.StreamTextMuted
import com.example.ui.theme.StreamTextPrimary
import com.example.ui.theme.StreamTextSecondary

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val prefs = viewModel.prefs

    var tmdbKey by remember { mutableStateOf(prefs.tmdbApiKey) }
    var simklClientId by remember { mutableStateOf(prefs.simklClientId) }
    var simklClientSecret by remember { mutableStateOf(prefs.simklClientSecret) }
    var orionKey by remember { mutableStateOf(prefs.orionApiKey) }
    var orionAppKey by remember { mutableStateOf(prefs.orionAppKey) }
    var region by remember { mutableStateOf(prefs.watchRegion) }
    var autoPlayNext by remember { mutableStateOf(prefs.autoPlayNextEpisode) }

    val orionUserState by viewModel.orionUserState.collectAsState()

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearOrionConnectionStatus()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StreamBackground)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
            .testTag("settings_screen")
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.testTag("settings_back_button")
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = StreamTextPrimary
                )
            }
            Text(
                text = "Settings & API Keys",
                color = StreamTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "StreamVerse Architecture",
                color = StreamTextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            // TMDb API Key Card
            Card(
                colors = CardDefaults.cardColors(containerColor = StreamCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Key, contentDescription = null, tint = StreamPrimary)
                        Text(
                            text = "  TMDb API Key (Metadata Layer)",
                            color = StreamTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Powers full metadata, cast, high-res posters, and watch providers.",
                        color = StreamTextMuted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
                    )
                    OutlinedTextField(
                        value = tmdbKey,
                        onValueChange = { tmdbKey = it },
                        placeholder = { Text("Enter TMDB API Key...", color = StreamTextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = StreamPrimary,
                            unfocusedBorderColor = StreamBorder,
                            focusedTextColor = StreamTextPrimary,
                            unfocusedTextColor = StreamTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("tmdb_key_input")
                    )
                }
            }

            // Orion API Key Card
            Card(
                colors = CardDefaults.cardColors(containerColor = StreamCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VpnKey, contentDescription = null, tint = StreamGreen)
                        Text(
                            text = "  Orion API Key (Streaming Layer)",
                            color = StreamTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Queries 4K, 1080P, Real-Debrid cached streams, and torrent hashes.",
                        color = StreamTextMuted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                    )
                    
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = orionKey,
                            onValueChange = { orionKey = it },
                            label = { Text("Orion User API Key", color = StreamTextMuted) },
                            placeholder = { Text("Enter Orion User API Key...", color = StreamTextMuted) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = StreamGreen,
                                unfocusedBorderColor = StreamBorder,
                                focusedTextColor = StreamTextPrimary,
                                unfocusedTextColor = StreamTextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("orion_key_input")
                        )

                        OutlinedTextField(
                            value = orionAppKey,
                            onValueChange = { orionAppKey = it },
                            label = { Text("Orion App API Key (Developer - Optional)", color = StreamTextMuted) },
                            placeholder = { Text("Enter 'streamverse' or custom developer app key...", color = StreamTextMuted) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = StreamGreen,
                                unfocusedBorderColor = StreamBorder,
                                focusedTextColor = StreamTextPrimary,
                                unfocusedTextColor = StreamTextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("orion_app_key_input")
                        )

                        Button(
                            onClick = { viewModel.checkOrionConnection(orionKey, orionAppKey) },
                            enabled = !orionUserState.checking,
                            colors = ButtonDefaults.buttonColors(containerColor = StreamGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("check_orion_button")
                        ) {
                            if (orionUserState.checking) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Check Credentials Connection", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Display connection status or user info
                    if (orionUserState.errorMessage != null) {
                        Text(
                            text = "Connection Failed: ${orionUserState.errorMessage}",
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    } else if (orionUserState.success) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                                .background(StreamBackground.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "Connection Successful!",
                                color = StreamGreen,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Username: ${orionUserState.username ?: "Unknown"}",
                                color = StreamTextPrimary,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "Account Type: ${orionUserState.accountType?.uppercase() ?: "FREE"}",
                                color = StreamTextSecondary,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "Daily Limit: ${orionUserState.dailyLimit} links",
                                color = StreamTextSecondary,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "Remaining Quota: ${orionUserState.remainingLimit} links",
                                color = StreamTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Simkl Media Tracking & Popularity Layer (Free Alternative to Trakt)
            Card(
                colors = CardDefaults.cardColors(containerColor = StreamCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Simkl (Free Alternative to Trakt)",
                            color = StreamTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = StreamGreen.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, StreamGreen)
                        ) {
                            Text(
                                text = "100% FREE",
                                color = StreamGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                    Text(
                        text = "Simkl is an open community-driven media tracker & scrobbler. Real-time top trending movies and shows are retrieved directly with zero keys or subscriptions needed.",
                        color = StreamTextMuted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 6.dp, bottom = 10.dp)
                    )
                    OutlinedTextField(
                        value = simklClientId,
                        onValueChange = { simklClientId = it },
                        placeholder = { Text("Simkl Client ID (Optional)", color = StreamTextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = StreamGreen,
                            unfocusedBorderColor = StreamBorder,
                            focusedTextColor = StreamTextPrimary,
                            unfocusedTextColor = StreamTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("simkl_client_id_input")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = simklClientSecret,
                        onValueChange = { simklClientSecret = it },
                        placeholder = { Text("Simkl Client Secret (Optional)", color = StreamTextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = StreamGreen,
                            unfocusedBorderColor = StreamBorder,
                            focusedTextColor = StreamTextPrimary,
                            unfocusedTextColor = StreamTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("simkl_client_secret_input")
                    )
                }
            }

            // Streaming Preferences
            Card(
                colors = CardDefaults.cardColors(containerColor = StreamCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Streaming Preferences",
                        color = StreamTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Autoplay Next Episode",
                                color = StreamTextPrimary,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Countdown automatically at end of episode",
                                color = StreamTextMuted,
                                fontSize = 12.sp
                            )
                        }
                        Switch(
                            checked = autoPlayNext,
                            onCheckedChange = {
                                autoPlayNext = it
                                prefs.autoPlayNextEpisode = it
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = StreamPrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = region,
                        onValueChange = { region = it },
                        label = { Text("OTT Watch Region (US, UK, CA, DE, FR)", color = StreamTextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = StreamPrimary,
                            unfocusedBorderColor = StreamBorder,
                            focusedTextColor = StreamTextPrimary,
                            unfocusedTextColor = StreamTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Save Button
            Button(
                onClick = {
                    viewModel.saveApiKeys(
                        tmdbKey = tmdbKey,
                        simklClientId = simklClientId,
                        simklSecret = simklClientSecret,
                        orionKey = orionKey,
                        orionAppKey = orionAppKey,
                        region = region
                    )
                    Toast.makeText(context, "Credentials and preferences saved!", Toast.LENGTH_SHORT).show()
                    onNavigateBack()
                },
                colors = ButtonDefaults.buttonColors(containerColor = StreamPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_settings_button")
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                Text(
                    text = "  SAVE & RELOAD CATALOG",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}
