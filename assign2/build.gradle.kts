plugins {
    id("java")

    // application {
    //     mainClassName = "pt.up.fe.cpd.proj2"
    // }
}

group = "pt.up.fe.cpd.proj2"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}