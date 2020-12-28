package pw.seppuku.installer.view.impl

import imgui.ImGui
import pw.seppuku.installer.view.View

object InitialView : View("Install Seppuku") {

    override fun render() {
        ImGui.run {
            text("Welcome to the Seppuku installer!")
            text("Please select the Minecraft launcher you'd like to install for.")

            if (button("Vanilla"))
                CURRENT = VanillaView

            sameLine()

            if (button("MultiMC"))
                CURRENT = MultiMCView
        }
    }
}