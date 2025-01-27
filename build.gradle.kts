import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    signing
    `java-library`
    `maven-publish`
    id("org.jetbrains.dokka") version "1.9.20"
    id("org.cadixdev.licenser") version "0.6.1"
    id("org.jetbrains.kotlin.jvm") version "2.1.10"
    id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
}

group = "me.dkim19375"
version = "3.4.7"

val javaVersion = "1.8"

license {
    header.set(rootProject.resources.text.fromFile("LICENSE"))
    include("**/*.kt")
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://s01.oss.sonatype.org/content/repositories/releases")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    api("io.github.dkim19375:dkimcore:1.5.0")
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.25")

    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    // compileOnly("org.codemc.worldguardwrapper:worldguardwrapper:1.2.0-SNAPSHOT")

    // testing libs
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.25")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.4")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.4")
}

val sourcesJar by tasks.creating(Jar::class) {
    from(sourceSets.main.get().allSource.srcDirs)
    archiveClassifier.set("sources")
}

val dokkaHtmlJar by tasks.registering(Jar::class) {
    dependsOn(tasks.dokkaHtml)
    from(tasks.dokkaHtml)
    archiveClassifier.set("javadoc")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "io.github.dkim19375"
            artifactId = "dkim-bukkit-core"
            version = project.version as String

            from(components["kotlin"])

            artifact(sourcesJar)
            artifact(dokkaHtmlJar)

            pom {
                name.set("DkimBukkitCore")
                description.set("A kotlin library used to making spigot plugins easier!")
                url.set("https://github.com/dkim19375/DkimBukkitCore")

                packaging = "jar"

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("dkim19375")
                        timezone.set("America/New_York")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/dkim19375/DkimBukkitCore.git")
                    developerConnection.set("scm:git:ssh://git@github.com:dkim19375/DkimBukkitCore.git")
                    url.set("https://github.com/dkim19375/DkimBukkitCore")
                }
            }
        }
    }
}

nexusPublishing {
    packageGroup.set("io.github.dkim19375")
    this@nexusPublishing.repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            username.set(project.findProperty("mavenUsername") as? String ?: return@sonatype)
            password.set(project.findProperty("mavenPassword") as? String ?: return@sonatype)
        }
    }
}

signing.sign(publishing.publications["mavenJava"])

tasks {
    withType<Test> {
        useJUnitPlatform()
    }

    withType<JavaCompile> {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        options.encoding = "UTF-8"
    }

    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = javaVersion
        }
    }

    withType<Wrapper> {
        finalizedBy("licenseFormat")
    }
}
