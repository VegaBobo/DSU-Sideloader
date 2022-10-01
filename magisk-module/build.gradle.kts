import java.io.FileNotFoundException

plugins {
    kotlin("jvm")
}

tasks.register<Zip>("assembleMagiskModule") {
    val id = "dsu_sideloader"
    val name = "DSU Sideloader"
    val author = "VegaBobo"
    val description = "System mode for DSU Sideloader"

    val versionCode: Int by rootProject.extra
    val versionName: String by rootProject.extra
    val workDirectory = "${System.getProperty("user.dir")}/${project.name}"
    val moduleDirectory = "$workDirectory/src/main/resources/module"
    val outDirectory = "$workDirectory/out"

    val apkPath = "$moduleDirectory/system/priv-app/DSUSideloader/ReleaseDSUSideloader.apk"

    if (!File(apkPath).exists()) {
        throw FileNotFoundException("File $apkPath not found")
    }

    fun getProps():
            String = "id=$id\n" +
            "name=$name\n" +
            "version=$versionName\n" +
            "versionCode=$versionCode\n" +
            "author=$author\n" +
            "description=$description"

    fun getFilename():
            String = "module_${name.replace(" ", "_")}_$versionCode.zip"

    println("Building $id $versionName ($versionCode)")

    println("")
    println(getProps())
    File("$moduleDirectory/module.prop").writeText(getProps())
    archiveFileName.set(getFilename())
    destinationDirectory.set(File(outDirectory))
    from(File(moduleDirectory))
    println("")
    println("Module output: $outDirectory/${getFilename()}")
}