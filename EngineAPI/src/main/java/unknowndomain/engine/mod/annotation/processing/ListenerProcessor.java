package unknowndomain.engine.mod.annotation.processing;

import unknowndomain.engine.event.Event;
import unknowndomain.engine.event.Listener;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.List;
import java.util.Set;

import static unknowndomain.engine.mod.annotation.processing.ProcessingUtils.getQualifiedName;
import static unknowndomain.engine.mod.annotation.processing.ProcessingUtils.hasModifier;

@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class ListenerProcessor extends AbstractProcessor {

    private static final String CLASS_NAME = Listener.class.getName();

    private TypeMirror eventTypeMirror;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(CLASS_NAME);
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        eventTypeMirror = processingEnv.getElementUtils().getTypeElement(Event.class.getName()).asType();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver()) {
            for (Element element : roundEnv.getElementsAnnotatedWith(Listener.class)) {
                ExecutableElement method = (ExecutableElement) element;

                TypeElement owner = (TypeElement) method.getEnclosingElement();
                List<? extends VariableElement> parameters = method.getParameters();

                if (parameters.size() != 1) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, String.format("The count of listener method parameter must be 1. Listener: %s.%s(?)", owner.getQualifiedName(), method.getSimpleName()));
                }

                VariableElement event = parameters.get(0);

                if (!processingEnv.getTypeUtils().isAssignable(event.asType(), eventTypeMirror)) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, String.format("The parameter of listener method must be Event or it's child class. Listener: %s.%s(%s)", owner.getQualifiedName(), method.getSimpleName(), getQualifiedName(event.asType())));
                }

                if (!hasModifier(method, Modifier.PUBLIC)) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, String.format("Listener method must be public. Listener: %s.%s(%s)", owner.getQualifiedName(), method.getSimpleName(), getQualifiedName(event.asType())));
                }

                if (method.getReturnType().getKind() != TypeKind.VOID) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, String.format("The return type of listener method must be void. Listener: %s.%s(%s)", owner.getQualifiedName(), method.getSimpleName(), getQualifiedName(event.asType())));
                }
            }
        }
        return false;
    }
}
