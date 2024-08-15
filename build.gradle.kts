plugins {
    id("java")
    id("maven-publish")
    id("io.github.goooler.shadow") version "8.1.8"
}

allprojects {

    project.group = "cn.encmys.ykdz.forest"
    project.version = "0.2.2-Beta"

    apply<JavaPlugin>()
    apply(plugin = "java")
    apply(plugin = "io.github.goooler.shadow")
    apply(plugin = "org.gradle.maven-publish")

    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://mvn.lumine.io/repository/maven-public/")
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
}

subprojects {
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

    tasks.shadowJar {
        destinationDirectory.set(file("$rootDir/target"))
        archiveClassifier.set("")
        archiveFileName.set("DailyShop-" + project.name + "-" + project.version + ".jar")
    }

    tasks.shadowJar {
        destinationDirectory.set(file("C:\\Users\\YKDZ\\Desktop\\Forest项目\\插件测试端\\plugins"))
        archiveClassifier.set("")
        archiveFileName.set("DailyShop-" + project.name + "-" + project.version + ".jar")
    }

    if ("api" == project.name) {
        publishing {
            publications {
                create<MavenPublication>("mavenJava") {
                    groupId = "cn.encmys"
                    artifactId = "DailyShop"
                    version = rootProject.version.toString()
                    artifact(tasks.shadowJar)
                }
            }
        }
    }
}