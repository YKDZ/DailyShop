dependencies {
    compileOnly("org.spigotmc:spigot-api:1.17-R0.1-SNAPSHOT")

    compileOnly("dev.jorel:commandapi-annotations:9.3.0")
    compileOnly("dev.jorel:commandapi-bukkit-shade:9.3.0")
    annotationProcessor("dev.jorel:commandapi-annotations:9.3.0")

    compileOnly("xyz.xenondevs.invui:invui:1.26")

    compileOnly("net.kyori:adventure-api:4.16.0")
    compileOnly("net.kyori:adventure-platform-bukkit:4.3.1")
    compileOnly("net.kyori:adventure-text-minimessage:4.16.0")

    compileOnly("org.bstats:bstats-bukkit:3.0.2")

    compileOnly("com.github.Rubix327:ItemsLangAPI:1.0.2")

    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")

    compileOnly("com.github.LoneDev6:api-itemsadder:3.6.1")

    // compileOnly("com.cronutils:cron-utils:9.2.1")
}

tasks {
    shadowJar {
        relocate("dev.jorel.commandapi", "cn.encmys.ykdz.forest.dailyshop.libraries.commandapi")
        relocate("org.bstats", "cn.encmys.ykdz.forest.dailyshop.libraries.bstats")
        relocate("net.kyori", "cn.encmys.ykdz.forest.dailyshop.libraries")
        relocate("xyz.xenondevs", "cn.encmys.ykdz.forest.dailyshop.libraries")
        relocate("me.rubix327.itemslangapi", "cn.encmys.ykdz.forest.dailyshop.libraries.itemslangapi")
        relocate("org.intellij.lang.annotations", "cn.encmys.ykdz.forest.dailyshop.libraries.annotations.intellij")
        relocate("org.jetbrains.annotations", "cn.encmys.ykdz.forest.dailyshop.libraries.annotations.jetbrains")
        relocate("javax.annotation", "cn.encmys.ykdz.forest.dailyshop.libraries.annotations.javax")
        relocate("com.google", "cn.encmys.ykdz.forest.dailyshop.libraries.google")
        relocate("net.md_5", "cn.encmys.ykdz.forest.dailyshop.libraries.md_5")
        relocate("com.cronutils", "cn.encmys.ykdz.forest.dailyshop.libraries.cronutils")
    }
}