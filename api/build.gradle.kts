dependencies {
    compileOnly("org.spigotmc:spigot-api:1.17-R0.1-SNAPSHOT")

    compileOnly("dev.jorel:commandapi-annotations:9.5.1")
    compileOnly("dev.jorel:commandapi-bukkit-shade:9.5.1")
    annotationProcessor("dev.jorel:commandapi-annotations:9.5.1")

    compileOnly("xyz.xenondevs.invui:invui:1.33")

    compileOnly("net.kyori:adventure-api:4.17.0")
    compileOnly("net.kyori:adventure-platform-bukkit:4.3.3")
    compileOnly("net.kyori:adventure-text-minimessage:4.17.0")

    compileOnly("org.bstats:bstats-bukkit:3.0.2")

    compileOnly("me.clip:placeholderapi:2.11.6")

    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")

    compileOnly("com.github.LoneDev6:api-itemsadder:3.6.3-beta-14")

    annotationProcessor("org.jetbrains:annotations:24.1.0")

    implementation("com.google.code.gson:gson:2.11.0")

    // compileOnly("com.cronutils:cron-utils:9.2.1")
}

tasks {
    shadowJar {
        relocate("dev.jorel.commandapi", "cn.encmys.ykdz.forest.dailyshop.libraries.commandapi")
        relocate("org.bstats", "cn.encmys.ykdz.forest.dailyshop.libraries.bstats")
        relocate("net.kyori", "cn.encmys.ykdz.forest.dailyshop.libraries")
        relocate("xyz.xenondevs", "cn.encmys.ykdz.forest.dailyshop.libraries")
        relocate("org.intellij.lang.annotations", "cn.encmys.ykdz.forest.dailyshop.libraries.annotations.intellij")
        relocate("org.jetbrains.annotations", "cn.encmys.ykdz.forest.dailyshop.libraries.annotations.jetbrains")
        relocate("javax.annotation", "cn.encmys.ykdz.forest.dailyshop.libraries.annotations.javax")
        relocate("com.google", "cn.encmys.ykdz.forest.dailyshop.libraries.google")
        relocate("net.md_5", "cn.encmys.ykdz.forest.dailyshop.libraries.md_5")
        relocate("com.cronutils", "cn.encmys.ykdz.forest.dailyshop.libraries.cronutils")
    }
}