package pw.seppuku.installer.forge

import pw.seppuku.installer.system.OperatingSystem
import pw.seppuku.installer.view.impl.VanillaView
import java.io.BufferedInputStream
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.function.Predicate
import kotlin.concurrent.thread

class ForgeInstaller(private val url: URL) {

    fun install() {
        val stream = BufferedInputStream(url.openStream())
        Files.copy(stream, Paths.get("forge-installer.jar"), StandardCopyOption.REPLACE_EXISTING)

        val jar = File("forge-installer.jar")

        val classLoader = URLClassLoader(
            arrayOf<URL>(jar.toURI().toURL()),
            this::class.java.classLoader
        )

        val forgeUtil = Class.forName("net.minecraftforge.installer.json.Util", true, classLoader)
        val loadInstallProfile = forgeUtil.getDeclaredMethod("loadInstallProfile")
        val profile = loadInstallProfile.invoke(null)

        val progressCallback =
            Class.forName("net.minecraftforge.installer.actions.ProgressCallback", true, classLoader)
        val toStdOut = progressCallback.getDeclaredField("TO_STD_OUT").get(null)

        val clientInstall = Class.forName("net.minecraftforge.installer.actions.ClientInstall", true, classLoader)
        val clientInstallRun = clientInstall.getMethod("run", File::class.java, Predicate::class.java)
        val clientInstallInstance = clientInstall.constructors.first().newInstance(profile, toStdOut)

        clientInstallRun.invoke(
            clientInstallInstance,
            File(OperatingSystem.SYSTEM.getMinecraftInstallationDirectory()),
            Predicate<String> { true })
    }
}