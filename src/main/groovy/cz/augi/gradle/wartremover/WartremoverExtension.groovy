package cz.augi.gradle.wartremover

import groovy.transform.CompileStatic

@CompileStatic
class WartremoverExtension extends WartremoverSettings {
    private WartremoverSettings testValue = this
    void test(Closure closure) {
        if (testValue == this) {
            testValue = this.deepClone()
        }
        def closureToCall = (Closure)closure.clone()
        closureToCall.setResolveStrategy(Closure.DELEGATE_FIRST)
        closureToCall.setDelegate(testValue)
        if (closureToCall.getMaximumNumberOfParameters() == 0) {
            closureToCall.call()
        } else {
            closureToCall.call(testValue)
        }
    }
    WartremoverSettings getTest() { testValue }
}
