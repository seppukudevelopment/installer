package pw.seppuku.installer.seppuku

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.URL
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.stream.Collectors

class SeppukuInstaller(private val folder: File) {

    fun install() {
        val releases = BufferedReader(InputStreamReader(
            BufferedInputStream(URL("https://api.github.com/repos/seppukudevelopment/seppuku/releases").openStream()),
            StandardCharsets.UTF_8
        )).lines().collect(Collectors.joining("\n"));

        val json = Json.parseToJsonElement(releases)
        val latestDownload = json.jsonArray[0].jsonObject["assets"]?.jsonArray?.get(0)?.jsonObject?.get("browser_download_url").toString().replace("\"", "")

        File(folder.path + "/mods/").mkdirs()

        val stream = BufferedInputStream(URL(latestDownload).openStream())
        Files.copy(stream, Paths.get(folder.path + "/mods/seppuku.jar"), StandardCopyOption.REPLACE_EXISTING)
    }
}