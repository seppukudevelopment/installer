package pw.seppuku.installer.view.impl

import glm_.vec2.Vec2
import imgui.ImGui
import pw.seppuku.installer.forge.ForgeInstaller
import pw.seppuku.installer.seppuku.SeppukuInstaller
import pw.seppuku.installer.system.OperatingSystem
import pw.seppuku.installer.view.View
import java.io.File
import java.net.URL
import kotlin.concurrent.thread
import kotlin.system.exitProcess

object VanillaView : View("Seppuku installer for Vanilla") {

    var finishedInstalling = false
    var startedInstalling = false

    private fun install() {
        finishedInstalling = false
        startedInstalling = true

        thread {
            val installThread = thread {
                val forgeInstaller =
                    ForgeInstaller(URL("https://files.minecraftforge.net/maven/net/minecraftforge/forge/1.12.2-14.23.5.2854/forge-1.12.2-14.23.5.2854-installer.jar"))
                forgeInstaller.install()

                val seppukuInstaller =
                    SeppukuInstaller(File(OperatingSystem.SYSTEM.getMinecraftInstallationDirectory()))
                seppukuInstaller.install()
            }

            while (installThread.isAlive)
                Thread.sleep(100)

            finishedInstalling = true
        }
    }

    override fun render() {
        ImGui.run {
            if (!finishedInstalling) {
                text("Found Minecraft directory at " + OperatingSystem.SYSTEM.getMinecraftInstallationDirectory())

                if (!startedInstalling) {
                    if (button("Install"))
                        install()
                } else {
                    text("Installing...")
                }
            } else {
                text("Finished installing, a new profile has been made in your Minecraft launcher.")
                text("Make sure to restart the launcher if it was open.")
                if (button("Exit"))
                    exitProcess(0)
            }
        }
    }
}