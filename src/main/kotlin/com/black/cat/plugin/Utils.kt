package com.black.cat.plugin

import com.google.gson.Gson
import com.google.gson.JsonParser
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.jvm.tasks.Jar
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

fun File.zipDirectory(saveFile: File, predicate: (File) -> Boolean) {
    if (!this.isDirectory) return
    val repoPathLength = this.absolutePath.length + 1
    if (saveFile.exists()) saveFile.delete()
    saveFile.parentFile.mkdirs()
    val zipOutputStream = ZipOutputStream(FileOutputStream(saveFile))
    this.walk()
        .filter { file -> predicate(file) }
        .forEach { file ->
            val entryName = file.absolutePath.substring(repoPathLength)
            val zipEntry = ZipEntry(entryName)
            zipOutputStream.putNextEntry(zipEntry)
            file.inputStream().use {
                it.copyTo(zipOutputStream)
            }
            zipOutputStream.closeEntry()
        }
    zipOutputStream.closeEntry()
    zipOutputStream.close()
}

fun getAuthToken(mavenConfig: MavenConfig): String =
    encodeBase64(mavenConfig.mavenCentralUsername.get(), mavenConfig.mavenCentralPassword.get())

// 将username:password进行base64编码
private fun encodeBase64(username: String, password: String): String {
    val auth = "$username:$password"
    return java.util.Base64.getEncoder().encodeToString(auth.toByteArray())
}


fun File.uploadFile(url: String = "https://central.sonatype.com/api/v1/publisher/upload", authToken: String): String {
    if (this.exists()) {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.setRequestProperty("Authorization", "Bearer $authToken")
        val boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW"
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
        val outputStream = connection.outputStream
        outputStream.write("--$boundary\r\n".toByteArray())
        outputStream.write("Content-Disposition: form-data; name=\"bundle\"; filename=\"${this.name}\"\r\n".toByteArray())
        outputStream.write("Content-Type: application/octet-stream\r\n\r\n".toByteArray())
        this.inputStream().use { input ->
            input.copyTo(outputStream)
        }
        outputStream.write("\r\n".toByteArray())
        outputStream.write("--$boundary--\r\n".toByteArray())
        outputStream.flush()
        outputStream.close()
        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_CREATED) {
            val uid = (connection.content as InputStream).readBytes().decodeToString()
            println("[Result] Success! File Created by $uid.")
            return uid
        } else {
            throw RuntimeException("Failed to upload file. Response code: $responseCode ${connection.responseMessage}")
        }
    } else {
        throw FileNotFoundException("Upload for ${this.absolutePath}")
    }
}

fun decodeUnicodeEscapes(input: String): String {
    // 匹配\u后跟四个十六进制数字的Unicode转义序列
    val unicodePattern = Regex("""\\u([0-9a-fA-F]{4})""")
    return unicodePattern.replace(input) { matchResult ->
        // 将匹配的十六进制数字转换为字符
        val code = matchResult.groupValues[1].toInt(16)
        // 将Unicode码点转换为字符
        String(Character.toChars(code))
    }
}

fun checkUploadStatus(url: String, authToken: String): String {
    val connection = URL(url).openConnection() as HttpURLConnection
    connection.requestMethod = "POST"
    connection.setRequestProperty("Authorization", "Bearer $authToken")
    val responseCode = connection.responseCode
    if (responseCode == HttpURLConnection.HTTP_OK) {
        val response = connection.inputStream.bufferedReader().use { it.readText() }
        // 将response 解析成json对象，然后根据uid获取上传状态
        val jsonObject = JsonParser.parseString(response).asJsonObject
        // PENDING, VALIDATING, VALIDATED, PUBLISHING, PUBLISHED, FAILED
        val status = jsonObject.get("deploymentState").asString
        println("[Result] Deployment status1: $status")
        if (status == "FAILED") {
            val errors = decodeUnicodeEscapes(Gson().toJson(jsonObject.get("errors")))
            throw RuntimeException("Validate FAILED\nCausing by: $errors")
        } else {
            return status
        }
    } else {
        throw RuntimeException("Failed to check upload status. Response code: $responseCode ${connection.responseMessage}")
    }
}


val statusInfo = mapOf(
    "PENDING" to "部署已上传并等待验证服务处理",
    "VALIDATING" to "验证服务正在处理部署",
    "VALIDATED" to "部署已通过验证，正在等待用户通过中央门户 UI 手动发布 https://central.sonatype.com",
    "PUBLISHING" to "部署已自动或手动发布，并正在上传至 Maven Central",
    "PUBLISHED" to "部署已成功上传至 Maven Central",
    "FAILED" to "部署遇到错误（字段中将显示附加上下文errors）"
)