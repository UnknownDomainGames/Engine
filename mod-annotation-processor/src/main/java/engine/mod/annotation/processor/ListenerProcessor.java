package engine.mod.annotation.processor;

import engine.event.Event;
import engine.event.Listener;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.List;
import java.util.Set;

import static engine.mod.annotation.processor.ProcessorUtils.hasModifier;

@SupportedSourceVersion(SourceVersion.RELEASE_11)
@SupportedAnnotationTypes("engine.event.Listener")
public class ListenerProcessor extends AbstractProcessor {

    private TypeMirror eventTypeMirror;

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

                List<? extends VariableElement> parameters = method.getParameters();

                if (parameters.size() != 1) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "The count of listener method parameter must be 1.", method);
                }

                VariableElement event = parameters.get(0);

                if (!processingEnv.getTypeUtils().isAssignable(event.asType(), eventTypeMirror)) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "The type of parameter of listener method must be Event or its sub class.", method);
                }

                if (!hasModifier(method, Modifier.PUBLIC)) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Listener method must be public", method);
                }

                if (method.getReturnType().getKind() != TypeKind.VOID) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "The return type of listener method must be void.", method);
                }
            }
        }
        return false;
    }
}
