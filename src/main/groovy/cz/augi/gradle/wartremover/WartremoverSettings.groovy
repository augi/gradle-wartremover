package cz.augi.gradle.wartremover

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
                                'Var']
    Set<String> excludedFiles = []
    Set<String> classPaths = []

    WartremoverSettings deepClone() {
        def r = new WartremoverSettings()
        r.errorWarts = this.errorWarts.toSet()
        r.warningWarts = this.warningWarts.toSet()
        r.excludedFiles = this.excludedFiles.toSet()
        r.classPaths = this.classPaths.toSet()
        r
    }
}
