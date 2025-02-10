package cz.augi.gradle.wartremover

import groovy.transform.CompileStatic
import org.gradle.api.provider.Property

@CompileStatic
class WartremoverSettings {
    Set<String> errorWarts = []
    Set<String> warningWarts = ['Any',
                                'AsInstanceOf',
                                'DefaultArguments',
                                'EitherProjectionPartial',
                                'IsInstanceOf',
                                'TraversableOps',
                                'NonUnitStatements',
                                'Null',
                                'OptionPartial',
                                'Product',
                                'Return',
                                'Serializable',
                                'StringPlusAny',
                                'Throw',
                                'TryPartial',
                                'Var'].toSet()
    Set<String> excludedFiles = []
    Set<String> classPaths = []
    Property<String> scalaVersion
    Property<String> wartremoverVersion

    WartremoverSettings deepClone() {
        def r = new WartremoverSettings()
        r.errorWarts = this.errorWarts.toSet()
        r.warningWarts = this.warningWarts.toSet()
        r.excludedFiles = this.excludedFiles.toSet()
        r.classPaths = this.classPaths.toSet()
        r.scalaVersion = this.scalaVersion
        r.wartremoverVersion = this.wartremoverVersion
        return r
    }
}
