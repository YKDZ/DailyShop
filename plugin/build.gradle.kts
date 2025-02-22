dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")

    compileOnly("io.lumine:MythicLib-dist:1.6.2-SNAPSHOT")
    compileOnly("net.Indyuce:MMOItems-API:6.9.5-SNAPSHOT")

    compileOnly("com.github.LoneDev6:api-itemsadder:3.6.3-beta-14")

    compileOnly("org.jetbrains:annotations:24.1.0")
    annotationProcessor("org.jetbrains:annotations:24.1.0")

    implementation("xyz.xenondevs.invui:invui:2.0.0-alpha.7")

    implementation("org.bstats:bstats-bukkit:3.0.2")

    compileOnly("me.clip:placeholderapi:2.11.6")

    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")

    compileOnly("org.xerial:sqlite-jdbc:3.45.3.0")

    implementation("com.google.code.gson:gson:2.11.0")

    implementation("com.zaxxer:HikariCP:5.1.0")

    compileOnly("cn.encmys:HyphaScript:0.1.0-Beta")

    compileOnly("cn.encmys:HyphaRepo:0.1.0-Beta")

    compileOnly("cn.encmys:HyphaUtils:0.1.0-Beta")

    compileOnly("io.lumine:Mythic-Dist:5.6.1")

    implementation(project(":api"))
}

tasks {
    shadowJar {
        archiveFileName.set(rootProject.name + "-" + project.version + ".jar")
        relocate("org.bstats", "${project.group}.hyphashop.libraries.bstats")
    }
}