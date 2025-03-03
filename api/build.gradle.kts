java {
    withJavadocJar()
    withSourcesJar()
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")

    compileOnly("org.bstats:bstats-bukkit:3.0.2")

    compileOnly("me.clip:placeholderapi:2.11.6")

    compileOnly("com.github.LoneDev6:api-itemsadder:3.6.3-beta-14")

    compileOnly("xyz.xenondevs.invui:invui:2.0.0-alpha.7")

    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")

    compileOnly("org.jetbrains:annotations:24.1.0")
    annotationProcessor("org.jetbrains:annotations:24.1.0")

    compileOnly("com.google.code.gson:gson:2.11.0")

    compileOnly("cn.encmys:HyphaRepo:0.1.0-Beta")
}