package top.sandwwraith.kompositor

val junitLayer = "gradle-dependencies: testCompile junit:junit:4.12"

val jacksonLayer = """
        |gradle-ext-block: "ext.jackson-version = 2.9.3"
        |
        |gradle-dependencies:
        | - 'compile group: ''com.fasterxml.jackson.core'', name: ''jackson-core'', version: jacksonVersion'
        | - 'compile group: ''com.fasterxml.jackson.core'', name: ''jackson-databind'', version: jacksonVersion'
        | - 'compile group: ''com.fasterxml.jackson.module'', name: ''jackson-module-kotlin'', version: jacksonVersion'
        """.trimMargin()

val gradleKotlinTemplate = """buildscript {
    ext.kotlin_version = '{{kotlin.version}}'
    {{#layer:gradle-ext-block}}
    {{.}}
    {{/layer:gradle-ext-block}}

    repositories {
        jcenter()
    }

    dependencies {
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:${"$"}kotlin_version"
        {{#layer:gradle-build-dependencies}}
        {{.}}
        {{/layer:gradle-build-dependencies}}
    }
}

apply plugin: 'kotlin'

dependencies {
        compile "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${"$"}kotlin_version"

        {{#layer:gradle-dependencies}}
        {{.}}
        {{/layer:gradle-dependencies}}
}
"""