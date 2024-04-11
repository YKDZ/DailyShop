plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

allprojects {

    project.group = "cn.encmys.ykdz.forest"
    project.version = "0.1.0"

    apply<JavaPlugin>()
    apply(plugin = "java")
    apply(plugin = "com.github.johnrengelman.shadow")

    repositories {
        mavenLocal()
        mavenCentral()
        maven(url = "https://mvn.lumine.io/repository/maven-public/")
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://maven.aliyun.com/repository/public/")
        maven("https://papermc.io/repo/repository/maven-public/")
        maven("https://oss.sonatype.org/content/groups/public/")
        maven("https://jitpack.io/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://maven.aliyun.com/repository/public/")
        maven("https://papermc.io/repo/repository/maven-public/")
        maven("https://oss.sonatype.org/content/groups/public/")
        maven("https://repo.dmulloy2.net/repository/public/")
        maven("https://repo.codemc.org/repository/maven-public/")
        maven("https://maven.enginehub.org/repo/")
        maven("https://jitpack.io/")
        maven("https://repo.rapture.pw/repository/maven-releases/")
        maven("https://nexus.phoenixdevt.fr/repository/maven-public/")
        maven("https://r.irepo.space/maven/")
        maven("https://repo.auxilor.io/repository/maven-public/")
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        maven("https://repo.xenondevs.xyz/releases/")
        maven("https://nexus.phoenixdevt.fr/repository/maven-public/")
        maven("https://repo.dmulloy2.net/repository/public/")
        maven("https://repo.xenondevs.xyz/releases")
        maven("https://repo.oraxen.com/releases")
        maven("https://r.irepo.space/maven/")
    }

    tasks.processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("*plugin.yml") {
            expand(props)
        }
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(17)
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.17-R0.1-SNAPSHOT")

    compileOnly("io.lumine:MythicLib-dist:1.6.2-SNAPSHOT")
    compileOnly("net.Indyuce:MMOItems-API:6.9.5-SNAPSHOT")

    compileOnly("com.github.LoneDev6:api-itemsadder:3.6.1")

    compileOnly("io.th0rgal:oraxen:1.171.0")

    compileOnly("io.lumine:Mythic-Dist:5.6.1")

    compileOnly("pers.neige.neigeitems:NeigeItems:1.16.24")

    compileOnly("com.github.Xiao-MoMi:Custom-Fishing:2.1.2")
    compileOnly("com.github.Xiao-MoMi:Custom-Crops:3.4.3")

    compileOnly("com.comphenix.protocol:ProtocolLib:5.1.0")

    compileOnly("dev.jorel:commandapi-annotations:9.3.0")
    implementation("dev.jorel:commandapi-bukkit-shade:9.3.0")
    annotationProcessor("dev.jorel:commandapi-annotations:9.3.0")

    implementation("xyz.xenondevs.invui:invui:1.26")

    implementation("net.kyori:adventure-api:4.14.0")
    implementation("net.kyori:adventure-platform-bukkit:4.3.1")
    implementation("net.kyori:adventure-text-minimessage:4.14.0")

    implementation("org.bstats:bstats-bukkit:3.0.2")

    compileOnly("me.clip:placeholderapi:2.11.5")

    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")

    implementation("com.github.Rubix327:ItemsLangAPI:1.0.2")
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
    }
}