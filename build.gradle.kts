plugins {
    kotlin("jvm") version "1.6.10"
}

group = "me.coley.cafedude"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.slf4j:slf4j-api:1.7.36")
}