package pw.seppuku.installer.view

import glm_.vec4.Vec4
import gln.glClearColor
import gln.glViewport
import gln.texture.glGenTexture
import imgui.classes.Context
import imgui.impl.gl.ImplGL3
import imgui.impl.gl.glslVersion
import imgui.impl.glfw.ImplGlfw
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.stb.STBImage.stbi_load
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.Platform
import uno.glfw.GlfwWindow
import uno.glfw.VSync
import uno.glfw.glfw
import glm_.vec2.Vec2
import imgui.*

class ViewRenderer {

    private val window: GlfwWindow
    private val ctx: Context

    private val clearColor = Vec4((22.1 / 255), (35.0 / 255), (53.8 / 255), 1f)

    private val implGlfw: ImplGlfw
    private val implGl3: ImplGL3

    private val background: Texture
    private var flags: Int = 0

    init {
        flags = WindowFlag.NoTitleBar.i
        flags = flags.or(WindowFlag.NoBackground.i)
        flags = flags.or(WindowFlag.NoBringToFrontOnFocus.i)
        flags = flags.or(WindowFlag.NoInputs.i)
        flags = flags.or(WindowFlag.NoMove.i)
        flags = flags.or(WindowFlag.NoResize.i)
        flags = flags.or(WindowFlag.NoFocusOnAppearing.i)
        flags = flags.or(WindowFlag.NoScrollbar.i)

        glfw {
            init()

            windowHint {
                when (Platform.get()) {
                    Platform.MACOSX -> {
                        glslVersion = 150
                        context.version = "3.2"
                        profile = uno.glfw.windowHint.Profile.core
                        forwardComp = true
                    }
                    else -> {
                        glslVersion = 130
                        context.version = "3.0"
                    }
                }
            }
        }

        window = GlfwWindow(800, 600, "seppuku-installer")
        window.resizable = false

        window.makeContextCurrent()
        glfw.swapInterval = VSync.OFF

        GL.createCapabilities()

        ctx = Context()

        ImGui.styleColorsDark()

        implGlfw = ImplGlfw(window, true)
        implGl3 = ImplGL3()

        background = Texture("/bg.jpeg")

        window.loop(::loop)

        implGl3.shutdown()
        implGlfw.shutdown()
        ctx.destroy()
        window.destroy()
        glfw.terminate()
    }

    private fun loop(stack: MemoryStack) {
        implGl3.newFrame()
        implGlfw.newFrame()

        ImGui.run {
            newFrame()

            setNextWindowPos(Vec2(0, 0))
            begin("Background", null, 0, flags)
            image(background.id, Vec2(800, 600))
            end()

            View.CURRENT.internalRender()

            // render ImGui
            render()
        }

        glViewport(window.framebufferSize)
        glClearColor(clearColor)
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)

        implGl3.renderDrawData(ImGui.drawData!!)
    }
}