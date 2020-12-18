package com.axgrid.signal.processors;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;


@SupportedAnnotationTypes("com.axgrid.signal.processors.RawObject")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@SupportedOptions("debug")
public class AxSignalProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return false;
    }
}
