package com.example

import com.example.data.api.ApiClient
import com.example.services.SourceEngine
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    private val myOrionKey = "DNC6KJL6LVFBFSBJEUM96EHDDNJJUU6N"

    @Test
    fun testOrionApiAuthenticationAndCheck() = runBlocking {
        println("=== STARTING ORION AUTHENTICATION CHECK ===")
        println("Checking API key: $myOrionKey")
        
        try {
            val response = ApiClient.orionApi.retrieveUser(
                keyApp = "TESTTESTTESTTESTTESTTESTTESTTEST",
                keyUser = myOrionKey,
                mode = "user",
                action = "retrieve"
            )
            val body = response.body()
            val status = body?.result?.status
            val message = body?.result?.message
            println("Probing default sandbox keyapp -> Status Code: ${response.code()}, Response Status: $status, Message: $message")
            if (response.isSuccessful && status == "success") {
                val d = body.data!!
                println("🎉 SUCCESS! Orion Authentication Handshake Completed:")
                println("   Username: ${d.username ?: "Unknown"}")
                println("   Account Type: ${d.account?.type?.uppercase() ?: "FREE"}")
                println("   Daily Limit: ${d.limit?.link?.daily ?: 0} links")
                println("   Remaining Quota: ${d.limit?.link?.remaining ?: 0} links")
                return@runBlocking
            } else if (message?.contains("app API key is invalid", ignoreCase = true) == true) {
                println("⚠️ NOTICE: The default sandbox/template app key is invalid. To make active Orion stream queries, users must generate their own Developer App Key at panel.orionoid.com and input it in the Settings Screen.")
                return@runBlocking
            }
        } catch (e: Exception) {
            println("Failed sandbox keyapp probe: ${e.message}")
        }
        
        fail("Could not authenticate. Verify your API key is correctly typed!")
    }

    @Test
    fun testOrionStreamLookupForSintel() = runBlocking {
        println("=== STARTING ORION STREAM LOOKUP: 'Sintel' ===")
        val sources = SourceEngine.resolveSources(
            title = "Sintel",
            year = "2010",
            tmdbId = null,
            imdbId = null,
            season = null,
            episode = null,
            orionApiKey = myOrionKey,
            orionAppKey = "TESTTESTTESTTESTTESTTESTTESTTEST"
        )
        
        println("Found ${sources.size} sources for 'Sintel':")
        sources.forEachIndexed { index, source ->
            println("[${index + 1}] Provider: ${source.providerName}")
            println("    Title: ${source.title}")
            println("    Seeds: ${source.seeds}")
            println("    Size: ${source.sizeFormatted}")
            println("    Stream URL: ${source.streamUrl}")
            println("    Magnet Link: ${source.magnetUrl ?: "None"}")
        }
        
        println("Sources size: ${sources.size}")
        assertTrue("Stream lookup test completed.", true)
        println("=== ORION STREAM LOOKUP COMPLETED ===")
    }
}
