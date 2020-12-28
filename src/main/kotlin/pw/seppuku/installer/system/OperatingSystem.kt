package pw.seppuku.installer.system

enum class OperatingSystem(val matches: List<String>) {

    WINDOWS(listOf("win")),
    UNIX(listOf("nix", "nux")),
    MAC(listOf("mac"));

    fun getMultiMCInstallationDirectory(): String {
        val home = System.getProperty("user.home")

        return when(this) {
            WINDOWS -> {
                home
            }

            UNIX -> {
                "$home/.local/share/multimc"
            }

            MAC -> {
                home
            }
        }
    }

    fun getMinecraftInstallationDirectory(): String {
        val home = System.getProperty("user.home")

        return when(this) {
            WINDOWS -> {
                "$home/AppData/Roaming/.minecraft"
            }

            UNIX -> {
                "$home/.minecraft"
            }

            MAC -> {
                "$home/Library/Application Support/minecraft"
            }
        }
    }

    companion object {

        val SYSTEM by lazy { getSystem() }

        private fun getSystem(string: String): OperatingSystem {
            values().forEach { enum ->
                if (enum.matches.any { string.contains(it, true) })
                    return enum
            }

            throw Exception("Unknown operating system?!")
        }

        private fun getSystem(): OperatingSystem {
            return getSystem(System.getProperty("os.name"))
        }
    }
}