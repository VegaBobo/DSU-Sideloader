import java.io.FileNotFoundException

plugins {
    `kotlin-dsl`
}

tasks.register<Zip>("assembleMagiskModule") {

    val id = "dsu_sideloader"
    val name = "DSU Sideloader"
    val author = "VegaBobo"
    val description = "System mode for DSU Sideloader"

    val versionCode: Int by rootProject.extra
    val versionName: String by rootProject.extra

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
    val workDirectory = "${System.getProperty("user.dir")}/${project.name}"
    val moduleDirectory = "$workDirectory/src/main/resources"
    val outDirectory = "$workDirectory/out"

    val apkPath = "$moduleDirectory/system/priv-app/DSUSideloader/ReleaseDSUSideloader.apk"

    if (!File(apkPath).exists()) {
        System.err.println("A release build of DSU Sideloader was not found, please build one and move it to right directory")
        throw FileNotFoundException("File $apkPath not found")
    }

    println("")
    println(getProps())
    File("$moduleDirectory/module.prop").writeText(getProps())
    archiveFileName.set(getFilename())
    destinationDirectory.set(File(outDirectory))
    from(File(moduleDirectory))
    println("")
    println("Module output: $outDirectory/${getFilename()}")
}