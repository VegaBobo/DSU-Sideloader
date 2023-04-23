plugins {
    kotlin("jvm")
}

tasks.register<Zip>("assembleMagiskModule") {
    val id = "dsu_sideloader"
    val name = "DSU Sideloader"
    val author = "VegaBobo"
    val description = "System mode for DSU Sideloader"
    val updateJson = "https://raw.githubusercontent.com/VegaBobo/DSU-Sideloader/master/other/module_updater/updater_module_gsid.json"

    val versionCode: Int by rootProject.extra
    val versionName: String by rootProject.extra
    val workDirectory = "${System.getProperty("user.dir")}/${project.name}"
    val moduleDirectory = "$workDirectory/src/main/resources/module"
    val outDirectory = "$workDirectory/out"

    val releaseApk = File("${System.getProperty("user.dir")}/app/build/outputs/apk/release/app-release.apk")

    if (!releaseApk.exists()) {
        return@register
    }

    val apkPath = File("$moduleDirectory/system/priv-app/DSUSideloader/ReleaseDSUSideloader.apk")
    if (apkPath.exists()) apkPath.delete()
    releaseApk.copyTo(apkPath)

    fun getProps():
            String = "id=$id\n" +
            "name=$name\n" +
            "version=$versionName\n" +
            "versionCode=$versionCode\n" +
            "author=$author\n" +
            "description=$description\n" +
            "updateJson=$updateJson"

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