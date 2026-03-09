plugins { `kotlin-dsl` }
repositories { mavenCentral() }
dependencies {
    implementation("com.redis:testcontainers-redis:2.2.2")
    implementation("org.testcontainers:testcontainers:1.21.4")
}
