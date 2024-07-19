sourceSets {
    create("premium") {
        java.srcDir("src/premium/java")
        resources.srcDir("src/premium/resources")
        compileClasspath += sourceSets["main"].output
        runtimeClasspath += sourceSets["main"].output
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.17-R0.1-SNAPSHOT")

    compileOnly("io.lumine:MythicLib-dist:1.6.2-SNAPSHOT")
    compileOnly("net.Indyuce:MMOItems-API:6.9.5-SNAPSHOT")

    compileOnly("com.github.LoneDev6:api-itemsadder:3.6.1")

    compileOnly("org.jetbrains:annotations:24.1.0")

    compileOnly("io.lumine:Mythic-Dist:5.6.1")

    compileOnly("com.comphenix.protocol:ProtocolLib:5.1.0")

    compileOnly("dev.jorel:commandapi-annotations:9.5.1")
    implementation("dev.jorel:commandapi-bukkit-shade:9.5.1")
    annotationProcessor("dev.jorel:commandapi-annotations:9.5.1")

    implementation("xyz.xenondevs.invui:invui:1.33")

    implementation("net.kyori:adventure-api:4.17.0")
    implementation("net.kyori:adventure-platform-bukkit:4.3.3")
    implementation("net.kyori:adventure-text-minimessage:4.17.0")

    implementation("org.bstats:bstats-bukkit:3.0.2")

    compileOnly("me.clip:placeholderapi:2.11.6")

    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")

    compileOnly("org.xerial:sqlite-jdbc:3.45.3.0")

    implementation(project(":api"))
}

tasks {
    shadowJar {
        archiveFileName.set(rootProject.name + "-" + version + ".jar")
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