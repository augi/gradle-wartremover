package cz.augi.gradle.wartremover

class WartremoverExtension {
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
}
