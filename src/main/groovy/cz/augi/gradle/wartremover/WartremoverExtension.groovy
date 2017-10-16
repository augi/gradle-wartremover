package cz.augi.gradle.wartremover

import org.gradle.util.ConfigureUtil

class WartremoverExtension extends WartremoverSettings {
    private WartremoverSettings testValue = this
    void test(Closure closure) {
        if (testValue == this) {
            testValue = this.deepClone()
        }
        ConfigureUtil.configure(closure, testValue)
    }
    WartremoverSettings getTest() { testValue }
}
