package pw.seppuku.installer.view.impl

import imgui.ImGui
import pw.seppuku.installer.seppuku.SeppukuInstaller
import pw.seppuku.installer.system.OperatingSystem
import pw.seppuku.installer.view.View
import java.io.BufferedInputStream
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import javax.swing.JFileChooser
import kotlin.concurrent.thread
import kotlin.system.exitProcess

object MultiMCView : View("Seppuku installer for MultiMC") {

    var finishedInstalling = false
    var startedInstalling = false

    var directory: File? = null

    private fun isValidDirectory(): Boolean {
        return directory != null && directory?.exists() == true && directory?.isDirectory == true
    }

    private fun install() {
        if (directory != null) {
            finishedInstalling = false
            startedInstalling = true

            thread {
                val logoStream = BufferedInputStream(URL("https://seppuku.pw/images/seppuku_logo.png").openStream())
                Files.copy(logoStream, Paths.get(directory!!.path + "/icons/seppuku_logo.png"), StandardCopyOption.REPLACE_EXISTING)

                File(directory!!.path + "/instances/seppuku/.minecraft/mods/").mkdirs()

                val instanceCfgStream = BufferedInputStream(URL("https://seppuku.pw/files/multimc/instance.cfg").openStream())
                Files.copy(instanceCfgStream, Paths.get(directory!!.path + "/instances/seppuku/instance.cfg"), StandardCopyOption.REPLACE_EXISTING)

                val mmcPackStream = BufferedInputStream(URL("https://seppuku.pw/files/multimc/mmc-pack.json").openStream())
                Files.copy(mmcPackStream, Paths.get(directory!!.path + "/instances/seppuku/mmc-pack.json"), StandardCopyOption.REPLACE_EXISTING)

                val seppukuInstaller = SeppukuInstaller(File(directory!!.path + "/instances/seppuku/.minecraft/"))
                seppukuInstaller.install()
            }

            finishedInstalling = true
        }
    }

    override fun render() {
        ImGui.run {
            if (!finishedInstalling) {
                if (!startedInstalling) {
                    text("Please select the base MultiMC directory, e.g C:\\MultiMC")

                    if (button("Select directory")) {
                        thread {
                            JFileChooser().apply {
                                currentDirectory = File(OperatingSystem.SYSTEM.getMultiMCInstallationDirectory())
                                fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                                isAcceptAllFileFilterUsed = false

                                val result = showOpenDialog(null)
                                if (result == JFileChooser.APPROVE_OPTION)
                                    directory = selectedFile
                            }
                        }
                    }

                    if (isValidDirectory()) {
                        sameLine()
                        text(directory?.path ?: "Unknown path")

                        if (button("Install"))
                            install()
                    }
                } else {
                    text("Installing...")
                }
            } else {
                text("Finished installing, a new instance has been added to your MultiMC launcher.")
                text("Make sure to restart MultiMC if it was open.")
                if (button("Exit"))
                    exitProcess(0)
            }
        }
    }
}
