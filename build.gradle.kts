/* ------------------------------ Plugins ------------------------------ */
plugins {
    id("java") // Import Java plugin.
    id("java-library") // Import Java Library plugin.
    id("com.diffplug.spotless") version "8.1.0" // Import Spotless plugin.
    id("com.gradleup.shadow") version "8.3.9" // Import Shadow plugin.
    id("checkstyle") // Import Checkstyle plugin.
    eclipse // Import Eclipse plugin.
    kotlin("jvm") version "2.1.21" // Import Kotlin JVM plugin.
}

/* --------------------------- JDK / Kotlin ---------------------------- */
java {
    sourceCompatibility = JavaVersion.VERSION_17 // Compile with JDK 17 compatibility.
    toolchain { // Select Java toolchain.
        languageVersion.set(JavaLanguageVersion.of(17)) // Use JDK 17.
    }
}

kotlin { jvmToolchain(17) }

/* ----------------------------- Metadata ------------------------------ */

version = "2.9.16" // Declare plugin version (will be in .jar).

group = "net.trueog.illegalstack-og" // Declare bundle identifier.

val apiVersion = "1.19" // Declare minecraft server target version.

/* ----------------------------- Resources ----------------------------- */
tasks.named<ProcessResources>("processResources") {
    val props = mapOf("version" to version, "apiVersion" to apiVersion)
    inputs.properties(props) // Indicates to rerun if version changes.
    filesMatching("plugin.yml") { expand(props) }
    from("LICENSE") { into("/") } // Bundle licenses into jarfiles.
}

/* ---------------------------- Repos ---------------------------------- */
repositories {
    mavenCentral() // Import the Maven Central Maven Repository.
    gradlePluginPortal() // Import the Gradle Plugin Portal Maven Repository.
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
    } // Import the OSS Sonatype Maven Repository.
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") } // Import the PaperMC Maven Repository.
    maven {
        url = uri("https://repo.dmulloy2.net/nexus/repository/public/")
    } // Import the ProtocolLib Maven Repository.
    maven { url = uri("https://maven.elmakers.com/repository/") } // Import the Magic plugin Maven Repository.
    maven { url = uri("https://repo.codemc.org/repository/maven-public/") } // Import the CodeMC Maven Repository.
    maven { url = uri("https://jitpack.io") } // Import the Jitpack Maven Repository.
}

/* ---------------------- Java project deps ---------------------------- */
dependencies {
    compileOnly("dev.folia:folia-api:1.19.4-R0.1-SNAPSHOT") // Declare Folia API to be packaged.
    compileOnly(files("libs/ProtocolLib-5.0.jar")) // Import Legacy ProtocolLib API.
    compileOnly("com.elmakers.mine.bukkit:MagicAPI:10.2") // Import Magic API.
    compileOnly("de.tr7zw:item-nbt-api-plugin:2.14.1") // Import Item NBT API.
    compileOnly("com.github.TheBusyBiscuit:Slimefun4:RC-30") { isTransitive = false } // Import SlimeFun4.
    compileOnly("io.netty:netty-all:4.1.110.Final") { // Import netty API.
        because(
            "The version aligns with the version used by Minecraft itself." +
                "The minecraft server ships netty as well, so we don't need to include it in the jar."
        )
    }
    compileOnly("com.gmail.nossr50.mcMMO:mcMMO:2.1.217") { isTransitive = false } // Import mcMMO API.
    compileOnly("fr.minuskube.inv:smart-invs:1.2.7") // Import SmartInvs API.
    compileOnly("com.github.brcdev-minecraft:shopgui-api:3.0.0") // Import ShopGUI API.

    testImplementation("dev.folia:folia-api:1.19.4-R0.1-SNAPSHOT")
    testImplementation(platform("org.junit:junit-bom:5.13.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core:5.14.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test { useJUnitPlatform() }

/* ---------------------- Reproducible jars ---------------------------- */
tasks.withType<AbstractArchiveTask>().configureEach { // Ensure reproducible .jars
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}

/* ----------------------------- Shadow -------------------------------- */
tasks.shadowJar {
    exclude("io.github.miniplaceholders.*") // Exclude the MiniPlaceholders package from being shadowed.
    archiveClassifier.set("") // Use empty string instead of null.
    minimize()
}

tasks.jar { archiveClassifier.set("part") } // Applies to root jarfile only.

tasks.build { dependsOn(tasks.spotlessApply, tasks.shadowJar) } // Build depends on spotless and shadow.

/* --------------------------- Javac opts ------------------------------- */
tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-parameters") // Enable reflection for java code.
    options.isFork = true // Run javac in its own process.
    options.compilerArgs.add("-Xlint:deprecation") // Trigger deprecation warning messages.
    options.encoding = "UTF-8" // Use UTF-8 file encoding.
}

/* ----------------------------- Auto Formatting ------------------------ */
spotless {
    java {
        eclipse().configFile("config/formatter/eclipse-java-formatter.xml") // Eclipse java formatting.
        leadingTabsToSpaces() // Convert leftover leading tabs to spaces.
        removeUnusedImports() // Remove imports that aren't being called.
    }
    kotlinGradle {
        ktfmt().kotlinlangStyle().configure { it.setMaxWidth(120) } // JetBrains Kotlin formatting.
        target("build.gradle.kts", "settings.gradle.kts") // Gradle files to format.
    }
}

checkstyle {
    toolVersion = "10.18.1"
    config = resources.text.fromFile(file("config/checkstyle/checkstyle.xml"))
    isIgnoreFailures = true
    isShowViolations = true
    configProperties["basedir"] = rootProject.projectDir.absolutePath
}

tasks.withType<Checkstyle>().configureEach {
    configDirectory.set(layout.projectDirectory.dir("config/checkstyle"))
    inputs.file("config/checkstyle/checkstyle.xml")
    classpath = files()
}

tasks.named("compileJava") {
    dependsOn("spotlessApply") // Run spotless before compiling with the JDK.
}

tasks.named("spotlessCheck") {
    dependsOn("spotlessApply") // Run spotless before checking if spotless ran.
}
