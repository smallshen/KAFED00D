plugins {
    kotlin("jvm")
}


repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    api("org.slf4j:slf4j-api:1.7.36")
}

tasks {
    compileKotlin {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn", "-Xlambdas=indy")
        }
    }

    compileTestKotlin {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn", "-Xlambdas=indy")
        }
    }
}