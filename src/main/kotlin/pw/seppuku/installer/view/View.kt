package pw.seppuku.installer.view

import glm_.vec2.Vec2
import imgui.ImGui
import imgui.WindowFlag
import pw.seppuku.installer.view.impl.InitialView

abstract class View(private val title: String, private val flags: Int = 0) {

    abstract fun render()

    open fun internalRender() {
        ImGui.begin(
            title,
            null,
            0,
            WindowFlag.NoResize.i.or(WindowFlag.NoCollapse.i).or(flags).or(WindowFlag.NoScrollbar.i)
                .or(WindowFlag.NoScrollWithMouse.i).or(WindowFlag.AlwaysAutoResize.i)
        )

        render()

        if (title != "Install Seppuku") {
            ImGui.run {
                if (button("Back"))
                    CURRENT = InitialView
            }
        }

        ImGui.end()


    }

    companion object {

        var CURRENT: View = InitialView
    }
}